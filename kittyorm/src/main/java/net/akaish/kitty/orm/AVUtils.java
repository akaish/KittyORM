
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

import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN_ACCEPTED_VALUES;
import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.exceptions.KittyRestrictedValueException;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.util.Arrays;

/**
 * Class for checking accepted values defined in {@link KITTY_COLUMN_ACCEPTED_VALUES}
 * Created by akaish on 03.04.18.
 * @author akaish (Denis Bogomolov)
 */
class AVUtils {

    // Strings for KittyRestrictedValueException
    private final static String R_INT_V = "AMField.acceptedValuesInt()";
    private final static String R_BYTE_V = "AMField.acceptedValuesByte()";
    private final static String R_DOUBLE_V = "AMField.acceptedValuesDouble()";
    private final static String R_LONG_V = "AMField.acceptedValuesLong()";
    private final static String R_SHORT_V = "AMField.acceptedValuesShort()";
    private final static String R_FLOAT_V = "AMField.acceptedValuesFloat()";
    private final static String R_STRING_V = "AMField.acceptedValuesFloat()";

    /**
     * Checks if provided stringValue suites constraint described in {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesInt()} and throws
     * {@link KittyRuntimeException} if this value is restricted one.
     * @param value String value to check
     * @param column column configuration
     * @param modelClass modelClass class for generating exception info
     */
    static void checkStringValue(String value, KittyColumnConfiguration column, Class modelClass) {
        if(column.avConfiguration == null) return;
        if(column.avConfiguration != null && value == null)
            throw new KittyRestrictedValueException(R_STRING_V, modelClass, column.mainConfiguration.columnName);
        if(column.avConfiguration.acceptedValuesString.length!=0)
            if(!Arrays.asList(column.avConfiguration.acceptedValuesString).contains(value))
                throw new KittyRestrictedValueException(R_STRING_V, modelClass, column.mainConfiguration.columnName);
    }

    /**
     * Checks if provided intValue suites constraint described in {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesInt()} and throws
     * {@link KittyRuntimeException} if this value is restricted one.
     * @param intValue int value to check
     * @param column column configuration
     * @param modelClass modelClass class for generating exception info
     */
    static void checkIntValue(Integer intValue, KittyColumnConfiguration column, Class modelClass) {
        if(column.avConfiguration == null) return;
        if(column.avConfiguration != null && intValue == null)
            throw new KittyRestrictedValueException(R_INT_V, modelClass, column.mainConfiguration.columnName);
        if(column.avConfiguration.acceptedValuesInt.length!=0) {
            for(int i : column.avConfiguration.acceptedValuesInt)
                if(i == intValue)
                    return;
        } else {
            return;
        }
        throw new KittyRestrictedValueException(R_INT_V, modelClass, column.mainConfiguration.columnName);
    }

    /**
     * Checks if provided byteValue suites constraint described in {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesByte()} and throws
     * {@link KittyRuntimeException} if this value is restricted one.
     * @param byteValue byte value to check
     * @param column column configuration
     * @param modelClass modelClass class for generating exception info
     */
    static void checkByteValue(Byte byteValue, KittyColumnConfiguration column, Class modelClass) {
        if(column.avConfiguration == null) return;
        if(byteValue == null)
            throw new KittyRestrictedValueException(R_BYTE_V, modelClass, column.mainConfiguration.columnName);
        if(column.avConfiguration.acceptedValuesByte.length!=0) {
            for(byte i : column.avConfiguration.acceptedValuesByte)
                if(i == byteValue)
                    return;
        } else {
            return;
        }
        throw new KittyRestrictedValueException(R_BYTE_V, modelClass, column.mainConfiguration.columnName);
    }

    /**
     * Checks if provided doubleValue suites constraint described in {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesDouble()} and throws
     * {@link KittyRuntimeException} if this value is restricted one.
     * @param doubleValue double value to check
     * @param column column configuration
     * @param modelClass modelClass class for generating exception info
     */
    static final void checkDoubleValue(Double doubleValue, KittyColumnConfiguration column, Class modelClass) {
        if(column.avConfiguration == null) return;
        if(doubleValue == null)
            throw new KittyRestrictedValueException(R_DOUBLE_V, modelClass, column.mainConfiguration.columnName);
        if(column.avConfiguration.acceptedValuesDouble.length!=0) {
            for(double i : column.avConfiguration.acceptedValuesDouble)
                if(i == doubleValue)
                    return;
        } else {
            return;
        }
        throw new KittyRestrictedValueException(R_DOUBLE_V, modelClass, column.mainConfiguration.columnName);
    }

    /**
     * Checks if provided doubleValue suites constraint described in {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesLong()} and throws
     * {@link KittyRuntimeException} if this value is restricted one.
     * @param longValue long value to check
     * @param column column configuration
     * @param modelClass modelClass class for generating exception info
     */
    static final void checkLongValue(Long longValue, KittyColumnConfiguration column, Class modelClass) {
        if(column.avConfiguration == null) return;
        if(longValue == null)
            throw new KittyRestrictedValueException(R_LONG_V, modelClass, column.mainConfiguration.columnName);
        if(column.avConfiguration.acceptedValuesLong.length!=0) {
            for(long i : column.avConfiguration.acceptedValuesLong)
                if(i == longValue)
                    return;
        } else {
            return;
        }
        throw new KittyRestrictedValueException(R_LONG_V, modelClass, column.mainConfiguration.columnName);
    }

    /**
     * Checks if provided shortValue suites constraint described in {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesShort()} and throws
     * {@link KittyRuntimeException} if this value is restricted one.
     * @param shortValue short value to check
     * @param column column configuration
     * @param modelClass modelClass class for generating exception info
     */
    static final void checkShortValue(Short shortValue, KittyColumnConfiguration column, Class modelClass) {
        if(column.avConfiguration == null) return;
        if(shortValue == null)
            throw new KittyRestrictedValueException(R_LONG_V, modelClass, column.mainConfiguration.columnName);
        if(column.avConfiguration.acceptedValuesShort.length!=0) {
            for(short i : column.avConfiguration.acceptedValuesShort)
                if(i == shortValue)
                    return;
        } else {
            return;
        }
        throw new KittyRestrictedValueException(R_SHORT_V, modelClass, column.mainConfiguration.columnName);
    }

    /**
     * Checks if provided floatValue suites constraint described in {@link KITTY_COLUMN_ACCEPTED_VALUES#acceptedValuesFloat()} and throws
     * {@link KittyRuntimeException} if this value is restricted one.
     * @param floatValue float value to check
     * @param column column configuration
     * @param modelClass modelClass class for generating exception info
     */
    static final void checkFloatValue(Float floatValue, KittyColumnConfiguration column, Class modelClass) {
        if(column.avConfiguration == null) return;
        if(floatValue == null)
            throw new KittyRestrictedValueException(R_FLOAT_V, modelClass, column.mainConfiguration.columnName);
        if(column.avConfiguration.acceptedValuesFloat.length!=0) {
            for(float i : column.avConfiguration.acceptedValuesFloat)
                if(i == floatValue)
                    return;
        } else {
            return;
        }
        throw new KittyRestrictedValueException(R_FLOAT_V, modelClass, column.mainConfiguration.columnName);
    }
}
