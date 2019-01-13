
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

package net.akaish.kitty.orm.annotations;


import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.KittyMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.akaish.kitty.orm.util.KittyConstants.DEFAULT_LOG_TAG;
import static net.akaish.kitty.orm.util.KittyConstants.DEFAULT_MODEL_MAPPER_IMPLEMENTATIONS_PACKAGE;
import static net.akaish.kitty.orm.util.KittyConstants.ZERO_LENGTH_STRING;

/**
 * Created by akaish on 04.02.18.
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KITTY_DATABASE {

    /**
     * Database name
     * @return
     */
    String databaseName() default ZERO_LENGTH_STRING;

    /**
     * Database version int
     * @return
     */
    int databaseVersion() default 1;

    /**
     * Enable logging (false by default)
     * @return
     */
    boolean isLoggingOn() default false;

    /**
     * Enable logging for dex util component (false by default)
     * @return
     */
    boolean isKittyDexUtilLoggingEnabled() default false;

    /**
     * Log tag (KittySQLite by default)
     * @return
     */
    String logTag() default DEFAULT_LOG_TAG;

    /**
     * Pragma on value (by default false)
     * @return
     */
    boolean isPragmaOn() default false;

    /**
     * Production flag (default true), when true and logging on than models would be logged as well as
     * CV values with {@link KittyMapper} with usage of {@link KittyModel#toLogString()}.
     * <br> <b>Be sure to turn it off on production cause that may be potential threat for your's application
     * <br> user's private data, cause it would be outputted to serial console when production OFF!</b>
     * @return
     */
    boolean isProductionOn() default true;

    /**
     * Create modelMapperInstanceStorage by default method (via reflection)
     * @return
     */
    boolean isGenerateRegistryFromPackage() default true;
    

    /**
     * Package name where schema classes are
     * @return
     */
    String[] domainPackageNames() default {DEFAULT_MODEL_MAPPER_IMPLEMENTATIONS_PACKAGE};

}
