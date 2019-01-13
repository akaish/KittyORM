
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

package net.akaish.kitty.orm.dumputils.migrations;

import android.content.Context;

import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.util.KittyNamingUtils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * File dump migrator implementation
 * <br> This migrator looks for all files in specified directory and creates {@link KittyMigration} from
 * <br> them if possible (files are files with name in x-x-x-x.sql pattern, where 'x' are positive integers that
 * <br> are ranges, e.g. minLower-minUpper-maxLower-maxUpper ranges of current migrations scripts)
 * Created by akaish on 05.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyORMVersionFileDumpMigrator extends KittyORMVersionMigrator {

    private static String IA_EXCEPTION_NOT_DIRECTORY_OR_DOES_NOT_EXISTS = "File object {0} for databaseClass {1} used in KittyORMVersionFileDumpMigrator is not a directory or doesn't exist!";
    private static String IA_EXCEPTION_DIRECTORY_NO_PERMISSION_TO_LIST_FILES = "File object {0} for databaseClass {1} used in KittyORMVersionFileDumpMigrator has no access to list nested files!";
    private static String IA_EXCEPTION_BAD_MIGRATION_PARAMETERS = "KittyORMVersionFileDumpMigrator needs migrationsParameters to be an array with at least one value at [0] index that is a String or a File instance with path to directory with file migration scripts (schema: {0}, old schema version: {1}, new schema version {2})!";

    private static String IOE_ERROR_ON_ASSETS_SP = "KittyORMVersionFileDumpMigrator#setParameters(Object[] factoryParameters,  Object[] migrationsParameters) IOException caught, see nested exception for details!";
    private static String IOE_ERROR_ON_ASSETS_SM = "KittyORMVersionFileDumpMigrator#setMigrations(Object... migrationsParameters) IOException caught, see nested exception for details!";

    private File sqlMigrationsRoot;

    String assetsRootUriString;

    public KittyORMVersionFileDumpMigrator(int oldVersion, int currentVersion, Context ctx, String schemaName,
                                           String logTag, boolean logOn,  Object[] factoryParameters,
                                           Object[] migrationsParameters) {
        super(oldVersion, currentVersion, ctx, schemaName, logTag, logOn,  factoryParameters, migrationsParameters);
    }

    protected void setParameters(Object[] factoryParameters,  Object[] migrationsParameters) {
        String IAMessage = MessageFormat.format(IA_EXCEPTION_BAD_MIGRATION_PARAMETERS, schemaName, Integer.toString(oldVersion), Integer.toString(currentVersion));
        if(migrationsParameters == null) throw new IllegalArgumentException(IAMessage);
        if(migrationsParameters.length == 0) throw new IllegalArgumentException(IAMessage);
        if(migrationsParameters[0] instanceof String) {
            if(((String)migrationsParameters[0]).startsWith(KittyNamingUtils.ASSETS_URI_START))
                assetsRootUriString = (String) migrationsParameters[0];
            else
                sqlMigrationsRoot = KittyNamingUtils.getScriptFile((String)migrationsParameters[0], context);
        } else if(migrationsParameters[0] instanceof File) {
            sqlMigrationsRoot = (File) migrationsParameters[0];
        } else {
            throw new IllegalArgumentException(IAMessage);
        }
        if(sqlMigrationsRoot != null) {
            if (!sqlMigrationsRoot.isDirectory())
                throw new IllegalArgumentException(MessageFormat.format(IA_EXCEPTION_NOT_DIRECTORY_OR_DOES_NOT_EXISTS, sqlMigrationsRoot.getAbsolutePath(), schemaName));
            try {
                sqlMigrationsRoot.listFiles();
            } catch (SecurityException se) {
                throw new IllegalArgumentException(MessageFormat.format(IA_EXCEPTION_DIRECTORY_NO_PERMISSION_TO_LIST_FILES, sqlMigrationsRoot.getAbsolutePath(), schemaName));
            }
        } else if(assetsRootUriString != null){
            try {
                context.getAssets().list(assetsRootUriString.replace(KittyNamingUtils.ASSETS_URI_START, ""));
            } catch (IOException e) {
                throw new KittyRuntimeException(IOE_ERROR_ON_ASSETS_SP, e);
            }
        }
    }

    @Override
    protected KittyMigrationFactory getMigrationFactory(Object... factoryParameters) {
        KittyMigrationFactory fileMigrationFactory = new KittyFileMigrationFactory(context, schemaName, logTag, logOn);
        fileMigrationFactory.setVersionFilter(oldVersion, currentVersion);
        return fileMigrationFactory;
    }

    /**
     * <br> Hmmm, why I have done it like this instead of simple decencies injection via constructor?
     * <br> I don't know, seems to be chaos call to increase entropy. Refactor it in future.
     * @param migrationsParameters
     */
    @Override
    protected void setMigrations(Object... migrationsParameters) {
        if(sqlMigrationsRoot != null) {
            File[] filesInMigrationsDir = sqlMigrationsRoot.listFiles();
            for (File mFile : filesInMigrationsDir) {
                KittyMigration migration = ((KittyFileMigrationFactory) migrationFactory).setMigrationFile(mFile).newMigration();
                if (migration != null)
                    migrations.add(migration);
            }
        } else if (assetsRootUriString != null) {
            String assetsRoot = assetsRootUriString.replace(KittyNamingUtils.ASSETS_URI_START, "");
            try {
                String[] filesInMigrationAssets = context.getAssets().list(assetsRoot);
                for(String fileName : filesInMigrationAssets) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(assetsRoot).append(File.separatorChar).append(fileName);
                    KittyMigration migration = ((KittyFileMigrationFactory) migrationFactory).setMigrationAssetsPath(sb.toString()).newMigration();
                    if (migration != null)
                        migrations.add(migration);
                }
            } catch (IOException e) {
                throw new KittyRuntimeException(IOE_ERROR_ON_ASSETS_SM, e);
            }
        }
    }
}
