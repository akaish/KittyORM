
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

package net.akaish.kitty.orm.query;

import net.akaish.kitty.orm.annotations.table.KITTY_TABLE;
import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyColumnMainConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.constraints.table.CheckTableConstraint;
import net.akaish.kitty.orm.constraints.table.ForeignKeyTableConstraint;
import net.akaish.kitty.orm.constraints.table.UniqueTableConstraint;
import net.akaish.kitty.orm.enums.Keywords;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.indexes.Index;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static net.akaish.kitty.orm.enums.Keywords.AUTOINCREMENT;
import static net.akaish.kitty.orm.util.KittyConstants.COMMA;
import static net.akaish.kitty.orm.util.KittyConstants.DOT;
import static net.akaish.kitty.orm.util.KittyConstants.LEFT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.RIGHT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.SEMICOLON;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Created by akaish on 07.02.18.
 * @author akaish (Denis Bogomolov)
 */

public class CreateDropHelper {

    /**
     * Generates create tables and indexes script on provided instance of {@link KittyDatabaseConfiguration}
     * @param databaseConfiguration
     * @return
     */
    public static final LinkedList<KittySQLiteQuery> generateCreationScript(KittyDatabaseConfiguration databaseConfiguration) {
        LinkedList<KittySQLiteQuery> createStatements = new LinkedList<>();
        Iterator<KittyTableConfiguration> ktcIterator = databaseConfiguration.tableConfigurations.iterator();
        while (ktcIterator.hasNext()) {
            KittyTableConfiguration table = ktcIterator.next();
            createStatements.add(CreateDropHelper.generateCreateTableStatement(false, table, true));
            List<KittySQLiteQuery> createIndexes = CreateDropHelper.generateCreateIndexStatements(table, true);
            if (createIndexes != null)
                createStatements.addAll(createIndexes);
        }
//        for (KittyTableConfiguration table : databaseConfiguration.tableConfigurations) {
//            createStatements.add(CreateDropHelper.generateCreateTableStatement(false, table));
//            List<KittySQLiteQuery> createIndexes = CreateDropHelper.generateCreateIndexStatements(table);
//            if (createIndexes != null)
//                createStatements.addAll(createIndexes);
//        }
        return createStatements;
    }

    /**
     * Returns SQLite drop statement for table associated with this instance of {@link KittyTableConfiguration}
     * @param conf
     * @return
     */
    public static final KittySQLiteQuery generateDropTableStatement(KittyTableConfiguration conf) {
        return generateDropTableStatement(conf.tableName, conf.schemaName, conf.isTemporaryTable);
    }

    /**
     * Returns SQLite drop statement for specified table name, schema name and temporary table flag.
     * @param tableName
     * @param schemaName
     * @param isTemporaryTable
     * @return
     */
    public static final KittySQLiteQuery generateDropTableStatement(String tableName,
                                                                    String schemaName,
                                                                    boolean isTemporaryTable) {
        StringBuffer sb = new StringBuffer(128);
        sb.append(Keywords.DROP)
                .append(WHITESPACE)
                .append(Keywords.TABLE)
                .append(WHITESPACE)
                .append(Keywords.IF_EXISTS)
                .append(WHITESPACE);
        if(isTemporaryTable)
            sb.append(schemaName).append(DOT);
        sb.append(tableName).append(SEMICOLON);
        return new KittySQLiteQuery(sb.toString(), null);
    }

    /**
     * Returns list of indexes drop statements associated with provided table configuration or null
     * if no indexes defined.
     * @param conf
     * @return
     */
    public static final List<KittySQLiteQuery> generateDropIndexStatements(KittyTableConfiguration conf) {
        List<KittySQLiteQuery> indexesToDrop = new ArrayList<>();
        for(Index index : conf.indexes) {
            indexesToDrop.add(generateDropIndexStatement(index.getSchemaName(), index.getIndexName()));
        }
        if(indexesToDrop.size() == 0) return null;
        return indexesToDrop;
    }

