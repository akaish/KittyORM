
/*
 * ---
 *
 *  Copyright (c) 2018 Denis Bogomolov (akaish)
 *
 * This work is licensed under a
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm;

import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;

/**
 * Factory that wraps cursor values to model values
 * <br> Main purpose of writing it is to avoid slow calls of {@link Cursor#getColumnIndex(String)} and instead
 * of them use faster allocation method of those indexes.
 * Created by akaish on 07.09.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyModelCVFactory {
    private final KittyTableConfiguration tableConfiguration;

    private static String VALUE_OF_METHOD_NAME = "valueOf";
    private static String AME_CTM_BOOLEAN_OF = "Error, found value from db can't be treated as boolean ({0}.{1})!";
    private static String AME_CTM_BYTE_OF = "Error, found value from db can't be treated as byte ({0}.{1})!";
    private static String AME_DSE_NA = "Error, deserialization not available for primitive types ({0}.{1})!";
    private static String AME_DSE_NA_NO_INSTRUCTIONS = "Provided field {0} for {1} has no serialization\\deserialization instructions provided in modelClass class!";

    public static final int ROWID_INDEX = 0; // RowID index is always 0, and I found that when calling select rowid, * 0 column for some reasons called id
    // So i get two id columns somehow

    private boolean rowid = true;

    private final SparseArray<String> cursorIndexes = new SparseArray<>();

    public KittyModelCVFactory(KittyTableConfiguration tableConfiguration) {
        this.tableConfiguration = tableConfiguration;
    }

    public void resetIndexesOnRowidSupportChange(boolean rowidOn) {
        if(rowid && rowidOn) return;
        cursorIndexes.clear();
        rowid = rowidOn;
    }

    private int callNumber = 0;

    /**
     * Sets fields of provided instance of {@link KittyModel} (modelClass) with values from provided {@link Cursor} according to
     * provided with this instance's {@link KITTY_COLUMN} annotations data wrapped into {@link KittyColumnConfiguration} instance.
     * Also may throw some exceptions related with reflection access to
     * field of provided modelClass and {@link KittyRuntimeException} if there are some errors with {@link KITTY_COLUMN} implementation.
     *
     * @param cursor cursor to read
     * @param model blank modelClass to fill
     * @param <M>
     * @return filled from provided cursor instance of KittyModel
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */

    public <M extends KittyModel> M cursorToModel(Cursor cursor, M model, boolean rowidOn)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        Iterator<KittyColumnConfiguration> iterator = tableConfiguration.sortedColumns.iterator();
        resetIndexesOnRowidSupportChange(rowidOn);
        callNumber++;
        if (rowid) {
            model.setRowID(cursor.getLong(ROWID_INDEX));
        }
        while (iterator.hasNext()) {
            KittyColumnConfiguration column = iterator.next();
            int columnIndex = getColumnIndex(cursor, column.mainConfiguration.columnName);
            if (column.sdConfiguration != null) {
                model = putSDFromCursorToModel(column, model, cursor, columnIndex);
                continue;
            } else {
                Field columnField = column.mainConfiguration.columnField;
                columnField.setAccessible(true);
                Class<?> fieldType = columnField.getType();
                if (cursor.isNull(columnIndex))
                    continue;
                // boolean and Boolean
                if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
                    int booleanV = cursor.getInt(columnIndex);
                    if (booleanV != 0 && booleanV != 1)
                        throw new KittyRuntimeException(
                                MessageFormat.format(
                                        AME_CTM_BOOLEAN_OF,
                                        model.getClass().getCanonicalName(),
                                        columnField.getName()
                                )
                        );
                    columnField.set(model, booleanV == 1);
                    // String
                } else if (String.class.equals(fieldType)) {
                    String stringV = cursor.getString(columnIndex);
                    columnField.set(model, stringV);
                    // int and Integer
                } else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
                    int intV = cursor.getInt(columnIndex);
                    columnField.set(model, intV);
                    // byte and Byte
                } else if (byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
                    int byteV = cursor.getInt(columnIndex);
                    if (byteV > 127 || byteV < -128)
                        throw new KittyRuntimeException(
                                MessageFormat.format(
                                        AME_CTM_BYTE_OF,
                                        model.getClass().getCanonicalName(),
                                        columnField.getName()
                                )
                        );
                    columnField.set(model, (byte) byteV);
                    // byte array
                } else if (byte[].class.equals(fieldType)) {
                    byte[] bytes = cursor.getBlob(columnIndex);
                    columnField.set(model, bytes);
                    // double and Double
                } else if (double.class.equals(fieldType) || Double.class.equals(fieldType)) {
                    double doubleV = cursor.getDouble(columnIndex);
                    columnField.set(model, doubleV);
                    // long and Long
                } else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
                    long longV = cursor.getLong(columnIndex);
                    columnField.set(model, longV);
                    // short and Short
                } else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
                    short shortV = cursor.getShort(columnIndex);
                    columnField.set(model, shortV);
                    // float and Float
                } else if (float.class.equals(fieldType) || Float.class.equals(fieldType)) {
                    float floatV = cursor.getFloat(columnIndex);
                    columnField.set(model, floatV);
                    // sub long types
                } else if (Calendar.class.equals(fieldType)) {
                    long longV = cursor.getLong(columnIndex);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(longV);
                    columnField.set(model, calendar);
                } else if (Timestamp.class.equals(fieldType)) {
                    long longV = cursor.getLong(columnIndex);
                    columnField.set(model, new Timestamp(longV));
                } else if (Date.class.equals(fieldType)) {
                    long longV = cursor.getLong(columnIndex);
                    columnField.set(model, new Date(longV));
                    // sub String types
                } else if (BigDecimal.class.equals(fieldType)) {
                    String stringV = cursor.getString(columnIndex);
                    columnField.set(model, new BigDecimal(stringV));
                } else if (BigInteger.class.equals(fieldType)) {
                    String stringV = cursor.getString(columnIndex);
                    columnField.set(model, new BigInteger(stringV));
                } else if(Uri.class.equals(fieldType)) {
                    String stringV = cursor.getString(columnIndex);
                    columnField.set(model, Uri.parse(stringV));
                } else if(File.class.equals(fieldType)) {
                    String stringV = cursor.getString(columnIndex);
                    columnField.set(model, new File(stringV));
                } else if(Currency.class.equals(fieldType)) {
                    String stringV = cursor.getString(columnIndex);
                    columnField.set(model, Currency.getInstance(stringV));
                } else if (Enum.class.isAssignableFrom(fieldType)) {
                    Method valueOf = fieldType.getMethod(VALUE_OF_METHOD_NAME, String.class);
                    String strVal = cursor.getString(columnIndex);
                    Object enumVal = valueOf.invoke(fieldType, strVal);
                    columnField.set(model, enumVal);
                } else {
                    throw new KittyRuntimeException(
                            MessageFormat.format(
                                    AME_DSE_NA_NO_INSTRUCTIONS,
                                    columnField.getName(),
                                    model.getClass().getCanonicalName()
                            )
                    );
                }
            }
        }
        return model;
    }


    private final int getColumnIndex(Cursor cursor, String columnName) {
        int storedIndex = cursorIndexes.indexOfValue(columnName);
        if(storedIndex > 0) return cursorIndexes.keyAt(storedIndex);
        int cursorIndex = cursor.getColumnIndex(columnName);
        cursorIndexes.append(cursorIndex, columnName);
        return cursorIndex;
    }

    /**
     * Sets modelClass's field with deserialized value from cursor
     * @param column
     * @param model
     * @param cursor
     * @param <M>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private final <M extends KittyModel> M putSDFromCursorToModel(KittyColumnConfiguration column,
                                                                   M model, Cursor cursor, int columnIndex)
            throws InvocationTargetException, IllegalAccessException {
        Field columnField = column.mainConfiguration.columnField;
        columnField.setAccessible(true);
        Method deserializer = column.sdConfiguration.deserealizationMethod;
        deserializer.setAccessible(true);
        Object fieldData = null;
        switch(column.mainConfiguration.columnAffinity) {
            case BLOB:
                fieldData = cursor.getBlob(columnIndex);
                break;
            case NONE:
                fieldData = cursor.getBlob(columnIndex);
                break;
            case TEXT:
                fieldData = cursor.getString(columnIndex);
                break;
            default:
                throw new KittyRuntimeException(
                        MessageFormat.format(
                                AME_DSE_NA,
                                model.getClass().getCanonicalName(),
                                columnField.getName()
                        )
                );
        }
        deserializer.setAccessible(true);
        columnField.set(model, deserializer.invoke(model, fieldData));
        return model;
    }
}
