
/*
 * ---
 *
 *  Copyright (c) 2018-2020 Denis Bogomolov (akaish)
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
import android.net.Uri;

import net.akaish.kitty.orm.annotations.column.Column;
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

    public static final int INCLUDE_ONLY_SELECTED_FIELDS = 101;
    public static final int INCLUDE_ALL_EXCEPT_SELECTED_FIELDS = 102;
    public static final int INCLUDE_ONLY_SELECTED_COLUMN_NAMES = 103;
    public static final int INCLUDE_ALL_EXCEPT_SELECTED_COLUMN_NAMES = 104;
    public static final int IGNORE_INCLUSIONS_AND_EXCLUSIONS = 105;

    /**
     * Generates {@link ContentValues} instance from provided instance of {@link KittyModel} with usage
     * of {@link KittyTableConfiguration} configuration. Also may throw some exceptions related with reflection access to
     * field of provided model and {@link KittyRuntimeException} if there are some errors with {@link Column} implementation.
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
        for (KittyColumnConfiguration column : table.sortedColumns) {
            if (skipInclusionsExclusions(names, skipOrInclude, column)) continue;
            if (column.sdConfiguration != null) {
                values = putSDToCV(model, values, column);
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
                } else if (isDefaultTypeRepresentedType(fieldType)) {
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
            case INCLUDE_ALL_EXCEPT_SELECTED_COLUMN_NAMES:
                return namesList.contains(column.mainConfiguration.columnName);
            case INCLUDE_ONLY_SELECTED_COLUMN_NAMES:
                return !namesList.contains(column.mainConfiguration.columnName);
            case INCLUDE_ALL_EXCEPT_SELECTED_FIELDS:
                return namesList.contains(column.mainConfiguration.columnField.getName());
            case INCLUDE_ONLY_SELECTED_FIELDS:
                return !namesList.contains(column.mainConfiguration.columnField.getName());
            case IGNORE_INCLUSIONS_AND_EXCLUSIONS:
            default:
                return false;
        }
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
                    bytesToCV[i] = bytes[i];
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
        Long longV ;
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
