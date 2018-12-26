
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

import android.util.Log;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.joins.JOIN_MANY_TO_ONE;
import net.akaish.kitty.orm.annotations.column.joins.JOIN_ONE_TO_MANY;
import net.akaish.kitty.orm.annotations.table.KITTY_TABLE;
import net.akaish.kitty.orm.annotations.table.index.INDEX;
import net.akaish.kitty.orm.annotations.table.index.INDEX_ARRAY;
import net.akaish.kitty.orm.annotations.table.constraints.CHECK_T;
import net.akaish.kitty.orm.annotations.table.constraints.CHECK_T_ARRAY;
import net.akaish.kitty.orm.annotations.table.constraints.FOREIGN_KEY_T;
import net.akaish.kitty.orm.annotations.table.constraints.FOREIGN_KEY_T_ARRAY;
import net.akaish.kitty.orm.annotations.table.constraints.PRIMARY_KEY_T;
import net.akaish.kitty.orm.annotations.table.constraints.UNIQUE_T;
import net.akaish.kitty.orm.annotations.table.constraints.UNIQUE_T_ARRAY;
import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyJoinConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfigurationBuilder;
import net.akaish.kitty.orm.constraints.table.CheckTableConstraint;
import net.akaish.kitty.orm.constraints.table.ForeignKeyTableConstraint;
import net.akaish.kitty.orm.constraints.table.PrimaryKeyTableConstraint;
import net.akaish.kitty.orm.constraints.table.UniqueTableConstraint;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.indexes.Index;
import net.akaish.kitty.orm.pkey.KittyPrimaryKey;
import net.akaish.kitty.orm.pkey.KittyPrimaryKeyBuilder;
import net.akaish.kitty.orm.pkey.KittyPrimaryKeyPart;
import net.akaish.kitty.orm.util.KittyColumnsKey;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static net.akaish.kitty.orm.util.KittyNamingUtils.generateTableNameFromRecordName;

/**
 * Utility class for generating {@link KittyTableConfiguration} from Class extends KittyModel Model
 * annotated with {@link KITTY_TABLE}
 * <br> Created by akaish on 11.02.18.
 * TODO more logging info in future
 * @author akaish (Denis Bogomolov)
 */
public class KittyAnnoTableConfigurationUtil {

	private static final String EXC_MSG_MORE_THAN_ONE_PK = "For modelClass {0} (database {1}) specified more than one PK!";