    /**
     * Returns drop index statement
     * @param schemaName
     * @param indexName
     * @return
     */
    public static final KittySQLiteQuery generateDropIndexStatement(String schemaName, String indexName) {
        StringBuffer sb = new StringBuffer(128);
        sb.append(Keywords.DROP)
                .append(WHITESPACE)
                .append(Keywords.INDEX)
                .append(WHITESPACE)
                .append(Keywords.IF_EXISTS)
                .append(WHITESPACE)
                .append(schemaName)
                .append(DOT)
                .append(indexName)
                .append(SEMICOLON);
        return new KittySQLiteQuery(sb.toString(), null);
    }

    /**
     * Generates CREATE statements for indexes described in provided table configuration.
     * @param conf
     * @return
     */
    public static List<KittySQLiteQuery> generateCreateIndexStatements(KittyTableConfiguration conf, boolean skipSchemaName) {
        if(conf.indexes == null) return null;
        else {
            List<KittySQLiteQuery> indexCreateStatements = new LinkedList<>();
            for(Index index : conf.indexes) {
                indexCreateStatements.add(new KittySQLiteQuery(index.toString(skipSchemaName), null));
            }
            return indexCreateStatements;
        }
    }

    /**
     * Generates CREATE statements for indexes described in provided table configuration.
     * @param conf
     * @param skipSchemaName
     * @return
     */
    public static HashMap<String, KittySQLiteQuery> generateCreateIndexStatementsMap(KittyTableConfiguration conf, boolean skipSchemaName) {
        if(conf.indexes == null) return null;
        else {
            HashMap<String, KittySQLiteQuery> indexCreateStatements = new HashMap<>();
            for(Index index : conf.indexes) {
                indexCreateStatements.put(index.getIndexName(), new KittySQLiteQuery(index.toString(skipSchemaName), null));
            }
            return indexCreateStatements;
        }
    }

    private static String AME_BAD_MODEL = "Provided configuration not marked as schema model ({0} for {1}.{2})";

    /**
     * Generates create statement for table described in provided configuration.
     * {@link KittyRuntimeException} would be thrown if provided configuration is not configuration of schema model.
     * @param ifNotExistsFlag IF NOT EXISTS flag, overrides same setting defined in {@link KITTY_TABLE}, set null if no override needed
     * @param conf table configuration
     * @param skipSchemaName skips {SCHEMA_NAME}. at creation statement
     * @return
     */
    public static final KittySQLiteQuery generateCreateTableStatement(Boolean ifNotExistsFlag, KittyTableConfiguration conf, boolean skipSchemaName) {
        if(!conf.isSchemaModel)
            throw new KittyRuntimeException(MessageFormat.format(
                    AME_BAD_MODEL,
                    conf.modelClass.getCanonicalName(),
                    conf.schemaName,
                    conf.tableName
            ));
        // First generate create table statement
        StringBuffer sb = new StringBuffer(512);
        sb.append(Keywords.CREATE);
        sb.append(WHITESPACE);
        if(conf.isTemporaryTable) {
            sb.append(Keywords.TEMPORARY).append(WHITESPACE);
        }
        sb.append(Keywords.TABLE);
        // Starting creation statement
        if(ifNotExistsFlag!=null)
            if (ifNotExistsFlag)
                sb.append(WHITESPACE).append(Keywords.IF_NOT_EXISTS).append(WHITESPACE);
        else
            if(conf.isCreateIfNotExists)
                sb.append(WHITESPACE).append(Keywords.IF_NOT_EXISTS).append(WHITESPACE);
        if(!conf.isTemporaryTable && !skipSchemaName) {
            sb.append(conf.schemaName);
            sb.append(DOT);
        }
        sb.append(conf.tableName);
        sb.append(WHITESPACE);
        sb.append(LEFT_BKT);
        // adding fields
        sb.append(generateCreateStatementColumnsSection(conf));
        String tableConstraints = generateTableConstraintsSection(conf);
        if(tableConstraints!=null) {
            sb.append(COMMA).append(WHITESPACE).append(tableConstraints);
        }
        sb.append(RIGHT_BKT);
        if(conf.isNoRowid) {
            sb.append(WHITESPACE).append(Keywords.WITHOUT).append(WHITESPACE).append(Keywords.ROWID);
        }
        sb.append(SEMICOLON);
        KittySQLiteQuery out = new KittySQLiteQuery(sb.toString(), null);
        return out;
    }

