
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

package net.akaish.kitty.orm.util;

import android.content.Context;

import net.akaish.kitty.orm.KittyDatabase;
import net.akaish.kitty.orm.KittyModel;

import java.io.File;
import java.lang.reflect.Field;
import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittyConstants.EMPTY_STRING;
import static net.akaish.kitty.orm.util.KittyConstants.MODEL_END;
import static net.akaish.kitty.orm.util.KittyConstants.RECORD_END;

/**
 * Just some namings utils for default strings and filenames
 * Created by akaish on 28.07.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittyNamingUtils {

    private static String IA_ASATL_BAD_ARRAY_LENGTH = "Expected value must be a String array with 0 or 1 elements but provided array has more elements!";

    private static String DB_SCRIPTS_PATH_ROOT = "kittysqliteorm";
    private static String DB_MIGRATIONS_SCRIPTS_ROOT = "version_migrations";

    public static final String ASSETS_URI_START = "file:///android_asset/";
    public static final String INTERNAL_MEM_URI_START = "file:///internal_memory/";
    public static final String EXTERNAL_MEM_URI_START = "file:///external_memory/";

    public static final String CREATE_SCRIPT_FILENAME_PATTERN = "{0}-v-{1}-create.sql";
    public static final String DROP_SCRIPT_FILENAME_PATTERN = "{0}-v-{1}-drop.sql";
    public static final String AFTER_CREATE_SCRIPT_FILENAME_PATTERN = "{0}-v-{1}-after_create.sql";
    public static final String AFTER_MIGRATE_SCRIPT_FILENAME_PATTERN = "{0}-v-{1}-after_migrate.sql";

    private static final String SERIALIZE = "Serialize";
    public static final String DESERIALIZE = "Deserialize";

    private static String DEVELOPMENT_DUMPS_ROOT = "dev_dumps";

    /**
     * Returns default schema name generated from {@link KittyDatabase} implementation classname.
     * <br> Name generated with usage of {@link KittyUtils#fieldNameToLowerCaseUnderScore(String)}
     * @param kittyDatabaseClass
     * @param <T>
     * @return
     */
    public static final <T extends KittyDatabase> String generateSchemaNameFromDatabaseClassName(Class<T> kittyDatabaseClass) {
        return KittyUtils.fieldNameToLowerCaseUnderScore(kittyDatabaseClass.getSimpleName());
    }

    /**
     * Returns default column name generated from {@link KittyModel} field.
     * <br> Name generated with usage of {@link KittyUtils#fieldNameToLowerCaseUnderScore(String)}
     * @param f
     * @return
     */
    public static final String generateColumnNameFromModelField(Field f) {
        return KittyUtils.fieldNameToLowerCaseUnderScore(f.getName());
    }