	/**
	 * Returns instance of {@link KittyTableConfiguration} from Class extends KittyModel Model
	 * annotated with {@link KITTY_TABLE}
	 * <br> Returns Null if provided class not annotated with KITTY_TABLE annotation
	 * <br> May throw {@link KittyRuntimeException} if more than one PK declaration found or no columns defined in modelClass
	 * <br> Also may throw {@link KittyRuntimeException} with reasons related with {@link KittyAnnoColumnConfigurationUtil#generateAMColumnConfiguration(Field, Class, String, String)}
	 * @param schemaName  databaseClass name for this modelClass
	 * @param modelClass modelClass class
	 * @param <T>
	 * @return
	 * @throws NoSuchMethodException - may be thrown on {@link KittyAnnoColumnConfigurationUtil#generateAMColumnConfiguration(Field, Class, String, String)}
	 */
	public static final <T extends KittyModel> KittyTableConfiguration generateTableConfiguration(Class<T> modelClass, String schemaName) throws NoSuchMethodException {
		if(modelClass.isAnnotationPresent(KITTY_TABLE.class)) {
			KITTY_TABLE tableAnno = modelClass.getAnnotation(KITTY_TABLE.class);
			String tableName = getTableName(tableAnno, modelClass);
			// Retrieving table PK constraint
			//Set of unique column names of PK, used for checking that all of pk columns
			// are presented in table
			HashSet<String> uniqueColumnNames = new HashSet<>();
			HashSet<String> tablePKColumnNames = new HashSet<>();
			List<KittyPrimaryKeyPart> pkParts = new LinkedList<>();
			PrimaryKeyTableConstraint pkTableC = null;
			KittyPrimaryKey pk = null;
			if(modelClass.isAnnotationPresent(PRIMARY_KEY_T.class)) {
				pkTableC = new PrimaryKeyTableConstraint(modelClass.getAnnotation(PRIMARY_KEY_T.class));
				tablePKColumnNames.addAll(Arrays.asList(pkTableC.getColumns()));
			}
			// Generating sorted column's configuration list
			LinkedList<KittyColumnConfiguration> sortedColumns = new LinkedList<>();
			LinkedList<KittyJoinConfiguration> joins = null;
			for(Field f : modelClass.getFields()) {
				// joins implementations to be added in future
				if(f.isAnnotationPresent(JOIN_MANY_TO_ONE.class) ||
						f.isAnnotationPresent(JOIN_ONE_TO_MANY.class) ||
						f.isAnnotationPresent(JOIN_MANY_TO_ONE.class)) {
					KittyJoinConfiguration join = getJoinConfiguration(f, modelClass);
					if(join!=null) {
						if (joins == null)
							joins = new LinkedList<>();
						joins.add(join);
					}
					continue;
				}
				KittyColumnConfiguration columnCfg = KittyAnnoColumnConfigurationUtil.generateAMColumnConfiguration(f, modelClass, schemaName, tableName);
				if(columnCfg == null) continue;
				// checking that there there is no duplication of PK definitions
				if(columnCfg.mainConfiguration.primaryKeyColumnConstraint != null) {
					if(pk!=null) {
						throw new KittyRuntimeException(
								MessageFormat.format(
										EXC_MSG_MORE_THAN_ONE_PK, modelClass.getCanonicalName(),
										schemaName));
					}
					KittyPrimaryKeyBuilder pkb = new KittyPrimaryKeyBuilder();
					pkb.setDatabaseName(schemaName)
							.setTableName(tableName)
							.setModelClass(modelClass);
					if(columnCfg.mainConfiguration.isIPK) {
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
				if(pkTableC != null) {
					if(tablePKColumnNames.contains(columnCfg.mainConfiguration.columnName)) {
						pkParts.add(new KittyPrimaryKeyPart(columnCfg.mainConfiguration.isValueGeneratedOnInsert,
								columnCfg.mainConfiguration.columnName, f, columnCfg.mainConfiguration.columnAffinity));
					}
				}
				sortedColumns.add(columnCfg);
			}
			sortedColumns = sortColumns(sortedColumns);
			// Also it is future too, indexes adding
			List<Index> indexes = getIndexesList(modelClass, schemaName, tableName);
			for(KittyColumnConfiguration kittyColumn : sortedColumns) {
				if(kittyColumn.columnIndex != null)
					indexes.add(kittyColumn.columnIndex);
			}
			if(indexes != null) {
				for (Index index : indexes)
					checkIndex(index, uniqueColumnNames, modelClass);
			}

			// working with complex pk
			if(pkTableC != null) {
				checkComplexPK(tablePKColumnNames, schemaName, tableName, modelClass);
				if(pk!=null) {
					throw new KittyRuntimeException(
							MessageFormat.format(
									EXC_MSG_MORE_THAN_ONE_PK, modelClass.getCanonicalName(),
									schemaName));
				}
				KittyPrimaryKeyBuilder pkb = new KittyPrimaryKeyBuilder();
				pkb.setDatabaseName(schemaName)
						.setTableName(tableName)
						.setModelClass(modelClass);
				for(KittyPrimaryKeyPart pkPart : pkParts) {
					pkb.addPKeyPart(pkPart.getColumnName(), pkPart);
				}
				pk = pkb.build();
			}

			// Checking things, you know
			checkJoins(joins, sortedColumns);
			checkColumnNames(uniqueColumnNames, sortedColumns, schemaName, tableName, modelClass);
			checkPK(pk, schemaName, tableName, modelClass);

			// Building table configuration
			KittyTableConfigurationBuilder confBuilder = new KittyTableConfigurationBuilder();
			confBuilder.setTableName(tableName)
					.setSchemaName(schemaName)
					.setIsTemporaryTable(tableAnno.temporaryTable())
					.setIsWithoutRowid(tableAnno.withoutRowid())
					.setIsSchemaModel(tableAnno.isSchemaTable())
					.setIsCreateOnDemand(tableAnno.isCreateOnDemand())
					.setModelClass(modelClass)
					.setKittyPrimaryKey(pk)
					.setPrimaryKey(pkTableC)
					.setForeignKeys(getFKTList(modelClass, schemaName))
					.setUniques(getUNQTList(modelClass, schemaName))
					.setChecks(getChecksTList(modelClass, schemaName))
					.setIndexes(indexes)
					.setJoins(joins)
					.setSortedColumns(sortedColumns)
					.setIsCreateIfNotExists(tableAnno.createIfNotExists())
					.setDefaultColumnsInclusionPatternId(KittyColumnsKey.generateKittyArrayKey(sortedColumns, null));

			return confBuilder.createAMTableConfiguration();
		}
		return null;
	}

	private static String AME_PK_AUTOGENFIELD_IS_PRIMITIVE = "Part of PK for {0}.{1} defined in model {2} is marked as generated on insert but corresponding field is primitive (field: {3}). It's not allowed by KittyORM.";

	/**
	 * Checks that all PK parts that marked as generated on insert are not primitives.
	 * @param pk
	 */
	private static void checkPK(KittyPrimaryKey pk, String schemaName, String tableName, Class<? extends KittyModel> modelClass) {
		if(pk==null) return;
		for(String kPartColumn : pk.getPrimaryKeyColumnNames()) {
			if(pk.getPrimaryKeyPart(kPartColumn).isColumnValueGeneratedOnInsert() && pk.getPrimaryKeyPart(kPartColumn).getField().getType().isPrimitive()) {
				throw new KittyRuntimeException(MessageFormat.format(
						AME_PK_AUTOGENFIELD_IS_PRIMITIVE,
						schemaName,
						tableName,
						modelClass.getCanonicalName(),
						pk.getPrimaryKeyPart(kPartColumn).getField().getName()
				));
			}
		}
	}

	private static String AME_INDEX_COLUMN_NOT_PRESENTED_IN_TABLE = "Index column {0} for index {1} at {2}.{3} defined in {4} not presented in table's columns list!";

	/**
	 * Checks that all index columns defined in index are defined in table
	 * @param index
	 * @param columnNames
	 */
	private static void checkIndex(Index index, Set<String> columnNames, Class<? extends KittyModel> modelClass) {
		for(String indexColumn : index.getIndexColumsSet()) {
			if(!columnNames.contains(indexColumn)) {
				throw new KittyRuntimeException(MessageFormat.format(
						AME_INDEX_COLUMN_NOT_PRESENTED_IN_TABLE,
						indexColumn,
						index.getIndexName(),
						index.getSchemaName(),
						index.getTableName(),
						modelClass.getCanonicalName()
				));
			}
		}
	}

	private static final String AME_NO_COLUMNS_FOR_PK = "No columns defined for table PRIMARY KEY constraint defined at {0}, {1}.{2}. At least one should be defined!";

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
			throw new KittyRuntimeException(MessageFormat.format(
					AME_NO_COLUMNS_FOR_PK,
					modelClass.getCanonicalName(),
					schemaName,
					tableName
			));
		}
	}

