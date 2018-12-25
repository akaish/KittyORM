
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

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.constraints.table.CheckTableConstraint;
import net.akaish.kitty.orm.constraints.table.ForeignKeyTableConstraint;
import net.akaish.kitty.orm.constraints.table.PrimaryKeyTableConstraint;
import net.akaish.kitty.orm.constraints.table.UniqueTableConstraint;
import net.akaish.kitty.orm.indexes.Index;
import net.akaish.kitty.orm.pkey.KittyPrimaryKey;
import net.akaish.kitty.orm.util.KittyArrayKey;

import java.util.LinkedList;
import java.util.List;

/**
 * @author akaish (Denis Bogomolov)
 */
public class KittyTableConfigurationBuilder {
    private String schemaName = null;
    private String tableName = null;
    private boolean isTemporaryTable = false;
    private boolean isWithoutRowid = false;
    private PrimaryKeyTableConstraint primaryKey = null;
    private List<UniqueTableConstraint> uniques = null;
    private List<CheckTableConstraint> checks = null;
    private List<ForeignKeyTableConstraint> foreignKeys = null;
    private boolean isSchemaModel = true;
    private boolean isCreateOnDemand = false;
    private List<Index> indexes = null;
    private LinkedList<KittyJoinConfiguration> joins = null;
    private LinkedList<KittyColumnConfiguration> sortedColumns = null;
    private Class<? extends KittyModel> modelClass = null;
    private KittyPrimaryKey kittyPrimaryKey = null;
    private boolean isCreateIfNotExists = false;
    private KittyArrayKey defaultColumnsInclusionPatternId = null;

    public KittyTableConfigurationBuilder setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    public KittyTableConfigurationBuilder setDefaultColumnsInclusionPatternId(KittyArrayKey defaultColumnsInclusionPatternId) {
        this.defaultColumnsInclusionPatternId = defaultColumnsInclusionPatternId;
        return this;
    }

    public KittyTableConfigurationBuilder setIsCreateIfNotExists(boolean isCreateIfNotExists) {
        this.isCreateIfNotExists = isCreateIfNotExists;
        return this;
    }

    public KittyTableConfigurationBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public KittyTableConfigurationBuilder setIsTemporaryTable(boolean isTemporaryTable) {
        this.isTemporaryTable = isTemporaryTable;
        return this;
    }

    public KittyTableConfigurationBuilder setIsWithoutRowid(boolean isNoRowid) {
        this.isWithoutRowid = isNoRowid;
        return this;
    }

    public KittyTableConfigurationBuilder setPrimaryKey(PrimaryKeyTableConstraint primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public KittyTableConfigurationBuilder setUniques(List<UniqueTableConstraint> uniques) {
        this.uniques = uniques;
        return this;
    }

    public KittyTableConfigurationBuilder setChecks(List<CheckTableConstraint> checks) {
        this.checks = checks;
        return this;
    }

    public KittyTableConfigurationBuilder setForeignKeys(List<ForeignKeyTableConstraint> foreignKeys) {
        this.foreignKeys = foreignKeys;
        return this;
    }

    public KittyTableConfigurationBuilder setIsSchemaModel(boolean isSchemaModel) {
        this.isSchemaModel = isSchemaModel;
        return this;
    }

    public KittyTableConfigurationBuilder setIsCreateOnDemand(boolean isCreateOnDemand) {
        this.isCreateOnDemand = isCreateOnDemand;
        return this;
    }

    public KittyTableConfigurationBuilder setIndexes(List<Index> indexes) {
        this.indexes = indexes;
        return this;
    }

    public KittyTableConfigurationBuilder setJoins(LinkedList<KittyJoinConfiguration> joins) {
        this.joins = joins;
        return this;
    }

    public KittyTableConfigurationBuilder setSortedColumns(LinkedList<KittyColumnConfiguration> sortedColumns) {
        this.sortedColumns = sortedColumns;
        return this;
    }

    public <T extends KittyModel> KittyTableConfigurationBuilder setModelClass(Class<T> modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    public KittyTableConfigurationBuilder setKittyPrimaryKey(KittyPrimaryKey kittyPrimaryKey) {
        this.kittyPrimaryKey = kittyPrimaryKey;
        return this;
    }

    public KittyTableConfiguration createAMTableConfiguration() {
        return new KittyTableConfiguration(schemaName, tableName, isTemporaryTable, isWithoutRowid,
                primaryKey, uniques, checks, foreignKeys, isSchemaModel, isCreateOnDemand,
                indexes, joins, sortedColumns, modelClass, kittyPrimaryKey, isCreateIfNotExists,
                defaultColumnsInclusionPatternId);
    }
}