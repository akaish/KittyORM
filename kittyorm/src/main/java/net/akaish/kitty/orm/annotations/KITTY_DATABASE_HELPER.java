
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

package net.akaish.kitty.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.akaish.kitty.orm.util.KittyConstants.ZERO_LENGTH_STRING;

/**
 * Annotation for databaseClass helpers configuration
 * Created by akaish on 04.03.18.
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface KITTY_DATABASE_HELPER {

    /**
     * On upgrade behavior
     * @return
     */
    UpgradeBehavior onUpgradeBehavior() default UpgradeBehavior.DROP_AND_CREATE;

    /**
     * Location of databaseClass version migrations scripts
     * @return
     */
    String migrationScriptsPath() default ZERO_LENGTH_STRING;

    /**
     * Location of schema creation script path
     * @return
     */
    String createScriptPath() default ZERO_LENGTH_STRING;

    /**
     * Location of drop schema script path
     * @return
     */
    String dropScriptPath() default ZERO_LENGTH_STRING;


    /**
     * Location of script to apply after migration
     * @return
     */
    String afterMigrateScriptPath() default ZERO_LENGTH_STRING;

    /**
     * Location of script to apply after creation
     * @return
     */
    String afterCreateScriptPath() default ZERO_LENGTH_STRING;

    /**
     * Location of scripts, file dir or assets dir
     * @return
     */
    boolean migrationScriptsStoredInAssets() default true;

    enum UpgradeBehavior {
        DO_NOTHING,
        DROP_AND_CREATE,
        USE_FILE_MIGRATIONS,
        USE_SIMPLE_MIGRATIONS,
        USE_CUSTOM_MIGRATOR
    }
}
