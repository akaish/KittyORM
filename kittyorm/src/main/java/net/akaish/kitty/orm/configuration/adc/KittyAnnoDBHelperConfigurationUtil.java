
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

package net.akaish.kitty.orm.configuration.adc;

import android.content.Context;

import net.akaish.kitty.orm.KittyDatabase;
import net.akaish.kitty.orm.annotations.KITTY_DATABASE_HELPER;
import net.akaish.kitty.orm.configuration.conf.KittyDBHelperConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyDBHelperConfigurationBuilder;
import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.util.KittyUtils;

import static net.akaish.kitty.orm.util.KittyNamingUtils.getAfterCreateScriptDefaultFilename;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getAfterMigrateScriptDefaultFilename;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getSQLiteScriptPath;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getCreateSchemaDefaultFilename;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDefaultAssetDatabaseScriptsPath;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDefaultDatabaseMigrationScriptsPath;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDefaultDatabaseScriptsPath;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDevelopmentDumpsDefaultPath;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDropSchemaDefaultFilename;

/**
 * Helper class for generating {@link KittyDBHelperConfiguration}
 * Created by akaish on 04.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyAnnoDBHelperConfigurationUtil {

    public static final <T extends KittyDatabase> KittyDBHelperConfiguration getDBHelperConfiguration(
            KittyDatabaseConfiguration databaseConfiguration, Class<T> kittyDatabaseClass, Context ctx) {
        KittyDBHelperConfigurationBuilder dbhb = new KittyDBHelperConfigurationBuilder();
        dbhb.setDatabaseName(databaseConfiguration.schemaName)
                .setDatabaseVersion(databaseConfiguration.schemaVersion)
                .setLogOn(databaseConfiguration.isLoggingOn)
                .setLogTag(databaseConfiguration.logTag)
                .setPragmaOn(databaseConfiguration.isPragmaON)
                .setProductionOn(databaseConfiguration.isProductionOn);

        KITTY_DATABASE_HELPER.UpgradeBehavior onUpgradeBehavior = KITTY_DATABASE_HELPER.UpgradeBehavior.DROP_AND_CREATE;
        String DMMUpdateScriptsPath = null;
        String createSchemaScriptPath = null;
        String dropSchemaScriptPath = null;

        String afterCreateScriptPath = null;
        String afterMigrateScriptPath = null;

        boolean scriptsInAssets = true;

        if(kittyDatabaseClass.isAnnotationPresent(KITTY_DATABASE_HELPER.class)) {
            KITTY_DATABASE_HELPER dbhAnno = kittyDatabaseClass.getAnnotation(KITTY_DATABASE_HELPER.class);
            onUpgradeBehavior = dbhAnno.onUpgradeBehavior();
            DMMUpdateScriptsPath = dbhAnno.migrationScriptsPath().length() == 0 ? null : dbhAnno.migrationScriptsPath();
            createSchemaScriptPath = dbhAnno.createScriptPath().length() == 0 ? null : dbhAnno.createScriptPath();
            dropSchemaScriptPath = dbhAnno.dropScriptPath().length() == 0 ? null : dbhAnno.dropScriptPath();

            afterCreateScriptPath = dbhAnno.afterCreateScriptPath().length() == 0 ? null : dbhAnno.afterCreateScriptPath();
            afterMigrateScriptPath = dbhAnno.afterMigrateScriptPath().length() == 0 ? null : dbhAnno.afterMigrateScriptPath();

            scriptsInAssets = dbhAnno.migrationScriptsStoredInAssets();
        }

        String schemaName = KittyUtils.removeAllIllegalCharactersFromPathString(
                databaseConfiguration.schemaName
        );

        String databaseScriptsRoot;

        if(!scriptsInAssets)
            databaseScriptsRoot = getDefaultDatabaseScriptsPath(ctx, schemaName);
        else
            databaseScriptsRoot = getDefaultAssetDatabaseScriptsPath(schemaName);

        if(onUpgradeBehavior.equals(KITTY_DATABASE_HELPER.UpgradeBehavior.USE_FILE_MIGRATIONS)) {
            if(DMMUpdateScriptsPath == null)
                DMMUpdateScriptsPath = getDefaultDatabaseMigrationScriptsPath(ctx, schemaName, scriptsInAssets);
        }

        String createScriptFilename = getCreateSchemaDefaultFilename(schemaName, databaseConfiguration.schemaVersion);

        String dropScriptFilename = getDropSchemaDefaultFilename(schemaName, databaseConfiguration.schemaVersion);

        String afterCreateSchemaFilename = getAfterCreateScriptDefaultFilename(schemaName, databaseConfiguration.schemaVersion);

        String afterMigrateSchemaFilename = getAfterMigrateScriptDefaultFilename(schemaName, databaseConfiguration.schemaVersion);

        if(createSchemaScriptPath == null)
            createSchemaScriptPath = getSQLiteScriptPath(databaseScriptsRoot, createScriptFilename);

        if(dropSchemaScriptPath == null)
            dropSchemaScriptPath = getSQLiteScriptPath(databaseScriptsRoot, dropScriptFilename);

        if(afterCreateScriptPath == null)
            afterCreateScriptPath = getSQLiteScriptPath(databaseScriptsRoot, afterCreateSchemaFilename);

        if(afterMigrateScriptPath == null)
            afterMigrateScriptPath = getSQLiteScriptPath(databaseScriptsRoot, afterMigrateSchemaFilename);

        String devCreateScriptPath = getDevelopmentDumpsDefaultPath(databaseScriptsRoot, createScriptFilename);

        String devDropScriptPath = getDevelopmentDumpsDefaultPath(databaseScriptsRoot, dropScriptFilename);

        dbhb.setCreateSchemaScriptPath(createSchemaScriptPath)
                .setDevDumpCreateSchemaScriptPath(devCreateScriptPath)
                .setDropSchemaScriptPath(dropSchemaScriptPath)
                .setDevDumpDropSchemaScriptPath(devDropScriptPath)
                .setOnUpgradeBehavior(onUpgradeBehavior)
                .setMigrationsRoot(DMMUpdateScriptsPath)
                .setAfterCreateScriptPath(afterCreateScriptPath)
                .setAfterMigrateScriptPath(afterMigrateScriptPath);

        return dbhb.createAMDBHelperConfiguration();
    }
}
