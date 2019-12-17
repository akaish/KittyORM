
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

package net.akaish.kitty.orm.configuration.conf;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.util.KittyUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Configuration DAO for KittyDatabase implementation
 * Created by akaish on 13.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyDatabaseConfiguration<M extends KittyModel, D extends KittyMapper<M>> {
    /**
     * Registry of model-mapper classes for this databaseClass
     */
    public final Map<Class<M>, Class<D>> registry;

    /**
     * List of configurations for all models defined to be used with databaseClass
     */
    public final List<KittyTableConfiguration> tableConfigurations;

    /**
     * Database name
     */
    public final String schemaName;

    /**
     * Current databaseClass version
     */
    public final int schemaVersion;

    /**
     * Logging flag
     */
    public final boolean isLoggingOn;

    /**
     * Logging tag
     */
    public final String logTag;

    /**
     * Pragma on (foreign keys support) flag
     */
    public final boolean isPragmaON;

    public final boolean isKittyDexUtilLoggingEnabled;

    /**
     * Production on (usage of predefined SQL dumps for creating\\upgrading\\dropping tables in databaseClass) flag
     */
    public final boolean isProductionOn;

    /**
     * Flag, when true and there is no predefined registry in databaseClass class, registry would be automatically created from
     * application's model classes located in defined packages
     *
     * <br> If false and no registry exists than registry would be automatically created from all application's model classes
     * <br> If registry is defined manually in databaseClass class than this flag would be ignored
     */
    public final boolean isGenerateRegistryFromPackage;

    /**
     * Defines external filepath that should be used as database file
     */
    public final String databaseFilePath;

    /**
     * Flag that defines usage of external database. If you use external database, KittyORM would
     * try to open database located at provided filepath and would skip all onUpgrade and onCreate
     * helper routine
     */
    public final boolean useExternalFilepath;


     /**
      * Supported by current schema external database file numbers,
      * if empty than no check on version mismatch would be run.
      */
    public final int[] externalDatabaseSupportedVersions;

    /**
     * Package names where model classes are located
     */
    public final String[] mmPackageNames;

    public KittyDatabaseConfiguration(List<KittyTableConfiguration> tableConfigurations, String schemaName,
                                      int schemaVersion, boolean isLoggingOn, String logTag,
                                      boolean isPragmaON, boolean isProductionOn,
                                      boolean isGenerateRegistryFromPackage,
                                      String[] mmPackageNames, Map<Class<M>, Class<D>> registry,
                                      boolean isKittyDexUtilLoggingEnabled,
                                      String databaseFilePath, boolean useExternalFilepath,
                                      int[] externalDatabaseSupportedVersions) {
        this.tableConfigurations = tableConfigurations;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.isLoggingOn = isLoggingOn;
        this.logTag = logTag;
        this.isPragmaON = isPragmaON;
        this.isProductionOn = isProductionOn;
        this.isGenerateRegistryFromPackage = isGenerateRegistryFromPackage;
        this.mmPackageNames = mmPackageNames;
        this.registry = registry;
        this.isKittyDexUtilLoggingEnabled = isKittyDexUtilLoggingEnabled;
        this.databaseFilePath = databaseFilePath;
        this.useExternalFilepath = useExternalFilepath;
        this.externalDatabaseSupportedVersions = externalDatabaseSupportedVersions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("[ schemaName = ").append(schemaName)
                .append(" ; schemaVersion = ").append(schemaVersion)
                .append(" ; isLoggingOn = ").append(isLoggingOn)
                .append(" ; logTag = ").append(logTag)
                .append(" ; isPragmaOn = ").append(isPragmaON)
                .append(" ; isProductionOn = ").append(isProductionOn)
                .append(" ; isGenerateRegistryFromPackage = ").append(isGenerateRegistryFromPackage)
                .append(" ; mmPackageNames = ").append(KittyUtils.implodeWithCommaInBKT(mmPackageNames))
                .append(" ; isKittyDexUtilLoggingEnabled = ").append(isKittyDexUtilLoggingEnabled)
                .append(" ; databaseFilePath = ").append(databaseFilePath)
                .append(" ; useExternalFilePath = ").append(useExternalFilepath)
                .append(" ; externalDatabaseSupportedVersions : ").append(supportedExternalDatabaseVersionNumbersString())
                .append(" ; registry = ").append(registryString()).append(" ]");
        return sb.toString();
    }

    private String registryString() {
        Iterator<Map.Entry<Class<M>, Class<D>>> registryIterator = registry.entrySet().iterator();
        LinkedList<String> registryToPrint = new LinkedList<>();
        while (registryIterator.hasNext()) {
            Map.Entry<Class<M>, Class<D>> registryEntry = registryIterator.next();
            StringBuilder sb = new StringBuilder(64);
            sb.append(registryEntry.getKey().getCanonicalName()).append(" = ").append(registryEntry.getValue().getCanonicalName());
            registryToPrint.addLast(sb.toString());
        }
        return KittyUtils.implodeWithCommaInBKT(registryToPrint.toArray(new String[registryToPrint.size()]));
    }

    private String supportedExternalDatabaseVersionNumbersString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for(int i : externalDatabaseSupportedVersions) {
            sb.append(i).append(WHITESPACE);
        }
        sb.append("]");
        return sb.toString();
    }
}
