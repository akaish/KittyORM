
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


import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import net.akaish.kitty.orm.annotations.KITTY_DATABASE_HELPER;
import net.akaish.kitty.orm.configuration.conf.KittyDBHelperConfiguration;
import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;
import net.akaish.kitty.orm.dumputils.migrations.KittyDevDropCreateMigrator;
import net.akaish.kitty.orm.dumputils.migrations.KittyMigration;
import net.akaish.kitty.orm.dumputils.migrations.KittyORMVersionFileDumpMigrator;
import net.akaish.kitty.orm.dumputils.migrations.KittyORMVersionMigrator;
import net.akaish.kitty.orm.dumputils.migrations.KittySimpleMigrator;
import net.akaish.kitty.orm.dumputils.scripts.KittySQLiteAssetsFileDumpScript;
import net.akaish.kitty.orm.dumputils.scripts.KittySQLiteDumpScript;
import net.akaish.kitty.orm.dumputils.scripts.KittySQLiteFileDumpScript;
import net.akaish.kitty.orm.exceptions.KittyDBVersionMismatchException;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.query.CreateDropHelper;
import net.akaish.kitty.orm.query.KittyQueryBuilder;
import net.akaish.kitty.orm.query.KittySQLiteQuery;
import net.akaish.kitty.orm.util.KittyLog;
import net.akaish.kitty.orm.util.KittyNamingUtils;
import net.akaish.kitty.orm.util.KittyUtils;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.query.KittyQueryBuilder.QUERY_TYPE.SELECT_TABLE_NAMES;
import static net.akaish.kitty.orm.util.KittyConstants.EMPTY_STRING;
import static net.akaish.kitty.orm.util.KittyConstants.TYPE_NAME_COLUMN;
import static net.akaish.kitty.orm.util.KittyLog.LOG_LEVEL.D;
import static net.akaish.kitty.orm.util.KittyLog.LOG_LEVEL.E;
import static net.akaish.kitty.orm.util.KittyLog.LOG_LEVEL.I;
import static net.akaish.kitty.orm.util.KittyLog.LOG_LEVEL.W;
import static net.akaish.kitty.orm.util.KittyLog.kLog;
import static net.akaish.kitty.orm.util.KittyNamingUtils.getDropSchemaDefaultFilename;

/**
 * Helper implementation to be used with KittyORM with support of database version migrations and with
 * implementation of cloning interface.
 * <br>
 * <br> <b>(!) There is no need to annotate this class or it's child with {@link KITTY_DATABASE_HELPER} annotation.
 * {@link KITTY_DATABASE_HELPER} annotation should be applied only to implementations of {@link KittyDatabase}. </b>
 * @author akaish (Denis Bogomolov)
 */
public class KittyDatabaseHelper extends SQLiteOpenHelper implements Cloneable {

	private static final String LOG_I_ON_UPGRADE = "onUpgrade call for helper {0} for schema {1} v. {2} : starting upgrading routine";
	private static final String LOG_I_ON_UPGRADE_NO_ACTION = "onUpgrade call for helper {0} for schema {1} v. {2} : no action selected";
	private final static String LOG_W_HELPER_DS_AME = "Caught exception while getting dumped script from {0}! See exception details!";
	private static final String LOG_W_NO_VALID_DUMP_FILE = "Provided file dump path ({0}) not valid (no file/file not readable/it is dir/whatever) at {1} for schema {2} ver. {3}";
	private static final String AME_UNABLE_TO_MIGRATE = "Unable to migrate database {0} from version {1} to version {2}, migration sequence not applicable!";
	private static final String LOG_I_MG_APPLYING = "Applying migration patch {0}-{1}-{2}-{3} for databaseClass {4} (from ver. {5} to ver. {6})!";
	private static final String LOG_I_ON_CREATE = "onCreate call for helper {0} for schema {1} v. {2}";
	private static final String LOG_CE_NO_CREATION_SCRIPT_DEFINED = "Database helper {0} for schema {1} v. {2} has no schema creation script defined!";
	private static final String EXCEPTION_UNABLE_TO_CLONE = "Unable to clone databaseClass helper class instance, see exception details!";
	private static final String EXCEPTION_GET_RW_DB_ENCRYPTED = "Not implemented, you should override this method in child implementation in order to use db encryption!";
	private static final String EXCEPTION_GET_CUSTOM_MIGRATOR = "Not implemented, you should override this method in child implementation in order to use your owm custom migrator implementation!";

	private final static String LOG_E_UNABLE_TO_FINISH_TRANSACTION = "Unable to finish transaction at {0} database helper for schema {1}, see exception details!";
	private final static String LOG_CE_UNABLE_TO_FINISH_ON_CREATE_SEQUENCE = "Critical error, unable to finish onCreate transaction error at {0} database helper for schema {1} v. {2}, see exception details!";

	private static String DC_CE_NO_CREATE_SEQUENCE_IN_ABDSS = "Unable to generate drop tables script at #acquireBestDropScriptSequence(int oldDatabaseVersion, SQLiteDatabase database) for helper: {0} and schema: {1} v. {2}";
	private static String LOG_I_ABDSS_FILEDUMP_MESSAGE = "Fetching drop script sequence at #acquireBestDropScriptSequence(int oldDatabaseVersion, SQLiteDatabase database) from file dump: {0} for helper: {1} and schema: {2} v. {3}";
	private static String LOG_I_ABDSS_FILEDUMP_MESSAGE_OK = "Drop script sequence at #acquireBestDropScriptSequence(int oldDatabaseVersion, SQLiteDatabase database) from file dump: {0} fetched for helper: {1} and schema: {2} v. {3}";
	private static String LOG_I_ABDSS_FILEDUMP_FAILED_RETURNING_PREGEN = "Unable to fetch drop script from file dump, returning predefined drop script for helper: {0} and schema: {1} v. {2}";
	private static String LOG_I_ABDSS_GENERATING_DROP_SCRIPT_FROM_TABLE_LIST = "Generating drop script from tables list for helper: {0} and schema: {1} v. {2} ...";

