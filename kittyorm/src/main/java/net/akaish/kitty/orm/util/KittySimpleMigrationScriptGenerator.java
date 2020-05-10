
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

package net.akaish.kitty.orm.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.indexes.Index;
import net.akaish.kitty.orm.query.CreateDropHelper;
import net.akaish.kitty.orm.query.KittySQLiteQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittyLog.LOG_LEVEL.I;
import static net.akaish.kitty.orm.util.KittyLog.kLog;

/**
 * Simple utility for generating migration script sequence.
 * <br> It is capable only to delete and create new indexes and table as well
 * as adding or deleting columns in existing tables, so use it only when you're sure
 * that there are no constraints defined that can be violated with script created by
 * this utility class.
 * <br> KSMSG v.1
 * Created by akaish on 13.10.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittySimpleMigrationScriptGenerator {

    private static final String SQL_FETCH_TABLES_NAMES = "SELECT name FROM sqlite_master WHERE type='table';";
    private static final String SQL_FETCH_INDEXES_NAMES = "SELECT name FROM sqlite_master WHERE type='index';";
    private static final String SQL_RENAME_TABLE = "ALTER TABLE {0} RENAME TO {1};";
    private static final String SQL_COPY_DATA = "INSERT INTO {0} ({1}) SELECT {2} FROM {3};";
    private static final String SQL_ALTER_TABLE_ADD_COLUMN = "ALTER TABLE {0} ADD COLUMN {1};";

    final Context ctx;
    final KittyDatabaseConfiguration databaseConfiguration;
    final SQLiteDatabase database;
    final ArrayList<String> existingTableNames;
    final ArrayList<String> existingIndexNames;
    final HashMap<String, LinkedList<String>> existingColumnNames;

    static final String I_STARTED = "[KittySimpleMigrationScriptGenerator] Created new instance of KittySimpleMigrationScriptGenerator for {0} v. {1}!";
    static final String I_FETCHING_TABLE_NAMES = "[KittySimpleMigrationScriptGenerator] Fetching existing tables for {0}:";
    static final String I_FOUND_TABLE = "[KittySimpleMigrationScriptGenerator] Found existing table for {0}: {1}";
    static final String I_FOUND_TABLE_AMOUNT = "[KittySimpleMigrationScriptGenerator] Found {0} existing tables for {1}.";

    static final String I_FETCHING_INDEXES_NAMES = "[KittySimpleMigrationScriptGenerator] Fetching existing indexes for {0}:";
    static final String I_FOUND_INDEX = "[KittySimpleMigrationScriptGenerator] Found existing index for {0}: {1}";
    static final String I_FOUND_INDEX_AMOUNT = "[KittySimpleMigrationScriptGenerator] Found {0} existing indexes for {1}.";

    static final String I_FETCHING_EXISTING_COLUMNS = "[KittySimpleMigrationScriptGenerator] Fetching existing columns for table {0}.{1}:";
    static final String I_FETCHED_EXISTING_COLUMN = "[KittySimpleMigrationScriptGenerator] Found column {0} for {1}.{2} .";
    static final String I_FETCHED_EXISTING_COLUMN_AMOUNT = "[KittySimpleMigrationScriptGenerator] Found {0} existing columns for table {1}.{2} .";

    static final String I_GENERATING_DIFFS = "[KittySimpleMigrationScriptGenerator] Generating diffs collection for {0} v.{1} .";
    static final String I_GENERATED_DIFFS = "[KittySimpleMigrationScriptGenerator] Diffs generated for {0} v.{1} .";

    static final String I_PRINTING_GENERATED_DIFFS = "[KittySimpleMigrationScriptGenerator] Printing generated diffs for {0} v.{1} :";

    public enum DIFF_STATES {
        NEW_TABLE ("NEW_TABLE"),
        REDUNDANT_TABLE ("REDUNDANT_TABLE"),
        TABLE_HAS_NO_DIFFS ("TABLE_HAS_NO_DIFFS"),

        ALTER_TABLE ("ALTER_TABLE"),
        TABLE_HAS_NEW_COLUMNS ("TABLE_HAS_NEW_COLUMNS"),
        TABLE_HAS_REDUNDANT_COLUMNS ("TABLE_HAS_REDUNDANT_COLUMNS"),

        NEW_INDEX ("NEW_INDEX"),
        REDUNDANT_INDEX ("REDUNDANT_INDEX"),
        INDEX_HAS_NO_DIFFS ("INDEX_HAS_NO_DIFFS");

        private final String name;

        DIFF_STATES(String name) {
            this.name = name;
        }

        public boolean equalsName(String sqlText) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return this.name.equals(sqlText);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum TYPE {
        TABLE ("TABLE"), INDEX ("INDEX");

        private final String name;

        TYPE(String name) {
            this.name = name;
        }

        public boolean equalsName(String sqlText) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return this.name.equals(sqlText);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    static final String IA_TYPE_MISMATCH = "Can't set redundant, noDiff or new column names for DiffElement instance with type = INDEX!";

    /**
     * Diff model
     */
    public class DiffElement {
        final String name;
        final TYPE type;
        final List<DIFF_STATES> diffStates;

        private ArrayList<String> redundantColumns = new ArrayList<>();
        private ArrayList<String> newColumns = new ArrayList<>();
        private ArrayList<String> noDiffColumns = new ArrayList<>();

        public DiffElement(String name, TYPE type, DIFF_STATES... diffStates) {
            this.name = name;
            this.type = type;
            this.diffStates = Arrays.asList(diffStates);
        }

        public void setRedundantColumns(ArrayList<String> redundantColumns) {
            if(redundantColumns == null)
                throw new NullPointerException();
            if(type == TYPE.INDEX)
                throw new IllegalArgumentException(IA_TYPE_MISMATCH);
            this.redundantColumns = redundantColumns;
        }

        public void setNewColumns(ArrayList<String> newColumns) {
            if(newColumns == null)
                throw new NullPointerException();
            if(type == TYPE.INDEX)
                throw new IllegalArgumentException(IA_TYPE_MISMATCH);
            this.newColumns = newColumns;
        }

        public void setNoDiffColumns(ArrayList<String> noDiffColumns) {
            if(noDiffColumns == null)
                throw new NullPointerException();
            if(type == TYPE.INDEX)
                throw new IllegalArgumentException(IA_TYPE_MISMATCH);
            this.noDiffColumns = noDiffColumns;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            String[] diffStatesStr = new String[diffStates.size()];
            for(int i = 0; i < diffStates.size(); i++) {
                diffStatesStr[i] = diffStates.get(i).toString();
            }
            sb.append("[ diff element type: ")
                    .append(type)
                    .append(" ; diff element name: ")
                    .append(name)
                    .append(" ; diff states: ")
                    .append(KittyUtils.implodeWithCommaInBKT(diffStatesStr));
            if(diffStates.contains(DIFF_STATES.TABLE_HAS_NEW_COLUMNS)) {
                sb.append(" ; new columns: ")
                        .append(
                                KittyUtils.implodeWithCommaInBKT(
                                        newColumns.toArray(new String[newColumns.size()]
                                        )
                                ));
            }
            if(diffStates.contains(DIFF_STATES.TABLE_HAS_REDUNDANT_COLUMNS)) {
                sb.append(" ; redundant columns: ")
                        .append(
                                KittyUtils.implodeWithCommaInBKT(
                                        redundantColumns.toArray(
                                                new String[redundantColumns.size()]
                                        )
                                ));
            }
            if(diffStates.contains(DIFF_STATES.ALTER_TABLE)) {
                sb.append(" ; noDiff columns: ")
                        .append(
                                KittyUtils.implodeWithCommaInBKT(
                                        noDiffColumns.toArray(
                                                new String[noDiffColumns.size()]
                                        )
                                ));
            }
            sb.append(" ]");
            return sb.toString();
        }
    }

    public final GeneratedDiffResult diffs;

    /**
     * At this constructor would be generated diff collection. Also in this constructor would be some database calls, so it is quite
     * slow. Good idea to start transaction for provided database before initializing it.
     * @param ctx
     * @param databaseConfiguration
     * @param database
     */
    public KittySimpleMigrationScriptGenerator(Context ctx, KittyDatabaseConfiguration databaseConfiguration, SQLiteDatabase database) {
        if(databaseConfiguration.isLoggingOn)
            kLog(I, databaseConfiguration.logTag, format(I_STARTED, databaseConfiguration.schemaName, databaseConfiguration.schemaVersion), null);
        this.ctx = ctx;
        this.databaseConfiguration = databaseConfiguration;
        this.database = database;
        if(databaseConfiguration.isLoggingOn)
            kLog(I, databaseConfiguration.logTag, format(I_FETCHING_TABLE_NAMES, databaseConfiguration.schemaName), null);
        existingTableNames = getExistingTablesNames(database, databaseConfiguration);
        existingColumnNames = new HashMap<>();
        Iterator<String> tableNameIterator = existingTableNames.iterator();
        while (tableNameIterator.hasNext()) {
            String tableName = tableNameIterator.next();
            existingColumnNames.put(tableName, getExistingColumnsNames(tableName, database, databaseConfiguration));
        }
        if(databaseConfiguration.isLoggingOn)
            kLog(I, databaseConfiguration.logTag, format(I_FETCHING_INDEXES_NAMES, databaseConfiguration.schemaName), null);
        existingIndexNames = getExistingIndexesNames(database, databaseConfiguration);
        if(databaseConfiguration.isLoggingOn)
            kLog(I, databaseConfiguration.logTag, format(I_GENERATING_DIFFS, databaseConfiguration.schemaName, databaseConfiguration.schemaVersion), null);
        diffs = generateDiffs(databaseConfiguration, existingTableNames, existingIndexNames, existingColumnNames);
        if(databaseConfiguration.isLoggingOn)
            kLog(I, databaseConfiguration.logTag, format(I_GENERATED_DIFFS, databaseConfiguration.schemaName, databaseConfiguration.schemaVersion), null);
        if(databaseConfiguration.isLoggingOn) {
            kLog(I, databaseConfiguration.logTag, format(I_PRINTING_GENERATED_DIFFS, databaseConfiguration.schemaName, databaseConfiguration.schemaVersion), null);
            printDiffsToLog(diffs.diffs, databaseConfiguration);
        }
    }

    /**
     * Generates migration script sequence
     * @return
     */
    public final LinkedList<KittySQLiteQuery> generateMigrationScript() {
        LinkedList<KittySQLiteQuery> out = new LinkedList<>();
        Iterator<DiffElement> diffIterator = diffs.diffs.iterator();
        while (diffIterator.hasNext()) {
            DiffElement diff = diffIterator.next();
            switch (diff.type) {
                case TABLE:
                    out.addAll(generateTableQueries(diff, diffs.tables, databaseConfiguration.schemaName));
                    break;
                case INDEX:
                    out.addAll(generateIndexQueries(databaseConfiguration, diff, diffs.indexes));
                    break;
            }
        }
        return out;
    }

    private final LinkedList<KittySQLiteQuery> generateIndexQueries(KittyDatabaseConfiguration conf, DiffElement diff, HashMap<String, Index> newIndexes) {
        LinkedList<KittySQLiteQuery> out = new LinkedList<>();
        if(diff.diffStates.contains(DIFF_STATES.NEW_INDEX)) {
            out.add(newIndex(newIndexes.get(diff.name)));
        } else if(diff.diffStates.contains(DIFF_STATES.REDUNDANT_INDEX)) {
            out.add(dropIndex(conf.schemaName, diff.name));
        }
        return out;
    }

    /**
     * Generates query sequence for
     * @param diff
     * @param newTables
     * @param schemaName
     * @return
     */
    private final LinkedList<KittySQLiteQuery> generateTableQueries(DiffElement diff,
                                                                    HashMap<String, KittyTableConfiguration> newTables,
                                                                    String schemaName) {
        LinkedList<KittySQLiteQuery> queries = new LinkedList<>();
        if(diff.diffStates.contains(DIFF_STATES.NEW_TABLE)) {
            queries.addLast(newTable(newTables.get(diff.name)));
            return queries;
        } else if(diff.diffStates.contains(DIFF_STATES.REDUNDANT_TABLE)) {
            queries.addLast(dropTable(diff.name, schemaName));
            return queries;
        } else if(diff.diffStates.contains(DIFF_STATES.TABLE_HAS_NO_DIFFS)) {
            return queries;
        } else if(diff.diffStates.contains(DIFF_STATES.ALTER_TABLE)) {
            if (diff.diffStates.contains(DIFF_STATES.TABLE_HAS_REDUNDANT_COLUMNS)) {
                queries.addAll(generateAlterTableWithRedundantColumns(newTables.get(diff.name), diff));
                return queries;
            } else if (diff.diffStates.contains(DIFF_STATES.TABLE_HAS_NEW_COLUMNS)) {
                queries.addAll(generateAlterTableWithNewColumns(diff, newTables, schemaName));
                return queries;
            }
            return queries;
        }
        return queries;
    }

    /**
     * Returns alter table add columns query sequence
     * @param element
     * @param newTables
     * @param schemaName
     * @return
     */
    public LinkedList<KittySQLiteQuery> generateAlterTableWithNewColumns(DiffElement element,
                                                                         HashMap<String, KittyTableConfiguration> newTables,
                                                                         String schemaName) {
        LinkedList<KittySQLiteQuery> out = new LinkedList<>();
        Iterator<KittyColumnConfiguration> columnIterator = newTables.get(element.name).sortedColumns.iterator();
        while (columnIterator.hasNext()) {
            KittyColumnConfiguration columnCfg = columnIterator.next();
            if(element.newColumns.contains(columnCfg.mainConfiguration.columnName)) {
                out.addLast(generateAlterTableAddColumn(columnCfg, element.name, schemaName));
            }
        }
        return out;
    }

    /**
     * Returns alter table query depends on provided column cfg and table name
     * <br> schemaName parameter not used right now, maybe in future
     * @param columnCfg
     * @param tableName
     * @param schemaName
     * @return
     */
    private KittySQLiteQuery generateAlterTableAddColumn(KittyColumnConfiguration columnCfg, String tableName, String schemaName) {
        return new KittySQLiteQuery(
            format(
                    SQL_ALTER_TABLE_ADD_COLUMN,
                    tableName,
                    columnCfg.mainConfiguration.columnName
            ),
                null
        );
    }

    /**
     * Generates query sequence that doing following things:
     * <br> Renames current existing table to new name
     * <br> Creates new table without redundant columns
     * <br> Copies fields same fields from old table to new one
     * <br> Wipes old table
     *
     * @param conf
     * @param element
     * @return
     */
    public LinkedList<KittySQLiteQuery> generateAlterTableWithRedundantColumns(KittyTableConfiguration conf, DiffElement element) {
        LinkedList<KittySQLiteQuery> alterScript = new LinkedList<>();
        String oldTable = new StringBuilder(64).append(conf.tableName).append("_t_old").toString();
        alterScript.addLast(renameTable(conf.tableName, oldTable));
        alterScript.addLast(newTable(conf));
        String columns = KittyUtils.implode(element.noDiffColumns.toArray(new String[element.noDiffColumns.size()]), ", ");
        String copyDataSQL = format(
                SQL_COPY_DATA,
                conf.tableName,
                columns,
                columns,
                oldTable);
        alterScript.addLast(new KittySQLiteQuery(copyDataSQL, null));
        alterScript.addLast(dropTable(oldTable, conf.schemaName));
        return alterScript;
    }


    /**
     * Generates query for renaming table
     * @param tableName
     * @param newTableName
     * @return
     */
    private final KittySQLiteQuery renameTable(String tableName, String newTableName) {
        return new KittySQLiteQuery(format(SQL_RENAME_TABLE, tableName, newTableName), null);
    }

    /**
     * Generates query fo creating new table
     * @param table
     * @return
     */
    private final KittySQLiteQuery newTable(KittyTableConfiguration table) {
        return CreateDropHelper.generateCreateTableStatement(true, table, true);
    }

    /**
     * Generates drop index statement
     * @param indexName
     * @param schemaName
     * @return
     */
    private final KittySQLiteQuery dropIndexStatement(String indexName, String schemaName) {
        return CreateDropHelper.generateDropIndexStatement(schemaName, indexName);
    }

    /**
     * Generates drop table by table name and scheman name
     * @param tableName
     * @param schemaName
     * @return
     */
    private final KittySQLiteQuery dropTable(String tableName, String schemaName) {
        return CreateDropHelper.generateDropTableStatement(tableName, schemaName, false);
    }

    private final KittySQLiteQuery dropIndex(String schemaName, String indexName) {
       return CreateDropHelper.generateDropIndexStatement(schemaName, indexName);
    }

    private final KittySQLiteQuery newIndex(Index index) {
        return new KittySQLiteQuery(index.toString(true), null);
    }

    /**
     * Generates create index statement
     * @param table
     * @return
     */
    private final HashMap<String, KittySQLiteQuery> generateIndexesForTable(KittyTableConfiguration table) {
        return CreateDropHelper.generateCreateIndexStatementsMap(table, true);
    }

    /**
     * GenerateDiffResults class designed to be used as result of {@link #generateDiffs(KittyDatabaseConfiguration, ArrayList, ArrayList, HashMap)}
     */
    class GeneratedDiffResult {
        final ArrayList<DiffElement> diffs;
        final HashMap<String, KittyTableConfiguration> tables;
        final HashMap<String, Index> indexes;

        public GeneratedDiffResult(ArrayList<DiffElement> diffs,
                                  HashMap<String, KittyTableConfiguration> tables,
                                   HashMap<String, Index> indexes) {
            this.diffs = diffs;
            this.tables = tables;
            this.indexes = indexes;
        }
    }

    /**
     * Generates diff collection with usage of passed parameters.
     * @param conf
     * @param eTables
     * @param eIndexes
     * @param eColumnNames
     * @return
     */
    private GeneratedDiffResult generateDiffs(KittyDatabaseConfiguration conf,
                                                 ArrayList<String> eTables, ArrayList<String> eIndexes,
                                                 HashMap<String, LinkedList<String>> eColumnNames) {
        ArrayList<DiffElement> diffs = new ArrayList<>();
        Iterator<KittyTableConfiguration> newSchemaTableIterator = conf.tableConfigurations.iterator();
        LinkedList<String> implementationSchemaTableNames = new LinkedList<>();
        LinkedList<String> implementationSchemaIndexNames = new LinkedList<>();
        HashMap<String, KittyTableConfiguration> newTables = new HashMap<>();
        HashMap<String, Index> newIndexes = new HashMap<>();
        while (newSchemaTableIterator.hasNext()) {
            KittyTableConfiguration tableImplementation = newSchemaTableIterator.next();
            implementationSchemaTableNames.addLast(tableImplementation.tableName);
            newTables.put(tableImplementation.tableName, tableImplementation);
            DiffElement tableDiffElement;
            if(!eTables.contains(tableImplementation.tableName)) {
                tableDiffElement = new DiffElement(tableImplementation.tableName, TYPE.TABLE, DIFF_STATES.NEW_TABLE);
                diffs.add(tableDiffElement);
            } else {
                tableDiffElement = checkTableOnColumnChanges(tableImplementation, eColumnNames.get(tableImplementation.tableName));
                diffs.add(tableDiffElement);

            }
            if(tableImplementation.indexes != null) {
                Iterator<Index> newSchemaImplementationIndexIterator = tableImplementation.indexes.iterator();
                while (newSchemaImplementationIndexIterator.hasNext()) {
                    Index index = newSchemaImplementationIndexIterator.next();
                    implementationSchemaIndexNames.addLast(index.getIndexName());
                    newIndexes.put(index.getIndexName(), index);
                    if (tableDiffElement != null) {
                        if (tableDiffElement.redundantColumns.size() > 0) {
                            // We have to recreate index because if table has redundant columns, old table and all
                            // indexes would be deleted
                            diffs.add(new DiffElement(index.getIndexName(), TYPE.INDEX, DIFF_STATES.NEW_INDEX));
                        } else if (!eIndexes.contains(index.getIndexName())) {
                            // In this case we assume that this is newindex is the same, cause this generator
                            // not able to change existing indexes
                            diffs.add(new DiffElement(index.getIndexName(), TYPE.INDEX, DIFF_STATES.NEW_INDEX));
                        } else {
                            // So here we assume that index should be created
                            diffs.add(new DiffElement(index.getIndexName(), TYPE.INDEX, DIFF_STATES.INDEX_HAS_NO_DIFFS));
                        }
                    }
                }
            }
        }
        Iterator<String> eTablesIterator = eTables.iterator();
        while (eTablesIterator.hasNext()) {
            String existingTable = eTablesIterator.next();
            if(!implementationSchemaTableNames.contains(existingTable))
                diffs.add(new DiffElement(existingTable, TYPE.TABLE, DIFF_STATES.REDUNDANT_TABLE));
        }
        Iterator<String> eIndexesIterator = eIndexes.iterator();
        while (eIndexesIterator.hasNext()) {
            String existingIndex = eIndexesIterator.next();
            if(!implementationSchemaIndexNames.contains(existingIndex))
                diffs.add(new DiffElement(existingIndex, TYPE.INDEX, DIFF_STATES.REDUNDANT_INDEX));
        }
        return new GeneratedDiffResult(diffs, newTables, newIndexes);
    }

    /**
     * Prints generated diff collection to log stream
     * @param diffs
     * @param conf
     */
    private void printDiffsToLog(List<DiffElement> diffs, KittyDatabaseConfiguration conf) {
        Iterator<DiffElement> diffIterator = diffs.iterator();
        while (diffIterator.hasNext()) {
            kLog(I, conf.logTag, diffIterator.next().toString(), null);
        }
    }

    /**
     * Checks tables that exists both in SQLite database and in new KittyDatabase domain implementation
     * on new and redundant columns.
     * @param table
     * @param existingColumns
     * @return
     */
    private DiffElement checkTableOnColumnChanges(KittyTableConfiguration table, LinkedList<String> existingColumns) {
        Iterator<KittyColumnConfiguration> columnsIterator = table.sortedColumns.iterator();
        LinkedList<String> implementationColumns = new LinkedList<>();
        ArrayList<String> redundantColumns = new ArrayList<>();
        ArrayList<String> noDiffColumns = new ArrayList<>();
        ArrayList<String> newColumns = new ArrayList<>();
        while (columnsIterator.hasNext()) {
            KittyColumnConfiguration columnConfiguration = columnsIterator.next();
            implementationColumns.addLast(columnConfiguration.mainConfiguration.columnName);
            if(!existingColumns.contains(columnConfiguration.mainConfiguration.columnName)) {
                newColumns.add(columnConfiguration.mainConfiguration.columnName);
            } else {
                noDiffColumns.add(columnConfiguration.mainConfiguration.columnName);
            }
        }
        Iterator<String> eColumnsIterator = existingColumns.iterator();
        while (eColumnsIterator.hasNext()) {
            String eColumn = eColumnsIterator.next();
            if(!implementationColumns.contains(eColumn)) {
                redundantColumns.add(eColumn);
            }
        }
        List<DIFF_STATES> diff_states = new ArrayList<>();
        if(redundantColumns.size() == 0 && newColumns.size() == 0) {
            diff_states.add(DIFF_STATES.TABLE_HAS_NO_DIFFS);
        } else {
            diff_states.add(DIFF_STATES.ALTER_TABLE);
            if(redundantColumns.size() > 0)
                diff_states.add(DIFF_STATES.TABLE_HAS_REDUNDANT_COLUMNS);
            if(newColumns.size() > 0)
                diff_states.add(DIFF_STATES.TABLE_HAS_NEW_COLUMNS);
        }
        DiffElement out = new DiffElement(
                table.tableName,
                TYPE.TABLE,
                diff_states.toArray(new DIFF_STATES[diff_states.size()])
        );
        out.setNewColumns(newColumns);
        out.setRedundantColumns(redundantColumns);
        out.setNoDiffColumns(noDiffColumns);
        return out;
    }

    /**
     * Returns list of existing indexes defined for passed database.
     * @param db
     * @param conf
     * @return
     */
    private ArrayList<String> getExistingIndexesNames(SQLiteDatabase db, KittyDatabaseConfiguration conf) {
        ArrayList<String> out = new ArrayList<>();
        Cursor cursor = db.rawQuery(SQL_FETCH_INDEXES_NAMES, null);
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String indexName = cursor.getString(cursor.getColumnIndex("name"));
                out.add(indexName);
                if (conf.isLoggingOn)
                    kLog(I, conf.logTag, format(I_FOUND_INDEX, databaseConfiguration.schemaName, indexName), null);
                cursor.moveToNext();
            }
        }
        cursor.close();
        if(conf.isLoggingOn)
            kLog(I, conf.logTag, format(I_FOUND_INDEX_AMOUNT, out.size(), databaseConfiguration.schemaName), null);
        return out;
    }

    /**
     * Returns list of existing tables defined for passed database.
     * @param db
     * @param conf
     * @return
     */
    private ArrayList<String> getExistingTablesNames(SQLiteDatabase db, KittyDatabaseConfiguration conf) {
        ArrayList<String> out = new ArrayList<>();
        Cursor cursor = db.rawQuery(SQL_FETCH_TABLES_NAMES, null);
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String tableName = cursor.getString(cursor.getColumnIndex("name"));
                if(!tableName.equals("android_metadata")) {
                    out.add(tableName);
                    if (conf.isLoggingOn)
                        kLog(I, conf.logTag, format(I_FOUND_TABLE, databaseConfiguration.schemaName, tableName), null);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        if(conf.isLoggingOn)
            kLog(I, conf.logTag, format(I_FOUND_TABLE_AMOUNT, out.size(), databaseConfiguration.schemaName), null);
        return out;
    }

    /**
     * Returns list of existing columns for defined database table.
     * @param tableName
     * @param db
     * @param conf
     * @return
     */
    private LinkedList<String> getExistingColumnsNames(String tableName, SQLiteDatabase db, KittyDatabaseConfiguration conf) {
        if(conf.isLoggingOn)
            kLog(I, conf.logTag, format(I_FETCHING_EXISTING_COLUMNS, databaseConfiguration.schemaName, tableName), null);
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        LinkedList<String> columns = new LinkedList<>();

        for (int i = 0; i < cursor.getColumnCount(); i++) {
            String columnName = cursor.getColumnName(i);
            if(conf.isLoggingOn)
                kLog(I, conf.logTag, format(I_FETCHED_EXISTING_COLUMN, columnName, databaseConfiguration.schemaName, tableName), null);
            columns.add(columnName);
        }
        cursor.close();
        if(conf.isLoggingOn)
            kLog(I, conf.logTag, format(I_FETCHED_EXISTING_COLUMN_AMOUNT, columns.size(), databaseConfiguration.schemaName, tableName), null);
        return columns;
    }
}
