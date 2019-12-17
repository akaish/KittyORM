
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

import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

/**
 * Default KittyModel to KittyMapper entry factory
 * Created by akaish on 04.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyMMEntryFactory {

    private String databasePassword = null;
    private KittyDatabaseConfiguration databaseConfiguration;

    private static String AME_MODEL_INIT = "Unable to instantiate kitty modelClass class {0}, see exception details!";
    private static String AME_MAPPER_NO_TABLE_CFG = "Unable to find {0}-{1} table configuration for {2} databaseClass!";
    private static String AME_MAPPER_INIT = "Unable to instantiate kitty data mapper class {0}, see exception details!";
    private static String IA_NOT_SET = "At least databaseConfiguration should be set in KittyMMEntryFactory for calling newMMEntry(mapper, tableCfg)!";


    /**
     * Sets databaseClass configuration that should be used with this factory
     * @param databaseConfiguration kitty databaseClass configuration
     * @return this instance
     */
    public KittyMMEntryFactory setDatabaseConfiguration(KittyDatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
        return this;
    }

    /**
     * Sets databaseClass password for this helper if db encryption implemented, optional setter
     * @param password databaseClass encryption password
     * @return this instance
     */
    public KittyMMEntryFactory setDatabasePassword(String password) {
        this.databasePassword = password;
        return this;
    }

    /**
     * Creates MM entry for specified mapper class and with usage of current table configuration
     * @param mapperClass data mapper class
     * @param tableCfg table configuration
     * @param <D>
     * @param <M>
     * @return MM entry
     * @throws KittyRuntimeException if any errors happened (original exception is wrapped inside KittyRuntimeException)
     */
    public <D extends KittyMapper<M>, M extends KittyModel> KittyMMEntry newMMEntry(Class<D> mapperClass, KittyTableConfiguration tableCfg) {
        if(databaseConfiguration == null)
            throw new IllegalArgumentException(IA_NOT_SET);
        M kittyModel = newKittyModel( (Class<M>) tableCfg.modelClass);
        D kittyMapper = newKittyMapper(mapperClass, tableCfg, databaseConfiguration, kittyModel, databasePassword);
        return new KittyMMEntry(kittyModel, kittyMapper);
    }

    /**
     * Creates new KittyModel with provided modelClass class
     * @param kittyModelClass kitty modelClass's class
     * @param <M>
     * @return KittyModel
     * @throws KittyRuntimeException (InstantiationException and IllegalAccessException will be wrapped into KittyRuntimeException)
     */
    static final <M extends KittyModel> M newKittyModel(Class<M> kittyModelClass) {
        try {
            return kittyModelClass.newInstance();
        } catch (InstantiationException e) {
           throw new KittyRuntimeException(MessageFormat.format(AME_MODEL_INIT, kittyModelClass.getCanonicalName()), e);
        } catch (IllegalAccessException e) {
            throw new KittyRuntimeException(MessageFormat.format(AME_MODEL_INIT, kittyModelClass.getCanonicalName()), e);
        }
    }

    /**
     * Creates new KittyMapper from mapper class and other needed stuff
     * @param kittyDataMapperClass data mapper class to instantiate
     * @param tableConfiguration table configuration to use in mapper instantiation
     * @param dbConfiguration databaseClass configuration
     * @param blankModel modelClass instance
     * @param dbPassword databaseClass password (can be null, optional parameter)
     * @param <D>
     * @param <M>
     * @return KittyMapper instance ready to be used with provided model
     * @throws KittyRuntimeException (InstantiationException, NoSuchMethodException, InvocationTargetException and IllegalAccessException will be wrapped into KittyRuntimeException)
     */
    static final <D extends KittyMapper<M>, M extends KittyModel> D newKittyMapper(Class<D> kittyDataMapperClass,
                                                                                KittyTableConfiguration tableConfiguration,
                                                                                KittyDatabaseConfiguration dbConfiguration,
                                                                                M blankModel,
                                                                                String dbPassword) {
        if(!dbConfiguration.tableConfigurations.contains(tableConfiguration)) {
            throw new KittyRuntimeException(
                    MessageFormat.format(
                            AME_MAPPER_NO_TABLE_CFG,
                            tableConfiguration.tableName,
                            tableConfiguration.modelClass.getCanonicalName(),
                            dbConfiguration.schemaName
                    )
            );
        }
        try {
            Constructor<D> mapperConstructor = (Constructor<D>) kittyDataMapperClass.getConstructors()[0];
            D kittyMapper = mapperConstructor.newInstance(tableConfiguration, blankModel, dbPassword);
            kittyMapper.setLogOn(dbConfiguration.isLoggingOn);
            kittyMapper.setLogTag(dbConfiguration.logTag);
            kittyMapper.setRowIDSupport(!tableConfiguration.isNoRowid);
            // TODO
            // TODO
            // TODO better solution than getconstructors [0]
            return kittyMapper;
        } catch (IndexOutOfBoundsException e) {
            throw new KittyRuntimeException(MessageFormat.format(AME_MAPPER_INIT, kittyDataMapperClass.getCanonicalName()), e);
        } catch (InstantiationException e) {
            throw new KittyRuntimeException(MessageFormat.format(AME_MAPPER_INIT, kittyDataMapperClass.getCanonicalName()), e);
        } catch (IllegalAccessException e) {
            throw new KittyRuntimeException(MessageFormat.format(AME_MAPPER_INIT, kittyDataMapperClass.getCanonicalName()), e);
        } catch (InvocationTargetException e) {
            throw new KittyRuntimeException(MessageFormat.format(AME_MAPPER_INIT, kittyDataMapperClass.getCanonicalName()), e);
        }
    }
}