	private static String LOG_I_ABCSS_GETTING_CREATE_SCRIPT_FROM_FILE = "Trying to read create script file dump located at: {0} ; helper: {1} and schema: {2} v. {3} ...";
	private static String LOG_I_ABCSS_UNABLE_TO_GET_DATA_FROM_FILE_RETURNING_PREGEN = "Unable to read create file dump script, returning predefined create script instead ; helper: {0} and schema: {1} v. {2}";

	private static String LOG_I_MIGRATION_ROUTINE_STARTED = "Migration routine started at helper {0} for schema {1} v. {2} ...";
	private static String LOG_I_MIGRATION_INFO = "Migration obj: {0}";
	private static String LOG_E_MIGRATION_TRANSACTION_FAILED = "Migration transaction failed at helper {0} for schema {1} v. {2}! See exception details...";

	private static String LOG_I_ON_UPGRADE_DROP_AND_CREATE = "DropCreate migration option selected at onUpdate method of helper {0} for schema {1} v. {2} (!)";
	private static String LOG_I_ON_UPGRADE_USE_FILE_MIGRATIONS = "File dump migration option selected at onUpdate method of helper {0} for schema {1} v. {2} (!)";;
	private static String LOG_I_ON_UPGRADE_USE_CUSTOM_MIGRATOR = "Custom migration implementation option selected at onUpdate method of helper {0} for schema {1} v. {2} (!)";
	private static String LOG_I_ON_UPGRADE_USE_SIMPLE_AUTOGEN_MIGRATIONS = "Simple autogenerated migration option selected at onUpdate method of helper {0} for schema {1} v. {2} (!)";;

	private static String LOG_I_KDHI = "KittyDatabaseHelper or KDH implementation started; helper {0} for schema {1} v. {2}!";

	protected static String DC_CE_NO_DROP_SEQUENCE = "No drop sequence generated for helper: {0} and schema: {1} v. {2}";
	protected static String DC_CE_NO_CREATE_SEQUENCE = "No create sequence generated for helper: {0} and schema: {1} v. {2}";

	private static String LOG_I_FK_SUPPORT_15 = "Setting foreign key support ON (API 15 or lower) for helper {0}; Schema {1} v. {2}";
	private static String LOG_I_FK_SUPPORT_16 = "Setting foreign key support ON (API 16 or higher) for helper {0}; Schema {1} v. {2}";

	private static final String LOG_I_ON_CREATE_APPLYING_AFTER_CREATE_SCRIPT = "onCreate; applying after create script sequence for helper {0} for schema {1} v. {2}";
	private static final String LOG_I_ON_CREATE_AFTER_CREATE_SCRIPT_NOT_DEFINED_AT_DB_IMPL = "onCreate; after create script sequence not defined for helper {0} for schema {1} v. {2}, trying to generate it from file {3}";
	private static final String LOG_I_ON_CREATE_AFTER_CREATE_SCRIPT_SCRIPT_FROM_FILE = "onCreate; after create script successfully acquired for helper {0} for  for schema {1} v. {2} from file {3}; applying it";
	private static final String LOG_I_ON_CREATE_NO_AFTER_CREATE_SCRIPT = "onCreate; no valid after create script sequence found for helper {0} for schema {1} v. {2}";
	private static final String LOG_I_ON_CREATE_APPLYING_AFTER_CREATE_DB_IMP_SCRIPT = "onCreate; applying after create script sequence defined at KittyORM database implementation for helper {0} for schema {1} v. {2}";
	private static final String LOG_I_ON_CREATE_USING_EXTERNAL_DB_EXITING_ON_CREATE = "onCreate; using external database, skipping onCreate routine: ";

	private static final String LOG_I_ON_UPDATE_APPLYING_AFTER_MIGRATE_SCRIPT = "onUpdate; applying after migrate script sequence for helper {0} for schema {1} v. {2}";
	private static final String LOG_I_ON_UPDATE_AFTER_MIGRATE_SCRIPT_NOT_DEFINED_AT_DB_IMPL = "onUpdate; after migrate script sequence not defined for helper {0} for schema {1} v. {2}, trying to generate it from file {3}";
	private static final String LOG_I_ON_UPDATE_AFTER_MIGRATE_SCRIPT_FROM_FILE = "onUpdate; after migrate script successfully acquired for helper {0} for  for schema {1} v. {2} from file {3}; applying it";
	private static final String LOG_I_ON_UPDATE_CREATE_NO_AFTER_MIGRATE_SCRIPT = "onUpdate; no valid after migrate script sequence found for helper {0} for schema {1} v. {2}";
	private static final String LOG_I_ON_UPDATE_CREATE_APPLYING_AFTER_MIGRATE_DB_IMP_SCRIPT = "onUpdate; applying after migrate script sequence defined at KittyORM database implementation for helper {0} for schema {1} v. {2}";
    private static final String LOG_I_ON_UPDATE_USING_EXTERNAL_DB_EXITING_ON_CREATE = "onUpdate; using external database, skipping onCreate routine: ";

	protected static final String PRAGMA_ON = "PRAGMA foreign_keys=ON;";
	protected final KittyDBHelperConfiguration helperConfiguration;
	protected final KittyDatabaseConfiguration databaseConfiguration;
	protected final Context context;

	protected LinkedList<KittySQLiteQuery> createSchemaAutogeneratedScript;
	protected LinkedList<KittySQLiteQuery> dropSchemaAutogeneratedScript;

	protected KittySQLiteDumpScript afterCreateScript = null;
	protected KittySQLiteDumpScript afterMigrateScript = null;

