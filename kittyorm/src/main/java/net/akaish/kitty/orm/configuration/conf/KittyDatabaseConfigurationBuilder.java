
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

import java.util.List;
import java.util.Map;

/**
 * Builder for {@link KittyDatabaseConfiguration}
 * @author akaish (Denis Bogomolov)
 */
public class KittyDatabaseConfigurationBuilder<M extends KittyModel, D extends KittyMapper<M>> {

    private List<KittyTableConfiguration> recordsConfigurations;
    private String databaseName;
    private int databaseVersion;
    private boolean isLoggingOn;
    private String logTag;
    private boolean isPragmaON;
    private boolean isProductionOn;
    private boolean isGenerateRegistryFromPackage;
    private String[] mmPackageNames;
    private Map<Class<M>, Class<D>> registry;
    private boolean isKittyDexUtilLoggingEnabled;

    private String externalDatabasePath;
    private boolean useExternalDatabase;
    private int[] externalSupportedDatabaseVersions;

    public KittyDatabaseConfigurationBuilder setRecordsConfigurations(List<KittyTableConfiguration> recordsConfigurations) {
        this.recordsConfigurations = recordsConfigurations;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setIsLoggingOn(boolean isLoggingOn) {
        this.isLoggingOn = isLoggingOn;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setLogTag(String logTag) {
        this.logTag = logTag;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setIsPragmaON(boolean isPragmaON) {
        this.isPragmaON = isPragmaON;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setIsProductionOn(boolean isProductionOn) {
        this.isProductionOn = isProductionOn;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setIsGenerateRegistryFromPackage(boolean isGenerateRegistryFromPackage) {
        this.isGenerateRegistryFromPackage = isGenerateRegistryFromPackage;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setMmPackageNames(String[] mmPackageNames) {
        this.mmPackageNames = mmPackageNames;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setRegistry(Map<Class<M>, Class<D>> registry) {
        this.registry = registry;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setIsKittyDexUtilLoggingEnabled(boolean isKittyDexUtilLoggingEnabled) {
        this.isKittyDexUtilLoggingEnabled = isKittyDexUtilLoggingEnabled;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setExternalDatabaseFilePath(String externalDatabaseFilePath) {
        this.externalDatabasePath = externalDatabaseFilePath;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setIsUseExternalDatabase(boolean isUseExternalDatabase) {
        this.useExternalDatabase = isUseExternalDatabase;
        return this;
    }

    public KittyDatabaseConfigurationBuilder setExternalDatabaseSupportedVersions(int[] externalDatabaseSupportedVersions) {
        this.externalSupportedDatabaseVersions = externalDatabaseSupportedVersions;
        return this;
    }

    public KittyDatabaseConfiguration createKittyDatabaseConfiguration() {
        return new KittyDatabaseConfiguration(
                recordsConfigurations, databaseName, databaseVersion, isLoggingOn, logTag,
                isPragmaON, isProductionOn, isGenerateRegistryFromPackage, mmPackageNames,
                registry, isKittyDexUtilLoggingEnabled,
                externalDatabasePath, useExternalDatabase, externalSupportedDatabaseVersions
        );
    }
}