//    /**
//     * Method used to implement nullable values for KittyORM annotation Strings. All simple, if provided
//     * array has 0 legnth or NULL than NULL would be returned. If array length is 1 than [0] element would be returned.
//     * <br> If array legth more than 1 than {@link IllegalArgumentException} would be thrown.
//     * @param annotationNullableLiteralValue
//     * @return
//     */
//    public static final String annotationStringArrayToLiteral(String[] annotationNullableLiteralValue) {
//        if(annotationNullableLiteralValue == null) return null;
//        if(annotationNullableLiteralValue.length == 0) return null;
//        if(annotationNullableLiteralValue.length > 1) throw new IllegalArgumentException(IA_ASATL_BAD_ARRAY_LENGTH);
//        return annotationNullableLiteralValue[0];
//    }

    /**
     * Returns default scripts location for provided database name, output would be:
     * <br> file:///android_asset/ + kittysqliteorm/database_name/
     * @param schemaName
     * @return
     */
    public static final String getDefaultAssetDatabaseScriptsPath(String schemaName) {
        return new StringBuffer(32)
                .append(ASSETS_URI_START)
                .append(DB_SCRIPTS_PATH_ROOT)
                .append(File.separatorChar)
                .append(schemaName)
                .append(File.separatorChar)
                .toString();
    }

    public static final String IA_BAD_URI = "Uri string can't be started with file:///android_asset/ (can't get File object from assets location for uri string {0})";

    /**
     * Returns script file instance depends on locationUri value (if locationUri starts
     * with {@link #ASSETS_URI_START} IA exception would be thrown)
     * @param locationUri
     * @param ctx
     * @return
     */
    public static final File getScriptFile(String locationUri, Context ctx) {
        if(locationUri.startsWith(ASSETS_URI_START))
            throw new IllegalArgumentException(format(IA_BAD_URI, locationUri));
        if(locationUri.startsWith(INTERNAL_MEM_URI_START)) {
            File internalMemory = ctx.getFilesDir();
            StringBuffer sb = new StringBuffer(64);
            sb.append(internalMemory.getAbsolutePath());
            if(sb.charAt(sb.length()-1) != File.separatorChar) {
                sb.append(File.separatorChar);
            }
            sb.append(locationUri.replace(INTERNAL_MEM_URI_START, EMPTY_STRING));
            return new File(sb.toString());
        } else if(locationUri.startsWith(EXTERNAL_MEM_URI_START)) {
            File externalMemory = ctx.getExternalFilesDir(null);
            StringBuffer sb = new StringBuffer(64);
            sb.append(externalMemory.getAbsolutePath());
            if(sb.charAt(sb.length()-1) != File.separatorChar) {
                sb.append(File.separatorChar);
            }
            sb.append(locationUri.replace(EXTERNAL_MEM_URI_START, EMPTY_STRING));
            return new File(sb.toString());
        } else {
            return new File(locationUri);
        }
    }

    /**
     * Returns default scripts location for provided database name, output would be:
     * <br> file:///internal_memory/ + kittysqliteorm/database_name/
     * <br> that is equivalent for
     * <br> getFilesDir().getAbsolutePath() + /kittysqliteorm/database_name/
     * @param context
     * @param schemaName
     * @return
     */
    public static final String getDefaultDatabaseScriptsPath(Context context, String schemaName) {
        return new StringBuffer(32)
                .append(INTERNAL_MEM_URI_START)
                .append(DB_SCRIPTS_PATH_ROOT)
                .append(File.separatorChar)
                .append(schemaName)
                .append(File.separatorChar)
                .toString();
    }

    /**
     * Returns default scripts location for provided database name, output would be:
     * <br> getFilesDir().getAbsolutePath() + /kittysqliteorm/database_name/version_migrations
     * <br> or
     * <br> file:///android_asset + /kittysqliteorm/database_name/version_migrations
     * @param ctx
     * @param schemaName
     * @return
     */
    public static final String getDefaultDatabaseMigrationScriptsPath(Context ctx, String schemaName, boolean scriptsInAssets) {
        StringBuilder sb = new StringBuilder(64);
        if(scriptsInAssets)
            sb.append(getDefaultAssetDatabaseScriptsPath(schemaName));
        else
            sb.append(getDefaultDatabaseScriptsPath(ctx, schemaName));
        return sb.append(DB_MIGRATIONS_SCRIPTS_ROOT).toString();
    }

    /**
     * Returns default filename for schema create script. For schema "sample" v.3 it would be
     * sample-v-3-create.sql (pattern is {0}-v-{1}-create.sql)
     * @param schemaName
     * @param schemaVersion
     * @return
     */
    public static final String getCreateSchemaDefaultFilename(String schemaName, int schemaVersion) {
        return format(CREATE_SCRIPT_FILENAME_PATTERN,
                schemaName,
                schemaVersion);
    }

    /**
     * Returns default filename for schema drop script. For schema "sample" v.3 it would be
     * sample-v-3-drop.sql (pattern is {0}-v-{1}-drop.sql)
     * @param schemaName
     * @param schemaVersion
     * @return
     */
    public static final String getDropSchemaDefaultFilename(String schemaName, int schemaVersion) {
        return format(DROP_SCRIPT_FILENAME_PATTERN,
                schemaName,
                schemaVersion);
    }

    /**
     * Returns default filename for schema after create script. For schema "sample" v.3 it would be
     * sample-v-3-after_create.sql (pattern is {0}-v-{1}-after_create.sql)
     * @param schemaName
     * @param schemaVersion
     * @return
     */
    public static final String getAfterCreateScriptDefaultFilename(String schemaName, int schemaVersion) {
        return format(AFTER_CREATE_SCRIPT_FILENAME_PATTERN,
                schemaName,
                schemaVersion);
    }

    /**
     * Returns default filename for schema after migrate script. For schema "sample" v.3 it would be
     * sample-v-3-after_migrate.sql (pattern is {0}-v-{1}-after_migrate.sql)
     * @param schemaName
     * @param schemaVersion
     * @return
     */
    public static final String getAfterMigrateScriptDefaultFilename(String schemaName, int schemaVersion) {
        return format(AFTER_MIGRATE_SCRIPT_FILENAME_PATTERN,
                schemaName,
                schemaVersion);
    }

    /**
     * Returns filepath for create\drop scripts for specified scripts root.
     * @param scriptRootPath
     * @param scriptName
     * @return
     */
    public static final String getSQLiteScriptPath(String scriptRootPath, String scriptName) {
        StringBuffer sb = new StringBuffer(64)
                .append(scriptRootPath);
        if(sb.charAt(sb.length()-1) != File.separatorChar)
            sb.append(File.separatorChar);
        return  sb.append(scriptName).toString();
    }

    /**
     * Returns filepath for development create\drop scripts (or other if necessary) for specified scripts root.
     * @param scriptRootPath
     * @param scriptName
     * @return
     */
    public static final String getDevelopmentDumpsDefaultPath(String scriptRootPath, String scriptName) {
        StringBuffer sb = new StringBuffer(64)
                .append(scriptRootPath);
        if(sb.charAt(sb.length()-1) != File.separatorChar)
            sb.append(File.separatorChar);
        return  sb.append(DEVELOPMENT_DUMPS_ROOT)
                .append(File.separatorChar).append(scriptName).toString();
    }

    /**
     * Returns default serialization name for provided field name. For example, for field _id
     * returned String would be _idSerialize
     * @param fieldName
     * @return
     */
    public static final String getDefaultSerializationMethodName(String fieldName) {
        return new StringBuffer(32).append(fieldName).append(SERIALIZE).toString();
    }

    /**
     * Returns default serialization name for provided field name. For example, for field _id
     * returned String would be _idDeserialize
     * @param fieldName
     * @return
     */
    public static final String getDefaultDeserializationMethodName(String fieldName) {
        return new StringBuffer(32).append(fieldName).append(DESERIALIZE).toString();
    }

    /**
     * Returns generated name of table
     * <br> 1) Record and Model endings would be deleted
     * <br> 2) Camel case would be changes to lower_case_underscore
     * @param model model class
     * @param <T> implementation of {@link KittyModel}
     * @return table name
     */
    public static final <T extends KittyModel> String generateTableNameFromRecordName(Class<T> model) {
        String className = model.getSimpleName();
        StringBuffer clnB = new StringBuffer(className);
        if(KittyUtils.endsWithIgnoreCase(className, MODEL_END))
            clnB.replace(className.lastIndexOf(MODEL_END), className.lastIndexOf(MODEL_END) + MODEL_END.length(), EMPTY_STRING );
        if(KittyUtils.endsWithIgnoreCase(className, RECORD_END))
            clnB.replace(className.lastIndexOf(RECORD_END), className.lastIndexOf(RECORD_END) + RECORD_END.length(), EMPTY_STRING );
        return KittyUtils.fieldNameToLowerCaseUnderScore(clnB.toString());
    }
}