	/**
	 * Create a KittyORM helper object to create, open, and/or manage a database.
	 * This method always returns very quickly.  The database is not actually
	 * created or opened until one of {@link #getWritableDatabase} or
	 * {@link #getReadableDatabase} is called.
	 * <br>
	 * <br> <b>(!) There is no need to annotate this class or it's child with {@link KITTY_DATABASE_HELPER} annotation.
	 * {@link KITTY_DATABASE_HELPER} annotation should be applied only to implementations of {@link KittyDatabase}. </b>
	 *
	 * @param context Android context.
	 * @param helperCfg KittyORM helper configuration instance.
	 */
	public KittyDatabaseHelper(Context context, KittyDBHelperConfiguration helperCfg, KittyDatabaseConfiguration conf) {
		super(context, getDBPathOrName(conf, helperCfg), null, helperCfg.schemaVersion);
		this.helperConfiguration = helperCfg;
		this.context = context;
		this.databaseConfiguration = conf;
		if(helperCfg.logOn) {
			String message = format(
					LOG_I_KDHI,
					getClass().getCanonicalName(),
					conf.schemaName,
					conf.schemaVersion
			);
			log(I, message, false, null);
		}
	}

	/**
	 * Create a helper object to create, open, and/or manage a database.
	 * This method always returns very quickly.  The database is not actually
	 * created or opened until one of {@link #getWritableDatabase} or
	 * {@link #getReadableDatabase} is called.
	 * <br>
	 * <br> <b>(!) There is no need to annotate this class or it's child with {@link KITTY_DATABASE_HELPER} annotation.
	 * {@link KITTY_DATABASE_HELPER} annotation should be applied only to implementations of {@link KittyDatabase}. </b>
	 *
	 * @param context Android context.
	 * @param helperCfg KittyORM helper configuration instance.
	 * @param cursorFactory cursor factory to use instead default one.
	 */
	public KittyDatabaseHelper(Context context, KittyDBHelperConfiguration helperCfg, KittyDatabaseConfiguration conf,
							   SQLiteDatabase.CursorFactory cursorFactory) {
		super(context, getDBPathOrName(conf, helperCfg), cursorFactory, helperCfg.schemaVersion);
		this.helperConfiguration = helperCfg;
		this.context = context;
		this.databaseConfiguration = conf;
		if(helperCfg.logOn) {
			String message = format(
					LOG_I_KDHI,
					getClass().getCanonicalName(),
					conf.schemaName,
					conf.schemaVersion
			);
			log(I, message, false, null);
		}
	}

	/**
	 * Create a helper object to create, open, and/or manage a database.
	 * This method always returns very quickly.  The database is not actually
	 * created or opened until one of {@link #getWritableDatabase} or
	 * {@link #getReadableDatabase} is called.
	 * @param context Android context.
	 * @param helperCfg KittyORM helper configuration instance.
	 * @param cursorFactory cursor factory to use instead default one.
	 * @param createSchemaAutogeneratedScript predefined create script sequence for this database.
	 *                                        See {@link #setCreateSchemaAutogeneratedScript(LinkedList)}
	 *                                        for more details.
	 * @param dropSchemaAutogeneratedScript predefined drop script sequence for this database.
	 *                                      See {@link #setDropSchemaAutogeneratedScript(LinkedList)}
	 *                                      for more details.
	 */
	public KittyDatabaseHelper(Context context, KittyDBHelperConfiguration helperCfg,
							   KittyDatabaseConfiguration conf,
							   SQLiteDatabase.CursorFactory cursorFactory,
							   LinkedList<KittySQLiteQuery> createSchemaAutogeneratedScript,
							   LinkedList<KittySQLiteQuery> dropSchemaAutogeneratedScript,
							   KittySQLiteDumpScript afterCreateScript, KittySQLiteDumpScript afterMigrateScript) {
		super(context, getDBPathOrName(conf, helperCfg), cursorFactory, helperCfg.schemaVersion);
		this.helperConfiguration = helperCfg;
		this.context = context;
		this.databaseConfiguration = conf;
		this.createSchemaAutogeneratedScript = createSchemaAutogeneratedScript;
		this.dropSchemaAutogeneratedScript = dropSchemaAutogeneratedScript;
		this.afterCreateScript = afterCreateScript;
		this.afterMigrateScript = afterMigrateScript;
		if(helperCfg.logOn) {
			String message = format(
					LOG_I_KDHI,
					getClass().getCanonicalName(),
					conf.schemaName,
					conf.schemaVersion
			);
			log(I, message, false, null);
		}
	}

    /**
     * Returns {@link KittyDatabaseConfiguration#databaseFilePath} if not null and if
     * {@link KittyDatabaseConfiguration#useExternalFilepath} is true.
     * <br> Otherwise returns {@link KittyDBHelperConfiguration#schemaName}
     * @param dbConf
     * @param helperConfiguration
     * @return
     */
	private static final String getDBPathOrName(KittyDatabaseConfiguration dbConf, KittyDBHelperConfiguration helperConfiguration) {
	    if(dbConf.useExternalFilepath && dbConf.databaseFilePath != null)
	        return dbConf.databaseFilePath;
	    else
	        return helperConfiguration.schemaName;
    }

	/**
	 * Sets CREATE schema script sequence wrapped into instances of {@link KittySQLiteQuery} to be used
	 * in {@link #onCreate(SQLiteDatabase)} and {@link #onUpgrade(SQLiteDatabase, int, int)}.
	 * <br> This script would be used in following cases:
	 * <br> In {@link #onCreate(SQLiteDatabase)} if {@link #getFileScript(String, boolean)}
	 * returned null (buy default this method returns LinkedList of {@link KittySQLiteQuery} fetched from
	 * dump file)
	 * <br> In {@link #onUpgrade(SQLiteDatabase, int, int)} if {@link #getFileVersionMigrator(int, int)} returned
	 * null or helper forced by helper configuration to use drop and created migrator
	 * @param createSchemaAutogeneratedScript
	 */
	public void setCreateSchemaAutogeneratedScript(LinkedList<KittySQLiteQuery> createSchemaAutogeneratedScript) {
		this.createSchemaAutogeneratedScript = createSchemaAutogeneratedScript;
	}

