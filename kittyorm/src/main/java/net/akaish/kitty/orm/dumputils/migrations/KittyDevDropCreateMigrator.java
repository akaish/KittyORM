
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

package net.akaish.kitty.orm.dumputils.migrations;

import android.content.Context;
import android.util.Log;

import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.query.CreateDropHelper;
import net.akaish.kitty.orm.query.KittySQLiteQuery;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for processing migrations from different versions of schema to current without full sequences.
 * <br> This migrator should be used only in development purposes because it can't handle old existing indexes and tables (and whatever)
 * <br> that may exist in old schema version but were deprecated in current. This migrator just drops all tables and indexes with same names that are used
 * <br> in current version creates them. For example old schema contained tables "goods" "clients" and "contacts" but in new version you decided to get rid
 * <br> of "contacts" table. Your current schema would have only two tables, "goods" and "clients". So they would be dropped with IF EXISTS flag and then created.
 * <br> However "contacts" table and all associated data would stay cause this migrator doesn't now anything about it (it works only with current schema).
 * <br> If you want pass already existing migration script sequence you should pass an instance of LinkedList<KittySQLiteQuery> as [1] of migrationsParameters.
 * <br> In last case you can manually drop ALL schema and than apply creation script generated with {@link CreateDropHelper#generateCreationScript(KittyDatabaseConfiguration)}.
 * Created by akaish on 14.06.2018.
 * @author akaish (Denis Bogomolov)
 */

public class KittyDevDropCreateMigrator extends KittyORMVersionMigrator {

    private KittyDatabaseConfiguration databaseConfiguration;
    private LinkedList<KittySQLiteQuery> migrationScript;

    private static String IA_EXCEPTION_BAD_MIGRATION_PARAMETERS = "KittyDevDropCreateMigrator needs migrationsParameters to be an array with at least one value at [0] index that is a KittyDatabaseConfiguration instance or one value at [1] index that is instance of LinkedList<KittySQLiteQuery> (schema: {0}, old schema version: {1}, new schema version {2})!";
    private static String LOG_I_ACCEPTING_MIGRATION_SCRIPT = "KittyDevDropCreateMigrator migrationsParameters[1] is an instance of LinkedList, assuming that it is List<KittySQLiteQuery> that can be used as migration script! (schema: {0}, old schema version: {1}, new schema version {2})";
    /**
     * Create Drop development migrator. To use it you should pass an instance of KittyDatabaseConfiguration as
     * [0] of migrationsParameters. If you want pass already existing migration script sequence you should pass
     * an instance of LinkedList<KittySQLiteQuery> as [1] of migrationsParameters.
     * @param oldVersion
     * @param currentVersion
     * @param ctx
     * @param schemaName
     * @param logTag
     * @param logOn
     * @param factoryParameters
     * @param migrationsParameters
     */
    public KittyDevDropCreateMigrator(int oldVersion, int currentVersion, Context ctx,
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
        if(migrationsParameters == null) throw new IllegalArgumentException(IAMessage);
        if(migrationsParameters.length == 0) throw new IllegalArgumentException(IAMessage);
        if(migrationsParameters[0] instanceof KittyDatabaseConfiguration) {
            databaseConfiguration = (KittyDatabaseConfiguration) migrationsParameters[0];
        } else if(migrationsParameters[1] instanceof  LinkedList) {
            // do nothing
        } else {
            throw new IllegalArgumentException(IAMessage);
        }
        if(migrationsParameters.length == 2) {
            if(migrationsParameters[1] instanceof LinkedList) {
                if(logOn) {
                    String logMessage = MessageFormat.format(
                            LOG_I_ACCEPTING_MIGRATION_SCRIPT,
                            schemaName,
                            Integer.toString(oldVersion),
                            Integer.toString(currentVersion));
                    Log.i(logTag, logMessage);
                }
                migrationScript = (LinkedList<KittySQLiteQuery>) migrationsParameters[1];
            }
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

    @Override
    protected void setMigrations(Object... parameters) {
        if(migrationScript != null) {
            migrations.add(new KittyMigration(
                    oldVersion,
                    oldVersion,
                    currentVersion,
                    currentVersion,
                    migrationScript,
                    schemaName
            ));
        } else {
            LinkedList<KittySQLiteQuery> statements = new LinkedList<>();
            LinkedList<KittySQLiteQuery> createStatements = new LinkedList<>();
            Iterator<KittyTableConfiguration> ktcIterator = databaseConfiguration.tableConfigurations.iterator();
            while (ktcIterator.hasNext()) {
                KittyTableConfiguration table = ktcIterator.next();
                List<KittySQLiteQuery> dropIndexes = CreateDropHelper.generateDropIndexStatements(table);
                if (dropIndexes != null)
                    statements.addAll(dropIndexes);
                statements.add(CreateDropHelper.generateDropTableStatement(table));
                createStatements.add(CreateDropHelper.generateCreateTableStatement(false, table, true));
                List<KittySQLiteQuery> createIndexes = CreateDropHelper.generateCreateIndexStatements(table, true);
                if (createIndexes != null)
                    createStatements.addAll(createIndexes);
            }
            statements.addAll(createStatements);
            migrations.add(new KittyMigration(
                    oldVersion,
                    oldVersion,
                    currentVersion,
                    currentVersion,
                    statements,
                    schemaName
            ));
        }
    }
}
