
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

package net.akaish.kitty.orm.configuration.adc;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.table.KittyTable;
import net.akaish.kitty.orm.annotations.table.constraints.TableCheck;
import net.akaish.kitty.orm.annotations.table.constraints.CheckEntree;
import net.akaish.kitty.orm.annotations.table.constraints.TableForeignKey;
import net.akaish.kitty.orm.annotations.table.constraints.TableForeignKeys;
import net.akaish.kitty.orm.annotations.table.constraints.TablePrimaryKey;
import net.akaish.kitty.orm.annotations.table.constraints.TableUnique;
import net.akaish.kitty.orm.annotations.table.constraints.UniqueEntree;
import net.akaish.kitty.orm.annotations.table.index.TableIndex;
import net.akaish.kitty.orm.annotations.table.index.TableIndexes;
import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.constraints.table.CheckTableConstraint;
import net.akaish.kitty.orm.constraints.table.ForeignKeyTableConstraint;
import net.akaish.kitty.orm.constraints.table.PrimaryKeyTableConstraint;
import net.akaish.kitty.orm.constraints.table.UniqueTableConstraint;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.indexes.Index;
import net.akaish.kitty.orm.pkey.KittyPrimaryKey;
import net.akaish.kitty.orm.pkey.KittyPrimaryKeyPart;
import net.akaish.kitty.orm.util.KittyColumnsKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittyNamingUtils.generateTableNameFromRecordName;