	/**
	 * Sets after create schema script sequence as dump script. Script set from this method had higher priority
	 * than any script acquired from any other source.
	 * @param afterCreateScript
	 */
	public void setAfterCreateScript(KittySQLiteDumpScript afterCreateScript) {
		this.afterCreateScript = afterCreateScript;
	}

	/**
	 * Sets after migrate schema script sequence as dump script. Script set from this method had higher priority
	 * than any script acquired from any other source.
	 * @param afterMigrateScript
	 */
	public void setAfterMigrateScript(KittySQLiteDumpScript afterMigrateScript) {
		this.afterMigrateScript = afterMigrateScript;
	}

	/**
	 * Sets DROP schema script sequence wrapped into instances of {@link KittySQLiteQuery} to be used
	 * in {@link #onUpgrade(SQLiteDatabase, int, int)}.
	 * <br> This script would be used in following case:
	 * In {@link #onUpgrade(SQLiteDatabase, int, int)} if {@link #getFileVersionMigrator(int, int)} returned
	 * null or helper forced by helper configuration to use drop and created migrator
	 * <br> Also notice, that if you want to use helper onUpgrade method with simple drop create migrator,
	 * you can skip this method and ALL tables would be automatically deleted (only {@link #setCreateSchemaAutogeneratedScript(LinkedList)})
	 * should be set.
	 * @param dropSchemaAutogeneratedScript
	 */
	public void setDropSchemaAutogeneratedScript(LinkedList<KittySQLiteQuery> dropSchemaAutogeneratedScript) {
		this.dropSchemaAutogeneratedScript = dropSchemaAutogeneratedScript;
	}

	/**
	 * Returns writable databaseClass object created and\or getWritableDatabase with w access and with use of
	 * <br> databaseClass encryption implementation
	 * @param pwd databaseClass password
	 * @return encrypted writable databaseClass object created and\or getWritableDatabase with w access
	 * @throws KittyRuntimeException if call from {@link KittyDatabaseHelper} (not implemented here)
	 */
	public SQLiteDatabase getWritableDatabase(String pwd) {
		throw new KittyRuntimeException(EXCEPTION_GET_RW_DB_ENCRYPTED);
	}

	/**
	 * Returns readable databaseClass object created and\or getWritableDatabase with r access and with use of
	 * <br> databaseClass encryption implementation
	 * @param pwd databaseClass password
	 * @return encrypted readable databaseClass object created and\or getWritableDatabase with r access
	 * @throws KittyRuntimeException if call from {@link KittyDatabaseHelper} (not implemented here)
	 */
	public SQLiteDatabase getReadableDatabase(String pwd) {
		throw new KittyRuntimeException(EXCEPTION_GET_RW_DB_ENCRYPTED);
	}

	// Helper implementations
	/**
	 * Called when the database has been opened.
	 * <br> In this implementation foreign key support would be set for Android version 15 and lower
	 * if necessary.
	 * @param database
	 */
	@Override
	public void onOpen(SQLiteDatabase database) {
		super.onOpen(database);
		setPragmaKeysAPI15AndLower(database, helperConfiguration.pragmaOn);
	}

	/**
	 * Called when the database connection is being configured. In this implementation
	 * foreign keys support enabled for Android versions 16 and higher via
	 * {@link #setPragmaKeysAPI16(SQLiteDatabase, boolean)}
	 * @param database database instance.
	 */
	@Override
	public void onConfigure(SQLiteDatabase database) {
		setPragmaKeysAPI16(database, helperConfiguration.pragmaOn);
	}

