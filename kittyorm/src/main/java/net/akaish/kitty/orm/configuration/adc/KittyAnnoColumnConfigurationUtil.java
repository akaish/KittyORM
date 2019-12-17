
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

package net.akaish.kitty.orm.configuration.adc;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN_ACCEPTED_VALUES;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN_SERIALIZATION;
import net.akaish.kitty.orm.annotations.column.ONE_COLUMN_INDEX;
import net.akaish.kitty.orm.annotations.column.constraints.CHECK;
import net.akaish.kitty.orm.annotations.column.constraints.COLLATE;
import net.akaish.kitty.orm.annotations.column.constraints.DEFAULT;
import net.akaish.kitty.orm.annotations.column.constraints.FOREIGN_KEY;
import net.akaish.kitty.orm.annotations.column.constraints.NOT_NULL;
import net.akaish.kitty.orm.annotations.column.constraints.PRIMARY_KEY;
import net.akaish.kitty.orm.annotations.column.constraints.UNIQUE;
import net.akaish.kitty.orm.configuration.conf.KittyColumnAcceptedValuesConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyColumnMainConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyColumnSDConfiguration;
import net.akaish.kitty.orm.constraints.column.CheckColumnConstraint;
import net.akaish.kitty.orm.constraints.column.CollationColumnConstraint;
import net.akaish.kitty.orm.constraints.column.DefaultColumnConstraint;
import net.akaish.kitty.orm.constraints.column.ForeignKeyColumnConstraint;
import net.akaish.kitty.orm.constraints.column.NotNullColumnConstraint;
import net.akaish.kitty.orm.constraints.column.PrimaryKeyColumnConstraint;
import net.akaish.kitty.orm.constraints.column.UniqueColumnConstraint;
import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.enums.ConflictClauses;
import net.akaish.kitty.orm.enums.TypeAffinities;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.indexes.Index;
import net.akaish.kitty.orm.util.KittyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.MessageFormat;

import static net.akaish.kitty.orm.util.KittyNamingUtils.generateColumnNameFromModelField;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDefaultDeserializationMethodName;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDefaultSerializationMethodName;

