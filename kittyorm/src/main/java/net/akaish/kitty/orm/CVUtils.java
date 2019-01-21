
/*
 * ---
 *
 *  Copyright (c) 2018 Denis Bogomolov (akaish)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class to bind data to {@link ContentValues} and back
 * Created by akaish on 07.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class CVUtils {

    private static String AME_SER_BAD_AFFINITY = "Error, serialization not available for INTEGER and REAL types ({0}.{1})!";
    private static String AME_NO_SD_MCV = "Provided field {0} for {1} has no serialization\\deserialization instructions provided in model class!";
    private static String AME_CTM_BOOLEAN_OF = "Error, found value from db can't be treated as boolean ({0}.{1})!";
    private static String AME_CTM_BYTE_OF = "Error, found value from db can't be treated as byte ({0}.{1})!";
    private static String AME_DSE_NA = "Error, deserialization not available for primitive types ({0}.{1})!";
    private static String AME_DSE_NA_NO_INSTRUCTIONS = "Provided field {0} for {1} has no serialization\\deserialization instructions provided in model class!";
    private static String VALUE_OF_METHOD_NAME = "valueOf";

    public static final int INCLUDE_ONLY_SELECTED_FIELDS = 101;
    public static final int INCLUDE_ALL_EXCEPT_SELECTED_FIELDS = 102;
    public static final int INCLUDE_ONLY_SELECTED_COLUMN_NAMES = 103;
    public static final int INCLUDE_ALL_EXCEPT_SELECTED_COLUMN_NAMES = 104;
    public static final int IGNORE_INCLUSIONS_AND_EXCLUSIONS = 105;

    /**
     * Got some bug with queries like SELECT rowid, * from blablablatable.
     * <br> Returned with this queries column names array has duplicated id column instead of having
     * <br> both rowid and id columns. It seems that there is some bug in generating cursor in current
     * <br> Android version that I use for testing or common bug. Also it may be cause used PK are INTEGER NOT NUL PRIMARY KEY
     * <br> that are aliased with ROWID. I don't care, instead of  model.setRowID(cursor.getLong(cursor.getColumnIndex(ROWID)));
     * <br> just would use model.setRowID(cursor.getLong(0));
     *
     */
    public static final int ROWID_INDEX = 0; // RowID index is always 0, and I found that when calling select rowid, * 0 column for some reasons called id
    // So i get two id columns somehow

    /**
     * Generates {@link ContentValues} instance from provided instance of {@link KittyModel} with usage
     * of {@link KittyTableConfiguration} configuration. Also may throw some exceptions related with reflection access to
     * field of provided model and {@link KittyRuntimeException} if there are some errors with {@link KITTY_COLUMN} implementation.
     *
     * @param model instance of {@link KittyModel}
     * @param table table configuration
     *
     * @param names array of fields names or column names to include\exclude with usage of {@link #skipInclusionsExclusions(String[], int, KittyColumnConfiguration)}
     * @param skipOrInclude flag that determines what to do with provided {@link KittyColumnConfiguration} instance, may be {@link #IGNORE_INCLUSIONS_AND_EXCLUSIONS},
     *                      {@link #INCLUDE_ALL_EXCEPT_SELECTED_COLUMN_NAMES}, {@link #INCLUDE_ONLY_SELECTED_COLUMN_NAMES}, {@link #INCLUDE_ALL_EXCEPT_SELECTED_FIELDS}
     *                      or {@link #INCLUDE_ONLY_SELECTED_FIELDS}. If flag's value differs from any of inclusion predefined flags than would be used default flag
     *                      ({@link #IGNORE_INCLUSIONS_AND_EXCLUSIONS})
     *
     * @return ready to insert instance of {@link ContentValues}
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static <M extends KittyModel> ContentValues modelToCV( M model, KittyTableConfiguration table, String[] names, int skipOrInclude)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException,
            NoSuchMethodException, InvocationTargetException {
        ContentValues values = new ContentValues();
        Iterator<KittyColumnConfiguration> iterator = table.sortedColumns.iterator();
        while (iterator.hasNext()) {
            KittyColumnConfiguration column = iterator.next();
            if(skipInclusionsExclusions(names, skipOrInclude, column)) continue;
            if (column.sdConfiguration != null) {
                values = putSDToCV(model, values, column);
                continue;
            } else {
                Class<?> fieldType = column.mainConfiguration.columnField.getType();
                Field columnField = column.mainConfiguration.columnField;
                columnField.setAccessible(true);
                if (fieldType.isPrimitive()) {
                    values = setPrimitivesToCV(fieldType, values, model, columnField, column);
                } else if (isLongRepresentedType(fieldType)) {
                    values = setLongToCV(fieldType, values, model, columnField, column);
                } else if (isStringRepresentedType(fieldType)) {
                    values = setStringsToCV(fieldType, values, model, columnField, column);
                } else if (isDefaultTypeRepresentedType(fieldType)){
                    values = setDefTypesToCV(fieldType, values, model, columnField, column);
                } else {
                    throw new KittyRuntimeException(MessageFormat.format(AME_NO_SD_MCV,
                            column.mainConfiguration.columnName, model.getClass().getCanonicalName()));
                }
            }
        }
        return values;
    }

    /**
     * Generates {@link ContentValues} instance from provided instance of {@link KittyModel} with usage
     * of {@link KittyTableConfiguration} configuration. Also may throw some exceptions related with reflection access to
     * field of provided model and {@link KittyRuntimeException} if there are some errors with {@link KITTY_COLUMN} implementation.
     *
     * @return ready to insert instance of {@link ContentValues}
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static <M extends KittyModel> ContentValues modelToCV( M model, KittyTableConfiguration table)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException,
            NoSuchMethodException, InvocationTargetException {
        return modelToCV(
                model, table, null, IGNORE_INCLUSIONS_AND_EXCLUSIONS
        );
    }

    /**
     * Returns true if provided column should be excluded from generating {@link ContentValues} or false
     * if not. Exclusion decision generated based on input names String array and provided flag.
     * <br> Names array can contain set of field names to include or exclude or set of column names of related tables
     * to include or exclude.
     * <br> Names array can't contain both field names and column names.
     * @param names array of fields names or column names
     * @param skipOrInclude flag that determines what to do with provided {@link KittyColumnConfiguration} instance, may be {@link #IGNORE_INCLUSIONS_AND_EXCLUSIONS},
     *                      {@link #INCLUDE_ALL_EXCEPT_SELECTED_COLUMN_NAMES}, {@link #INCLUDE_ONLY_SELECTED_COLUMN_NAMES}, {@link #INCLUDE_ALL_EXCEPT_SELECTED_FIELDS}
     *                      or {@link #INCLUDE_ONLY_SELECTED_FIELDS}. If flag's value differs from any of inclusion predefined flags than would be used default flag
     *                      ({@link #IGNORE_INCLUSIONS_AND_EXCLUSIONS})
     * @param column column to exclude or include in {@link ContentValues]} generation
     * @return
     */
    private static boolean skipInclusionsExclusions(String[] names, int skipOrInclude, KittyColumnConfiguration column) {
        if(names == null) return false;
        List<String> namesList = Arrays.asList(names);
        switch (skipOrInclude) {
            case IGNORE_INCLUSIONS_AND_EXCLUSIONS:
                return false;
            case INCLUDE_ALL_EXCEPT_SELECTED_COLUMN_NAMES:
                if(namesList.contains(column.mainConfiguration.columnName))
                    return true;
                else return false;
            case INCLUDE_ONLY_SELECTED_COLUMN_NAMES:
                if(namesList.contains(column.mainConfiguration.columnName))
                    return false;
                else return true;
            case INCLUDE_ALL_EXCEPT_SELECTED_FIELDS:
                if(namesList.contains(column.mainConfiguration.columnField.getName()))
                    return true;
                else return false;
            case INCLUDE_ONLY_SELECTED_FIELDS:
                if(namesList.contains(column.mainConfiguration.columnField.getName()))
                    return false;
                else return true;
            default:
                return false;
        }
    }


    /**
     * <br><b>Deprecated (it was too slow), use {@link KittyModelCVFactory#cursorToModel(Cursor, KittyModel, boolean)} instead</b>
     *  Sets fields of provided instance of {@link KittyModel} (model) with values from provided {@link Cursor} according to
     * provided with this instance's {@link KITTY_COLUMN} annotations data wrapped into {@link KittyColumnConfiguration} instance.
     * Also may throw some exceptions related with reflection access to
     * field of provided model and {@link KittyRuntimeException} if there are some errors with {@link KITTY_COLUMN} implementation.
     *
     * @param rowIDSupport RowID support flag
     * @param cursor cursor to read
     * @param model blank model to fill
     * @param table table configuration to use with this model and cursor
     * @param <M>
     * @return filled from provided cursor instance of KittyModel
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    @Deprecated
    public static <M extends KittyModel> M cursorToModel(boolean rowIDSupport, Cursor cursor, M model,
                                                         KittyTableConfiguration table)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        Iterator<KittyColumnConfiguration> iterator = table.sortedColumns.iterator();
        if(rowIDSupport) {
            model.setRowID(cursor.getLong(ROWID_INDEX));
        }
        while(iterator.hasNext()) {
            KittyColumnConfiguration column = iterator.next();
            if(model.exclusions.contains(column.mainConfiguration.columnField.getName()))
                continue;
            if (column.sdConfiguration != null) {
                model = putSDFromCursorToModel(column, model, cursor);
                continue;
            } else {
                Field columnField = column.mainConfiguration.columnField;
                columnField.setAccessible(true);
                Class<?> fieldType = columnField.getType();
                if(cursor.isNull(cursor.getColumnIndex(column.mainConfiguration.columnName)))
                    continue;
                // boolean and Boolean
                if(boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
                    int booleanV = cursor.getInt(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    if (booleanV != 0 && booleanV != 1)
                        throw new KittyRuntimeException(MessageFormat.format(AME_CTM_BOOLEAN_OF, model.getClass().getCanonicalName(), columnField.getName()));
                        columnField.set(model, booleanV == 1);
                    // String
                } else if(String.class.equals(fieldType)) {
                    String stringV = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, stringV);
                    // int and Integer
                } else if(int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
                    int intV = cursor.getInt(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, intV);
                    // byte and Byte
                } else if(byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
                    int byteV = cursor.getInt(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    if (byteV > 127 || byteV < -128)
                        throw new KittyRuntimeException(MessageFormat.format(AME_CTM_BYTE_OF, model.getClass().getCanonicalName(), columnField.getName()));
                    columnField.set(model, (byte) byteV);
                    // byte array
                } else if(byte[].class.equals(fieldType)) {
                    byte[] bytes = cursor.getBlob(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, bytes);
                } else if(Byte[].class.equals(fieldType)) {
                    byte[] bytes = cursor.getBlob(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    Byte[] bytesObj = new Byte[bytes.length];
                    for (int i = 0; i < bytes.length; i++)
                        bytesObj[i] = bytes[i];
                    columnField.set(model, bytesObj);
                    // double and Double
                } else if(double.class.equals(fieldType) || Double.class.equals(fieldType)) {
                    double doubleV = cursor.getDouble(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, doubleV);
                    // long and Long
                } else if(long.class.equals(fieldType) || Long.class.equals(fieldType)) {
                    long longV = cursor.getLong(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, longV);
                    // short and Short
                } else if(short.class.equals(fieldType) || Short.class.equals(fieldType)) {
                    short shortV = cursor.getShort(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, shortV);
                    // float and Float
                } else if(float.class.equals(fieldType) || Float.class.equals(fieldType)) {
                    float floatV = cursor.getFloat(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, floatV);
                    // sub long types
                } else if(Calendar.class.equals(fieldType)) {
                    long longV = cursor.getLong(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(longV);
                    columnField.set(model, calendar);
                } else if(Timestamp.class.equals(fieldType)) {
                    long longV = cursor.getLong(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, new Timestamp(longV));
                } else if(Date.class.equals(fieldType)) {
                    long longV = cursor.getLong(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, new Date(longV));
                    // sub String types
                } else if(BigDecimal.class.equals(fieldType)) {
                    String stringV = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, new BigDecimal(stringV));
                } else if(BigInteger.class.equals(fieldType)) {
                    String stringV = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, new BigInteger(stringV));
                } else if(Uri.class.equals(fieldType)) {
                    String stringV = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, Uri.parse(stringV));
                } else if(File.class.equals(fieldType)) {
                    String stringV = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, new File(stringV));
                } else if(Currency.class.equals(fieldType)) {
                    String stringV = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    columnField.set(model, Currency.getInstance(stringV));
                } else if (Enum.class.isAssignableFrom(fieldType)) {
                    Method valueOf = fieldType.getMethod(VALUE_OF_METHOD_NAME, String.class);
                    String strVal = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                    Object enumVal = valueOf.invoke(fieldType, strVal);
                    columnField.set(model, enumVal);
                } else {
                    throw new KittyRuntimeException(MessageFormat.format(AME_DSE_NA_NO_INSTRUCTIONS, columnField.getName(), model.getClass().getCanonicalName()));
                }
            }
        }
        return model;
    }

    /**
     * Sets model's field with deserialized value from cursor
     * @param column
     * @param model
     * @param cursor
     * @param <M>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Deprecated
    private static <M extends KittyModel> M putSDFromCursorToModel(KittyColumnConfiguration column,
                                                                   M model, Cursor cursor)
            throws InvocationTargetException, IllegalAccessException {
        Field columnField = column.mainConfiguration.columnField;
        columnField.setAccessible(true);
        Method deserializer = column.sdConfiguration.deserealizationMethod;
        deserializer.setAccessible(true);
        Object fieldData = null;
        switch(column.mainConfiguration.columnAffinity) {
            case NONE:
                fieldData = cursor.getBlob(cursor.getColumnIndex(column.mainConfiguration.columnName));
                break;
            case TEXT:
                fieldData = cursor.getString(cursor.getColumnIndex(column.mainConfiguration.columnName));
                break;
            default:
                throw new KittyRuntimeException(MessageFormat.format(AME_DSE_NA, model.getClass().getCanonicalName(), columnField.getName()));
        }
        deserializer.setAccessible(true);
        columnField.set(model, deserializer.invoke(model, fieldData));
        return model;
    }

    ////////////////////////////////////////////////////////////
    ///// FROM MODEL TO CONTENT VALUES HELPER METHODS //////////
    ////////////////////////////////////////////////////////////
    /**
     * Returns true if provided field type is Boolean, Integer, Byte, byte[], Double, Short or Float
     * @param fieldType
     * @return
     */
    static boolean isDefaultTypeRepresentedType(Class<?> fieldType) {
        return Boolean.class.equals(fieldType) || Integer.class.equals(fieldType) || Byte.class.equals(fieldType)
                || byte[].class.equals(fieldType) || Byte[].class.equals(fieldType) || Double.class.equals(fieldType) || Short.class.equals(fieldType)
                || Float.class.equals(fieldType);
    }

    /**
     * Returns true if provided field type is String, BigDecimal, BigInteger or Enum
     * @param fieldType
     * @return
     */
    static boolean isStringRepresentedType(Class<?> fieldType) {
        return String.class.equals(fieldType) || fieldType.isEnum() || BigDecimal.class.equals(fieldType)
                || BigInteger.class.equals(fieldType) || Uri.class.equals(fieldType) || File.class.equals(fieldType)
                || Currency.class.equals(fieldType);
    }

    /**
     * Returns true if provided type is Long, Calendar, Timestamp or Date
     * @param fieldType
     * @return
     */
    static boolean isLongRepresentedType(Class<?> fieldType) {
        return Long.class.equals(fieldType) || Calendar.class.equals(fieldType) || Timestamp.class.equals(fieldType)
                || Date.class.equals(fieldType);
    }

    /**
     * Sets to provided cv object from model field that should be treated as Boolean, Integer, Byte, byte[], Double, Short or Float
     * @param fieldType
     * @param values
     * @param model
     * @param columnField
     * @param column
     * @param <M>
     * @return
     * @throws IllegalAccessException
     */
    private static <M extends KittyModel> ContentValues setDefTypesToCV(Class<?> fieldType,
                                                                        ContentValues values,
                                                                        M model, Field columnField,
                                                                        KittyColumnConfiguration column)
            throws IllegalAccessException {
        if (Boolean.class.equals(fieldType)) {
            Boolean boolV = (Boolean) columnField.get(model);
            if(boolV == null) {
                values.putNull(column.mainConfiguration.columnName);
            } else {
                values.put(column.mainConfiguration.columnName, (boolV ? 1 : 0));
            }
        } else if (Integer.class.equals(fieldType)) {
            Integer intV = (Integer) columnField.get(model);
            AVUtils.checkIntValue(intV, column, model.getClass());
            if (intV == null)
                values.putNull(column.mainConfiguration.columnName);
            else
                values.put(column.mainConfiguration.columnName, intV);
        } else if (Byte.class.equals(fieldType)) {
            Byte byteV = (Byte) columnField.get(model);
            AVUtils.checkByteValue(byteV, column, model.getClass());
            if (byteV == null) {
                values.putNull(column.mainConfiguration.columnName);
            } else {
                values.put(column.mainConfiguration.columnName, (int) byteV);
            }
        } else if (Byte[].class.equals(fieldType)) {
            Byte[] bytes = (Byte[]) columnField.get(model);
            if(bytes == null) {
                values.putNull(column.mainConfiguration.columnName);
            } else {
                byte[] bytesToCV = new byte[bytes.length];
                for(int i = 0; i < bytes.length; i++)
                    bytesToCV[i] = bytes[i].byteValue();
                values.put(column.mainConfiguration.columnName, bytesToCV);
            }
        } else if (byte[].class.equals(fieldType)) {
            byte[] bytes = (byte[]) columnField.get(model);
            if(bytes == null) {
                values.putNull(column.mainConfiguration.columnName);
            } else {
                values.put(column.mainConfiguration.columnName, bytes);
            }
        } else if (Double.class.equals(fieldType)) {
            Double doubleV = (Double) columnField.get(model);
            AVUtils.checkDoubleValue(doubleV, column, model.getClass());
            if(doubleV == null) {
                values.putNull(column.mainConfiguration.columnName);
            } else {
                values.put(column.mainConfiguration.columnName, doubleV);
            }
        } else if (Short.class.equals(fieldType)) {
            Short shortV = (Short) columnField.get(model);
            AVUtils.checkShortValue(shortV, column, model.getClass());
            if(shortV == null) {
                values.putNull(column.mainConfiguration.columnName);
            } else {
                values.put(column.mainConfiguration.columnName, shortV);
            }
        } else if (Float.class.equals(fieldType)) {
            Float floatV = (Float) columnField.get(model);
            AVUtils.checkFloatValue(floatV, column, model.getClass());
            if(floatV == null) {
                values.putNull(column.mainConfiguration.columnName);
            } else {
                values.put(column.mainConfiguration.columnName, floatV);
            }
        }
        return values;
    }

    /**
     * Sets to provided cv object from model field that should be treated as Long (Long, Date, Calendar, Timestamp)
     * @param fieldType
     * @param values
     * @param model
     * @param columnField
     * @param column
     * @param <M>
     * @return
     * @throws IllegalAccessException
     */
    private static <M extends KittyModel> ContentValues setLongToCV(Class<?> fieldType,
                                                                    ContentValues values,
                                                                    M model, Field columnField,
                                                                    KittyColumnConfiguration column)
            throws IllegalAccessException {
        Long longV = null;
        if(Calendar.class.equals(fieldType)) {
            Calendar cal = (Calendar) columnField.get(model);
            if(cal == null) longV = null;
            else longV = cal.getTimeInMillis();
        } else if(Timestamp.class.equals(fieldType)) {
            Timestamp tm = (Timestamp) columnField.get(model);
            if(tm == null) longV = null;
            else longV = tm.getTime();
        } else if(Date.class.equals(fieldType)) {
            Date date = (Date) columnField.get(model);
            if(date == null) longV = null;
            else longV = date.getTime();
        } else {
            longV = (Long) columnField.get(model);
        }
        AVUtils.checkLongValue(longV, column, model.getClass());
        if(longV == null) {
            values.putNull(column.mainConfiguration.columnName);
        } else {
            values.put(column.mainConfiguration.columnName, longV);
        }
        return values;
    }

    /**
     * Sets to provided cv object from model field that should be treated as String (TEXT) (String, BigDecimal, BigInteger, Enum)
     * @param fieldType
     * @param values
     * @param model
     * @param columnField
     * @param column
     * @param <M>
     * @return
     * @throws IllegalAccessException
     */
    private static <M extends KittyModel> ContentValues setStringsToCV(Class<?> fieldType,
                                                                       ContentValues values,
                                                                       M model, Field columnField,
                                                                       KittyColumnConfiguration column)
            throws IllegalAccessException {
        String stringV;
        if(String.class.equals(fieldType)) {
            stringV = (String) columnField.get(model);
        } else if(BigDecimal.class.equals(fieldType)) {
            BigDecimal bd = (BigDecimal) columnField.get(model);
            if(bd == null) stringV = null;
            else  stringV = bd.toString();
        } else if(BigInteger.class.equals(fieldType)) {
            BigInteger bi = (BigInteger) columnField.get(model);
            if(bi == null) stringV = null;
            else stringV = bi.toString();
        } else if(Uri.class.equals(fieldType)) {
            Uri uri = (Uri) columnField.get(model);
            if(uri == null) stringV = null;
            else stringV = uri.toString();
        } else if(File.class.equals(fieldType)) {
            File file = (File) columnField.get(model);
            if(file == null) stringV = null;
            else stringV = file.getAbsolutePath();
        } else if(Currency.class.equals(fieldType)) {
            Currency currency = (Currency) columnField.get(model);
            if(currency == null) stringV = null;
            else stringV = currency.getCurrencyCode();
        } else {
            Enum ev = (Enum) columnField.get(model);
            if(ev == null) stringV = null;
            else stringV = ev.name();
        }
        AVUtils.checkStringValue(stringV, column, model.getClass());
        if(stringV == null) {
            values.putNull(column.mainConfiguration.columnName);
        } else {
            values.put(column.mainConfiguration.columnName, stringV);
        }
        return values;
    }

    /**
     * Sets primitive from model's field to provided cv
     * <br> (boolean, int, byte, double, long, short, float)
     * @param fieldType
     * @param values
     * @param model
     * @param columnField
     * @param column
     * @param <M>
     * @return
     * @throws IllegalAccessException
     */
    private static <M extends KittyModel> ContentValues setPrimitivesToCV(Class<?> fieldType,
                                                                          ContentValues values,
                                                                          M model, Field columnField,
                                                                          KittyColumnConfiguration column)
            throws IllegalAccessException {
        if(boolean.class.equals(fieldType)) {
            boolean bool = columnField.getBoolean(model);
            values.put(column.mainConfiguration.columnName, (bool ? 1 : 0));
        } else if(int.class.equals(fieldType)) {
            int intV = columnField.getInt(model);
            AVUtils.checkIntValue(intV, column, model.getClass());
            values.put(column.mainConfiguration.columnName, intV);
        }   else if(byte.class.equals(fieldType)) {
            byte byteV = columnField.getByte(model);
            AVUtils.checkByteValue(byteV, column, model.getClass());
            values.put(column.mainConfiguration.columnName, (int) byteV);
        } else if(double.class.equals(fieldType)) {
            double doubleV = columnField.getDouble(model);
            AVUtils.checkDoubleValue(doubleV, column, model.getClass());
            values.put(column.mainConfiguration.columnName, doubleV);
        } else if(long.class.equals(fieldType)) {
            long longV = columnField.getLong(model);
            AVUtils.checkLongValue(longV, column, model.getClass());
            values.put(column.mainConfiguration.columnName, longV);
        } else if(short.class.equals(fieldType)) {
            short shortV = columnField.getShort(model);
            AVUtils.checkShortValue(shortV, column, model.getClass());
            values.put(column.mainConfiguration.columnName, shortV);
        } else if(float.class.equals(fieldType)) {
            float floatV = columnField.getFloat(model);
            AVUtils.checkFloatValue(floatV, column, model.getClass());
            values.put(column.mainConfiguration.columnName, floatV);
        }
        return values;
    }

    /**
     * Puts serialized data to provided content values from specified column
     * @param model model to use
     * @param values content values to fill
     * @param column column configuration
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws KittyRuntimeException when specified affinity not NONE (BLOB) or TEXT
     */
    private static ContentValues putSDToCV(KittyModel model, ContentValues values, KittyColumnConfiguration column)
            throws InvocationTargetException, IllegalAccessException {
        Method serializer = column.sdConfiguration.serealizationMethod;
        serializer.setAccessible(true);
        switch(column.mainConfiguration.columnAffinity) {
            case BLOB:
                byte[] blob = (byte[]) serializer.invoke(model);
                values.put(column.mainConfiguration.columnName, blob);
                break;
            case NONE:
                byte[] none = (byte[]) serializer.invoke(model);
                values.put(column.mainConfiguration.columnName, none);
                break;
            case TEXT:
                String string = (String) serializer.invoke(model);
                values.put(column.mainConfiguration.columnName, string);
                break;
            default:
                throw new KittyRuntimeException(MessageFormat.format(AME_SER_BAD_AFFINITY,
                        model.getClass().getCanonicalName(), column.mainConfiguration.columnName));
        }
        return values;
    }
}
