
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

package net.akaish.kitty.orm.configuration.conf;

import net.akaish.kitty.orm.annotations.KITTY_DATABASE_HELPER;

/**
 * Database helper configuration builder
 * @author akaish (Denis Bogomolov)
 */
public class KittyDBHelperConfigurationBuilder {

    private KITTY_DATABASE_HELPER.UpgradeBehavior onUpgradeBehavior;
    private String migrationsRoot;
    private String createSchemaScriptPath;
    private String dropSchemaScriptPath;
    private String devDumpCreateSchemaScriptPath;
    private String devDumpDropSchemaScriptPath;
    private String afterCreateScriptPath;
    private String afterMigrateScriptPath;
    private String logTag;
    private boolean logOn;
    private boolean pragmaOn;
    private String databaseName;
    private int databaseVersion;
    private boolean productionOn;
    private boolean scriptsLocatedInAssets;

    public KittyDBHelperConfigurationBuilder setOnUpgradeBehavior(KITTY_DATABASE_HELPER.UpgradeBehavior onUpgradeBehavior) {
        this.onUpgradeBehavior = onUpgradeBehavior;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setScriptsLocatedInAssets(boolean scriptsLocatedInAssets) {
        this.scriptsLocatedInAssets = scriptsLocatedInAssets;
        return this;
    }


    public KittyDBHelperConfigurationBuilder setMigrationsRoot(String migrationsRoot) {
        this.migrationsRoot = migrationsRoot;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setCreateSchemaScriptPath(String createSchemaScriptPath) {
        this.createSchemaScriptPath = createSchemaScriptPath;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setDropSchemaScriptPath(String dropSchemaScriptPath) {
        this.dropSchemaScriptPath = dropSchemaScriptPath;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setDevDumpCreateSchemaScriptPath(String devDumpCreateSchemaScriptPath) {
        this.devDumpCreateSchemaScriptPath = devDumpCreateSchemaScriptPath;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setDevDumpDropSchemaScriptPath(String devDumpDropSchemaScriptPath) {
        this.devDumpDropSchemaScriptPath = devDumpDropSchemaScriptPath;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setAfterCreateScriptPath(String afterCreateScriptPath) {
        this.afterCreateScriptPath = afterCreateScriptPath;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setAfterMigrateScriptPath(String afterMigrateScriptPath) {
        this.afterMigrateScriptPath = afterMigrateScriptPath;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setLogTag(String logTag) {
        this.logTag = logTag;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setProductionOn(boolean productionOn) {
        this.productionOn = productionOn;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setLogOn(boolean logOn) {
        this.logOn = logOn;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setPragmaOn(boolean pragmaOn) {
        this.pragmaOn = pragmaOn;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public KittyDBHelperConfigurationBuilder setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
        return this;
    }

    public KittyDBHelperConfiguration createAMDBHelperConfiguration() {
        return new KittyDBHelperConfiguration(onUpgradeBehavior, migrationsRoot,
                createSchemaScriptPath, dropSchemaScriptPath, devDumpCreateSchemaScriptPath,
                devDumpDropSchemaScriptPath, logTag, logOn, pragmaOn, databaseName, databaseVersion,
                productionOn, scriptsLocatedInAssets, afterCreateScriptPath, afterMigrateScriptPath);
    }
}