
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
 * Configuration DAO for table configuration
 * Created by akaish on 11.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyTableConfiguration {

	public final String schemaName;
	public final String tableName;

	public final boolean isTemporaryTable;
	public final boolean isNoRowid;
	public final boolean isCreateIfNotExists;

	public final PrimaryKeyTableConstraint primaryKey;
	public final List<UniqueTableConstraint> uniques;
	public final List<CheckTableConstraint> checks;
	public final List<ForeignKeyTableConstraint> foreignKeys;

	public final boolean isSchemaModel;
	public final boolean isCreateOnDemand;

	public final List<Index> indexes;

	public final LinkedList<KittyColumnConfiguration> sortedColumns;
	public final Class<? extends KittyModel> modelClass;
	public final KittyPrimaryKey kittyPrimaryKey;

	public final KittyArrayKey defaultColumnsInclusionPatternId;

	public KittyTableConfiguration(String schemaName, String tableName,
														  boolean isTemporaryTable, boolean isNoRowid,
														  PrimaryKeyTableConstraint primaryKey,
														  List<UniqueTableConstraint> uniques,
														  List<CheckTableConstraint> checks,
														  List<ForeignKeyTableConstraint> foreignKeys,
														  boolean isSchemaModel, boolean isCreateOnDemand,
														  List<Index> indexes,
														  LinkedList<KittyColumnConfiguration> sortedColumns,
														  Class<? extends KittyModel> modelClass, KittyPrimaryKey kittyPrimaryKey,
														  boolean isCreateIfNotExists,
														  KittyArrayKey defaultColumnsInclusionPatternId) {
		if(modelClass == null) throw new NullPointerException("No model class present");
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.isTemporaryTable = isTemporaryTable;
		this.isNoRowid = isNoRowid;
		this.primaryKey = primaryKey;
		this.uniques = uniques;
		this.checks = checks;
		this.foreignKeys = foreignKeys;
		this.isSchemaModel = isSchemaModel;
		this.isCreateOnDemand = isCreateOnDemand;
		this.indexes = indexes;
		this.sortedColumns = sortedColumns;
		this.modelClass = modelClass;
		this.kittyPrimaryKey = kittyPrimaryKey;
		this.isCreateIfNotExists = isCreateIfNotExists;
		this.defaultColumnsInclusionPatternId = defaultColumnsInclusionPatternId;
	}

	public static class Builder {
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
		private LinkedList<KittyColumnConfiguration> sortedColumns = null;
		private Class<? extends KittyModel> modelClass = null;
		private KittyPrimaryKey kittyPrimaryKey = null;
		private boolean isCreateIfNotExists = false;
		private KittyArrayKey defaultColumnsInclusionPatternId = null;

		public Builder setSchemaName(String schemaName) {
			this.schemaName = schemaName;
			return this;
		}

		public Builder setDefaultColumnsInclusionPatternId(KittyArrayKey defaultColumnsInclusionPatternId) {
			this.defaultColumnsInclusionPatternId = defaultColumnsInclusionPatternId;
			return this;
		}

		public Builder setIsCreateIfNotExists(boolean isCreateIfNotExists) {
			this.isCreateIfNotExists = isCreateIfNotExists;
			return this;
		}

		public Builder setTableName(String tableName) {
			this.tableName = tableName;
			return this;
		}

		public Builder setIsTemporaryTable(boolean isTemporaryTable) {
			this.isTemporaryTable = isTemporaryTable;
			return this;
		}

		public Builder setIsWithoutRowid(boolean isNoRowid) {
			this.isWithoutRowid = isNoRowid;
			return this;
		}

		public Builder setPrimaryKey(PrimaryKeyTableConstraint primaryKey) {
			this.primaryKey = primaryKey;
			return this;
		}

		public Builder setUniques(List<UniqueTableConstraint> uniques) {
			this.uniques = uniques;
			return this;
		}

		public Builder setChecks(List<CheckTableConstraint> checks) {
			this.checks = checks;
			return this;
		}

		public Builder setForeignKeys(List<ForeignKeyTableConstraint> foreignKeys) {
			this.foreignKeys = foreignKeys;
			return this;
		}

		public Builder setIsSchemaModel(boolean isSchemaModel) {
			this.isSchemaModel = isSchemaModel;
			return this;
		}

		public Builder setIsCreateOnDemand(boolean isCreateOnDemand) {
			this.isCreateOnDemand = isCreateOnDemand;
			return this;
		}

		public Builder setIndexes(List<Index> indexes) {
			this.indexes = indexes;
			return this;
		}

		public Builder setSortedColumns(LinkedList<KittyColumnConfiguration> sortedColumns) {
			this.sortedColumns = sortedColumns;
			return this;
		}

		public <T extends KittyModel> Builder setModelClass(Class<T> modelClass) {
			this.modelClass = modelClass;
			return this;
		}

		public Builder setKittyPrimaryKey(KittyPrimaryKey kittyPrimaryKey) {
			this.kittyPrimaryKey = kittyPrimaryKey;
			return this;
		}

		public KittyTableConfiguration build() {
			return new KittyTableConfiguration(schemaName, tableName, isTemporaryTable, isWithoutRowid,
					primaryKey, uniques, checks, foreignKeys, isSchemaModel, isCreateOnDemand,
					indexes, sortedColumns, modelClass, kittyPrimaryKey, isCreateIfNotExists,
					defaultColumnsInclusionPatternId);
		}
	}
}