	private static final String EXC_MSG_TABLE_SHOULD_HAVE_AT_LEAST_ONE_COLUMN = "For kitty model {0} ({1}.{2}) no columns found! At least one has to be defined!";
	private static final String EXC_MSG_AMOUNT_OF_COLUMN_NAMES_AND_MODEL_FIELDS_MISMATCH = "For kitty model {0} found {1} annotated with KITTY_COLUMN fields and {2} unique column names, table {3}.{4} (those values have to be equal each other)!";

	/**
	 * Checks that amount of unique column names and amount of fields annotated with KITTY_COLUMN are same.
	 * <br> Also checks that there is at least one column defined for table.
	 * @param columnNames
	 * @param sortedColumnList
	 * @param schemaName
	 * @param tableName
	 * @param modelClass
	 */
	private static void checkColumnNames(Set<String> columnNames,
										 LinkedList<KittyColumnConfiguration> sortedColumnList,
										 String schemaName, String tableName,
										 Class<? extends KittyModel> modelClass) {
		if(columnNames.size() != sortedColumnList.size()) {
			throw new KittyRuntimeException(MessageFormat.format(
					EXC_MSG_AMOUNT_OF_COLUMN_NAMES_AND_MODEL_FIELDS_MISMATCH,
					modelClass.getCanonicalName(),
					sortedColumnList.size(),
					columnNames.size(),
					schemaName,
					tableName
			));
		}
		if(columnNames.size() == 0) {
			throw new KittyRuntimeException(MessageFormat.format(
					EXC_MSG_TABLE_SHOULD_HAVE_AT_LEAST_ONE_COLUMN,
					modelClass.getCanonicalName(),
					schemaName,
					tableName
			));
		}
	}

