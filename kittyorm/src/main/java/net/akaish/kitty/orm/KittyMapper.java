
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

package net.akaish.kitty.orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;

import net.akaish.kitty.orm.configuration.conf.KittyColumnConfiguration;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.query.KittySQLiteQuery;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.query.conditions.SQLiteOperator;
import net.akaish.kitty.orm.query.precompiled.InsertValuesStatement;
import net.akaish.kitty.orm.util.KittyArrayKey;
import net.akaish.kitty.orm.util.KittyColumnsKey;
import net.akaish.kitty.orm.util.KittyReflectionUtils;
import net.akaish.kitty.orm.pkey.KittyPrimaryKey;
import net.akaish.kitty.orm.query.QueryParameters;
import net.akaish.kitty.orm.query.KittyQueryBuilder;
import net.akaish.kitty.orm.util.KittyUtils;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.CVUtils.modelToCV;
import static net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder.MODEL_FIELD_END;
import static net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder.MODEL_FIELD_START;
import static net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder.WHERE;
import static net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder.WHSP_STR;
import static net.akaish.kitty.orm.util.KittyConstants.DEFAULT_LOG_TAG;
import static net.akaish.kitty.orm.util.KittyConstants.EMPTY_STRING;
import static net.akaish.kitty.orm.util.KittyConstants.EQUAL_SIGN;
import static net.akaish.kitty.orm.util.KittyConstants.LEFT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.RIGHT_BKT;
import static net.akaish.kitty.orm.util.KittyConstants.ROWID;
import static net.akaish.kitty.orm.util.KittyConstants.SEMICOLON;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;

/**
 * Kitty Data Mapper class
 * @author akaish (Denis Bogomolov)
 */
public class KittyMapper implements Cloneable {

	//findWhere(SQLiteCondition where, QueryParameters qParams)
	private static String AME_FW_EXCEPTION = "Exception caught on KittyMapper#findWhere(where, queryParameters), see nested exception details for more info!";

	// findByRowID(Long rowid)
	static private String AME_FBRID_NULL_CONDITION = "Unable to get RowID condition for KittyMapper.findByRowID(rowid={0}) at {1} with model {2}!";
	static private String AME_FBRID_MORE_THAN_ONE_VALUE = "More than one value found for KittyMapper.findByRowID(rowid={0}) at {1} with model {2}!";

	// find bp PKV, PK, IPK
	private static String AME_FBPK_MORE_THAN_ONE_RESULT = "More than one value found in DB for table {0}.{1} by PK!";
	static private String AME_PKV_MORE_THAN_ONE_VALUE = "More than one value found for KittyMapper.findByPKV(pkv={0}) at {1} with model {2}!";
	static private String AME_IPK_MORE_THAN_ONE_VALUE = "More than one value found for KittyMapper.findByIPK(ipk={0}) at {1} with model {2}!";
	private static String AME_FBIPK_NOT_IPK = "Unable to fetch data from {0}.{1} because this table has no IPK (PK is complex or not defined)!";

	// cloning
	private static String AME_BMI_ERROR = "Some error of creation new instance of model class: {0} ; see nested exception details!";
	private static String AME_UNBLE_TO_CLONE = "Unable to clone myself (KittyMapper) at {0} instance, see nested exception details!";

	// TX
	private static String AME_TX_FINISH_EXCEPTION = "Caught exception while trying to finish started transaction in mapper {0}. See nested exception for details!";
	private static String AME_UTSTAIT = "Unable to start transaction in data mapper {0} with model {1}. Already in transaction!";
	private static String AME_ROWITX = "Unable to perform read operation while in transaction. Mapper: {0} ; model: {1} ; database: {2} ; table : {3}";

	private static String AME_INS_EXC = "Exception caught on KittyMapper#insert(model) ({0}.{1}), see nested exception details for more info!";
	private static String AME_UPD_EXC = "Exception caught on KittyMapper#update(model, ?) ({0}.{1}), see nested exception details for more info!";
	private static String AME_GPKVFM_EX = "Exception caught on KittyMapper#getPrimaryKeyValuesForModel for instance of model: {0}. See nested exception details for more info!";
	private static String AME_UTU_NO_INFO = "For KittyMapper#update(model, condition) method it is needed at least rowid specified or pk existence or passing not null condition. Model: {0}";
	static private String DEL_MNAME_DELETE = "KittyMapper.delete(model)";
	static private String DEL_MNAME_DELETEPK = "KittyMapper.deleteByPK(pk)";
	static private String DEL_MNAME_DELETEIPK = "KittyMapper.deleteByIPK(id)";
	static private String DEL_MNAME_DELETE_ROWID = "KittyMapper.deleteByRowID(rowid)";
	static private String AME_DEL_UNABLE_TO_CREATE_CONDITION = "{0} : unable to create delete condition for {1}!";

	/**
	 * Transaction modes
	 */
	public enum TRANSACTION_MODES {

		/**
		 * Default transaction mode, starts transaction in EXCLUSIVE MODE
		 */
		EXCLUSIVE_MODE,

		/**
		 * Transaction mode, starts transaction in IMMEDIATE mode, api level 11 and higher
		 */
		NON_EXCLUSIVE_MODE,

		/**
		 * Transaction mode, sets locking disabled, deprecated in api level 16
		 */
		LOCKING_FALSE_MODE
	}
	
	/**
	 * Condition builder
	 */
	protected final SQLiteConditionBuilder conditionBuilder = new SQLiteConditionBuilder();

	protected final List<String> insertPKExclusions;

	/**
	 * Table configuration
	 */
	protected final KittyTableConfiguration tableConfig;

	/**
	 * Cursor to model factory
	 */
	protected final KittyModelCVFactory cursorToModelFactory;

	/**
	 * Blank model to be used with this mapper
	 */
	protected final KittyModel blankModelInstance;

	protected KittyDatabaseHelper databaseHelper;

	protected final String databasePassword;

	protected SQLiteDatabase database;

	protected boolean rowidOn = true;

	protected boolean logOn = false;
	protected String  logTag = DEFAULT_LOG_TAG;

	protected boolean returnNullNotEmptyCollection = false;

	protected final HashMap<KittyArrayKey, InsertValuesStatement> precompiledInserts = new HashMap<>();
	protected final HashMap<String, KittyColumnConfiguration> columnConfigurations = new HashMap<>();

	//protected SQLiteStatement insertStatement;

	public <M extends KittyModel> KittyMapper(KittyTableConfiguration tableConfiguration,
											  M blankModelInstance,
											  String databasePassword) {
		this.tableConfig = tableConfiguration;
		this.cursorToModelFactory = new KittyModelCVFactory(tableConfiguration);
		this.blankModelInstance = blankModelInstance;
		this.databasePassword = databasePassword;
		this.insertPKExclusions = generateInsertionExclusionsForPK(tableConfiguration);
	}

	private List<String> generateInsertionExclusionsForPK(KittyTableConfiguration tableConfiguration) {
		List<String> exclusions = new ArrayList<>();
		Iterator<KittyColumnConfiguration> iterator = tableConfiguration.sortedColumns.iterator();
		while(iterator.hasNext()) {
			KittyColumnConfiguration cfg = iterator.next();
			if(cfg.mainConfiguration.isValueGeneratedOnInsert) {
				exclusions.add(cfg.mainConfiguration.columnField.getName());
			}
		}
		return exclusions;
	}

	public final <H extends KittyDatabaseHelper> KittyMapper setDatabaseHelper(H helper) {
		databaseHelper = helper;
		return this;
	}

	/**
	 * Defines what would be returned by fetch methods in cases when nothing found in
	 * database table. So, if passed true than if result set for select query contains no
	 * rows than NULL would be returned. Otherwise (false passed as parameter) empty collection
	 * would be returned in such cases.
	 * <br> By default it is false
	 * @param returnNullNotEmptyCollection
	 */
	public final void setReturnNullNotEmptyCollection(boolean returnNullNotEmptyCollection) {
		this.returnNullNotEmptyCollection = returnNullNotEmptyCollection;
	}

	public final void setLogOn(boolean logOn) {
		this.logOn = logOn;
	}

	private static String IAE_LTAG_LENGTH = "Log tag length can't be more than 23 digits!";

	public final void setLogTag(String logTag) {
		if(logTag.length() > 23)
			throw new IllegalArgumentException(IAE_LTAG_LENGTH);
		this.logTag = logTag;
	}

	/**
	 * Sets rowid support. That means that each SELECT query would for kitty model would be like
	 * SELECT rowid, * and each model selected from database table would have
	 * {@link KittyModel#rowid} value set. Also, if rowid not null that means
	 * that all operations with current models already fetched from db (delete\\update)
	 * would be done through rowid (that faster than fecthing PK values via reflection)
	 * @param rowIDSupport flag if rowid on or off (<b>by default rowid is ON</b>)
	 */
	public final void setRowIDSupport(boolean rowIDSupport) {
		this.rowidOn = rowIDSupport;
	}

	/**
	 * Opens current db
	 */
	public void getWritableDatabase() {
		if(databasePassword == null) {
			database = databaseHelper.getWritableDatabase();
		} else {
			database = databaseHelper.getWritableDatabase(databasePassword);
		}
	}

	/**
	 * Opens current encrypted db with provided password
	 * @param pwd
	 */
	public void getWritableDatabase(String pwd) {
		database = databaseHelper.getWritableDatabase(pwd);
	}

