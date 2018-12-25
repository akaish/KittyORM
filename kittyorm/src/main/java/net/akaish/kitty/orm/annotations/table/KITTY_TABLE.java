
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

package net.akaish.kitty.orm.annotations.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import static net.akaish.kitty.orm.util.KittyConstants.ZERO_LENGTH_STRING;

/**
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KITTY_TABLE {

	/**
	 * Returns table name of associated with this class sqlite table
	 * @return
	 */
	String tableName() default ZERO_LENGTH_STRING;

	/**
	 * True if temporary table
	 * @return
	 */
	boolean temporaryTable() default false;

	/**
	 * Flag that indicates usage of this Record in schema creation
	 * @return
	 */
	boolean isSchemaTable() default true;

	/**
	 * Flag that indicates when this table should be created, on DB initialization or on first
	 * initialization of this modelClass
	 * @return
	 */
	boolean isCreateOnDemand() default false;

	boolean withoutRowid() default false;

	boolean createIfNotExists() default true;
}
