
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

import net.akaish.kitty.orm.annotations.KITTY_DATABASE_HELPER;

/**
 * Database helper configuration
 * Created by akaish on 04.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyDBHelperConfiguration {

    public final KITTY_DATABASE_HELPER.UpgradeBehavior onUpgradeBehavior;
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

    public KittyDBHelperConfiguration(KITTY_DATABASE_HELPER.UpgradeBehavior onUpgradeBehavior,
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
}
