
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

package net.akaish.kitty.orm.configuration.conf;

import net.akaish.kitty.orm.annotations.KittyDatabaseHelper;

/**
 * Database helper configuration
 * Created by akaish on 04.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyDBHelperConfiguration {

    public final KittyDatabaseHelper.UpgradeBehavior onUpgradeBehavior;
    public final boolean scriptsLocatedInAssets;
    public final String DMMUpdateScriptsPath;
    public final String createSchemaScriptPath;
    public final String dropSchemaScriptPath;
    public final String devDumpCreateSchemaScriptPath;
    public final String devDumpDropSchemaScriptPath;
    public final String logTag;
    public final boolean logOn;
    public final boolean pragmaOn;
    public final boolean productionOn;
    public final String schemaName;
    public final int schemaVersion;
    public final String afterCreateScriptPath;
    public final String afterMigrateScriptPath;

    private KittyDBHelperConfiguration(KittyDatabaseHelper.UpgradeBehavior onUpgradeBehavior,
                                      String DMMUpdateScriptsPath, String createSchemaScriptPath,
                                      String dropSchemaScriptPath, String devDumpCreateSchemaScriptPath,
                                      String devDumpDropSchemaScriptPath, String logTag, boolean logOn,
                                      boolean pragmaOn, String schemaName, int schemaVersion, boolean productionOn,
                                      boolean scriptsLocatedInAssets, String afterCreateScriptPath,
                                      String afterMigrateScriptPath) {
        this.onUpgradeBehavior = onUpgradeBehavior;
        this.DMMUpdateScriptsPath = DMMUpdateScriptsPath;
        this.createSchemaScriptPath = createSchemaScriptPath;
        this.dropSchemaScriptPath = dropSchemaScriptPath;
        this.devDumpCreateSchemaScriptPath = devDumpCreateSchemaScriptPath;
        this.devDumpDropSchemaScriptPath = devDumpDropSchemaScriptPath;
        this.logTag = logTag;
        this.logOn = logOn;
        this.pragmaOn = pragmaOn;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.productionOn = productionOn;
        this.scriptsLocatedInAssets = scriptsLocatedInAssets;
        this.afterCreateScriptPath = afterCreateScriptPath;
        this.afterMigrateScriptPath = afterMigrateScriptPath;
    }

    public static class Builder {

        private KittyDatabaseHelper.UpgradeBehavior onUpgradeBehavior;
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

        public Builder setOnUpgradeBehavior(KittyDatabaseHelper.UpgradeBehavior onUpgradeBehavior) {
            this.onUpgradeBehavior = onUpgradeBehavior;
            return this;
        }

        public Builder setScriptsLocatedInAssets(boolean scriptsLocatedInAssets) {
            this.scriptsLocatedInAssets = scriptsLocatedInAssets;
            return this;
        }


        public Builder setMigrationsRoot(String migrationsRoot) {
            this.migrationsRoot = migrationsRoot;
            return this;
        }

        public Builder setCreateSchemaScriptPath(String createSchemaScriptPath) {
            this.createSchemaScriptPath = createSchemaScriptPath;
            return this;
        }

        public Builder setDropSchemaScriptPath(String dropSchemaScriptPath) {
            this.dropSchemaScriptPath = dropSchemaScriptPath;
            return this;
        }

        public Builder setDevDumpCreateSchemaScriptPath(String devDumpCreateSchemaScriptPath) {
            this.devDumpCreateSchemaScriptPath = devDumpCreateSchemaScriptPath;
            return this;
        }

        public Builder setDevDumpDropSchemaScriptPath(String devDumpDropSchemaScriptPath) {
            this.devDumpDropSchemaScriptPath = devDumpDropSchemaScriptPath;
            return this;
        }

        public Builder setAfterCreateScriptPath(String afterCreateScriptPath) {
            this.afterCreateScriptPath = afterCreateScriptPath;
            return this;
        }

        public Builder setAfterMigrateScriptPath(String afterMigrateScriptPath) {
            this.afterMigrateScriptPath = afterMigrateScriptPath;
            return this;
        }

        public Builder setLogTag(String logTag) {
            this.logTag = logTag;
            return this;
        }

        public Builder setProductionOn(boolean productionOn) {
            this.productionOn = productionOn;
            return this;
        }

        public Builder setLogOn(boolean logOn) {
            this.logOn = logOn;
            return this;
        }

        public Builder setPragmaOn(boolean pragmaOn) {
            this.pragmaOn = pragmaOn;
            return this;
        }

        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder setDatabaseVersion(int databaseVersion) {
            this.databaseVersion = databaseVersion;
            return this;
        }

        public KittyDBHelperConfiguration build() {
            return new KittyDBHelperConfiguration(onUpgradeBehavior, migrationsRoot,
                    createSchemaScriptPath, dropSchemaScriptPath, devDumpCreateSchemaScriptPath,
                    devDumpDropSchemaScriptPath, logTag, logOn, pragmaOn, databaseName, databaseVersion,
                    productionOn, scriptsLocatedInAssets, afterCreateScriptPath, afterMigrateScriptPath);
        }
    }
}