/**
 * Set of static methods for field (column) configuration
 * <br> Created by akaish on 08.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyAnnoColumnConfigurationUtil {

    /**
     * Returns KittyColumnConfiguration for provided field of modelClass
     * Returns null, if field not annotated with at least {@link KITTY_COLUMN}
     * @param field
     * @param record
     * @param <M>
     * @return
     * @throws NoSuchMethodException
     */
    public static <M extends KittyModel> KittyColumnConfiguration generateAMColumnConfiguration(Field field, Class<M> record, String schemaName, String tableName)
            throws NoSuchMethodException {
        // Fetching main configuration, if no KITTY_COLUMN annotation present - return null
        if(field.isAnnotationPresent(KITTY_COLUMN.class)) {
            KittyColumnMainConfiguration main = generateAMColumnMainConfiguration(field);
            KittyColumnSDConfiguration sd = null;
            if(field.isAnnotationPresent(KITTY_COLUMN_SERIALIZATION.class)) {
                sd = generateAMColumnSDConfiguration(field, record, main);
            }
            KittyColumnAcceptedValuesConfiguration av = null;
            if(field.isAnnotationPresent(KITTY_COLUMN_ACCEPTED_VALUES.class)) {
                av = generateAMColumnAcceptedValuesConfiguration(field);
            }
            Index columnIndex = null;
            if(field.isAnnotationPresent(ONE_COLUMN_INDEX.class)) {
                columnIndex =  new Index(field.getAnnotation(ONE_COLUMN_INDEX.class), main.columnName, schemaName, tableName);
            }
            return new KittyColumnConfiguration(main, av, sd, columnIndex);
        } else {
            return null;
        }
    }

    /**
     * Generates accepted values configuration from input field with {@link KITTY_COLUMN_ACCEPTED_VALUES} annotation
     * Throws NullPointerException when field not annotated with KITTY_COLUMN_ACCEPTED_VALUES annotation
     * Also, returns null if annotation has no accepted values or those values have another primitive type
     * than field
     * @param f
     * @return
     */
    private static KittyColumnAcceptedValuesConfiguration generateAMColumnAcceptedValuesConfiguration(Field f) {
        KITTY_COLUMN_ACCEPTED_VALUES avAnnotation = f.getAnnotation(KITTY_COLUMN_ACCEPTED_VALUES.class);
        boolean anySet = false;
        Type fType = f.getType();
        int[] avInt = null; byte[] avByte = null; long[] avLong = null; short[] avShort = null;
        float[] avFloat = null; double[] avDouble = null; String[] avString = null;
        while(!anySet) {
            if (fType.getClass().equals(int.class) || fType.getClass().equals(Integer.class))
                if (avAnnotation.acceptedValuesInt().length > 0) {
                    avInt = avAnnotation.acceptedValuesInt();
                    anySet = true;
                    break;
                }
            if(fType.getClass().equals(byte.class) || fType.getClass().equals(Byte.class))
                if (avAnnotation.acceptedValuesByte().length > 0) {
                    avByte = avAnnotation.acceptedValuesByte();
                    anySet = true;
                    break;
                }
            if(fType.getClass().equals(long.class) || fType.getClass().equals(Long.class))
                if(avAnnotation.acceptedValuesLong().length > 0) {
                    avLong = avAnnotation.acceptedValuesLong();
                    anySet = true;
                    break;
                }
            if(fType.getClass().equals(short.class) || fType.getClass().equals(Short.class))
                if(avAnnotation.acceptedValuesShort().length > 0) {
                    avShort = avAnnotation.acceptedValuesShort();
                    anySet = true;
                    break;
                }
            if(fType.getClass().equals(float.class) || fType.getClass().equals(Float.class))
                if(avAnnotation.acceptedValuesFloat().length > 0) {
                    avFloat = avAnnotation.acceptedValuesFloat();
                    anySet = true;
                    break;
                }
            if(fType.getClass().equals(double.class) || fType.getClass().equals(Double.class))
                if(avAnnotation.acceptedValuesDouble().length > 0) {
                    avDouble = avAnnotation.acceptedValuesDouble();
                    anySet = true;
                    break;
                }
            if(fType.getClass().equals(String.class)) {
                if(avAnnotation.acceptedValuesString().length > 0) {
                    avString = avAnnotation.acceptedValuesString();
                    anySet = true;
                    break;
                }
            }
            break;
        }
        if(anySet)
            return new KittyColumnAcceptedValuesConfiguration(avString, avInt, avLong, avShort, avByte, avFloat, avDouble);
        return null;
    }

    private static final String AM_EXC_PATTERN_BAD_AFFINITY_FOR_SD = "Unable to generate SD for field {0} of class {1} cause only BLOB, NONE and TEXT affinities are allowed ({2} affinity found!)";

    /**
     * Returns instance of SD configuration for input field, annotated with {@link KITTY_COLUMN_SERIALIZATION}.
     * {@link NullPointerException} would be thrown if field not annotated with KITTY_COLUMN_SERIALIZATION annotation
     * Also {@link KittyRuntimeException} would be thrown if SD cannot be applied
     * for the field due to fact that field's affinity not {@link TypeAffinities#BLOB},
     * {@link TypeAffinities#NONE} or {@link TypeAffinities#TEXT}
     * @param f
     * @param record
     * @param mainConfiguration
     * @param <M>
     * @return
     * @throws NoSuchMethodException
     */
    private static <M extends KittyModel> KittyColumnSDConfiguration generateAMColumnSDConfiguration(Field f, Class<M> record, KittyColumnMainConfiguration mainConfiguration)
            throws NoSuchMethodException {
        KITTY_COLUMN_SERIALIZATION columnSD = f.getAnnotation(KITTY_COLUMN_SERIALIZATION.class);
        //if(!columnSD.useSD()) return null;
        // checking that type affinity of field is ok
        Type rawDataType;
        switch (mainConfiguration.columnAffinity) {
            case BLOB:
            case NONE:
                rawDataType = byte[].class;
                break;
            case TEXT:
                rawDataType = String.class;
                break;
            default:
                throw new KittyRuntimeException(MessageFormat.format(AM_EXC_PATTERN_BAD_AFFINITY_FOR_SD,
                        f.getName(), record.getCanonicalName(), mainConfiguration.columnAffinity.toString()));
        }
        // now we fetching methods names for sd
        String serializationMethodName;
        String deserializationMethodName;
        if(columnSD.serializationMethodName().length() == 0) {
           serializationMethodName = getDefaultSerializationMethodName(f.getName());
        } else {
            serializationMethodName = columnSD.serializationMethodName();
        }
        if(columnSD.deserializationMethodName().length() == 0) {
            deserializationMethodName = getDefaultDeserializationMethodName(f.getName());
        } else {
            deserializationMethodName = columnSD.deserializationMethodName();
        }
        // now we checking method existence
        Method serializer = null; Method deserializer = null;
        serializer = record.getDeclaredMethod(serializationMethodName);
        deserializer = record.getDeclaredMethod(deserializationMethodName, ((Class<?>)rawDataType));
        return new KittyColumnSDConfiguration(
                serializer,
                deserializer
        );
    }

    /**
     * Returns instance of {@link KittyColumnMainConfiguration} built based on {@link KITTY_COLUMN}
     * annotated field f. NullPointer would be thrown if no field not annotated with KITTY_COLUMN
     * annotation.
     * @param f
     * @return
     */
    private static final KittyColumnMainConfiguration generateAMColumnMainConfiguration(Field f) {
        KITTY_COLUMN column = f.getAnnotation(KITTY_COLUMN.class);
        String columnName;
        if(column.columnName().length() == 0) {
            columnName = generateColumnNameFromModelField(f);
        } else {
            columnName = column.columnName();
        }
        Type columnFieldType = f.getType();
        TypeAffinities columnAffinity;
        if(column.columnAffinity() == TypeAffinities.NOT_SET_USE_DEFAULT_MAPPING) {
            columnAffinity = KittyUtils.typeToAffinity(columnFieldType);
        } else {
            columnAffinity = column.columnAffinity();
        }

        boolean isSimpleIndex = column.isIPK();

        // Column constraints
        CheckColumnConstraint checkConstraint = null;
        DefaultColumnConstraint defaultConstraint = null;
        NotNullColumnConstraint notNullColumnConstraint = null;
        UniqueColumnConstraint uniqueColumnConstraint = null;
        CollationColumnConstraint collationColumnConstraint = null;
        PrimaryKeyColumnConstraint primaryKeyColumnConstraint = null;
        ForeignKeyColumnConstraint foreignKeyColumnConstraint = null;

        if (f.isAnnotationPresent(NOT_NULL.class))
            notNullColumnConstraint = new NotNullColumnConstraint(f.getAnnotation(NOT_NULL.class));
        if(!isSimpleIndex) {
            if (f.isAnnotationPresent(CHECK.class))
                checkConstraint = new CheckColumnConstraint(f.getAnnotation(CHECK.class));
            if (f.isAnnotationPresent(DEFAULT.class))
                defaultConstraint = new DefaultColumnConstraint(f.getAnnotation(DEFAULT.class));
            if (f.isAnnotationPresent(PRIMARY_KEY.class))
                primaryKeyColumnConstraint = new PrimaryKeyColumnConstraint(f.getAnnotation(PRIMARY_KEY.class));
            if (f.isAnnotationPresent(UNIQUE.class))
                uniqueColumnConstraint = new UniqueColumnConstraint(f.getAnnotation(UNIQUE.class));
            if (f.isAnnotationPresent(COLLATE.class))
                collationColumnConstraint = new CollationColumnConstraint(f.getAnnotation(COLLATE.class));
            if(f.isAnnotationPresent(FOREIGN_KEY.class))
                foreignKeyColumnConstraint = new ForeignKeyColumnConstraint(f.getAnnotation(FOREIGN_KEY.class));
        } else {
            if(notNullColumnConstraint == null)
                notNullColumnConstraint = new NotNullColumnConstraint(ConflictClauses.CONFLICT_CLAUSE_NOT_SET);
            primaryKeyColumnConstraint = new PrimaryKeyColumnConstraint(AscDesc.ASCENDING, ConflictClauses.CONFLICT_CLAUSE_NOT_SET, false);
            columnAffinity = TypeAffinities.INTEGER;
        }
        // also we check that manually set
        if(primaryKeyColumnConstraint != null && notNullColumnConstraint != null && uniqueColumnConstraint == null
                && defaultConstraint == null && checkConstraint == null && collationColumnConstraint == null
                && foreignKeyColumnConstraint == null && columnAffinity.equals(TypeAffinities.INTEGER))
            isSimpleIndex = true;

        return new KittyColumnMainConfiguration(
                columnName,
                columnAffinity,
                f,
                defaultConstraint,
                isSimpleIndex,
                checkConstraint,
                column.columnOrder(),
                column.isValueGeneratedOnInsert(),
                notNullColumnConstraint,
                uniqueColumnConstraint,
                collationColumnConstraint,
                primaryKeyColumnConstraint,
                foreignKeyColumnConstraint,
                false // So we do not need this flag
        );
    }
}
