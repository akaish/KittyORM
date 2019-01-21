
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

package net.akaish.kitty.orm.dumputils.migrations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.query.KittySQLiteQuery;
import net.akaish.kitty.orm.util.KittySimpleMigrationScriptGenerator;

import java.text.MessageFormat;
import java.util.LinkedList;

/**
 * Migrator that returns script automatically generated with {@link net.akaish.kitty.orm.util.KittySimpleMigrationScriptGenerator}
 * <br> Be aware to use this migrator with database schema that differs with old version in things related with constraints
 * <br> Main idea is that this migrator would do following things:
 * <br> 1) Gets list of existing tables and indexes
 * <br> 2) Generates diff collection without constraint checks on tables and table columns
 * <br> 3) If some of tables in new version would have less fields than old version, old version table
 * would be renamed, new version of table would be created and all values except redundant column values
 * would be copied to new location
 * <br> 4) Is some of tables in new version would have more fields, than would be generated alter table add
 * column query list
 * <br> 5) If new version table has same amount of same named columns as in old version table, nothing would be done
 * <br> 6) If new version index not present in old schema, it would be created
 * <br> 7) etc
 * <br> Just check {@link net.akaish.kitty.orm.util.KittySimpleMigrationScriptGenerator} for more info
 * Created by akaish on 19.10.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittySimpleMigrator extends KittyORMVersionMigrator {

    private static String IA_EXCEPTION_BAD_MIGRATION_PARAMETERS = "KittyDevDropCreateMigrator needs migrationsParameters to be an array with at least one value at [0] index that is a KittyDatabaseConfiguration instance and one value at [1] index that is a SQLiteDatabase instance (schema: {0}, old schema version: {1}, new schema version {2})!";

    private KittyDatabaseConfiguration databaseConfiguration;
    private SQLiteDatabase database;
    private LinkedList<KittySQLiteQuery> migrationScript;

    /**
     * Simple migration script generator migrator. To use it you should pass an instance of KittyDatabaseConfiguration as
     * [0] of migrationsParameters.
     * @param oldVersion
     * @param currentVersion
     * @param ctx
     * @param schemaName
     * @param logTag
     * @param logOn
     * @param factoryParameters
     * @param migrationsParameters
     */
    public KittySimpleMigrator(int oldVersion, int currentVersion, Context ctx,
                                      String schemaName, String logTag, boolean logOn,
                                      Object[] factoryParameters, Object[] migrationsParameters) {
        super(oldVersion, currentVersion, ctx, schemaName, logTag, logOn, factoryParameters, migrationsParameters);
    }

    @Override
    protected void setParameters(Object[] factoryParameters, Object[] migrationsParameters) {
        String IAMessage = MessageFormat.format(
                IA_EXCEPTION_BAD_MIGRATION_PARAMETERS,
                schemaName,
                Integer.toString(oldVersion),
                Integer.toString(currentVersion));
        if (migrationsParameters == null) throw new IllegalArgumentException(IAMessage);
        if (migrationsParameters.length != 2) throw new IllegalArgumentException(IAMessage);
        if (migrationsParameters[0] instanceof KittyDatabaseConfiguration) {
            databaseConfiguration = (KittyDatabaseConfiguration) migrationsParameters[0];
        } else {
            throw new IllegalArgumentException(IAMessage);
        }
        if (migrationsParameters[1] instanceof SQLiteDatabase) {
            database = (SQLiteDatabase) migrationsParameters[1];
        } else {
            throw new IllegalArgumentException(IAMessage);
        }
    }

    @Override
    protected <T extends KittyMigrationFactory> T getMigrationFactory(Object... parameters) { // do nothing
        return null;
    }

    @Override
    public boolean isMigrationSequenceApplicable() {
        return true;
    }

    /**
     * Fills {@link #migrations} with {@link #migrationFactory}
     *
     * @param parameters
     */
    @Override
    protected void setMigrations(Object... parameters) {
        KittySimpleMigrationScriptGenerator generator = new KittySimpleMigrationScriptGenerator(context, databaseConfiguration, database);
        migrations.add(new KittyMigration(
                oldVersion,
                oldVersion,
                currentVersion,
                currentVersion,
                generator.generateMigrationScript(),
                schemaName
        ));
    }
}
