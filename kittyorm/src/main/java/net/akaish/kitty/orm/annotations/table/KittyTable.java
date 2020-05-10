
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

package net.akaish.kitty.orm.annotations.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.akaish.kitty.orm.util.KittyConstants.ZERO_LENGTH_STRING;

/**
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KittyTable {

	/**
	 * Returns table name of associated with this class sqlite table
	 * @return
	 */
	String name() default ZERO_LENGTH_STRING;

	/**
	 * True if temporary table
	 * @return
	 */
	boolean isTemporaryTable() default false;

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
