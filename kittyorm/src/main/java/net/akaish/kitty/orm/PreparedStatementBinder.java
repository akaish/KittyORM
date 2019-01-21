
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

import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

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
 * Created by akaish on 08.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class PreparedStatementBinder {

    private static String AME_SER_BAD_AFFINITY = "Error, serialization not available for INTEGER and REAL types ({0}.{1})!";
    private static String AME_NO_SD_MCV = "Provided field {0} for {1} has no serialization\\deserialization instructions provided in modelClass class!";

    public static <M extends KittyModel> SQLiteStatement
    bindModelValuesToPreparedStatement(M model, KittyTableConfiguration table, SQLiteStatement statement)
            throws InvocationTargetException, IllegalAccessException {
        statement.clearBindings();
        Iterator<KittyColumnConfiguration> iterator = table.sortedColumns.iterator();
        int bindingIndex = 1;
        while (iterator.hasNext()) {
            KittyColumnConfiguration column = iterator.next();
            if(model.exclusions.contains(column.mainConfiguration.columnField.getName()))
                continue;
            if(column.sdConfiguration != null) {
                statement = bindSD(model, statement, bindingIndex, column);
                bindingIndex++;
                continue;
            } else {
                Class<?> fieldType = column.mainConfiguration.columnField.getType();
                Field columnField = column.mainConfiguration.columnField;
                columnField.setAccessible(true);
                if (fieldType.isPrimitive()) {
                    statement = bindPrimitives(fieldType, statement, bindingIndex, model, columnField, column);
                } else if (CVUtils.isLongRepresentedType(fieldType)) {
                    statement = bindLong(fieldType, statement, bindingIndex, model, columnField, column);
                } else if (CVUtils.isStringRepresentedType(fieldType)) {
                    statement = bindString(fieldType, statement, bindingIndex, model, columnField, column);
                } else if (CVUtils.isDefaultTypeRepresentedType(fieldType)){
                    statement = bindWrappers(fieldType, statement, bindingIndex, model, columnField, column);
                } else {
                    throw new KittyRuntimeException(MessageFormat.format(AME_NO_SD_MCV,
                            column.mainConfiguration.columnName, model.getClass().getCanonicalName()));
                }
            }
            bindingIndex++;
        }
        return statement;
    }

    private static <M extends KittyModel> SQLiteStatement bindWrappers(Class<?> fieldType,
                                                                       SQLiteStatement statement,
                                                                       int bindingIndex,
                                                                       M model, Field columnField,
                                                                       KittyColumnConfiguration column)
            throws IllegalAccessException {
        if (Boolean.class.equals(fieldType)) {
            Boolean boolV = (Boolean) columnField.get(model);
            if(boolV == null) {
                statement.bindNull(bindingIndex);
            } else {
                statement.bindLong(bindingIndex, (boolV ? 1 : 0));
            }
            return statement;
        } else if (Integer.class.equals(fieldType)) {
            Integer intV = (Integer) columnField.get(model);
            AVUtils.checkIntValue(intV, column, model.getClass());
            if (intV == null)
                statement.bindNull(bindingIndex);
            else
                statement.bindLong(bindingIndex, intV);
            return statement;
        } else if (Byte.class.equals(fieldType)) {
            Byte byteV = (Byte) columnField.get(model);
            AVUtils.checkByteValue(byteV, column, model.getClass());
            if (byteV == null) {
                statement.bindNull(bindingIndex);
            } else {
                statement.bindLong(bindingIndex, byteV);
            }
            return statement;
        } else if (Byte[].class.equals(fieldType)) {
            Byte[] bytes = (Byte[]) columnField.get(model);
            if(bytes == null) {
                statement.bindNull(bindingIndex);
            } else {
                byte[] bytesToStatement = new byte[bytes.length];
                for(int i = 0; i < bytes.length; i++)
                    bytesToStatement[i] = bytes[i].byteValue();
                statement.bindBlob(bindingIndex, bytesToStatement);
            }
            return statement;
        } else if (byte[].class.equals(fieldType)) {
            byte[] bytes = (byte[]) columnField.get(model);
            if(bytes == null) {
                statement.bindNull(bindingIndex);
            } else {
                statement.bindBlob(bindingIndex, bytes);
            }
            return statement;
        } else if (Double.class.equals(fieldType)) {
            Double doubleV = (Double) columnField.get(model);
            AVUtils.checkDoubleValue(doubleV, column, model.getClass());
            if(doubleV == null) {
                statement.bindNull(bindingIndex);
            } else {
                statement.bindDouble(bindingIndex, doubleV);
            }
            return statement;
        } else if (Short.class.equals(fieldType)) {
            Short shortV = (Short) columnField.get(model);
            AVUtils.checkShortValue(shortV, column, model.getClass());
            if(shortV == null) {
                statement.bindNull(bindingIndex);
            } else {
                statement.bindLong(bindingIndex, shortV);
            }
            return statement;
        } else if (Float.class.equals(fieldType)) {
            Float floatV = (Float) columnField.get(model);
            AVUtils.checkFloatValue(floatV, column, model.getClass());
            if(floatV == null) {
                statement.bindNull(bindingIndex);
            } else {
                statement.bindDouble(bindingIndex, floatV);
            }
            return statement;
        }
        // TODO maybe exception here?
        return statement;
    }

    private static <M extends KittyModel> SQLiteStatement bindString(Class<?> fieldType,
                                                                     SQLiteStatement statement,
                                                                     int bindingIndex,
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
            if (bi == null) stringV = null;
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
            statement.bindNull(bindingIndex);
        } else {
            statement.bindString(bindingIndex, stringV);
        }
        return statement;
    }

    private static <M extends KittyModel> SQLiteStatement bindLong(Class<?> fieldType,
                                                                   SQLiteStatement statement,
                                                                   int bindingIndex,
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
            statement.bindNull(bindingIndex);
        } else {
            statement.bindLong(bindingIndex, longV);
        }
        return statement;
    }

    private static SQLiteStatement bindSD(KittyModel model, SQLiteStatement statement,
                                        int bindingIndex, KittyColumnConfiguration column)
            throws InvocationTargetException, IllegalAccessException {
        Method serializer = column.sdConfiguration.serealizationMethod;
        serializer.setAccessible(true);
        switch(column.mainConfiguration.columnAffinity) {
            case BLOB:
                byte[] blob = (byte[]) serializer.invoke(model);
                if(blob != null)
                    statement.bindBlob(bindingIndex, blob);
                else
                    statement.bindNull(bindingIndex);
                break;
            case NONE:
                byte[] none = (byte[]) serializer.invoke(model);
                if(none != null)
                    statement.bindBlob(bindingIndex, none);
                else
                    statement.bindNull(bindingIndex);
                break;
            case TEXT:
                String string = (String) serializer.invoke(model);
                if(string != null)
                    statement.bindString(bindingIndex, string);
                else
                    statement.bindNull(bindingIndex);
                break;
            default:
                throw new KittyRuntimeException(MessageFormat.format(AME_SER_BAD_AFFINITY,
                        model.getClass().getCanonicalName(), column.mainConfiguration.columnName));
        }
        return statement;
    }

    private static <M extends KittyModel> SQLiteStatement bindPrimitives(Class<?> fieldType,
                                                                         SQLiteStatement statement,
                                                                         int bindingIndex,
                                                                         M model, Field columnField,
                                                                         KittyColumnConfiguration column)
            throws IllegalAccessException {
        if(boolean.class.equals(fieldType)) {
            boolean bool = columnField.getBoolean(model);
            statement.bindLong(bindingIndex, (bool ? 1 : 0));
        } else if(int.class.equals(fieldType)) {
            int intV = columnField.getInt(model);
            AVUtils.checkIntValue(intV, column, model.getClass());
            statement.bindLong(bindingIndex, intV);
        }   else if(byte.class.equals(fieldType)) {
            byte byteV = columnField.getByte(model);
            AVUtils.checkByteValue(byteV, column, model.getClass());
            statement.bindLong(bindingIndex, byteV);
        } else if(double.class.equals(fieldType)) {
            double doubleV = columnField.getDouble(model);
            AVUtils.checkDoubleValue(doubleV, column, model.getClass());
            statement.bindDouble(bindingIndex, doubleV);
        } else if(long.class.equals(fieldType)) {
            long longV = columnField.getLong(model);
            AVUtils.checkLongValue(longV, column, model.getClass());
            statement.bindLong(bindingIndex, longV);
        } else if(short.class.equals(fieldType)) {
            short shortV = columnField.getShort(model);
            AVUtils.checkShortValue(shortV, column, model.getClass());
            statement.bindLong(bindingIndex, shortV);
        } else if(float.class.equals(fieldType)) {
            float floatV = columnField.getFloat(model);
            AVUtils.checkFloatValue(floatV, column, model.getClass());
            statement.bindDouble(bindingIndex, floatV);
        }
        return statement;
    }
}