/**
 * Utility class for generating {@link KittyTableConfiguration} from Class extends KittyModel Model
 * annotated with {@link KittyTable}
 * <br> Created by akaish on 11.02.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyAnnoTableConfigurationUtil {

	/**
	 * Returns instance of {@link KittyTableConfiguration} from Class extends KittyModel Model
	 * annotated with {@link KittyTable}
	 * <br> Returns Null if provided class not annotated with KITTY_TABLE annotation
	 * <br> May throw {@link KittyRuntimeException} if more than one PK declaration found or no columns defined in modelClass
	 * <br> Also may throw {@link KittyRuntimeException} with reasons related with {@link KittyAnnoColumnConfigurationUtil#generateAMColumnConfiguration(Field, Class, String, String)}
	 * @param schemaName  databaseClass name for this modelClass
	 * @param modelClass modelClass class
	 * @param <T>
	 * @return
	 * @throws NoSuchMethodException - may be thrown on {@link KittyAnnoColumnConfigurationUtil#generateAMColumnConfiguration(Field, Class, String, String)}
	 */
	public static <T extends KittyModel> KittyTableConfiguration generateTableConfiguration(Class<T> modelClass, String schemaName) throws NoSuchMethodException {
		KittyTable tableAnnotation = getTableDefinition(modelClass);
		if(tableAnnotation == null) return null;

		KittyTableConfiguration.Builder confBuilder = new KittyTableConfiguration.Builder();

		// Setting values from annotation: temporaryTable, rowid flag, schema table, create on demand flag and create if not exists flag
		// Also setting schema name and model class
		confBuilder.setSchemaName(schemaName)
				.setModelClass(modelClass)
				.setIsTemporaryTable(tableAnnotation.isTemporaryTable())
				.setIsWithoutRowid(tableAnnotation.withoutRowid())
				.setIsSchemaModel(tableAnnotation.isSchemaTable())
				.setIsCreateOnDemand(tableAnnotation.isCreateOnDemand())
				.setIsCreateIfNotExists(tableAnnotation.createIfNotExists());

		// Table name
		String tableName = getTableName(tableAnnotation, modelClass);
		confBuilder.setTableName(tableName);


		// Retrieving table PK constraint
		//Set of unique column names of PK, used for checking that all of pk columns
		// are presented in table
		HashSet<String> uniqueColumnNames = new HashSet<>();
		HashSet<String> tablePKColumnNames = new HashSet<>();
		List<KittyPrimaryKeyPart> pkParts = new LinkedList<>();
		PrimaryKeyTableConstraint pkTableC = null;
		KittyPrimaryKey pk = null;
		if (modelClass.isAnnotationPresent(TablePrimaryKey.class)) {
			pkTableC = new PrimaryKeyTableConstraint(modelClass.getAnnotation(TablePrimaryKey.class));
			tablePKColumnNames.addAll(Arrays.asList(pkTableC.getColumns()));
		}
		// Generating sorted column's configuration list
		LinkedList<KittyColumnConfiguration> sortedColumns = new LinkedList<>();
		for (Field f : modelClass.getFields()) {
			// joins implementations to be added in future
			KittyColumnConfiguration columnCfg = KittyAnnoColumnConfigurationUtil.generateAMColumnConfiguration(f, modelClass, schemaName, tableName);
			if (columnCfg == null) continue;
			// checking that there there is no duplication of PK definitions
			if (columnCfg.mainConfiguration.primaryKeyColumnConstraint != null) {
				if (pk != null) {
					throw new KittyRuntimeException(
							format("For modelClass {0} (database {1}) specified more than one PK!",
									modelClass.getCanonicalName(),
									schemaName));
				}
				KittyPrimaryKey.Builder pkb = new KittyPrimaryKey.Builder();
				pkb.setDatabaseName(schemaName)
						.setTableName(tableName)
						.setModelClass(modelClass);
				if (columnCfg.mainConfiguration.isIPK) {
					pkb.addPKeyPart(columnCfg.mainConfiguration.columnName,
							new KittyPrimaryKeyPart(true,
									columnCfg.mainConfiguration.columnName, f,
									columnCfg.mainConfiguration.columnAffinity));
				} else {
					pkb.addPKeyPart(columnCfg.mainConfiguration.columnName,
							new KittyPrimaryKeyPart(columnCfg.mainConfiguration.isValueGeneratedOnInsert,
									columnCfg.mainConfiguration.columnName, f,
									columnCfg.mainConfiguration.columnAffinity));
				}
				pk = pkb.build();

			}
			// adding table name to unique table name's collection
			uniqueColumnNames.add(columnCfg.mainConfiguration.columnName);
			// setting complex PK collection
			if (pkTableC != null) {
				if (tablePKColumnNames.contains(columnCfg.mainConfiguration.columnName)) {
					pkParts.add(new KittyPrimaryKeyPart(columnCfg.mainConfiguration.isValueGeneratedOnInsert,
							columnCfg.mainConfiguration.columnName, f, columnCfg.mainConfiguration.columnAffinity));
				}
			}
			sortedColumns.add(columnCfg);
		}
		sortedColumns = sortColumns(sortedColumns);
		// Also it is future too, indexes adding
		List<Index> indexes = getIndexesList(modelClass, schemaName, tableName);
		for (KittyColumnConfiguration kittyColumn : sortedColumns) {
			if (kittyColumn.columnIndex != null)
				indexes.add(kittyColumn.columnIndex);
		}
		for (Index index : indexes) checkIndex(index, uniqueColumnNames, modelClass);

		// working with complex pk
		if (pkTableC != null) {
			checkComplexPK(tablePKColumnNames, schemaName, tableName, modelClass);
			if (pk != null) {
				throw new KittyRuntimeException(
						format("For modelClass {0} (database {1}) specified more than one PK!",
								modelClass.getCanonicalName(),
								schemaName));
			}
			KittyPrimaryKey.Builder pkb = new KittyPrimaryKey.Builder();
			pkb.setDatabaseName(schemaName)
					.setTableName(tableName)
					.setModelClass(modelClass);
			for (KittyPrimaryKeyPart pkPart : pkParts) {
				pkb.addPKeyPart(pkPart.getColumnName(), pkPart);
			}
			pk = pkb.build();
		}

		// Checking things, you know
		validateColumnNames(uniqueColumnNames, sortedColumns, schemaName, tableName, modelClass);
		checkPK(pk, schemaName, tableName, modelClass);

		// Building table configuration

		confBuilder.setKittyPrimaryKey(pk)
				.setPrimaryKey(pkTableC)
				.setForeignKeys(getFKTList(modelClass, schemaName))
				.setUniques(getUNQTList(modelClass, schemaName))
				.setChecks(getChecksTList(modelClass, schemaName))
				.setIndexes(indexes)
				.setSortedColumns(sortedColumns)
				.setDefaultColumnsInclusionPatternId(KittyColumnsKey.generateKittyArrayKey(sortedColumns, null));

		return confBuilder.build();
	}

	/**
	 * @return instance of KittyTable annotation if provided class annotated with it
	 */
	private static <T extends KittyModel> KittyTable getTableDefinition(Class<T> modelClass) {
		if(modelClass.isAnnotationPresent(KittyTable.class)) return modelClass.getAnnotation(KittyTable.class);
		else return null;
	}

	/**
	 * Checks that all PK parts that marked as generated on insert are not primitives.
	 * @param pk
	 */
	private static void checkPK(KittyPrimaryKey pk, String schemaName, String tableName, Class<? extends KittyModel> modelClass) {
		if(pk==null) return;
		for(String kPartColumn : pk.getPrimaryKeyColumnNames()) {
			if(pk.getPrimaryKeyPart(kPartColumn).isColumnValueGeneratedOnInsert() && pk.getPrimaryKeyPart(kPartColumn).getField().getType().isPrimitive()) {
				throw new KittyRuntimeException(format(
						"Part of PK for {0}.{1} defined in model {2} is marked as generated on insert but corresponding field is primitive (field: {3}). It's not allowed by KittyORM.",
						schemaName,
						tableName,
						modelClass.getCanonicalName(),
						pk.getPrimaryKeyPart(kPartColumn).getField().getName()
				));
			}
		}
	}

	/**
	 * Checks that all index columns defined in index are defined in table
	 * @param index
	 * @param columnNames
	 */
	private static void checkIndex(Index index, Set<String> columnNames, Class<? extends KittyModel> modelClass) {
		for(String indexColumn : index.getIndexColumsSet()) {
			if(!columnNames.contains(indexColumn)) {
				throw new KittyRuntimeException(format(
						"Index column {0} for index {1} at {2}.{3} defined in {4} not presented in table's columns list!",
						indexColumn,
						index.getIndexName(),
						index.getSchemaName(),
						index.getTableName(),
						modelClass.getCanonicalName()
				));
			}
		}
	}

	/**
	 * Checks that at least one column defined for complex PK
	 * @param pkColumnNames
	 * @param schemaName
	 * @param tableName
	 * @param modelClass
	 */
	private static void checkComplexPK(Set<String> pkColumnNames,
									   String schemaName, String tableName, Class<? extends KittyModel> modelClass) {
		if(pkColumnNames.size() == 0) {
			throw new KittyRuntimeException(format(
					"No columns defined for table PRIMARY KEY constraint defined at {0}, {1}.{2}. At least one should be defined!",
					modelClass.getCanonicalName(),
					schemaName,
					tableName
			));
		}
	}


	/**
	 * Checks that amount of unique column names and amount of fields annotated with KITTY_COLUMN are same.
	 * <br> Also checks that there is at least one column defined for table.
	 * @param columnNames
	 * @param sortedColumnList
	 * @param schemaName
	 * @param tableName
	 * @param modelClass
	 */
	private static void validateColumnNames(Set<String> columnNames,
											LinkedList<KittyColumnConfiguration> sortedColumnList,
											String schemaName, String tableName,
											Class<? extends KittyModel> modelClass) {
		if(columnNames.size() != sortedColumnList.size()) {
			throw new KittyRuntimeException(format(
					"For kitty model {0} found {1} annotated with Column fields and {2} unique column names, table {3}.{4} (those values have to be equal each other)!",
					modelClass.getCanonicalName(),
					sortedColumnList.size(),
					columnNames.size(),
					schemaName,
					tableName
			));
		}
		if(columnNames.size() == 0) {
			throw new KittyRuntimeException(format(
					"For kitty model {0} ({1}.{2}) no columns found! At least one has to be defined!",
					modelClass.getCanonicalName(),
					schemaName,
					tableName
			));
		}
	}

	/**
	 * Sorts provided list of {@link KittyColumnConfiguration} with usage of column order in {@link KittyColumnConfiguration#mainConfiguration}
	 * <br> Columns will be sorted in ascending order
	 * @return sorted accordingly list of KittyColumnConfiguration
	 */
	private static LinkedList<KittyColumnConfiguration> sortColumns(LinkedList<KittyColumnConfiguration> columnsToSort) {
		if(columnsToSort==null) return null;
		Collections.sort(columnsToSort, new Comparator<KittyColumnConfiguration>() {
			@Override public int compare(KittyColumnConfiguration e1, KittyColumnConfiguration e2) {
				if(e1.mainConfiguration.columnOrder < e2.mainConfiguration.columnOrder) return -1;
				else if(e1.mainConfiguration.columnOrder == e2.mainConfiguration.columnOrder) return 0;
				else return 1;
			}
		});
		return columnsToSort;
	}


	/**
	 * Returns table name for this modelClass from if specified, otherwise
	 * returns name generated with {@link net.akaish.kitty.orm.util.KittyNamingUtils#generateTableNameFromRecordName(Class)} method.
	 * @param tableAnno table annotation
	 * @param model modelClass class
	 * @return table name for specified modelClass class
	 */
	private static String getTableName(KittyTable tableAnno, Class model) {
		if(tableAnno.name().isEmpty()) return generateTableNameFromRecordName(model);
		else return tableAnno.name();
	}

	// HELPERS FOR GETTING CONSTRAINT LISTS

	/**
	 * @return list of {@link ForeignKeyTableConstraint} from KittyModel child class annotated with {@link TableForeignKey} or {@link TableForeignKeys} annotation
	 */
	private static <M extends KittyModel> List<ForeignKeyTableConstraint> getFKTList(Class<M> modelClass, String tableName) {
		boolean fkt = modelClass.isAnnotationPresent(TableForeignKey.class);
		boolean fktA = modelClass.isAnnotationPresent(TableForeignKeys.class);
		if(fkt && fktA) {
			throw new KittyRuntimeException(format(
					"Kitty model can't be annotated with both TableForeignKey and TableForeignKeys annotations at same time, model class {0}!",
					modelClass.getCanonicalName()));
		}
		if(fkt || fktA) {
			List<ForeignKeyTableConstraint> foreignKeyTableConstraintList = new ArrayList<>();
			if(fkt) {
				foreignKeyTableConstraintList.add(new ForeignKeyTableConstraint(modelClass.getAnnotation(TableForeignKey.class)));
			} else {
				int counter = 0;
				for(TableForeignKey fkAnno : modelClass.getAnnotation(TableForeignKeys.class).foreignKeys()) {
					counter++;
					if(fkAnno.name().length() != 0)
						foreignKeyTableConstraintList.add(new ForeignKeyTableConstraint(fkAnno));
					else {
						ForeignKeyTableConstraint fktc = new ForeignKeyTableConstraint(fkAnno);
						fktc.setCounterForAutoGenName(counter);
						fktc.setTableNameForAutoGenName(tableName);
						foreignKeyTableConstraintList.add(fktc);
					}
				}
			}
			return foreignKeyTableConstraintList;
		}
		return null;
	}

	/**
	 * @return list of {@link Index} from KittyModel child class annotated with {@link TableIndex} or {@link TableIndexes} annotation
	 */
	private static <M extends KittyModel> List<Index> getIndexesList(Class<M> modelClass, String schemaName, String tableName) {
		boolean indexA = modelClass.isAnnotationPresent(TableIndex.class);
		boolean indexAA = modelClass.isAnnotationPresent(TableIndexes.class);
		if(indexA && indexAA) {
			throw new KittyRuntimeException(format("Kitty model can't be annotated with both TableIndex and TableIndexes annotations at same time, model class {0}!",
					modelClass.getCanonicalName()));
		}
		if(indexA || indexAA) {
			List<Index> indexList = new ArrayList<>();
			if(indexA) {
				indexList.add(new Index(modelClass.getAnnotation(TableIndex.class), schemaName, tableName));
			} else {
				for(TableIndex tableIndexAnno : modelClass.getAnnotation(TableIndexes.class).indexes()) {
					indexList.add(new Index(tableIndexAnno, schemaName, tableName));
				}
			}
			return indexList;
		}
		return new ArrayList<>();
	}

	/**
	 * @return list of {@link UniqueTableConstraint} from KittyModel child class annotated with {@link TableUnique} or {@link UniqueEntree} annotation
	 */
	private static <M extends KittyModel> List<UniqueTableConstraint> getUNQTList(Class<M> modelClass, String tableName) {
		boolean unqt = modelClass.isAnnotationPresent(TableUnique.class);
		boolean unqtA = modelClass.isAnnotationPresent(UniqueEntree.class);
		if(unqt && unqtA) {
			throw new KittyRuntimeException(format(
					"Kitty model can't be annotated with both TableUnique and UniqueEntree annotations at same time, model class {0}!",
					modelClass.getCanonicalName()));
		}
		if(unqt || unqtA) {
			List<UniqueTableConstraint> unqTList = new ArrayList<>();
			if(unqt) {
				unqTList.add(new UniqueTableConstraint(modelClass.getAnnotation(TableUnique.class)));
			} else {
				int counter = 0;
				for(TableUnique unqAnno : modelClass.getAnnotation(UniqueEntree.class).uniques()) {
					counter++;
					if(unqAnno.name().length() != 0)
						unqTList.add(new UniqueTableConstraint(unqAnno));
					else {
						UniqueTableConstraint unqtc = new UniqueTableConstraint(unqAnno);
						unqtc.setCounterForAutoGenName(counter);
						unqtc.setTableNameForAutoGenName(tableName);
						unqTList.add(unqtc);
					}
				}
			}
			return unqTList;
		}
		return null;
	}

	/**
	 * Generates list of {@link CheckTableConstraint} from KittyModel child class annotated with {@link CheckEntree} or {@link TableCheck} annotation
	 * @param modelClass
	 * @param tableName
	 * @param <M>
	 * @return
	 */
	private static <M extends KittyModel> List<CheckTableConstraint> getChecksTList(Class<M> modelClass, String tableName) {
		boolean cht = modelClass.isAnnotationPresent(CheckEntree.class);
		boolean chtA = modelClass.isAnnotationPresent(TableCheck.class);
		if(cht && chtA) {
			throw new KittyRuntimeException(format(
					"Kitty model can't be annotated with both TableCheck and CheckEntree annotations at same time, model class {0}!",
					modelClass.getCanonicalName()));
		}
		if(cht || chtA) {
			List<CheckTableConstraint> chTList = new ArrayList<>();
			if(cht) {
				chTList.add(new CheckTableConstraint(modelClass.getAnnotation(CheckEntree.class)));
			} else {
				int counter = 0;
				for(CheckEntree chAnno : modelClass.getAnnotation(TableCheck.class).checks()) {
					counter++;
					if(chAnno.name().length() != 0)
						chTList.add(new CheckTableConstraint(chAnno));
					else {
						CheckTableConstraint chtc = new CheckTableConstraint(chAnno);
						chtc.setCounterForAutoGenName(counter);
						chtc.setTableNameForAutoGenName(tableName);
						chTList.add(chtc);
					}
				}
			}
			return chTList;
		}
		return null;
	}
}