    /**
     * Generates table constraint section of CREATE table section.
     * @param conf
     * @return
     */
    private static String generateTableConstraintsSection(KittyTableConfiguration conf) {
        StringBuffer sb = new StringBuffer(128);
        boolean firstConstraint = true;
        if(conf.primaryKey != null) {
            sb.append(conf.primaryKey);
            firstConstraint = false;
        }
        if(conf.foreignKeys != null) {
            for(ForeignKeyTableConstraint fkt : conf.foreignKeys) {
                if(firstConstraint) {
                    sb.append(fkt);
                    firstConstraint = false;
                } else {
                    sb.append(COMMA).append(WHITESPACE).append(fkt);
                }
            }
        }
        if(conf.uniques != null) {
            for(UniqueTableConstraint utc : conf.uniques) {
                if(firstConstraint) {
                    sb.append(utc);
                    firstConstraint = false;
                } else {
                    sb.append(COMMA).append(WHITESPACE).append(utc);
                }
            }
        }
        if(conf.checks != null) {
            for(CheckTableConstraint ctc : conf.checks) {
                if(firstConstraint) {
                    sb.append(ctc);
                    firstConstraint = false;
                } else {
                    sb.append(COMMA).append(WHITESPACE).append(ctc);
                }
            }
        }
        if (sb.length()>0) return sb.toString();
        return null;
    }

    /**
     * Generates column section for specified column for crete\alter statement
     * @param columnCfg
     * @return
     */
    public static String generateCreateColumnSectionPart(KittyColumnConfiguration columnCfg) {
        StringBuffer sb = new StringBuffer(64);
        KittyColumnMainConfiguration column = columnCfg.mainConfiguration;
        sb.append(column.columnName);
        sb.append(WHITESPACE);
        sb.append(column.columnAffinity.toString());
        if(column.notNullConstraint!=null)
            sb.append(WHITESPACE).append(column.notNullConstraint);
        //if(column.autoincrement)
        //    sb.append(WHITESPACE).append(AUTOINCREMENT.toString());
        if(column.primaryKeyColumnConstraint!=null)
            sb.append(WHITESPACE).append(column.primaryKeyColumnConstraint);
        if(column.checkConstraint!=null)
            sb.append(WHITESPACE).append(column.checkConstraint);
        if(column.uniqueColumnConstraint!=null)
            sb.append(WHITESPACE).append(column.uniqueColumnConstraint);
        if(column.defaultConstraint!=null)
            sb.append(WHITESPACE).append(column.defaultConstraint);
        if(column.collationColumnConstraint!=null)
            sb.append(WHITESPACE).append(column.collationColumnConstraint);
        if(column.foreignKeyColumnConstraint!=null)
            sb.append(WHITESPACE).append(column.foreignKeyColumnConstraint);
        return sb.toString();
    }

    /**
     * Generates column sections of CREATE TABLE statement
     * @param conf
     * @return
     */
    private static String generateCreateStatementColumnsSection(KittyTableConfiguration conf) {
        StringBuffer sb = new StringBuffer(conf.sortedColumns.size() * 64);
        int endFieldCounter = conf.sortedColumns.size();
        Iterator<KittyColumnConfiguration> columnIterator = conf.sortedColumns.iterator();
        boolean firstColumn = true;
        while (columnIterator.hasNext()) {
            KittyColumnConfiguration columnCfg = columnIterator.next();

            if(firstColumn) {
                firstColumn = false;
            } else {
                sb.append(WHITESPACE);
            }
            sb.append(generateCreateColumnSectionPart(columnCfg));
            if(endFieldCounter==1) break;
            sb.append(COMMA);
            endFieldCounter--;
        }
        return sb.toString();
    }
}