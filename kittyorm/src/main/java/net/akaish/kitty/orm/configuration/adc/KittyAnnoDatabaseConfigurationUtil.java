
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

import android.content.Context;
import android.util.Log;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyDatabase;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE_REGISTRY;
import net.akaish.kitty.orm.annotations.KITTY_EXTENDED_CRUD;
import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfigurationBuilder;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.util.KittyDexClassFilter;
import net.akaish.kitty.orm.util.KittyDexClassNameFilter;
import net.akaish.kitty.orm.util.KittyDexUtils;
import net.akaish.kitty.orm.util.KittyUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittyNamingUtils.generateSchemaNameFromDatabaseClassName;

/**
 * Configuration utility class for generating databaseClass configuration
 * Created by akaish on 13.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyAnnoDatabaseConfigurationUtil {

    private static final String[] DEX_CLASS_NAME_FILTER_STARTS_WITH = {"android.", "net.akaish.kitty.orm"};

    private static final String LOG_W_NOT_ASSIGNABLE = "[KittyAnnoDatabaseConfigurationUtil] Unable to add {0} as extended CRUD controller to model {1}. {2} can't be assigned from {3}";
    private static final int LOG_TAG_MAX_LENGTH = 23;
    private static final String IA_EXCEPTION_LOG_TAG_TOO_LONG = "[KittyAnnoDatabaseConfigurationUtil] Defined log tag ({0}) is too long, max length is 23, but current log tag has length {1}!";
    private static final String IA_EXCEPTION_TABLE_CONFIGURATION_ERROR = "[KittyAnnoDatabaseConfigurationUtil] Error on generating table configuration for model {0}, see exception details for more info.";
    private static final String IA_EXCEPTION_PROVIDED_DBCLASS_NOT_ANNOTATED = "[KittyAnnoDatabaseConfigurationUtil] Provided databaseClass class {0} not annotated with KITTY_DATABASE annotation!";

    private static final String LI_STARTED = "[KittyAnnoDatabaseConfigurationUtil] Starting generation of KittyORM database configuration for database class {0}";
    private static final String LI_SCHEMA_NAME_FOUND = "[KittyAnnoDatabaseConfigurationUtil] Schema name \"{0}\" found for database class {1}";
    private static final String LI_SCHEMA_NOT_FOUND_GENERATED = "[KittyAnnoDatabaseConfigurationUtil] No schema name defined for database class {0}, KittyORM generated schema name for this database is \"{1}\"";

    private static final String LI_REGISTRY_ACQUIRED = "[KittyAnnoDatabaseConfigurationUtil] Static registry for {0}: {1} v. {2} received.";
    private static final String LI_REGISTRY_NOT_DEFINED = "[KittyAnnoDatabaseConfigurationUtil] Static registry not defined for {0}: {1} v. {2}; Generating registry from app classes with domain package names {3}...";
    private static final String LI_REGISTRY_NOT_DEFINED_NO_DOMAIN = "[KittyAnnoDatabaseConfigurationUtil] Static registry not defined for {0}: {1} v. {2}; Generating registry from app classes...";

    private static final String LI_GENERATING_COLUMNS_STARTED = "[KittyAnnoDatabaseConfigurationUtil] Generation of table configurations started [{0}: {1} v. {2}]";
    private static final String LI_GENERATING_COLUMNS_FINISHED = "[KittyAnnoDatabaseConfigurationUtil] Table configuration generation finished [{0}: {1} v. {2}]";

    private static final String LI_GENERATING_DB_CONF_FINISHED = "[KittyAnnoDatabaseConfigurationUtil] Database configuration successfully created [{0}: {1} v. {2}] : {3}";

    private static final String LI_REGISTRY_PRESENT_IN_ANNOTATION = "[KittyAnnoDatabaseConfigurationUtil] Static registry for {0}: {1} v. {2} present as @interface.";
    private static final String LI_REGISTRY_ANNOTATION_PAIRS = "[KittyAnnoDatabaseConfigurationUtil] Static registry for {0}: {1} v. {2} present as @interface defined as registry pairs.";
    private static final String LI_REGISTRY_ANNOTATION_MODELS = "[KittyAnnoDatabaseConfigurationUtil] Static registry for {0}: {1} v. {2} present as @interface defined as array of domain models.";

    private static final String IA_REGISTRY_PAIRS_BAD_ARRAY = "[KittyAnnoDatabaseConfigurationUtil] Static registry for {0}: {1} v. {2} present as @interface defined as registry pairs unable to process, reason: {3}";

    public static <T extends KittyDatabase, M extends KittyModel> KittyDatabaseConfiguration generateDatabaseConfiguration(
            Class<T> database, Context ctx, Map<Class<M>, Class<KittyMapper>> registry) {
        if(database.isAnnotationPresent(KITTY_DATABASE.class)) {
            KITTY_DATABASE databaseAnno = database.getAnnotation(KITTY_DATABASE.class);
            if(databaseAnno.logTag().length()>LOG_TAG_MAX_LENGTH) {
                throw new IllegalArgumentException(format(IA_EXCEPTION_LOG_TAG_TOO_LONG, databaseAnno.logTag(), Integer.toString(databaseAnno.logTag().length())));
            }
            if(databaseAnno.isLoggingOn())
                Log.i(databaseAnno.logTag(), format(LI_STARTED, database.getCanonicalName()));
            String databaseName = null;
            if(databaseAnno.databaseName().length() == 0) {
                databaseName = generateSchemaNameFromDatabaseClassName(database);
                if(databaseAnno.isLoggingOn())
                    Log.i(databaseAnno.logTag(), format(LI_SCHEMA_NOT_FOUND_GENERATED, database.getCanonicalName(), databaseName));
            } else {
                if(databaseAnno.isLoggingOn())
                    Log.i(databaseAnno.logTag(), format(LI_SCHEMA_NAME_FOUND, databaseAnno.databaseName(), database.getCanonicalName()));
                databaseName = databaseAnno.databaseName();
            }
            // Setting databaseClass registry
            Map<Class<M>,
                    Class<KittyMapper>> generatedRegistry = registry;
            if(registry==null && database.isAnnotationPresent(KITTY_DATABASE_REGISTRY.class)) {
                if(databaseAnno.isLoggingOn())
                    Log.i(databaseAnno.logTag(), format(LI_REGISTRY_PRESENT_IN_ANNOTATION,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion()));
                KITTY_DATABASE_REGISTRY registryAnnotation = database.getAnnotation(KITTY_DATABASE_REGISTRY.class);
                if(registryAnnotation.domainPairs().length > 0) {
                    if(databaseAnno.isLoggingOn())
                        Log.i(databaseAnno.logTag(), format(LI_REGISTRY_ANNOTATION_PAIRS,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion()));
                    try {
                        generatedRegistry = generateRegistryFromRegistryPairsAnnotationArray(registryAnnotation);
                    } catch (IllegalArgumentException iae) {
                        if(databaseAnno.isLoggingOn())
                            Log.i(databaseAnno.logTag(), format(IA_REGISTRY_PAIRS_BAD_ARRAY,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion(), iae.getMessage()));
                        throw iae;
                    }
                } else {
                    if(databaseAnno.isLoggingOn())
                        Log.i(databaseAnno.logTag(), format(LI_REGISTRY_ANNOTATION_MODELS,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion()));
                    generatedRegistry = getRecordClassesList(
                            ctx,
                            databaseAnno.domainPackageNames(),
                            registryAnnotation.domainModels(),
                            databaseAnno.isKittyDexUtilLoggingEnabled(),
                            databaseAnno.logTag()
                    );
                }
            } else if(registry==null && databaseAnno.isGenerateRegistryFromPackage()) {
                if(databaseAnno.isLoggingOn())
                    Log.i(databaseAnno.logTag(), format(LI_REGISTRY_NOT_DEFINED,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion(),
                            KittyUtils.implodeWithCommaInBKT(databaseAnno.domainPackageNames())));
                generatedRegistry = getRecordClassesList(
                        ctx,
                        databaseAnno.domainPackageNames(),
                        null,
                        databaseAnno.isKittyDexUtilLoggingEnabled(),
                        databaseAnno.logTag());
            } else if(registry == null) {
                if(databaseAnno.isLoggingOn())
                    Log.i(databaseAnno.logTag(), format(LI_REGISTRY_NOT_DEFINED_NO_DOMAIN,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion()));
                generatedRegistry = getRecordClassesList(
                        ctx,
                        null,
                        null,
                        databaseAnno.isKittyDexUtilLoggingEnabled(),
                        databaseAnno.logTag()
                );
            } else {
                if(databaseAnno.isLoggingOn())
                    Log.i(databaseAnno.logTag(), format(LI_REGISTRY_ACQUIRED,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion()));
            }
            if(databaseAnno.isLoggingOn())
                printRegistry(generatedRegistry, databaseAnno.logTag(), database, databaseName, databaseAnno.databaseVersion());
            // setting configurations
            if(databaseAnno.isLoggingOn())
                Log.i(databaseAnno.logTag(), format(LI_GENERATING_COLUMNS_STARTED,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion()));
            LinkedList<KittyTableConfiguration> tableConfigurations = new LinkedList<>();
            for(Class<? extends KittyModel> modelClass : generatedRegistry.keySet()) {
                try {
                    tableConfigurations.add(KittyAnnoTableConfigurationUtil.generateTableConfiguration(modelClass, databaseName));
                } catch (NoSuchMethodException e) {
                    String errorMessage = format(IA_EXCEPTION_TABLE_CONFIGURATION_ERROR, modelClass.getCanonicalName());
                    if(databaseAnno.isLoggingOn())
                        Log.e(databaseAnno.logTag(), errorMessage, e);
                    throw new KittyRuntimeException(errorMessage, e);
                }
            }
            if(databaseAnno.isLoggingOn())
                Log.i(databaseAnno.logTag(), format(LI_GENERATING_COLUMNS_FINISHED,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion()));
            KittyDatabaseConfigurationBuilder builder = new KittyDatabaseConfigurationBuilder();
            final KittyDatabaseConfiguration configuration = builder.setDatabaseName(databaseName)
                        .setDatabaseVersion(databaseAnno.databaseVersion())
                        .setIsGenerateRegistryFromPackage(databaseAnno.isGenerateRegistryFromPackage())
                        .setIsLoggingOn(databaseAnno.isLoggingOn())
                        .setIsPragmaON(databaseAnno.isPragmaOn())
                        .setLogTag(databaseAnno.logTag())
                        .setIsProductionOn(databaseAnno.isProductionOn())
                        .setMmPackageNames(databaseAnno.domainPackageNames())
                        .setRecordsConfigurations(tableConfigurations)
                        .setRegistry(generatedRegistry)
                        .setIsKittyDexUtilLoggingEnabled(databaseAnno.isKittyDexUtilLoggingEnabled())
                        .createKittyDatabaseConfiguration();
            if(databaseAnno.isLoggingOn())
                Log.i(databaseAnno.logTag(), format(LI_GENERATING_DB_CONF_FINISHED,  database.getCanonicalName(), databaseName, databaseAnno.databaseVersion(), configuration));
            return configuration;
        } else {
            throw new KittyRuntimeException(format(IA_EXCEPTION_PROVIDED_DBCLASS_NOT_ANNOTATED, database.getCanonicalName()));
        }
    }

    private static String LW_UNABLE_TO_PRINT_REGISTRY_NULL = "[KittyAnnoDatabaseConfigurationUtil] Unable to print registry for {0}: {1} v. {2}; reason: registry is NULL";
    private static String LW_UNABLE_TO_PRINT_REGISTRY_EMPTY = "[KittyAnnoDatabaseConfigurationUtil] Unable to print registry for {0}: {1} v. {2}; reason: registry is EMPTY";
    private static String LI_PRINTING_REGISTRY_START = "[KittyAnnoDatabaseConfigurationUtil] Printing registry for {0}: {1} v. {2} (START):";
    private static String LI_PRINTING_REGISTRY_END = "[KittyAnnoDatabaseConfigurationUtil] Printing registry for {0}: {1} v. {2} (END)";
    private static String LI_REGISTRY_ITEM = "[KittyAnnoDatabaseConfigurationUtil] REGISTRY ITEM ({0}: {1} v. {2}): model: {3}, mapper: {4}";

    private static <M extends KittyModel> void printRegistry(Map<Class<M>, Class<KittyMapper>> registry,
                                                             String logTag, Class databaseClass,
                                                             String schemaName,
                                                             int schemaVersion) {
        if(registry == null) {
            Log.w(logTag, format(LW_UNABLE_TO_PRINT_REGISTRY_NULL, databaseClass.getCanonicalName(), schemaName, schemaVersion));
            return;
        }
        if(registry.size() == 0) {
            Log.w(logTag, format(LW_UNABLE_TO_PRINT_REGISTRY_EMPTY, databaseClass.getCanonicalName(), schemaName, schemaVersion));
            return;
        }
        Log.i(logTag, format(LI_PRINTING_REGISTRY_START, databaseClass.getCanonicalName(), schemaName, schemaVersion));
        Iterator<Map.Entry<Class<M>, Class<KittyMapper>>> registryItemIterator = registry.entrySet().iterator();
        while (registryItemIterator.hasNext()) {
            Map.Entry<Class<M>, Class<KittyMapper>> registryItem = registryItemIterator.next();
            Log.i(logTag, format(
                    LI_REGISTRY_ITEM, databaseClass.getCanonicalName(), schemaName, schemaVersion,
                    registryItem.getKey().getCanonicalName(), registryItem.getValue().getCanonicalName()
            ));
        }
        Log.i(logTag, format(LI_PRINTING_REGISTRY_END, databaseClass.getCanonicalName(), schemaName, schemaVersion));
    }

    public static final String IA_REGISTRY_BAD_MODEL = "This is not valid KittyModel class: {0}";
    public static final String IA_REGISTRY_BAD_MAPPER = "This is not valid KittyMapper class: {0}";
    public static final String IA_REGISTRY_ARRAY_ZEROLENGTH = "Provided KITTY_DATABASE_REGISTRY annotation has no elements in domainPairs() array!";

    /**
     * Returns map of registry classes generated from {@link KITTY_DATABASE_REGISTRY#domainPairs()}
     * array. If any of model or mapper classes not regular implementations of KittyMapper or KittyModel
     * than IllegalArgumentException would bw thrown.
     * <br> Also IllegalArgumentException would be thrown if {@link KITTY_DATABASE_REGISTRY#domainPairs()}
     * length is zerolength
     * @param registryAnnotation
     * @param <T>
     * @param <E>
     * @return
     */
    private static <T extends KittyModel, E extends KittyMapper> Map<Class<T>, Class<E>>
                generateRegistryFromRegistryPairsAnnotationArray(KITTY_DATABASE_REGISTRY registryAnnotation) {
        if(registryAnnotation.domainPairs().length > 0) {
            Map<Class<T>, Class<E>> out = new HashMap<>();
            for(int i = 0; i < registryAnnotation.domainPairs().length; i++) {
                if(!isValidModel(registryAnnotation.domainPairs()[i].model())) {
                    throw new IllegalArgumentException(
                            format(
                                    IA_REGISTRY_BAD_MODEL,
                                    registryAnnotation.domainPairs()[i].model().getCanonicalName()
                            )
                    );
                }
                if(!isValidMapper(registryAnnotation.domainPairs()[i].mapper())) {
                    throw new IllegalArgumentException(
                            format(
                                    IA_REGISTRY_BAD_MAPPER,
                                    registryAnnotation.domainPairs()[i].mapper().getCanonicalName()
                            )
                    );
                }
                out.put(
                        registryAnnotation.domainPairs()[i].model(),
                        registryAnnotation.domainPairs()[i].mapper()
                );
            }
            return out;
        }
        throw new IllegalArgumentException(IA_REGISTRY_ARRAY_ZEROLENGTH);
    }

    /**
     * Returns true if parameter class is regular class,
     * otherwise - false
     * @param cls
     * @return
     */
    private static boolean isRegularClass(Class cls) {
        if (cls.isEnum()) return false;
        if (cls.isAnnotation()) return false;
        if (Modifier.isAbstract(cls.getModifiers())) return false;
        if (cls.isInterface()) return false;
        if (cls.isArray()) return false;
        if (cls.isLocalClass()) return false;
        if (cls.isMemberClass()) return false;
        if (cls.isAnonymousClass()) return false;
        if (cls.isPrimitive()) return false;
        return true;
    }

    /**
     * Returns true if provided model class is regular {@link KittyModel} class
     * implementation.
     * @param modelClass
     * @return
     */
    private static boolean isValidModel(Class modelClass) {
        return isRegularClass(modelClass) && KittyModel.class.isAssignableFrom(modelClass);
    }

    /**
     * Returns true if provided mapper class is regular {@link KittyMapper} class
     * implementation
     * @param mapperClass
     * @return
     */
    private static boolean isValidMapper(Class mapperClass) {
        return isRegularClass(mapperClass) && KittyMapper.class.isAssignableFrom(mapperClass);
    }

    /**
     * Generates Map of model\crud controller entries from classes in this application namespace
     * @param ctx
     * @param packages
     * @param logOn
     * @param logTag
     * @param <T>
     * @param <E>
     * @return
     */
    private static <T extends KittyModel, E extends KittyMapper> Map<Class<T>, Class<E>>
                            getRecordClassesList(Context ctx, String[] packages, Class[] modelClasses, boolean logOn, String logTag) {
        // setting dex naming filter
        KittyDexClassNameFilter filter = new KittyDexClassNameFilter(null, DEX_CLASS_NAME_FILTER_STARTS_WITH);
        KittyDexUtils utils = KittyDexUtils.getInstance(ctx, filter, false, logOn, logTag, KittyDexUtils.LOG_LEVEL_INFO);
        // setting filter to get only regular implementations of records in specified packages
        KittyDexClassFilter classFilter = new KittyDexClassFilter();
        classFilter.setOnlyAssignableFromSuperTypes(KittyModel.class)
                .setPackageNamesFilter(packages)
                .setSkipAbstract(true)
                .setSkipAnnotations(true)
                .setSkipAnonymousClass(true)
                .setSkipArrays(true)
                .setSkipEnums(true)
                .setSkipInterfaces(true)
                .setSkipLocalClass(true)
                .setSkipMemberClass(true)
                .setSkipPrimitives(true);
        List<Class> regularImplementationsOfModels = null;
        if(modelClasses == null) {
            regularImplementationsOfModels = utils.getFilteredAppClasses(classFilter);
        } else {
            regularImplementationsOfModels = new ArrayList<>();
            for(Class appClass : modelClasses) {
                regularImplementationsOfModels.add(appClass);
            }
            regularImplementationsOfModels = utils.filterProvidedClasses(classFilter, regularImplementationsOfModels);
        }

        regularImplementationsOfModels = Collections.synchronizedList(regularImplementationsOfModels);
        Map<Class<T>, Class<E>> out = new HashMap<>();
        synchronized (regularImplementationsOfModels) {
            Iterator<Class> classIterator = regularImplementationsOfModels.iterator();
            while (classIterator.hasNext()) {
                Class cls = classIterator.next();
                if(cls.isAnnotationPresent(KITTY_EXTENDED_CRUD.class)) {
                    KITTY_EXTENDED_CRUD anno = (KITTY_EXTENDED_CRUD)cls.getAnnotation(KITTY_EXTENDED_CRUD.class);
                    Class<E> crudClass = (Class<E>) anno.extendedCrudController();
                    if(KittyMapper.class.isAssignableFrom(crudClass)) {
                        out.put((Class<T>) cls, crudClass);
                        continue;
                    }
                    if(logOn) Log.w(logTag, format(LOG_W_NOT_ASSIGNABLE,
                            crudClass.getName(), cls.getName(), crudClass.getName(),
                            KittyMapper.class.getCanonicalName()));
                } else {
                    out.put((Class<T>) cls, (Class<E>) KittyMapper.class);
                }
            }
        }
        return out;
    }

}