	/**
	 * Generates join configuration on properly annotated field
	 * // TODO implement it in future releases
	 * @param f
	 * @param model
	 * @return
	 */
	private static final KittyJoinConfiguration getJoinConfiguration(Field f, Class model) {
		return null;
	}

	/**
	 * Checks joins and throws {@link KittyRuntimeException} if some error in join generation
	 * // TODO implement it in future releases
	 * @param sortedColumns
	 */
	private static final void checkJoins(LinkedList<KittyJoinConfiguration> joins, LinkedList<KittyColumnConfiguration> sortedColumns) {
		// do nothing right now
	}

	/**
	 * Sorts provided list of {@link KittyColumnConfiguration} with usage of column order in {@link KittyColumnConfiguration#mainConfiguration}
	 * <br> Columns will be sorted in ascending order
	 * @param columnsToSort
	 * @return
	 */
	private static final LinkedList<KittyColumnConfiguration> sortColumns(LinkedList<KittyColumnConfiguration> columnsToSort) {
		if(columnsToSort==null) return null;
		Collections.sort(columnsToSort, new Comparator<KittyColumnConfiguration>() {
			@Override
			public int compare(KittyColumnConfiguration e1, KittyColumnConfiguration e2) {
				if(e1.mainConfiguration.columnOrder < e2.mainConfiguration.columnOrder){
					return -1;
				} else {
					return 1;
				}
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
	private static String getTableName(KITTY_TABLE tableAnno, Class model) {
		String tableName = null;
		if(tableAnno.tableName().length() == 0)
			tableName = generateTableNameFromRecordName(model);
		else
			tableName = tableAnno.tableName();
		return tableName;
	}

	// HELPERS FOR GETTING CONSTRAINT LISTS
	private static String AME_FKTL = "Kitty model can't be annotated with both FOREIGN_KEY_T and FOREIGN_KEY_T_ARRAY annotations at same time, model class {0}!";

	/**
	 * Generates list of {@link ForeignKeyTableConstraint} from KittyModel child class annotated with {@link FOREIGN_KEY_T} or {@link FOREIGN_KEY_T_ARRAY} annotation
	 * @param modelClass
	 * @param tableName
	 * @param <M>
	 * @return
	 */
	private static final <M extends KittyModel> List<ForeignKeyTableConstraint> getFKTList(Class<M> modelClass, String tableName) {
		boolean fkt = modelClass.isAnnotationPresent(FOREIGN_KEY_T.class);
		boolean fktA = modelClass.isAnnotationPresent(FOREIGN_KEY_T_ARRAY.class);
		if(fkt && fktA) {
			throw new KittyRuntimeException(MessageFormat.format(AME_FKTL, modelClass.getCanonicalName()));
		}
		if(fkt || fktA) {
			List<ForeignKeyTableConstraint> foreignKeyTableConstraintList = new ArrayList<>();
			if(fkt) {
				foreignKeyTableConstraintList.add(new ForeignKeyTableConstraint(modelClass.getAnnotation(FOREIGN_KEY_T.class)));
			} else {
				int counter = 0;
				for(FOREIGN_KEY_T fkAnno : modelClass.getAnnotation(FOREIGN_KEY_T_ARRAY.class).foreignKeys()) {
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

	private static String AME_INDEXQTL = "Kitty model can't be annotated with both INDEX and INDEX_ARRAY annotations at same time, model class {0}!";

	/**
	 * Generates list of {@link Index} from KittyModel child class annotated with {@link INDEX} or {@link INDEX_ARRAY} annotation
	 * @param modelClass
	 * @param schemaName
	 * @param tableName
	 * @param <M>
	 * @return
	 */
	private static final <M extends KittyModel> List<Index> getIndexesList(Class<M> modelClass, String schemaName, String tableName) {
		boolean indexA = modelClass.isAnnotationPresent(INDEX.class);
		boolean indexAA = modelClass.isAnnotationPresent(INDEX_ARRAY.class);
		if(indexA && indexAA) {
			throw new KittyRuntimeException(MessageFormat.format(AME_INDEXQTL, modelClass.getCanonicalName()));
		}
		if(indexA || indexAA) {
			List<Index> indexList = new ArrayList<>();
			if(indexA) {
				indexList.add(new Index(modelClass.getAnnotation(INDEX.class), schemaName, tableName));
			} else {
				for(INDEX indexAnno : modelClass.getAnnotation(INDEX_ARRAY.class).indexes()) {
					indexList.add(new Index(indexAnno, schemaName, tableName));
				}
			}
			return indexList;
		}
		return new ArrayList<Index>();
	}

	private static String AME_UNQTL = "Kitty model can't be annotated with both UNIQUE_T and UNIQUE_T_ARRAY annotations at same time, model class {0}!";

	/**
	 * Generates list of {@link UniqueTableConstraint} from KittyModel child class annotated with {@link UNIQUE_T} or {@link UNIQUE_T_ARRAY} annotation
	 * @param modelClass
	 * @param tableName
	 * @param <M>
	 * @return
	 */
	private static final <M extends KittyModel> List<UniqueTableConstraint> getUNQTList(Class<M> modelClass, String tableName) {
		boolean unqt = modelClass.isAnnotationPresent(UNIQUE_T.class);
		boolean unqtA = modelClass.isAnnotationPresent(UNIQUE_T_ARRAY.class);
		if(unqt && unqtA) {
			throw new KittyRuntimeException(MessageFormat.format(AME_UNQTL, modelClass.getCanonicalName()));
		}
		if(unqt || unqtA) {
			List<UniqueTableConstraint> unqTList = new ArrayList<>();
			if(unqt) {
				unqTList.add(new UniqueTableConstraint(modelClass.getAnnotation(UNIQUE_T.class)));
			} else {
				int counter = 0;
				for(UNIQUE_T unqAnno : modelClass.getAnnotation(UNIQUE_T_ARRAY.class).uniques()) {
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

	private static String AME_CHTL = "Kitty model can't be annotated with both CHECK_T and CHECK_T_ARRAY annotations at same time, model class {0}!";

	/**
	 * Generates list of {@link CheckTableConstraint} from KittyModel child class annotated with {@link CHECK_T} or {@link CHECK_T_ARRAY} annotation
	 * @param modelClass
	 * @param tableName
	 * @param <M>
	 * @return
	 */
	private static final <M extends KittyModel> List<CheckTableConstraint> getChecksTList(Class<M> modelClass, String tableName) {
		boolean cht = modelClass.isAnnotationPresent(CHECK_T.class);
		boolean chtA = modelClass.isAnnotationPresent(CHECK_T_ARRAY.class);
		if(cht && chtA) {
			throw new KittyRuntimeException(MessageFormat.format(AME_CHTL, modelClass.getCanonicalName()));
		}
		if(cht || chtA) {
			List<CheckTableConstraint> chTList = new ArrayList<>();
			if(cht) {
				chTList.add(new CheckTableConstraint(modelClass.getAnnotation(CHECK_T.class)));
			} else {
				int counter = 0;
				for(CHECK_T chAnno : modelClass.getAnnotation(CHECK_T_ARRAY.class).checks()) {
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