	/**
	 * Sets usage of foreign keys for android versions 16 and higher
	 * @param database database instance.
	 * @param pragmaKeys on\off foreign key support
	 */
	@TargetApi(16)
	private void setPragmaKeysAPI16(SQLiteDatabase database, boolean pragmaKeys) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if(pragmaKeys)
				log(I, LOG_I_FK_SUPPORT_16, true, null);
			database.setForeignKeyConstraintsEnabled(pragmaKeys);
		}
	}

	/**
	 * Called when the database is created for the first time. This is where the
	 * creation of tables and the initial population of the tables happens.
	 * <br> In default kitty orm helper in this void are handled following things:
	 * <br> 1) Getting create script with {@link #acquireBestCreateScriptSequence()}, if it would
	 * be null than exception ({@link KittyRuntimeException}) would be thrown.
	 * <br> 2) Trying to run this script via {@link #runScriptSequenceInTransaction(LinkedList, SQLiteDatabase)},
	 * if it failed than exception ({@link KittyRuntimeException}) would be thrown.
	 * @param database The database.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		if(databaseConfiguration.useExternalFilepath && databaseConfiguration.databaseFilePath != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(LOG_I_ON_CREATE_USING_EXTERNAL_DB_EXITING_ON_CREATE)
					.append(databaseConfiguration.databaseFilePath);
			Log.i(databaseConfiguration.logTag, sb.toString());
			return;
		}
		log(I, LOG_I_ON_CREATE, true, null);
		// create manually at first time
		LinkedList<KittySQLiteQuery> creationScript = acquireBestCreateScriptSequence();
		if(creationScript == null) {
			throw new KittyRuntimeException(
					log(E, LOG_CE_NO_CREATION_SCRIPT_DEFINED, true, null)
			);
		}
		try {
			runScriptSequenceInTransaction(creationScript, database);
		} catch (KittyRuntimeException e) {
			log(E, LOG_CE_UNABLE_TO_FINISH_ON_CREATE_SEQUENCE, true, e);
			throw e;
		}
		log(I, LOG_I_ON_CREATE_APPLYING_AFTER_CREATE_SCRIPT, true, null);
		if(afterCreateScript == null) {
			if(helperConfiguration.logOn) {
				String message = format(
						LOG_I_ON_CREATE_AFTER_CREATE_SCRIPT_NOT_DEFINED_AT_DB_IMPL,
						getClass().getCanonicalName(),
						helperConfiguration.schemaName,
						helperConfiguration.schemaVersion,
						helperConfiguration.afterCreateScriptPath
				);
				log(I, message, false, null);
			}
			LinkedList<KittySQLiteQuery> after = getScriptSequenceFromFileDump(helperConfiguration.afterCreateScriptPath);
			if(after != null) {
				if(helperConfiguration.logOn) {
					String message = format(
							LOG_I_ON_CREATE_AFTER_CREATE_SCRIPT_SCRIPT_FROM_FILE,
							getClass().getCanonicalName(),
							helperConfiguration.schemaName,
							helperConfiguration.schemaVersion,
							helperConfiguration.afterCreateScriptPath
					);
					log(I, message, false, null);
				}
				runScriptSequenceInTransaction(after, database);
			} else {
				log(I, LOG_I_ON_CREATE_NO_AFTER_CREATE_SCRIPT, true, null);
			}
		} else {
			log(I, LOG_I_ON_CREATE_APPLYING_AFTER_CREATE_DB_IMP_SCRIPT, true, null);
			runScriptSequenceInTransaction(afterCreateScript.getSqlScript(), database);
		}
	}

	/**
	 * Called when the database needs to be upgraded. In this implementation it does following things:
	 * <br> 1) Checking that oldVersion less or equal newVersion, if false that method would return.
	 * <br> 2) Based on helper configuration defined in {@link KITTY_DATABASE_HELPER}
	 * starts upgrading routine. If something failed than {@link KittyRuntimeException} would be thrown.
	 * <br> So, according to {@link KittyDBHelperConfiguration#onUpgradeBehavior} it would
	 * try to get simple DropCreate migrator with {@link #getDropCreateVersionMigrator(int, int, SQLiteDatabase)}
	 * or file dump migrator with {@link #getFileVersionMigrator(int, int)} or user predefined migrator
	 * with implemented by developer {@link #getCustomMigratorImplementation(SQLiteDatabase, int, int)}.
	 * <br> Or do nothing and return.
	 * <br> After migrator instance was acquired it would be used in {@link #migrate(SQLiteDatabase, KittyORMVersionMigrator, int, int)}
	 * method. If something failed than {@link KittyRuntimeException} would be thrown.
	 * @param database The database.
	 * @param oldVersion The old database version.
	 * @param newVersion The new database version.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if(databaseConfiguration.useExternalFilepath && databaseConfiguration.databaseFilePath != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(LOG_I_ON_UPDATE_USING_EXTERNAL_DB_EXITING_ON_CREATE)
					.append(databaseConfiguration.databaseFilePath);
			return;
		}
		if(oldVersion == newVersion) return;
		if(oldVersion > newVersion)
			throw new KittyDBVersionMismatchException(oldVersion, newVersion, helperConfiguration.schemaName);
		KittyORMVersionMigrator migrator;
		log(I, LOG_I_ON_UPGRADE, true, null);
		switch (helperConfiguration.onUpgradeBehavior) {
			case DROP_AND_CREATE:
				log(I, LOG_I_ON_UPGRADE_DROP_AND_CREATE, true, null);
				migrator = getDropCreateVersionMigrator(oldVersion, newVersion, database);
				break;
			case USE_FILE_MIGRATIONS:
				log(I, LOG_I_ON_UPGRADE_USE_FILE_MIGRATIONS, true, null);
				migrator = getFileVersionMigrator(oldVersion, newVersion);
				break;
			case USE_CUSTOM_MIGRATOR:
				log(I, LOG_I_ON_UPGRADE_USE_CUSTOM_MIGRATOR, true, null);
				migrator = getCustomMigratorImplementation(database, oldVersion, newVersion);
				break;
			case USE_SIMPLE_MIGRATIONS:
				log(I, LOG_I_ON_UPGRADE_USE_SIMPLE_AUTOGEN_MIGRATIONS , true, null);
				migrator = getSimpleMigrator(oldVersion, newVersion, database);
				break;
			case DO_NOTHING:
			default:
				log(I, LOG_I_ON_UPGRADE_NO_ACTION, true, null);
				return;
		}
		migrate(database, migrator, oldVersion, newVersion);
		log(I, LOG_I_ON_UPDATE_APPLYING_AFTER_MIGRATE_SCRIPT, true, null);
		if(afterMigrateScript == null) {
			if(helperConfiguration.logOn) {
				String message = format(
						LOG_I_ON_UPDATE_AFTER_MIGRATE_SCRIPT_NOT_DEFINED_AT_DB_IMPL,
						getClass().getCanonicalName(),
						helperConfiguration.schemaName,
						helperConfiguration.schemaVersion,
						helperConfiguration.afterCreateScriptPath
				);
				log(I, message, false, null);
			}
			LinkedList<KittySQLiteQuery> after = getScriptSequenceFromFileDump(helperConfiguration.afterMigrateScriptPath);
			if(after != null) {
				if(helperConfiguration.logOn) {
					String message = format(
							LOG_I_ON_UPDATE_AFTER_MIGRATE_SCRIPT_FROM_FILE,
							getClass().getCanonicalName(),
							helperConfiguration.schemaName,
							helperConfiguration.schemaVersion,
							helperConfiguration.afterCreateScriptPath
					);
					log(I, message, false, null);
				}
				runScriptSequenceInTransaction(after, database);
			} else {
				log(I, LOG_I_ON_UPDATE_CREATE_NO_AFTER_MIGRATE_SCRIPT, true, null);
			}
		} else {
			log(I, LOG_I_ON_UPDATE_CREATE_APPLYING_AFTER_MIGRATE_DB_IMP_SCRIPT, true, null);
			runScriptSequenceInTransaction(afterMigrateScript.getSqlScript(), database);
		}
	}

	/**
	 * Method for getting custom {@link KittyORMVersionMigrator} implementation for this
	 * KittyORM database. Not implemented in this helper implementation and usage of this method
	 * would cause {@link KittyRuntimeException} be thrown. To use this method you should manually
	 * overload this method in child implementation.
	 * @param database The database.
	 * @param oldVersion The old database version.
	 * @param newVersion The new database version.
	 * @return custom migrator implementation.
	 */
	public KittyORMVersionMigrator
	getCustomMigratorImplementation(SQLiteDatabase database, int oldVersion, int newVersion) {
		throw new KittyRuntimeException(EXCEPTION_GET_CUSTOM_MIGRATOR);
	}

	private static String LOG_D_MIGRATION_SKIP = "Skipping not applicable migration {0}-{1}-{2}-{3} (helper {4} for schema {5} v. {6} - {7})";

	// Migration and other dump scripts
	/**
	 * Tries to migrate from old db version to current with usage of custom migrator
	 * @param database databaseClass object
	 * @param migrator to use
	 * @param oldVersion old version of databaseClass
	 * @param currentVersion current version of databaseClass
	 * @throws KittyRuntimeException if migration sequence not applicable for input db and schema versions
	 */
	protected void migrate(SQLiteDatabase database, KittyORMVersionMigrator migrator, int oldVersion, int currentVersion) {
		log(I, LOG_I_MIGRATION_ROUTINE_STARTED, true, null);
		log(I, format(LOG_I_MIGRATION_INFO, migrator.getClass().getCanonicalName()), false, null);
		if(!migrator.isMigrationSequenceApplicable())
			throw new KittyRuntimeException(format(AME_UNABLE_TO_MIGRATE,
					helperConfiguration.schemaName, oldVersion, currentVersion));
		Iterator<KittyMigration> migrationIterator = migrator.getMigrationsIterator();
		while (migrationIterator.hasNext()) {
			KittyMigration migration = migrationIterator.next();
			if(migration.getMinVersionLower() < oldVersion) {
				if(helperConfiguration.logOn) {
					String message = format(
							LOG_D_MIGRATION_SKIP,
							migration.getMinVersionLower(),
							migration.getMinVersionUpper(),
							migration.getMaxVersionLower(),
							migration.getMaxVersionUpper(),
							helperConfiguration.schemaName,
							oldVersion,
							currentVersion
					);
					log(D, message, false, null);
				}
				continue;
			}
			if(helperConfiguration.logOn) {
				String message = format(
						LOG_I_MG_APPLYING,
						migration.getMinVersionLower(),
						migration.getMinVersionUpper(),
						migration.getMaxVersionLower(),
						migration.getMaxVersionUpper(),
						helperConfiguration.schemaName,
						oldVersion,
						currentVersion
				);
				log(I, message, false, null);
			}
			try {
				runScriptSequenceInTransaction(migration.getMigrationScript(), database);
			} catch (KittyRuntimeException kre) {
				log(E, LOG_E_MIGRATION_TRANSACTION_FAILED, true, kre);
				throw kre;
			}
		}
	}

	/**
	 * Returns version migrator from version oldVersion to version currentVersion
	 * @param oldVersion old databaseClass version
	 * @param currentVersion current databaseClass version
	 * @return migrator for databaseClass versions if possible
	 */
	protected KittyORMVersionMigrator getFileVersionMigrator(int oldVersion, int currentVersion) {
		return new KittyORMVersionFileDumpMigrator(
				oldVersion,
				currentVersion,
				context,
				helperConfiguration.schemaName,
				helperConfiguration.logTag,
				helperConfiguration.logOn,
				null,
				new Object[]{helperConfiguration.DMMUpdateScriptsPath}
		);
	}

	protected KittyORMVersionMigrator getSimpleMigrator(int oldVersion, int currentVersion, SQLiteDatabase database) {
		return new KittySimpleMigrator(oldVersion,
				currentVersion,
				context,
				helperConfiguration.schemaName,
				helperConfiguration.logTag,
				helperConfiguration.logOn,
				null,
				new Object[] {databaseConfiguration, database});
	}

	/**
	 * Returns default DropCreate migrator that contains only one Migration generated
	 * <br> from create and drop statements fetched from {@link #acquireBestDropScriptSequence(int, SQLiteDatabase)}
	 * <br> and {@link #acquireBestCreateScriptSequence()}  if possible (if drop script for old schema version
	 * <br> doesn't exists than for getting drop dumped script would be used {@link #generateDropTablesScript(String, SQLiteDatabase)})
	 * @param oldVersion old databaseClass version
	 * @param currentVersion current databaseClass version
	 * @return default drop-create migrator implementation
	 */
	protected KittyORMVersionMigrator getDropCreateVersionMigrator(int oldVersion, int currentVersion, SQLiteDatabase database) {
		LinkedList<KittySQLiteQuery> migrateScript = acquireBestDropScriptSequence(oldVersion, database);
		if(migrateScript == null)
			throw new KittyRuntimeException(
					log(E, DC_CE_NO_DROP_SEQUENCE, true, null)
			);
		LinkedList<KittySQLiteQuery> createScript = acquireBestCreateScriptSequence();
		if(createScript == null) {
			throw new KittyRuntimeException(
					log(E, DC_CE_NO_CREATE_SEQUENCE, true, null)
			);
		}
		migrateScript.addAll(createScript);
		return new KittyDevDropCreateMigrator(oldVersion, currentVersion, context,
				helperConfiguration.schemaName, helperConfiguration.logTag,
				helperConfiguration.logOn, null, new Object[]{null, migrateScript});
	}

	/**
	 * Returns best create script sequence available.
	 * <br> If possible - returns create script from file dump, else - predefined create script.
	 * <br> File dump script stored as file in database's assets directory
	 * using pattern '{0}-v-{1}-create.sql' where {0} is schema name and {1} is schema version.
	 * <br> Full filepath would be: file:///android_asset/kittysqliteorm/{0}/{1}-v-{2}-create.sql where
	 * {0} and {1} is schema name and {2} is schema version
	 * @return if possible - returns create script from file dump, else - predefined create script.
	 */
	protected LinkedList<KittySQLiteQuery> acquireBestCreateScriptSequence() {
		log(I, format(LOG_I_ABCSS_GETTING_CREATE_SCRIPT_FROM_FILE,
				helperConfiguration.createSchemaScriptPath,
				getClass().getCanonicalName(),
				helperConfiguration.schemaName,
				helperConfiguration.schemaVersion
		), false, null);
		LinkedList<KittySQLiteQuery> creationScript = getScriptSequenceFromFileDump(helperConfiguration.createSchemaScriptPath);
		if(creationScript != null) {
			return creationScript;
		}
		log(I, LOG_I_ABCSS_UNABLE_TO_GET_DATA_FROM_FILE_RETURNING_PREGEN, true, null);
		return createSchemaAutogeneratedScript;
	}

	/**
	 * Returns drop tables sequence following those priority:
	 * <br> 1) Returning old drop script stored as file in database's assets directory
	 * using pattern '{0}-v-{1}-drop.sql' where {0} is schema name and {1} is schema version..
	 * If not possible than step 2.
	 * <br> Full filepath would be: file:///android_asset/kittysqliteorm/{0}/{1}-v-{2}-drop.sql where
	 * {0} and {1} is schema name and {2} is schema version
	 * <br> 2) Returning drop sequence set by kitty DB instance into current helper.
	 * If not possible than step 3.
	 * <br> 3) Returning drop sequence from {@link #generateDropTablesScript(String, SQLiteDatabase)}.
	 * If not possible than step 4.
	 * <br> 4) Return null.
	 * @return best drop sequence for policy described as comment for this method or null if
	 * not possible.
	 */
	protected LinkedList<KittySQLiteQuery> acquireBestDropScriptSequence(int oldDatabaseVersion, SQLiteDatabase database) {
		String dumpLocation = getOldDropScriptLocation(helperConfiguration, oldDatabaseVersion);
		log(I,
				format(
						LOG_I_ABDSS_FILEDUMP_MESSAGE,
						dumpLocation,
						getClass().getCanonicalName(),
						helperConfiguration.schemaName,
						helperConfiguration.schemaVersion),
				false, null);
		LinkedList<KittySQLiteQuery> dropScriptSequence =
				getScriptSequenceFromFileDump(
						dumpLocation
				);
		if(dropScriptSequence != null) {
			log(I,
					format(
							LOG_I_ABDSS_FILEDUMP_MESSAGE_OK,
							dumpLocation,
							getClass().getCanonicalName(),
							helperConfiguration.schemaName,
							helperConfiguration.schemaVersion),
					false, null);
			return  dropScriptSequence;
		}
		if(dropSchemaAutogeneratedScript != null) {
			log(I, LOG_I_ABDSS_FILEDUMP_FAILED_RETURNING_PREGEN, true, null);
			return dropSchemaAutogeneratedScript;
		}
		try {
			log(I, LOG_I_ABDSS_GENERATING_DROP_SCRIPT_FROM_TABLE_LIST, true, null);
			dropScriptSequence = generateDropTablesScript(helperConfiguration.schemaName, database);
		} catch (Throwable tr) {
			log(E, DC_CE_NO_CREATE_SEQUENCE_IN_ABDSS, true, tr);
		} finally {
			return dropScriptSequence;
		}
	}

	/**
	 * Returns drop script generated from list of tables acquired via {@link net.akaish.kitty.orm.query.SelectTableNamesQuery}.
	 * <br> Or null if cursor was null.
	 * @param database
	 * @return
	 */
	protected LinkedList<KittySQLiteQuery> generateDropTablesScript(String schemaName, SQLiteDatabase database) {
		LinkedList<KittySQLiteQuery> dropSequence = new LinkedList<>();
		KittyQueryBuilder queryBuilder = new KittyQueryBuilder(SELECT_TABLE_NAMES, null);
		queryBuilder.setSchemaName(schemaName);
		Cursor cursor = database.rawQuery(queryBuilder.buildSQLQuery().getSql(), null);
		List<String> tableNames = new LinkedList<>();
		if(cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				tableNames.add(cursor.getString(cursor.getColumnIndex(TYPE_NAME_COLUMN)));
			}
			Iterator<String> tableNameIterator = tableNames.iterator();
			while(tableNameIterator.hasNext()) {
				dropSequence.add(CreateDropHelper.generateDropTableStatement(tableNameIterator.next(),
						schemaName, false));
			}
			return dropSequence;
		} else {
			return null;
		}
	}

	protected static final String QE_RIT = "KittyDatabaseHelper.runScriptSequenceInTransaction(LinkedList<KittySQLiteQuery> scriptSequence, SQLiteDatabase database)";

	/**
	 * Tries to start transaction and end it. If any problem acquired while running transaction
	 * @param scriptSequence script sequence to execute
	 * @param database database to execute script sequence on
	 */
	protected void runScriptSequenceInTransaction(LinkedList<KittySQLiteQuery> scriptSequence, SQLiteDatabase database) {
		database.beginTransaction();
		for(KittySQLiteQuery query : scriptSequence) {
			logQuery(QE_RIT, query);
			database.execSQL(query.getSql());
		}
		Exception trxE = null;
		try {
			database.setTransactionSuccessful();
		} catch (Exception e) {
			trxE = e;
		} finally {
			database.endTransaction();
			if(trxE != null)
				throw new KittyRuntimeException(format(LOG_E_UNABLE_TO_FINISH_TRANSACTION,
						getClass().getCanonicalName(), helperConfiguration.schemaName), trxE);
		}
	}

	// Working with file dumped and predefined script sequences
	/**
	 * Return path (assets or fs) for old drop script using pattern '{0}-v-{1}-drop.sql' where {0} is databaseClass name
	 * <br> and {1} is old version integer code with prefix as absolute path to assets schema directory
	 * <br> If old script was stored as fs file and new as assets, would be returned path to assets
	 * @param configuration helper configuration
	 * @param oldVersion old version integer code
	 * @return name for old drop script
	 */
	protected String getOldDropScriptLocation(KittyDBHelperConfiguration configuration, int oldVersion) {
		String dropFileName = KittyUtils.getStringPartBeforeFirstOccurrenceOfChar(configuration.dropSchemaScriptPath, File.separatorChar, true);
		String dropScriptRoot = configuration.dropSchemaScriptPath.replace(dropFileName, EMPTY_STRING);
		StringBuffer sb = new StringBuffer(64);
		sb.append(dropScriptRoot);
		if(!dropScriptRoot.endsWith(File.separator))
			sb.append(File.separatorChar);
		sb.append(getDropSchemaDefaultFilename(configuration.schemaName, oldVersion));
		return sb.toString();
	}

	/**
	 * Returns SQLite script sequence from provided file path if possible or null.
	 * <br> If log on then on any errors where would be log output.
	 * @param filepathToScripts path to file script
	 * @return list of queries
	 */
	protected LinkedList<KittySQLiteQuery> getScriptSequenceFromFileDump(String filepathToScripts) {
		if(filepathToScripts.startsWith(KittyNamingUtils.ASSETS_URI_START)) {
			KittySQLiteDumpScript dmpScript = getAssetsScript(filepathToScripts);
			if(dmpScript == null) {
				return null;
			}
			return dmpScript.getSqlScript();
		} else {
			File fileToDump = KittyNamingUtils.getScriptFile(filepathToScripts, context);
			if(fileToDump.exists() && fileToDump.isFile() && fileToDump.canRead()) {
				return getFileScript(fileToDump.getAbsolutePath(), false).getSqlScript();
			} else {
				log(W, format(
						LOG_W_NO_VALID_DUMP_FILE,
						fileToDump.getAbsolutePath(),
						this.getClass().getCanonicalName(),
						helperConfiguration.schemaName,
						helperConfiguration.schemaVersion
				), false, null);
				return null;
			}
		}
	}

	/**
	 * Creates and returns KittySQLiteDumpScript implementation designed to be used
	 * <br> with this databaseClass helper (from assets)
	 * @param assetPath
	 * @return
	 */
	protected KittySQLiteDumpScript getAssetsScript(String assetPath) {
		try {
			return new KittySQLiteAssetsFileDumpScript(assetPath, context);
		} catch (Exception e) {
			log(W, LOG_W_HELPER_DS_AME, false, e);
			return null;
		}
	}

	/**
	 * Creates and returns KittySQLiteDumpScript implementation designed to be used
	 * <br> with this databaseClass helper (from fs)
	 * @param fileUriString fileUriString object that contains path to readable fileUriString with dumped script
	 * @param newDump flag that shows if this dump fileUriString is new and can be empty\not existing
	 * @return dump script
	 */
	protected KittySQLiteDumpScript getFileScript(String fileUriString, boolean newDump) {
		try {
			return new KittySQLiteFileDumpScript(fileUriString, newDump, context);
		} catch (Exception e) {
			log(W, LOG_W_HELPER_DS_AME, false, e);
			return null;
		}
	}

	// Other stuff
	/**
	 * Simple method for logging in this helper if log is on. Returns pattern parameter if no formatting
	 * should be used or formatted string where:
	 * <br> {0} is value for {@link #getClass()} canonical name
	 * <br> {1} is value for {@link KittyDBHelperConfiguration#schemaName}
	 * <br> {2} is value for {@link KittyDBHelperConfiguration#schemaVersion}
	 * @param level
	 * @param pattern
	 * @param useDefaultFormatter
	 * @param tr any throwable, may be null
	 * @return
	 */
	protected String log(KittyLog.LOG_LEVEL level, String pattern,
						 boolean useDefaultFormatter, Throwable tr) {
		if(useDefaultFormatter) {
			pattern = format(
					pattern,
					getClass().getCanonicalName(),
					helperConfiguration.schemaName,
					helperConfiguration.schemaVersion);
		}
		if(helperConfiguration.logOn) {
			kLog(level, helperConfiguration.logTag, pattern, tr);
		}
		return pattern;
	}

	/**
	 * Sets pragma key for Android API 15 and lower
	 * If API higher than 15 than nothing happens
	 * @param db databaseClass object
	 * @param pragmaKeys pragma on flag
	 */
	protected final void setPragmaKeysAPI15AndLower(SQLiteDatabase db, boolean pragmaKeys) {
		// If API higher than 16, than pragma is set in onConfigure with
		// db.setForeignKeyConstraintsEnabled(pragmaKeys)
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			return;
		if (!db.isReadOnly() && pragmaKeys) {
			log(I, LOG_I_FK_SUPPORT_15, true, null);
			// Enable foreign key constraints
			db.execSQL(PRAGMA_ON);
		}
	}

	protected final void logQuery(String executor, KittySQLiteQuery query) {
		KittyMapper.logQuery(executor, query, helperConfiguration.logTag, helperConfiguration.logOn,
				helperConfiguration.productionOn,
				this, helperConfiguration.schemaName, helperConfiguration.schemaVersion);
	}


	@Override
	public KittyDatabaseHelper clone() {
		try {
			return (KittyDatabaseHelper) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new KittyRuntimeException(EXCEPTION_UNABLE_TO_CLONE, e);
		}
	}
}
