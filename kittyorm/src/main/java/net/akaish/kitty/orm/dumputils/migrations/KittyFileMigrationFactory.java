
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

package net.akaish.kitty.orm.dumputils.migrations;

import android.content.Context;
import android.util.Log;

import net.akaish.kitty.orm.dumputils.scripts.KittySQLiteAssetsFileDumpScript;
import net.akaish.kitty.orm.dumputils.scripts.KittySQLiteFileDumpScript;
import net.akaish.kitty.orm.util.KittyUtils;

import java.io.File;
import java.text.MessageFormat;

/**
 * Migrations factory (from filesystem)
 * Created by akaish on 05.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyFileMigrationFactory extends KittyMigrationFactory {

    private static String MIGRATION_SCRIPT_EXTENSION = ".sql";

    protected static String LOG_I_STARTING = "Trying to create migration from file: {0}";
    protected static String LOG_W_BOTH_ASSETS_AND_FS_NOT_ALLOWED_F = "Both migration file and migration assets location not allowed, setting migration file to NULL!";
    protected static String LOG_W_BOTH_ASSETS_AND_FS_NOT_ALLOWED_A = "Both migration file and migration assets location not allowed, setting migration assets location to NULL!";
    protected static String LOG_I_STARTING_ASSETS = "Trying to create migration from asset: {0}";
    protected static String LOG_W_CANT_READ_OR_DIR = "File for migration {0} is a directory or has no read access.";
    protected static String LOG_W_BAD_MIGRATION_NAME = "File for migration {0} doesn't match migration naming pattern (x-x-x-x.sql where 'x' is integer), bad amount of x after splitting filename!";
    protected static String LOG_W_BAD_MIGRATION_NAME_IA = "File for migration {0} doesn't match migration naming pattern (x-x-x-x.sql where 'x' is integer), unable to parse 'x' to positive Integer (see exception details)!";
    protected static String IA_MESSAGE = "minLower or minUpper or maxLower or maxUpper has (have) negative value)";
    protected static String LOG_I_SKIPPED_MIGRATION = "Migration {0}-{1}-{2}-{3} for databaseClass {4} skipped due to filter {5}-{6}";
    protected static String LOG_E_IE = "Unable to read migration file {0}, see exception details!";
    protected static String LOG_E_IE_A = "Unable to read migration file from assets {0}, see exception details!";
    protected static String NPE_FILE_CANT_BE_NULL = "Migration file\\assets path for FileMigrationFactory not set or null value passed into #setMigrationFile(File f)\\#setMigrationAssetsOath(String assetsPath)!";

    protected File migrationFile;
    protected String assetsPath;

    protected final Context ctx;

    public KittyFileMigrationFactory(Context ctx, String databaseName, String logTag, boolean logOn) {
        super(databaseName, logTag, logOn);
        this.ctx = ctx;
    }

    /**
     * Set migration file to use in this factory with {@link #newMigration()}
     * @param f migration file
     */
    public KittyFileMigrationFactory setMigrationFile(File f) {
        if(f == null)
            throw new NullPointerException(NPE_FILE_CANT_BE_NULL);
        if(assetsPath != null) {
            if(logOn)
                Log.w(logTag, LOG_W_BOTH_ASSETS_AND_FS_NOT_ALLOWED_A);
            assetsPath = null;
        }
        migrationFile = f;
        return this;
    }

    /**
     * Sets assets path to use in this factory with {@link #newMigration()}
     * @param assetsPath
     * @return
     */
    public KittyFileMigrationFactory setMigrationAssetsPath(String assetsPath) {
        if(assetsPath == null)
            throw new NullPointerException(NPE_FILE_CANT_BE_NULL);
        if(migrationFile != null) {
            if(logOn)
                Log.w(logTag, LOG_W_BOTH_ASSETS_AND_FS_NOT_ALLOWED_F);
            migrationFile = null;
        }
        this.assetsPath = assetsPath;
        return this;
    }

    /**
     * Returns new {@link KittyMigration} instance from provided file if all ok or null if errors
     * Use logging on if you need to debug it
     * @return
     */
    public KittyMigration newMigration() {
        String filename = null;
        if(migrationFile != null) {
            if (logOn)
                Log.i(logTag, MessageFormat.format(LOG_I_STARTING, migrationFile.getAbsolutePath()));
            try {
                if (!migrationFile.canRead() || migrationFile.isDirectory()) {
                    if (logOn)
                        Log.w(logTag, MessageFormat.format(LOG_W_CANT_READ_OR_DIR, migrationFile.getAbsolutePath()));
                    return null;
                }
            } catch (SecurityException se) {
                Log.w(logTag, MessageFormat.format(LOG_W_CANT_READ_OR_DIR, migrationFile.getAbsolutePath()), se);
            }
            filename = migrationFile.getName();
        } else if(assetsPath != null) {
            if (logOn)
                Log.i(logTag, MessageFormat.format(LOG_I_STARTING_ASSETS, assetsPath));
            filename = KittyUtils.getStringPartBeforeFirstOccurrenceOfChar(assetsPath, File.separatorChar, true);
        } else {
            throw new NullPointerException(NPE_FILE_CANT_BE_NULL);
        }
        filename = filename.replace(MIGRATION_SCRIPT_EXTENSION, "");
        String[] migrationsNumbers = filename.split("-");
        if(migrationsNumbers.length != 4) {
            if(logOn)
                Log.w(logTag, MessageFormat.format(LOG_W_BAD_MIGRATION_NAME, migrationFile.getAbsolutePath()));
            return null;
        }
        int minLower = -1;
        int minUpper = -1;
        int maxLower = -1;
        int maxUpper = -1;
        try {
            minLower = Integer.parseInt(migrationsNumbers[0]);
            minUpper = Integer.parseInt(migrationsNumbers[1]);
            maxLower = Integer.parseInt(migrationsNumbers[2]);
            maxUpper = Integer.parseInt(migrationsNumbers[3]);
            if(minLower < 0 || minUpper < 0 || maxLower < 0 || maxUpper < 0) {
                throw new IllegalArgumentException(IA_MESSAGE);
            }
        } catch(IllegalArgumentException ia) {
            if(logOn)
                Log.w(logTag, MessageFormat.format(LOG_W_BAD_MIGRATION_NAME_IA, migrationFile.getAbsolutePath()), ia);
            return null;
        }
        if(oldVersionFilter > -1 && currentVersionFilter > -1) {
            if(minLower < oldVersionFilter || currentVersionFilter > maxUpper) {
                if(logOn) {
                    String logMessage = MessageFormat.format(LOG_I_SKIPPED_MIGRATION,
                            minLower, minUpper, maxLower, maxUpper, databaseName, oldVersionFilter,
                            currentVersionFilter);
                    Log.i(logTag, logMessage);
                }
                return null;
            }
        }
        try {
            if(migrationFile != null) {
                KittySQLiteFileDumpScript migrationScript = new KittySQLiteFileDumpScript(migrationFile.getAbsolutePath(), false, ctx);
                return new KittyMigration(minLower, minUpper, maxLower, maxUpper, migrationScript.getSqlScript(), databaseName);
            } else {
                KittySQLiteAssetsFileDumpScript migrationScript = new KittySQLiteAssetsFileDumpScript(assetsPath, ctx);
                return new KittyMigration(minLower, minUpper, maxLower, maxUpper, migrationScript.getSqlScript(), databaseName);
            }
        } catch (Exception ie) {
            if(logOn) {
                if(migrationFile != null)
                    Log.e(logTag, MessageFormat.format(LOG_E_IE, migrationFile.getAbsolutePath()), ie);
                else
                    Log.e(logTag, MessageFormat.format(LOG_E_IE_A, assetsPath), ie);
            }
            return null;
        }
    }
}