	/**
	 * Closes current db 
	 */
	public void close() {
		database.close();
	}

	// TRANSACTIONS

	/**
	 * Starts transaction on current database instance. That means that all write operations on DB would be accumulated
	 * to buffer and written to DB only after ending transaction ({@link #finishTransaction()}).
	 * <br> While in transaction, only insert, update, save and create methods are available.
	 * Calling read methods while in transaction would cause {@link KittyRuntimeException}.
	 * @param mode mode to start with, following modes are available:
	 *             <br>
	 *                 {@link TRANSACTION_MODES#EXCLUSIVE_MODE} - starting with this mode would start
	 *             transaction in <b>exclusive</b> mode ({@link SQLiteDatabase#beginTransaction()})
	 *
	 *             <br>
	 *                 {@link TRANSACTION_MODES#NON_EXCLUSIVE_MODE} - starting with this mode would start
	 *             transaction in <b>immediate</b> mode, this mode requires <b>api 11</b> and higher
	 *            ({@link SQLiteDatabase#beginTransactionNonExclusive()}). If current api level lower than
	 *            api 11 than transaction would be started in {@link TRANSACTION_MODES#EXCLUSIVE_MODE}
	 *
	 *             <br>
	 *                 {@link TRANSACTION_MODES#LOCKING_FALSE_MODE} - starting transaction with false
	 *             locking option ({@link SQLiteDatabase#setLockingEnabled(boolean)}).
	 *
	 * @throws KittyRuntimeException if trying to start transaction when transaction already started
	 */
	public final void startTransaction(TRANSACTION_MODES mode) {
		if(database.inTransaction())
			throw new KittyRuntimeException(format(AME_UTSTAIT, getClass().getCanonicalName(),
					blankModelInstance.getClass().getCanonicalName()));
		switch (mode) {
			case EXCLUSIVE_MODE:
				database.beginTransaction();
				break;
			case NON_EXCLUSIVE_MODE:
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					database.beginTransactionNonExclusive();
				} else {
					database.beginTransaction();
				}
				break;
			case LOCKING_FALSE_MODE:
				database.beginTransaction();
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					database.setLockingEnabled(false);
				}
				break;
			default:
				database.beginTransaction();
				break;
		}
	}

	protected <M extends KittyModel> InsertValuesStatement getInsertStatement(M model) {
		if(model.exclusions.size() == 0) {
			if(precompiledInserts.containsKey(tableConfig.defaultColumnsInclusionPatternId))
				return precompiledInserts.get(tableConfig.defaultColumnsInclusionPatternId);
			else {
				precompiledInserts.put(
						tableConfig.defaultColumnsInclusionPatternId,
						new InsertValuesStatement(tableConfig, null));
				return precompiledInserts.get(tableConfig.defaultColumnsInclusionPatternId);
			}
		} else {
			KittyArrayKey key = KittyColumnsKey.generateKittyArrayKey(
					tableConfig.sortedColumns, model.exclusions);
			if(precompiledInserts.containsKey(key))
				return precompiledInserts.get(key);
			else {
				precompiledInserts.put(key, new InsertValuesStatement(tableConfig, model.exclusions));
				return precompiledInserts.get(key);
			}
		}
	}

	/**
	 * Starts transaction on current database instance in {@link TRANSACTION_MODES#EXCLUSIVE_MODE}.
	 * That means that all write operations on DB would be accumulated
	 * to buffer and written to DB only after ending transaction ({@link #finishTransaction()}).
	 * <br> While in transaction, only insert, update, save and create methods are available.
	 * Calling read methods while in transaction would cause {@link KittyRuntimeException}.
	 */
	public final void startTransaction() {
		startTransaction(TRANSACTION_MODES.EXCLUSIVE_MODE);
	}

	/**
	 * Trying to finish transaction, sets transaction successful and if failed than
	 * throws {@link KittyRuntimeException} that holds original exception.
	 */
	public final void finishTransaction() {
		if(!database.inTransaction()) return;
		Exception trxE = null;
		try {
			database.setTransactionSuccessful();
		} catch (Exception e) {
			trxE = e;
		} finally {
			database.endTransaction();
			if(trxE != null)
				throw new KittyRuntimeException(format(AME_TX_FINISH_EXCEPTION,
						getClass().getCanonicalName()), trxE);
		}
	}

	/**
	 * Returns transaction state.
	 * @return true if in transaction, otherwise false
	 */
	public final boolean isTransactionON() {
		return database.inTransaction();
	}

	/**
	 * Call this method in read OP to throw exception if in transaction
	 */
	public final void throwExcReadOPWhileInTransaction() {
		if(isTransactionON())
			throw new KittyRuntimeException(format(AME_ROWITX,
					getClass().getCanonicalName(), blankModelInstance.getClass().getCanonicalName(),
					tableConfig.schemaName, tableConfig.tableName));
	}

	// FIND (SELECT) QUERIES
	/**
	 * Returns list of models that suits provided condition string and query parameters. Select query. If
	 * no records found in db with passed condition and parameters than null would be returned.
	 *
	 * @param qParams additional query parameters (offset, limit, ordering etc), can be null
	 * @param condition sqlite condition string in format `column_name = ? AND #?fieldName = ?`
	 * @param conditionValues parameters for condition str
	 * @return
	 *
	 * @return list of models or null if nothing found and returnNullNotEmptyCollection is on
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> List<M> findWhere(QueryParameters qParams, String condition, Object... conditionValues) {
		if(condition == null)
			return findAll(qParams);
		return findWhere(
				conditionFromSQLString(condition, conditionValues),
				qParams
		);
	}

	/**
	 *
	 * Returns list of models that suits provided condition string. Select query. If
	 * no records found in db with passed condition and parameters than null would be returned.
	 *
	 * @param condition sqlite condition string in format `column_name = ? AND #?fieldName = ?`
	 * @param conditionValues parameters for condition str
	 *
	 * @return list of models or null if nothing found and returnNullNotEmptyCollection is on
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> List<M> findWhere(String condition, Object... conditionValues) {
		if(condition == null)
			return findAll();
		return findWhere(
				conditionFromSQLString(condition, conditionValues)
		);
	}

	/**
	 * Returns list of models that suits provided conditions and query parameters. Select query. If
	 * no records found in db with passed condition and parameters than null would be returned.
	 * @param where condition, can be null
	 * @param qParams additional query parameters (offset, limit, ordering etc), can be null
	 *
	 * @return list of models or null if nothing found and returnNullNotEmptyCollection is on
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> List<M> findWhere(SQLiteCondition where, QueryParameters qParams) {
		throwExcReadOPWhileInTransaction();
		KittyQueryBuilder qb = new KittyQueryBuilder(KittyQueryBuilder.QUERY_TYPE.SELECT, tableConfig.tableName);
		qb.setQueryParameters(qParams)
				.setWhereClause(where)
				.setRowIDSupport(rowidOn);
		KittySQLiteQuery query = qb.buildSQLQuery();
		return findWithRawQuery(rowidOn, query);
	}

	/**
	 * Returns list of models that suits provided conditions. Select query. If
	 * no records found in db with passed condition than null would be returned.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere(where, null)
	 * @param where condition, can be null
	 * @return list of models or null if nothing found and returnNullNotEmptyCollection is on
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> List<M> findWhere(SQLiteCondition where) {
		return findWhere(where, null);
	}

	/**
	 * Returns list of all models associated with records in backed database table with usage of
	 * passed qParams.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere(null, qParams)
	 * @param qParams query params such as limit, offset etc, can be null
	 * @return list of all models fetched according to passed params or null if nothing found and returnNullNotEmptyCollection is on
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> List<M> findAll(QueryParameters qParams) {
		return findWhere(null, qParams);
	}

	/**
	 * Returns list of all models associated with records in backed database table.
	 * @return list of all models associated with backed database table or null if nothing found and returnNullNotEmptyCollection is on
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> List<M> findAll() {
		throwExcReadOPWhileInTransaction();
		KittyQueryBuilder qb = new KittyQueryBuilder(KittyQueryBuilder.QUERY_TYPE.SELECT, tableConfig.tableName);
		qb.setRowIDSupport(rowidOn);
		KittySQLiteQuery query = qb.buildSQLQuery();
		return findWithRawQuery(rowidOn, query);
	}

	/**
	 * Returns model filled with data from database or null if no record with provided rowid found.
	 * <br> If more than one value found for provided rowid or {@link #getRowIDCondition(Long)}
	 * returned null, KittyRuntimeException would be thrown.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere("rowid = ?", null).get(0)
	 * with check that resulting collection's size == 0 | 1
	 * @param rowid RowID of database table's record to fetch
	 * @return model filled from record in db's table or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findByRowID(Long rowid) {
		SQLiteCondition condition = getRowIDCondition(rowid);
		if(condition == null) {
			throw new KittyRuntimeException(
					format(
							AME_FBRID_NULL_CONDITION,
							rowid,
							this.getClass().getCanonicalName(),
							this.blankModelInstance.getClass().getCanonicalName()
					)
			);
		}
		List<M> result = findWhere(condition);
		if(result == null) return null;
		if(result.size() == 0) return null;
		if(result.size() > 1) {
			throw new KittyRuntimeException(
					format(
							AME_FBRID_MORE_THAN_ONE_VALUE,
							rowid,
							this.getClass().getCanonicalName(),
							this.blankModelInstance.getClass().getCanonicalName()
					)
			);
		}
		return result.get(0);
	}

	/**
	 * Returns model filled with data from database or null if no record with provided PK ({@link KittyPrimaryKey}) found.
	 * <br> If more than one value found for provided PK or {@link #getSQLiteConditionForPKKeyValues(Map)}
	 * returned null, KittyRuntimeException would be thrown.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere({PKCondition}, null).get(0)
	 * with check that resulting collection's size == 0 | 1
	 * @param primaryKey primary key filled with values, also notice that there is no check for PK
	 * @param <M> instance of non abstract child of KittyModel
	 * @return model filled from record in db's table or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findByPK(KittyPrimaryKey primaryKey) {
		return findByPKV(primaryKey.getPrimaryKeyColumnValues());
	}

	/**
	 * Returns model filled with data from database or null if no record with provided PK MAP ({@link KittyPrimaryKey#getPrimaryKeyColumnValues()}) found.
	 * <br> If more than one value found for provided PK values or {@link #getSQLiteConditionForPKKeyValues(Map)}
	 * returned null, KittyRuntimeException would be thrown.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere({PKCondition}, null).get(0)
	 * with check that resulting collection's size == 0 | 1
	 * @param pkv primary key values map,  also notice that there is no check for PK values
	 * @return model filled from record in db's table or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findByPKV(Map<String, String> pkv) {
		SQLiteCondition condition = getSQLiteConditionForPKKeyValues(pkv);
		if(condition == null) {
			throw new KittyRuntimeException(
					format(
							AME_PKV_MORE_THAN_ONE_VALUE,
							pkvMapToString(pkv),
							this.getClass().getCanonicalName(),
							this.blankModelInstance.getClass().getCanonicalName()
					)
			);
		}
		List<M> result = findWhere(condition);
		if(result == null) return null;
		if(result.size() == 0) return null;
		if(result.size() > 1) throw new KittyRuntimeException(format(AME_FBPK_MORE_THAN_ONE_RESULT, tableConfig.schemaName, tableConfig.tableName));
		return result.get(0);
	}

	/**
	 * Returns model filled with data from database or null if no record with provided IPK found.
	 * <br> If more than one value found for provided IPK or {@link #getIPKCondition(Long)}
	 * returned null, KittyRuntimeException would be thrown.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere("{IPK} = ?", null).get(0)
	 * with check that resulting collection's size == 0 | 1
	 * @param ipk INTEGER PRIMARY KEY value of database table's record to fetch
	 * @return model filled from record in db's table or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findByIPK(Long ipk) {
		if(tableConfig.kittyPrimaryKey == null)
			throw new KittyRuntimeException(format(AME_FBIPK_NOT_IPK, tableConfig.schemaName, tableConfig.tableName));
		if(tableConfig.kittyPrimaryKey.isIPK()) {
			SQLiteCondition condition = getIPKCondition(ipk);
			if(condition == null) {
				throw new KittyRuntimeException(
						format(
								AME_IPK_MORE_THAN_ONE_VALUE,
								ipk.toString(),
								this.getClass().getCanonicalName(),
								this.blankModelInstance.getClass().getCanonicalName()
						)
				);
			}
			List<M> result = findWhere(condition);
			if(result == null) return null;
			if(result.size() == 0) return null;
			if(result.size() > 1)
				throw new KittyRuntimeException(format(AME_FBPK_MORE_THAN_ONE_RESULT, tableConfig.schemaName, tableConfig.tableName));
			return result.get(0);
		} else {
			throw new KittyRuntimeException(format(AME_FBIPK_NOT_IPK, tableConfig.schemaName, tableConfig.tableName));
		}
	}

	/**
	 * Returns first record in KittyModel wrapper in database table that suits provided condition.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere(conditions, {LIMIT = 1, ORDER BY rowid ASCENDING}
	 * @param where SQLiteCondition for select
	 * @return model filled from first record in db's table that suits provided condition or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findFirst(SQLiteCondition where) {
		QueryParameters qParams = new QueryParameters();
		qParams.setLimit(1l).setOrderByColumns(ROWID).setOrderAscDesc(AscDesc.ASCENDING);
		List<M> first = findWhere(where, qParams);
		if(first == null) return null;
		if(first.size() == 0) return null;
		return first.get(0);
	}

	/**
	 * Returns first record in KittyModel wrapper in database table that suits provided condition.
	 * <br> Alias for {@link #findWhere(QueryParameters, String, Object...)} (findWhere({LIMIT = 1, ORDER BY rowid ASCENDING}, conditionStr, conditionValues)
	 * @param condition sqlite condition string in format `column_name = ? AND #?fieldName = ?`
	 * @param conditionValues parameters for condition str
	 * @return model filled from first record in db's table that suits provided condition or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findFirst(String condition, Object... conditionValues) {
		return findFirst(conditionFromSQLString(condition, conditionValues));
	}

	/**
	 * Returns first record in KittyModel wrapper in database table that suits provided condition.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere(null, {LIMIT = 1, ORDER BY rowid ASCENDING}
	 * @return model filled from first record in db's table or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findFirst() {
		return findFirst(null);
	}

	/**
	 * Returns last record in KittyModel wrapper in database table that suits provided condition.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere(conditions, {LIMIT = 1, ORDER BY rowid DESCENDING}
	 * @param where SQLiteCondition for select
	 * @param <M> instance of non abstract child of KittyModel
	 * @return model filled from last record in db's table that suits provided condition or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findLast(SQLiteCondition where) {
		QueryParameters queryParameters = new QueryParameters();
		queryParameters.setLimit(1l).setOrderByColumns(ROWID).setOrderAscDesc(AscDesc.DESCENDING);
		List<M> last = findWhere(where, queryParameters);
		if(last == null) return null;
		if(last.size() == 0) return null;
		return last.get(0);
	}

	/**
	 * Returns last record in KittyModel wrapper in database table that suits provided condition.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere({LIMIT = 1, ORDER BY rowid DESCENDING}, conditionStr, conditionValues))
	 * @param condition sqlite condition string in format `column_name = ? AND #?fieldName = ?`
	 * @param conditionValues parameters for condition str
	 * @return model filled from last record in db's table that suits provided condition or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findLast(String condition, Object... conditionValues) {
		return findLast(conditionFromSQLString(condition, conditionValues));
	}

	/**
	 * Returns last record in KittyModel wrapper in database table that suits provided condition.
	 * <br> Alias for {@link #findWhere(SQLiteCondition, QueryParameters)} (findWhere(null, {LIMIT = 1, ORDER BY rowid DESCENDING}
	 * @param <M> instance of non abstract child of KittyModel
	 * @return model filled from last record in db's table or null if nothing found.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final <M extends KittyModel> M findLast() {
		return findLast(null);
	}

	// FUNCTION SELECT QUERIES (SUM AND COUNT)
	protected static final String QE_COUNT = "KittyMapper#countWhere(SQLiteCondition where, QueryParameters qParams)";
	/**
	 * Counts record's amount of those records that suits provided condition.
	 * @param where SQLite condition, may be null
	 * @param qParams query qParams, may be null
	 * @return record's amount of those records that suits provided condition.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long countWhere(SQLiteCondition where, QueryParameters qParams) {
		throwExcReadOPWhileInTransaction();
		KittyQueryBuilder qb = new KittyQueryBuilder(KittyQueryBuilder.QUERY_TYPE.SELECT_COUNT, tableConfig.tableName);
		qb.setQueryParameters(qParams)
				.setWhereClause(where);
		KittySQLiteQuery query = qb.buildSQLQuery();
		logQuery(QE_COUNT, query);
		Cursor cursor = database.rawQuery(query.getSql(), query.getConditionValues());
		if (cursor == null)
			return 0;
		if(cursor.getCount() == 0) return 0;
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}

	/**
	 * Counts record's amount of those records that suits provided condition.
	 * @param qParams query qParams, may be null
	 * @param condition sqlite condition string in format `column_name = ? AND #?fieldName = ?`
	 * @param conditionValues parameters for condition str
	 * @return record's amount of those records that suits provided condition.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long countWhere(QueryParameters qParams, String condition, Object... conditionValues) {
		if(condition == null)
			return countAll(qParams);
		return countWhere(
				conditionFromSQLString(condition, conditionValues),
				qParams
		);
	}

	/**
	 * Counts record's amount of those records that suits provided condition.
	 * @param condition sqlite condition string in format `column_name = ? AND #?fieldName = ?`
	 * @param conditionValues parameters for condition str
	 * @return record's amount of those records that suits provided condition.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long countWhere(String condition, Object... conditionValues) {
		if(condition == null)
			return countAll();
		return countWhere(
				conditionFromSQLString(condition, conditionValues)
		);
	}

	/**
	 * Counts record's amount of those records that suits provided condition.
	 * <br> Alias for {@link #countWhere(SQLiteCondition, QueryParameters)} (countWhere(condition, null)
	 * @param where SQLite condition, may be null
	 * @return record's amount of those records that suits provided condition.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long countWhere(SQLiteCondition where) {
		return countWhere(where, null);
	}

	/**
	 * Counts all records in table.
	 * <br> Alias for {@link #countWhere(SQLiteCondition, QueryParameters)} (countWhere(null, qParams)
	 * @param qParams query parameters such as OFFSET, LIMIT etc
	 * @return count of all records in table.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long countAll(QueryParameters qParams) {
		return countWhere(null, qParams);
	}

	/**
	 * Counts all records in table.
	 * <br> Alias for {@link #countWhere(SQLiteCondition, QueryParameters)} (countWhere(null, null)
	 * @return count of all records in table.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long countAll() {
		throwExcReadOPWhileInTransaction();
		KittyQueryBuilder qb = new KittyQueryBuilder(KittyQueryBuilder.QUERY_TYPE.SELECT_COUNT, tableConfig.tableName);
		KittySQLiteQuery query = qb.buildSQLQuery();
		logQuery(QE_COUNT, query);
		Cursor cursor = database.rawQuery(query.getSql(), query.getConditionValues());
		if (cursor == null)
			return 0;
		if(cursor.getCount() == 0) return 0;
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}

	protected static final String QE_SUM = "KittyMapper#sum(String sumColumn, SQLiteCondition where, QueryParameters qParams)";

	/**
	 * Sum all values in table in specified column that suits provided condition and additional query qParams.
	 * @param sumColumn
	 * @param qParams
	 * @param condition
	 * @param conditionValues
	 * @return
	 */
	public final long sum(String sumColumn, QueryParameters qParams, String condition, Object... conditionValues) {
		if(condition == null)
			return sumAll(sumColumn, qParams);
		return sum(
				sumColumn,
				conditionFromSQLString(condition, conditionValues),
				qParams
		);
	}

	/**
	 * Sum all values in table in specified column that suits provided condition and additional query qParams.
	 * @param sumColumn
	 * @param condition
	 * @param conditionValues
	 * @return
	 */
	public final long sum(String sumColumn, String condition, Object... conditionValues) {
		if(condition == null)
			return sumAll(sumColumn);
		return sum(
				sumColumn,
				conditionFromSQLString(condition, conditionValues)
		);
	}

	/**
	 * Sum all values in table in specified column that suits provided condition and additional query qParams.
	 * @param sumColumn column name from where should be values summed
	 * @param where SQLite condition for SUM
	 * @param qParams additional query qParams
	 * @return sum of values in table in specified column that suits provided condition and additional query qParams.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long sum(String sumColumn, SQLiteCondition where, QueryParameters qParams) {
		throwExcReadOPWhileInTransaction();
		KittyQueryBuilder qb = new KittyQueryBuilder(KittyQueryBuilder.QUERY_TYPE.SELECT_SUM, tableConfig.tableName);
		qb.setSumColumn(sumColumn)
				.setWhereClause(where)
				.setQueryParameters(qParams);
		KittySQLiteQuery query = qb.buildSQLQuery();
		logQuery(QE_SUM, query);
		Cursor cursor = database.rawQuery(query.getSql(), query.getConditionValues());
		if (cursor == null)
			return 0;
		cursor.moveToFirst();
		long sum = cursor.getLong(0);
		cursor.close();
		return sum;
	}

	/**
	 * Sum all values in table in specified column that suits provided condition.
	 * <br> Alias for {@link #sum(String, SQLiteCondition, QueryParameters)} (sum(colName, condition, null).
	 * @param sumColumn column name from where should be values summed
	 * @param where SQLite condition for SUM
	 * @return sum of values in table in specified column that suits provided condition.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long sum(String sumColumn, SQLiteCondition where) {
		return sum(sumColumn, where, null);
	}

	/**
	 * Sum all values in table in specified column that suits provided additional query qParams.
	 * <br> Alias for {@link #sum(String, SQLiteCondition, QueryParameters)} (sum(colName, null, qParams).
	 * @param sumColumn column name from where should be values summed
	 * @param qParams additional query qParams
	 * @return sum of values in table in specified column that suits provided additional query qParams.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long sumAll(String sumColumn, QueryParameters qParams) {
		throwExcReadOPWhileInTransaction();
		KittyQueryBuilder qb = new KittyQueryBuilder(KittyQueryBuilder.QUERY_TYPE.SELECT_SUM, tableConfig.tableName);
		qb.setSumColumn(sumColumn)
				.setQueryParameters(qParams);
		KittySQLiteQuery query = qb.buildSQLQuery();
		logQuery(QE_SUM, query);
		Cursor cursor = database.rawQuery(query.getSql(), query.getConditionValues());
		if (cursor == null)
			return 0;
		cursor.moveToFirst();
		long sum = cursor.getLong(0);
		cursor.close();
		return sum;
	}

	/**
	 * Sum all values in table in specified column.
	 * <br> Alias for {@link #sum(String, SQLiteCondition, QueryParameters)} (sum(colName, null, null).
	 * @param sumColumn column name from where should be values summed
	 * @return sum of values in table in specified column.
	 * @throws KittyRuntimeException if there some errors, if KittyRuntimeException was caused by another exception than
	 * first exception can be fetched by {@link KittyRuntimeException#getNestedException()}
	 */
	public final long sumAll(String sumColumn) {
		throwExcReadOPWhileInTransaction();
		KittyQueryBuilder qb = new KittyQueryBuilder(KittyQueryBuilder.QUERY_TYPE.SELECT_SUM, tableConfig.tableName);
		qb.setSumColumn(sumColumn);
		KittySQLiteQuery query = qb.buildSQLQuery();
		logQuery(QE_SUM, query);
		Cursor cursor = database.rawQuery(query.getSql(), query.getConditionValues());
		if (cursor == null)
			return 0;
		cursor.moveToFirst();
		long sum = cursor.getLong(0);
		cursor.close();
		return sum;
	}

	// SAVE (INSERT AND UPDATE)

	protected static final String QE_INSERT = "KittyMapper#insert(M model)";
	protected static final String QE_INSERT_FAKE_QUERY = "INSERT CALL OF SQLiteDatabase with parameters CV!";

	protected static String INS_PL = "<M extends KittyModel> long insert(M model)";

	/**
	 * Inserts into database provided model's values. No checks on auto generated values of PK fields performed.
	 * May throw exceptions related to reflection access to fields wrapped in {@link KittyRuntimeException}.
	 * @param model model to inserts
	 * @param <M> instance of non abstract child of KittyModel
	 * @return RowID of inserted record, or -1 if error occurred
	 */
	public final <M extends KittyModel> long insert(M model) {
		if(model == null) return -1;
		for(String exclusion : insertPKExclusions) {
			model.setFieldExclusion(exclusion);
		}
		//ContentValues cv;
		logModel(INS_PL, model);
		SQLiteStatement insertStatement = getInsertStatement(model).getStatement(database);
		//SQLiteStatement insertStatement = getInsertStatement(model);
		try {
			insertStatement = PreparedStatementBinder.bindModelValuesToPreparedStatement(model, tableConfig, insertStatement);
			//cv = modelToCV(model, tableConfig);
		} catch (Exception e) {
			if(e instanceof KittyRuntimeException)
				throw (KittyRuntimeException) e;
			else {
				throw new KittyRuntimeException(format(AME_INS_EXC, tableConfig.schemaName, tableConfig.tableName), e);

			}
		}
		logQuery(QE_INSERT, new KittySQLiteQuery(QE_INSERT_FAKE_QUERY, null));
		return insertStatement.executeInsert(); //database.insert(tableConfig.tableName, null, cv);
	}

	/**
	 * Inserts models in provided list into database table.
	 * <br> May throw exceptions related to reflection access to fields wrapped in {@link KittyRuntimeException}.
	 * @param models list of models to insert
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void insert(List<M> models) {
		if(models == null) return;
		for(M model : models) {
			insert(model);
		}
	}

	/**
	 * Inserts models in provided list into database table in internal method transaction started with
	 * transaction mode {@link TRANSACTION_MODES#EXCLUSIVE_MODE}.
	 * <br> Alias for {@link #insert(TRANSACTION_MODES, List)} insert(TRANSACTION_MODES.EXCLUSIVE_MODE, models)
	 * <br> May throw exceptions related to reflection access to fields wrapped in {@link KittyRuntimeException}.
	 * @param models list of models to insert
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void insertInTransaction(List<M> models) {
		insert(TRANSACTION_MODES.EXCLUSIVE_MODE, models);
	}

	protected static final String KE_INSERT_READ_ONLY = "SQLiteReadOnlyDatabaseException on KittyMapper.insert()";

	/**
	 * <br> Inserts models in provided list into database table in new transaction.
	 * <br> If there is already existing transaction than this transaction would be used.
	 * <br> If provided txMode is null than no transaction would be started.
	 * <br> If no transaction exists than new transaction would be created and finished in this method.
	 * @param txMode transaction mode, see {@link #startTransaction(TRANSACTION_MODES)} for modes info
	 * @param models list of models to insert
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void insert(TRANSACTION_MODES txMode, List<M> models) {
		boolean internalVoidTX = false;
		if(!database.inTransaction() && txMode != null) {
			startTransaction(txMode);
			internalVoidTX = true;
		}
		try {
			insert(models);
		} catch (SQLiteReadOnlyDatabaseException e) {
			finishTransaction();
			throw new KittyRuntimeException(KE_INSERT_READ_ONLY, e);
		}
		if(internalVoidTX) {
			finishTransaction();
		}
	}

	protected static final String QE_UPDATE = "KittyMapper#update(M model, SQLiteCondition condition)";
	protected static final String QE_UPDATE_FAKE_QUERY = "UPDATE CALL OF SQLiteDatabase with parameters : {0}";
	protected static final String UPDATE_PL = "<M extends KittyModel> long update(M model, SQLiteCondition condition)";

	/**
	 * Update records from database with values from provided model that suits provided condition.
	 * <br> This method would generate an update statement that would be applied to all record in database that
	 * suits provided condition. By default would be an attempt to update all database records with values
	 * from all model fields annotated with {@link net.akaish.kitty.orm.annotations.column.KITTY_COLUMN}.
	 * <br> If no condition available (condition parameter is NULL) then update operation would be applied to
	 * <b>ALL</b> records in database
	 * <br>
	 * <br> You can use this method for generating classic SQL update queries by passing model with set fields to be updated,
	 * names array that contains column or model field names to include\exclude in statement generation and flag that
	 * defines inclusion\exclusion tactic.
	 * <br> For example, you have model with following fields:
	 * <ol>
	 * <li>firstName (columnName = "first_name")</li>
	 * <li>secondName (columnName = "second_name")</li>
	 * <li>telNumber (columnName = "tel_number")</li>
	 * </ol>
	 * <br> And you want for some reason set to all Johnsons tel. 8800555355
	 * <br>
	 * <br> So you set to model field telNumber to 8800555355
	 * <br>
	 * <br> Then you set names array to {"tel_number"} and pass {@link CVUtils#INCLUDE_ONLY_SELECTED_COLUMN_NAMES}
	 * <br> or
	 * <br> set names array to {"telNumber"} and pass {@link CVUtils#INCLUDE_ONLY_SELECTED_FIELDS}
	 *
	 * @param model model to update
	 * @param condition condition for that model
	 * @param <M> instance of non abstract child of KittyModel
	 * @param names array of fields names or column names to include\exclude
	 * @param IEFlag flag that determines what to do with provided {@link KittyColumnConfiguration} instance, may be {@link CVUtils#IGNORE_INCLUSIONS_AND_EXCLUSIONS},
	 *                      {@link CVUtils#INCLUDE_ALL_EXCEPT_SELECTED_COLUMN_NAMES}, {@link CVUtils#INCLUDE_ONLY_SELECTED_COLUMN_NAMES}, {@link CVUtils#INCLUDE_ALL_EXCEPT_SELECTED_FIELDS}
	 *                      or {@link CVUtils#INCLUDE_ONLY_SELECTED_FIELDS}. If flag's value differs from any of inclusion predefined flags than would be used default flag
	 *                      ({@link CVUtils#IGNORE_INCLUSIONS_AND_EXCLUSIONS})
	 * @return the number of rows affected
	 */
	public final <M extends KittyModel> long update(M model, SQLiteCondition condition, String[] names, int IEFlag) {
		logModel(UPDATE_PL, model);
		ContentValues cv;
		try {
			cv = CVUtils.modelToCV(model, tableConfig, names, IEFlag);
		} catch (Exception e) {
			if (e instanceof KittyRuntimeException)
				throw (KittyRuntimeException) e;
			else
				throw new KittyRuntimeException(format(AME_UPD_EXC, tableConfig.schemaName, tableConfig.tableName), e);
		}
		logQuery(QE_UPDATE, new KittySQLiteQuery(format(QE_UPDATE_FAKE_QUERY, condition.getCondition()), condition.getValues()));
		if(condition!=null)
			return database.update(tableConfig.tableName, cv, condition.getCondition(), condition.getValues()); // TODO updates on statement
		else
			return database.update(tableConfig.tableName, cv, null, null);
	}

	/**
	 * See {@link #update(KittyModel, SQLiteCondition, String[], int)} for more info
	 * @param model
	 * @param names
	 * @param IEFlag
	 * @param condition
	 * @param conditionValues
	 * @param <M>
	 * @return
	 */
	public final <M extends KittyModel> long update(M model, String[] names, int IEFlag, String condition, Object... conditionValues) {
		if(condition == null)
			return update(model, null, names, IEFlag);
		else
			return update(
					model,
					conditionFromSQLString(condition, conditionValues),
					names,
					IEFlag
			);
	}

	/**
	 * Update records from database with values from provided model that suits provided condition.
	 * <br> If you use your own condition make sure that you know what are you doing cause
	 * in that case would be generated an update statement that can update all records in table
	 * that suit provided condition.
	 * <br> If provided condition is null than would be attempt to get RowID condition.
	 * <br> If no RowID found than would be attempt to get PK condition.
	 * <br> If no PK condition exists than {@link KittyRuntimeException} would be thrown.
	 * @param model
	 * @param condition
	 * @param <M>
	 * @return
	 */
	public final <M extends KittyModel> long update(M model, SQLiteCondition condition) {
		if(model == null) return 0;
		if(condition == null) {
			if (model.getRowID() != null) {
				condition = getRowIDCondition(model.getRowID());
			} else {
				condition = getPKCondition(model, condition);
			}
		}
		if(condition == null)
			throw new KittyRuntimeException(format(AME_UTU_NO_INFO, model.getClass().getCanonicalName()));
		return update(model, condition, null, CVUtils.IGNORE_INCLUSIONS_AND_EXCLUSIONS);
	}

	public final <M extends KittyModel> long update(M model, String condition, Object... conditionValues) {
		if(condition == null)
			return update(model);
		else
			return update(
					model,
					conditionFromSQLString(condition, conditionValues)
			);
	}

	/**
	 * Update records from database with values from provided model.
	 * <br> If no RowID found than would be attempt to get PK condition.
	 * <br> If no PK condition exists than {@link KittyRuntimeException} would be thrown.
	 * <br> Alias for {@link #update(KittyModel, SQLiteCondition)} update(model, null)
	 * @param model model to update
	 * @param <M> instance of non abstract child of KittyModel
	 * @return the number of affected rows
	 */
	public final <M extends KittyModel> long update(M model) {
		return update(model, null);
	}

	/**
	 * Update records from database with values from provided list of model.
	 * <br> If no RowID found than would be attempt to get PK condition.
	 * <br> If no PK condition exists than {@link KittyRuntimeException} would be thrown.
	 * @param models list of models to update
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> long update(List<M> models) {
		if(models == null) return -1;
		long out = 0;
		for(M model : models) {
			out = out + update(model);
		}
		return out;
	}

	protected final static String KE_UPDATE_READ_ONLY = "SQLiteReadOnlyDatabaseException on KittyMapper.update()";

	/**
	 * Updates values in database with values from models from provided list in new transaction..
	 * <br> Foreach model:
	 * <br> If no RowID found than would be attempt to get PK condition.
	 * <br> If no PK condition exists than {@link KittyRuntimeException} would be thrown.
	 * <br> If there is already existing transaction than this transaction would be used.
	 * <br> If provided txMode is null than no transaction would be started.
	 * <br> If no transaction exists than new transaction would be created and finished in this method.
	 * @param txMode transaction mode, see {@link #startTransaction(TRANSACTION_MODES)} for modes info
	 * @param models list of models to update
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void update(TRANSACTION_MODES txMode, List<M> models) {
		boolean internalVoidTX = false;
		if(!database.inTransaction() && txMode != null) {
			startTransaction(txMode);
			internalVoidTX = true;
		}
		try {
			update(models);
		} catch (SQLiteReadOnlyDatabaseException e) {
			finishTransaction();
			throw new KittyRuntimeException(KE_UPDATE_READ_ONLY, e);
		}
		if(internalVoidTX) {
			finishTransaction();
		}
	}

	/**
	 * Updates rows in database with models from provided list in internal method transaction started with
	 * transaction mode {@link TRANSACTION_MODES#EXCLUSIVE_MODE}.
	 * <br> Alias for {@link #update(TRANSACTION_MODES, List)} (update(TRANSACTION_MODES.EXCLUSIVE_MODE, models)
	 * <br> May throw exceptions related to reflection access to fields wrapped in {@link KittyRuntimeException}.
	 * @param models list of models to update
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void updateInTransaction(List<M> models) {
		update(TRANSACTION_MODES.EXCLUSIVE_MODE, models);
	}

	/**
	 * Saves data from provided model into database table.
	 * <br> If provided model has RowID set or auto generated PK values set than {@link #update(KittyModel)} would
	 * be called.
	 * <br> If provided model has no RowID or auto generated PK values set than {@link #insert(KittyModel)}
	 * would be called.
	 * @param model model to save
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void save(M model) {
		if(model == null) return;
		SQLiteCondition condition = getPKCondition(model, null);
		if(condition == null)
			insert(model);
		else
			update(model, condition);
	}

	/**
	 * Saves values from provided list of models into database. Foreach model in collection {@link #save(KittyModel)}
	 * method would be called.
	 * @param models list of models to save
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void save(List<M> models) {
		if(models == null) return;
		for(M model : models) {
			save(model);
		}
	}

	protected final static String KE_SAVE_READ_ONLY = "SQLiteReadOnlyDatabaseException on KittyMapper.save()";

	/**
	 * Saves values from provided list of models into database. Foreach model in collection {@link #save(KittyModel)}
	 * method would be called. Insert\\update operations would be run in internal method transaction.
	 * <br> If there is already existing transaction than this transaction would be used.
	 * <br> If provided txMode is null than no transaction would be started.
	 * <br> If no transaction exists than new transaction would be created and finished in this method.
	 * @param txMode transaction mode, see {@link #startTransaction(TRANSACTION_MODES)} for modes info
	 * @param models list of models to save
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void save(TRANSACTION_MODES txMode, List<M> models) {
		if(models == null) return;
		if(models.size() == 0) return;
		boolean internalVoidTX = false;
		if(!database.inTransaction() && txMode != null) {
			startTransaction(txMode);
			internalVoidTX = true;
		}
		try {
			save(models);
		} catch (SQLiteReadOnlyDatabaseException e) {
			finishTransaction();
			throw new KittyRuntimeException(KE_SAVE_READ_ONLY, e);
		}
		if(internalVoidTX) {
			finishTransaction();
		}
	}

	/**
	 * Saves values from provided list of models into database. Foreach model in collection {@link #save(KittyModel)}
	 * method would be called. Insert\\update operations would be run in internal method transaction
	 * started with transaction mode {@link TRANSACTION_MODES#EXCLUSIVE_MODE}.
	 * <br> Alias for {@link #save(TRANSACTION_MODES, List)} (save(TRANSACTION_MODES.EXCLUSIVE_MODE, models)
	 * <br> May throw exceptions related to reflection access to fields wrapped in {@link KittyRuntimeException}.
	 * @param models list of models to save
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void saveInTransaction(List<M> models) {
		save(TRANSACTION_MODES.EXCLUSIVE_MODE, models);
	}

	// DELETE QUERIES
	protected static final String QE_DELETE_ALL = "KittyMapper#deleteAll()";
	protected static final String QE_DELETE_ALL_FAKE_QUERY = "DELETE CALL OF SQLiteDatabase with parameters : 1 and NULL";

	/**
	 * Deletes all rows in table associated with this data mapper.
	 * @return rows affected
	 */
	public final long deleteAll() {
		// Pass into database.delete "1" in whereClause if you want to get count and delete all
		logQuery(QE_DELETE_ALL, new KittySQLiteQuery(QE_DELETE_ALL_FAKE_QUERY, null));
		return database.delete(tableConfig.tableName, "1", null);
	}

	protected static final String QE_DELETE_BY_WHERE = "KittyMapper#deleteWhere(SQLiteCondition condition)";
	protected static final String QE_DELETE_FAKE_QUERY = "DELETE CALL OF SQLiteDatabase with parameters : {0}";

	/**
	 * Deletes all rows in table associated with this data mapper that suit provided condition.
	 * @param condition not null
	 * @param conditionValues
	 * @return
	 */
	public final long deleteWhere(String condition, Object... conditionValues) {
		return deleteWhere(
			conditionFromSQLString(condition, conditionValues)
		);
	}

	/**
	 * Deletes all rows in table associated with this data mapper that suit provided condition.
	 * @param condition SQLite condition, not null
	 * @return rows affected
	 */
	public final long deleteWhere(SQLiteCondition condition) {
		logQuery(QE_DELETE_BY_WHERE, new KittySQLiteQuery(format(QE_DELETE_FAKE_QUERY, condition.getCondition()), condition.getValues()));
		return database.delete(tableConfig.tableName, condition.getCondition(), condition.getValues());
	}

	protected static String DELETE_PL2 = "<M extends KittyModel> int delete(M model) ";

	/**
	 * Deletes row that represented by provided model. If model has no RowID or PK fields set
	 * {@link KittyRuntimeException} would be thrown.
	 * @param model model to delete
	 * @param <M> instance of non abstract child of KittyModel
	 * @return rows affected
	 */
	public final <M extends KittyModel> long delete(M model) {
		SQLiteCondition condition = getPKCondition(model, null);
		logModel(DELETE_PL2, model);
		if(condition == null)
			throw new KittyRuntimeException(format(AME_DEL_UNABLE_TO_CREATE_CONDITION,
					DEL_MNAME_DELETE, model.getClass().getCanonicalName()));
		return deleteWhere(condition);
	}

	protected static String DELETE_PL = "<M extends KittyModel> void delete(List<M> models)";

	/**
	 * Deletes all rows that represented by provided model's list. If any of model has no RowID
	 * or PK fields set {@link KittyRuntimeException} would be thrown.
	 * @param models list of models to delete
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void delete(List<M> models) {
		for(M model : models) {
			logModel(DELETE_PL, model);
			delete(model);
		}
	}

	protected final static String KE_DELETE_READ_ONLY = "SQLiteReadOnlyDatabaseException on KittyMapper.delete()";

	/**
	 * Deletes all rows that represented by provided model's list. If any of model has no RowID
	 * or PK fields set {@link KittyRuntimeException} would be thrown.
	 * Delete operations would be run in internal method transaction.
	 * <br> If there is already existing transaction than this transaction would be used.
	 * <br> If provided txMode is null than no transaction would be started.
	 * <br> If no transaction exists than new transaction would be created and finished in this method.
	 * @param txMode transaction mode, see {@link #startTransaction(TRANSACTION_MODES)} for modes info
	 * @param models list of models to delete
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void delete(TRANSACTION_MODES txMode, List<M> models) {
		boolean internalVoidTX = false;
		if(!database.inTransaction() && txMode != null) {
			startTransaction(txMode);
			internalVoidTX = true;
		}
		try {
			delete(models);
		} catch (SQLiteReadOnlyDatabaseException e) {
			finishTransaction();
			throw new KittyRuntimeException(KE_DELETE_READ_ONLY, e);
		}
		if(internalVoidTX) {
			finishTransaction();
		}
	}

	/**
	 * Deletes all rows that represented by provided model's list. If any of model has no RowID
	 * or PK fields set {@link KittyRuntimeException} would be thrown.
	 * Delete operations would be run in internal method transaction
	 * started with transaction mode {@link TRANSACTION_MODES#EXCLUSIVE_MODE}.
	 * <br> Alias for {@link #delete(TRANSACTION_MODES, List)} (delete(TRANSACTION_MODES.EXCLUSIVE_MODE, models)
	 * <br> May throw exceptions related to reflection access to fields wrapped in {@link KittyRuntimeException}.
	 * @param models list of models to delete
	 * @param <M> instance of non abstract child of KittyModel
	 */
	public final <M extends KittyModel> void deleteInTransaction(List<M> models) {
		delete(TRANSACTION_MODES.EXCLUSIVE_MODE, models);
	}

	/**
	 * Deletes row in database's table depending on values in provided PK. If generated condition
	 * by method {@link #getSQLiteConditionForPK(KittyPrimaryKey)} null than {@link KittyRuntimeException}
	 * would be thrown.
	 * @param pk primary key for generating delete condition.
	 * @return number of affected rows.
	 */
	public long deleteByPK(KittyPrimaryKey pk) {
		SQLiteCondition condition = getSQLiteConditionForPK(pk);
		if(condition == null)
			throw new KittyRuntimeException(format(AME_DEL_UNABLE_TO_CREATE_CONDITION,
					DEL_MNAME_DELETEPK, tableConfig.modelClass.getCanonicalName()));
		return deleteWhere(condition);
	}

	/**
	 * Deletes row in database's table depending on provided RowID. If generated condition
	 * by method {@link #getRowIDCondition(Long)} null than {@link KittyRuntimeException}
	 * would be thrown.
	 * @param rowid row id
	 * @return count of affected rows
	 */
	public long deleteByRowID(Long rowid) {
		SQLiteCondition condition = getRowIDCondition(rowid);
		if(condition == null)
			throw new KittyRuntimeException(format(AME_DEL_UNABLE_TO_CREATE_CONDITION,
					DEL_MNAME_DELETE_ROWID, tableConfig.modelClass.getCanonicalName()));
		return deleteWhere(condition);
	}

	/**
	 * Deletes row in database's table depending on provided INTEGER PRIMARY KEY value. If generated condition
	 * by method {@link #getIPKCondition(Long)} null than {@link KittyRuntimeException}
	 * would be thrown.
	 * @param id INTEGER PRIMARY KEY VALUE
	 * @return count of affected rows
	 */
	public long deleteByIPK(Long id) {
		SQLiteCondition condition = getIPKCondition(id);
		if(condition == null)
			throw new KittyRuntimeException(format(AME_DEL_UNABLE_TO_CREATE_CONDITION,
					DEL_MNAME_DELETEIPK, tableConfig.modelClass.getCanonicalName()));
		return deleteWhere(condition);
	}

	protected static final String QE_FIND_WITH_RAW_QUERY = "KittyMapper#findWithRawQuery(boolean rowIdSupport, KittySQLiteQuery query)";

	/**
	 * Tries to fetch data from database table with provided query and wrap it into collection of models.
	 * @param rowIdSupport RowID flag
	 * @param query sql query
	 * @param <M> instance of non abstract child of KittyModel
	 * @return collection of models or null if nothing found and returnNullNotEmptyCollection is on
	 */
	public final <M extends KittyModel> List<M> findWithRawQuery(boolean rowIdSupport, KittySQLiteQuery query) {
		logQuery(QE_FIND_WITH_RAW_QUERY, query);
		Cursor cursor = database.rawQuery(query.getSql(), query.getConditionValues());

		if (cursor == null)
			if(returnNullNotEmptyCollection)
				return null;
			else
				return new ArrayList<M>();
		else {
			List<M> out = new ArrayList<>(cursor.getCount());

			if(cursor.moveToFirst()) {
				do {
					try {
						//out.add((M) cursorToModel(rowIdSupport, cursor, this.getBlankModelInstance(), tableConfig));
						out.add((M) cursorToModelFactory.cursorToModel(cursor, this.getBlankModelInstance(), rowIdSupport));
					} catch (Exception ame) {
						if (ame instanceof KittyRuntimeException)
							throw (KittyRuntimeException) ame;
						else {
							Log.e(databaseHelper.helperConfiguration.logTag, "Exception caught", ame);
							throw new KittyRuntimeException(AME_FW_EXCEPTION, ame);
						}
					}
				} while (cursor.moveToNext());
			}
			cursor.close();
			if(out.size() == 0) {
				if(returnNullNotEmptyCollection)
					return null;
				else
					return new ArrayList<M>();
			}
			return out;
		}

	}

	protected static final String QE_EXECUTE_RAW_QUERY = "KittyMapper#executeRawQuery(String querySQL, String... parameters)";

	/**
	 * Execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE.
	 * @param querySQL the SQL statement to be executed. Multiple statements separated by semicolons are
	 * not supported.
	 * @param parameters only byte[], String, Long and Double are supported in bindArgs.
	 * @throws SQLException if the SQL string is invalid
	 */
	public final void executeRawQuery(String querySQL, String... parameters) {
		logQuery(QE_EXECUTE_RAW_QUERY, new KittySQLiteQuery(querySQL, null));
		database.execSQL(querySQL, parameters);
	}
	
	/**
	 * Returns model class associated with this mapper child
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final <M extends KittyModel> Class<M> getModelClass() {
		return ((Class<M>) this.blankModelInstance.getClass());
	}
	
	/**
	 * Creates new model via default constructor
	 * You should rewrite it, I suppose, for better performance
	 * cause it uses one more reflection magic piece,
	 * however, you know, it makes model classes more pretty to look
	 * also, if you are big fan of entropy and want to make
	 * death of universe closer, you also can rewrite this method
	 * for parametrized models with {@link Context} for serializing
	 * objects to be stored in databaseClass (you're sick!)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <M extends KittyModel> M getBlankModelInstance() {
		try {
			return (M) this.blankModelInstance.clone();
		} catch (Exception e) {
			throw new KittyRuntimeException(format(AME_BMI_ERROR, this.getModelClass().getCanonicalName()), e);
		}
	}

	// CLONING IMPLEMENTATIONS

	/**
	 * Clones blank model associated with this data mapper
	 * @param modelClass
	 * @param <M>
	 * @return
	 */
	protected <M extends KittyModel> M cloneModel(Class<M> modelClass) {
		return blankModelInstance.clone(modelClass);
	}

	/**
	 * Cloning implementation with generic
	 * @param recordClass
	 * @param <D>
	 * @return
	 */
	public <D extends KittyMapper> D clone(Class<D> recordClass) {
		D record = (D) this.clone();
		return record;
	}

	/**
	 * Cloning implementation
	 * @return
	 */
	@Override
	public KittyMapper clone() {
		try {
			KittyMapper record = (KittyMapper) super.clone();
			return record;
		} catch (CloneNotSupportedException e) {
			throw new KittyRuntimeException(format(AME_UNBLE_TO_CLONE, this.getClass().getCanonicalName()), e);
		}
	}

	// HELPER METHODS (CONDITIONS)

	/**
	 * Returns PK condition for provided model, there are following steps:
	 * <br>If provided default condition not null, it would be returned.
	 * <br>If model's RowID set than condition would be "WHERE rowid = ?".
	 * <br>If model's getPrimaryKeyValues() not null than condition would be created from those
	 * values.
	 * <br>If model's RowID and getPrimaryKeyValues() null than DataMapper would try to acquire
	 * PK fields and values via reflection
	 * according to {@link KittyPrimaryKey} instance set in {@link KittyTableConfiguration} for provided
	 * model.
	 * <br>If no default condition, rowid, and no PK than null would be returned.
	 * @param model KittyModel child instance
	 * @param defaultCondition default condition, may be null, if not null than it would be returned
	 * @param <M> not abstract child of {@link KittyModel}
	 * @return SQLiteCondition that can fully define provided model as model in database (or null
	 * if it is impossible or provided SQLiteCondition passed as parameter if it is not null)
	 * @throws KittyRuntimeException throws exceptions wrapped into {@link KittyRuntimeException}
	 */
	protected final <M extends KittyModel> SQLiteCondition getPKCondition(M model, SQLiteCondition defaultCondition) {
		if (defaultCondition == null) {
			if (model.getRowID() == null) {
				if(tableConfig.kittyPrimaryKey != null || model.getPrimaryKeyValues() != null) {
					Map<String, String> pkValues;
					if(model.getPrimaryKeyValues() != null) {
						pkValues = model.getPrimaryKeyValues();
					} else {
						pkValues = getPrimaryKeyValuesForModel(model);
					}
					if(pkValues == null)
						return null;
					else if(pkValues.size() == 0)
						return null;
					else {
						return getSQLiteConditionForPKKeyValues(pkValues);
					}
				} else {
					return null;
				}
			} else {
				SQLiteConditionBuilder cb = new SQLiteConditionBuilder();
				cb.addColumn(ROWID)
						.addSQLOperator(SQLiteOperator.EQUAL)
						.addValue(model.getRowID());
				return cb.build();
			}
		} else {
			return defaultCondition;
		}
	}

	/**
	 * Returns SQLiteCondition from provided PK's map where keys of map are column's names of table and
	 * values are values of those columns. Returns null if provided PK's key value map null or has zero size.
	 * @param primaryKey PK with bounded values to use
	 * @return SQLiteCondition ready to use
	 */
	protected final SQLiteCondition getSQLiteConditionForPK(KittyPrimaryKey primaryKey) {
		return getSQLiteConditionForPKKeyValues(primaryKey.getPrimaryKeyColumnValues());
	}

	/**
	 * Returns SQLiteCondition from provided map where keys of map are column's names of table and
	 * values are values of those columns. Returns null if provided pkKeyValues null or has zero size.
	 * @param pkKeyValues map of column names and values to create SQLiteCondition
	 * @return SQLiteCondition ready to use
	 */
	protected final SQLiteCondition getSQLiteConditionForPKKeyValues(Map<String, String> pkKeyValues) {
		if(pkKeyValues == null) return null;
		if(pkKeyValues.size() == 0) return null;
		SQLiteConditionBuilder cb = new SQLiteConditionBuilder();
		int counter = 0; int mapSize = pkKeyValues.size();
		for (Map.Entry<String, String> kvk : pkKeyValues.entrySet()) {
			if(kvk.getValue() == null)
				return null;
			counter++;
			cb.addColumn(kvk.getKey())
					.addSQLOperator(SQLiteOperator.EQUAL)
					.addValue(kvk.getValue());
			if(counter<mapSize)
				cb.addSQLOperator(SQLiteOperator.AND);
		}
		return cb.build();
	}

	/**
	 * Tries to return primary key values from provided model as map where keys are column names of PK
	 * and values are string representations of PK's values from model's fields.
	 * If any of field's values of PK are NULL than ORM thinks that model is newly created so it is impossible
	 * to create PK condition from provided model. SQLite permits using NULL as values for PK, however
	 * SQL language not, so do I.
	 * @param model model from what should be PK condition be generated
	 * @param <M> not abstract child of {@link KittyModel}
	 * @return PK condition or null if it is impossible to create it
	 */
	protected final <M extends KittyModel> Map<String, String> getPrimaryKeyValuesForModel(M model) {
		Map<String, String> out = new HashMap<>();
		for(String pkp : tableConfig.kittyPrimaryKey.getPrimaryKeyColumnNames()) {
			// checking for auto generated fields
			try {
				String columnName = tableConfig.kittyPrimaryKey.getPrimaryKeyPart(pkp).getField().getName();
				String strRepresentation = KittyReflectionUtils.getStringRepresentationOfModelField(model, columnName);
				// If NULL than it is not permitted pk value fetched from database, SQLite permits NULL as value for PK column
				// but SQL doesn't permit this, so do I
				if(strRepresentation == null)
					return null;
				out.put(pkp, strRepresentation);
			} catch (Exception e) {
				if(e instanceof KittyRuntimeException)
					throw (KittyRuntimeException) e;
				throw new KittyRuntimeException(format(AME_GPKVFM_EX, model.getClass().getCanonicalName()), e);
			}
		}
		if(out.isEmpty()) return null;
		return out;
	}

	/**
	 * Creates condition "WHERE {IPK} = ?" with provided model.
	 * @param id
	 * @return
	 */
	protected final SQLiteCondition getIPKCondition(Long id) {
		if(tableConfig.kittyPrimaryKey != null) {
			if(tableConfig.kittyPrimaryKey.isIPK()) {
				SQLiteConditionBuilder cb = new SQLiteConditionBuilder();
				cb.addColumn(tableConfig.kittyPrimaryKey.getPrimaryKeyColumnNames()[0])
						.addSQLOperator(SQLiteOperator.EQUAL)
						.addValue(id);
				return cb.build();
			}
		}
		return null;
	}

	/**
	 * Creates condition "WHERE rowid = ?" with provided model.getRowID. If id is null than null would be returned.
	 * @param model model
	 * @param <M> instance of not abstract child of {@link KittyModel}
	 * @return SQLiteCondition "WHERE rowid = ?"
	 */
	protected final <M extends KittyModel> SQLiteCondition getRowIDCondition(M model) {
		return getRowIDCondition(model.getRowID());
	}

	/**
	 * Creates condition "WHERE rowid = ?" with provided id. If id is null than null would be returned.
	 * @param id RowID value
	 * @return SQLiteCondition "WHERE rowid = ?"
	 * <br> I hate people. I hate myself. I have no friends, money or health.
	 * <br> I ran my company and it wasn't successful. I got good job and failed it. For three years I have no income. I moved to my parents and rent my apartments, working on two projects with some hope.
	 * <br> My first love was a whore (literally, she fucked with four guys and lied to all of them). Fucking first love. Bad days. You know when you are young
	 * <br> you're not spoiled enough and stupid. You think that all will be good and love is love, not just word with what people hide animal instincts.
	 * <br> I'm working on this shit for half a year. My ex-wife betrayed me. She cheated on me and
	 * <br> threw me as a piece of used paper via text message in viber in two weeks before NY. This cunt fucked with me when decided to leave me. She wanted to be pregnant and make a child from me to get benefits while
	 * <br> was dating with other guy. In a moment she forgot all good things that I done to her. I even didn't lie to her in all our relations and for my honest I received only lie and hate.
	 * <br> Now I hate her and want her be unhappy and lonely for the rest of her fucking life. But seems she is ok and this fact piss me off.
	 * <br> People do not get what they deserve. I'm working hard and I was a good guy. Once I saved drunk man from death, for example. I was always good to people. But I do not need.
	 * <br> I'm near 27 ears old and have nothing. All my friend are buddies from online games, only VoIP. I have big problems with my health. My back, my head, my stomach and other liver.
	 * <br> And there is even no God. Fuck, this is not some kind of Lord's test or whatever. This is my shitty life that would be ended sometimes and nothing would happen.
	 * <br> Just dead corpse buried in ground and nothing more.
	 * <br> But I do not give up. Why? I have no alternative. This is my shitty life but it's the only life I have. Fucking disappointment.
	 */
	protected final SQLiteCondition getRowIDCondition(Long id) {
		if(id == null) return null;
		SQLiteConditionBuilder cb = new SQLiteConditionBuilder();
		return cb.addColumn(ROWID).addSQLOperator(SQLiteOperator.EQUAL).addValue(id).build();
	}

	// HELPER METHODS (LOGGING)

	/**
	 * Logs query or fake query to log
	 * @param executor
	 * @param query
	 */
	protected final void logQuery(String executor, KittySQLiteQuery query) {
		logQuery(
				executor, query, logTag, logOn,
				databaseHelper.helperConfiguration.productionOn,databaseHelper,
				databaseHelper.helperConfiguration.schemaName,
				databaseHelper.helperConfiguration.schemaVersion
		);
	}

	/**
	 * Logs model to log if logOn and !productionOn
	 * @param executor
	 * @param model
	 * @param <M>
	 */
	protected final <M extends KittyModel> void logModel(String executor, M model) {
		logModel(executor, model, logTag, logOn, databaseHelper.helperConfiguration.productionOn,
				databaseHelper, databaseHelper.helperConfiguration.schemaName,
				databaseHelper.helperConfiguration.schemaVersion
		);
	}

	public static final String QUERY_LOG_PATTERN = "Query generated and sent to be executed by {0} [helper: {1}, schema: {2} v. {3} ] : {4}";

	/**
	 * Logs query to log depends on parameter flags
	 * @param executor
	 * @param query
	 * @param logTag
	 * @param logOn
	 * @param productionMode
	 * @param helper
	 * @param schemaName
	 * @param schemaVersion
	 */
	public static final void logQuery(String executor, KittySQLiteQuery query, String logTag,
									  boolean logOn, boolean productionMode, Object helper, String schemaName, int schemaVersion) {
		if(logOn && !productionMode)
			Log.w(logTag, format(
					QUERY_LOG_PATTERN, executor, helper.getClass().getCanonicalName(), schemaName,
					schemaVersion, query.toString()
					)
			);
	}

	public static final String MODEL_LOG_PATTERN = "Model used for {0} [helper: {1}, schema: {2} v. {3}, model: {4} ] : {5}";
	public static final String MODEL_NOT_IMPL = "Model has no implemented toLogString() method or it returned NULL!";

	/**
	 * Logs model to log depends on parameter flags
	 * @param executor
	 * @param model
	 * @param logTag
	 * @param logOn
	 * @param productionOn
	 * @param helper
	 * @param schemaName
	 * @param schemaVersion
	 * @param <M>
	 */
	public static final <M extends KittyModel> void logModel(String executor, M model, String logTag,
															 boolean logOn, boolean productionOn,
															 Object helper, String schemaName, int schemaVersion) {
		if(logOn && !productionOn) {
			String modelLogString = model.toLogString();
			if(modelLogString == null)
				modelLogString = MODEL_NOT_IMPL;
			Log.w(logTag, format(
					MODEL_LOG_PATTERN, executor, helper.getClass().getCanonicalName(), schemaName,
					schemaVersion, model.getClass().getCanonicalName(), modelLogString
					)
			);
		}
	}

	/**
	 * Returns string like '( key1 = value1 ; key2 = value2 )' for provided pkv.
	 * <br> If pkv null or it's size = 0 than string '( )' would be returned.
	 * @param pkv map for PK
	 * @return string like '( key1 = value1 ; key2 = value2 )' or '( )' depends on pkv
	 */
	protected final String pkvMapToString(Map<String, String> pkv) {
		StringBuffer sb = new StringBuffer(128);
		if(pkv == null)
			return sb.append(LEFT_BKT).append(WHITESPACE).append(RIGHT_BKT).toString();
		if(pkv.size() == 0)
			return sb.append(LEFT_BKT).append(WHITESPACE).append(RIGHT_BKT).toString();
		sb.append(LEFT_BKT).append(WHITESPACE);
		int counter = 0;
		for(Map.Entry<String, String>  kv : pkv.entrySet()) {
			counter++;
			sb.append(kv.getKey()).append(EQUAL_SIGN).append(kv.getValue()).append(WHITESPACE);
			if(counter<pkv.size()) {
				sb.append(SEMICOLON).append(WHITESPACE);
			} else {
				sb.append(RIGHT_BKT);
			}
		}
		return sb.toString();
	}

	static private String KITTY_EXCEPTION_TEXT_NO_ASS_CLN_FOR_FN = "No associated column name found for provided field name ({0}) at {1} with model {2}!";

	/**
	 * Generates instance of {@link SQLiteCondition} from SQLite string and set of parameters.
	 * <br> Actually, it is {@link SQLiteConditionBuilder#fromSQL(String, Class, Object...)} but there is no
	 * need to pass model class cause mapper already knows it
	 * <br> So, better to refactor, cause a lot of same code
	 * @param condition
	 * @param params
	 * @return
	 */
	protected final SQLiteCondition conditionFromSQLString(String condition, Object... params) {
		// Getting str collection for parameters
		LinkedList<String> conditionArgs = new LinkedList<String>();
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				conditionArgs.addLast(KittyReflectionUtils.getSQLiteStringRepresentation(params[i]));
			}
		}
		String[] arguments = conditionArgs.toArray(new String[conditionArgs.size()]);
		// OK, trim and check if string condition starts with WHERE ignore case
		condition = condition.trim();
		if(condition.length() >= WHERE.length()) {
			String firstChars = condition.substring(0, WHERE.length() - 1);
			if(firstChars.toLowerCase().startsWith(WHERE))
				condition.replace(firstChars, EMPTY_STRING);
		}
		// Second step, we do the following thing: check occurrences of model field names (starts with #? and ends with ;)
		if(condition.contains(MODEL_FIELD_START)) {
			String[] parts = condition.split(WHSP_STR);
			StringBuilder rebuildCondition = new StringBuilder();
			for(String part : parts) {
				String trimmedPart = part.trim();
				if(trimmedPart == null) continue;
				if(trimmedPart.equals(WHSP_STR)) continue;
				if(trimmedPart.equals(EMPTY_STRING)) continue;
				if(rebuildCondition.length() > 0) rebuildCondition.append(WHITESPACE);
				if(trimmedPart.startsWith(MODEL_FIELD_START)) {
					//String fieldName = trimmedPart.replaceAll(MODEL_FIELD_START, EMPTY_STRING).replaceAll(MODEL_FIELD_END, EMPTY_STRING);
					String fieldName = trimmedPart.replaceAll("#\\?", EMPTY_STRING).replaceAll(MODEL_FIELD_END, EMPTY_STRING);
					KittyColumnConfiguration cf = getColumnByFieldName(fieldName);
					if(cf == null)
						throw new KittyRuntimeException(
								format(
										KITTY_EXCEPTION_TEXT_NO_ASS_CLN_FOR_FN,
										fieldName,
										this.getClass().getCanonicalName(),
										this.blankModelInstance.getClass().getCanonicalName()
								)
						);
					rebuildCondition.append(cf.mainConfiguration.columnName);
				} else {
					rebuildCondition.append(trimmedPart);
				}
			}
			return new SQLiteCondition(rebuildCondition.toString(), arguments);
		}
		return new SQLiteCondition(condition.toString(), arguments);
	}

	/**
	 * Returns column name associated with model field name or null if nothing found
	 * @param fieldName
	 * @return
	 */
	public KittyColumnConfiguration getColumnByFieldName(String fieldName) {
		if(columnConfigurations.size() == 0) {
			Iterator<KittyColumnConfiguration> iterator = tableConfig.sortedColumns.iterator();
			while(iterator.hasNext()) {
				KittyColumnConfiguration cf = iterator.next();
				columnConfigurations.put(cf.mainConfiguration.columnField.getName(), cf);
			}
		}
		return columnConfigurations.get(fieldName);
	}
}
