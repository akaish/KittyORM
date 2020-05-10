
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

package net.akaish.kitty.orm.annotations.column;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.constraints.NotNull;
import net.akaish.kitty.orm.annotations.column.constraints.PrimaryKey;
import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.enums.TypeAffinities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.akaish.kitty.orm.util.KittyConstants.ZERO_LENGTH_STRING;

/**
 * 
 * This interface designed for describing modelClass fields to be used in this lib automatically with SQLite DB. There are some things
 * that not obvious and while writing it it was kind of revelation.
 * <br>
 * <br><b>How to set synthetic primary key here, through field types and affinities:</b>
 * <br>
 * <br>If you want to use this field as primary key associated, use {@link TypeAffinities#INTEGER}
 * as affinity in {@link Column#affinity()} without {@link PrimaryKey#autoincrement()}
 * flag and without {@link AscDesc#DESCENDING} as value for {@link PrimaryKey#orderAscDesc()}, cause:
 * <br>1) there some issues with aliasing primary key with oid if key type is not integer, however other integer types
 * such as INT and BIGINT not presented in this code, they can be presented in future
 * ( Other integer type names like "INT" or "BIGINT" or "SHORT INTEGER" or "UNSIGNED INTEGER"
 * causes the primary key column to behave as an ordinary table column with integer affinity 
 * and a unique index, not as an alias for the rowid.)
 * <br>2) CREATE TABLE t(x INTEGER PRIMARY KEY DESCENDING, y, z)  doesn't result in "x" being an alias for the rowid
 * <br>3) the main purpose of using AUTOINCREMENT attribute is to prevent SQLite to reuse value that has not been 
 * used or from the previously deleted row. In case if integer primary key is associated with oid (row id) it
 * would be just overkill.
 * <br><b>So, use for synthetic primary key something like "x INTEGER NOT NULL PRIMARY KEY"</b>
 * <br> You can achieve this in two different ways:
 * <br>1) In {@link Column} set {@link Column#isIPK()} to true and {@link Column#affinity()} to
 * {@link TypeAffinities#INTEGER} or
 * <br>2) Set in {@link Column} @link KITTY_COLUMN#columnAffinity()} to {@link TypeAffinities#INTEGER} and
 * annotate model's field with {@link PrimaryKey} without setting {@link PrimaryKey#autoincrement()} and
 * {@link PrimaryKey#orderAscDesc()} to value {@link AscDesc#DESCENDING}. Also do not forget to annotate field with
 * {@link NotNull} (SQLite supports NULL values for IPK but they not supported by KittyORM).
 * <br>
 * <br><b>Why not implemented such things as additional types (such as SMALLINT, VARCHAR etc) and field size constraints  
 * (such as VARCHAR(20))</b>
 * <br>Ok, it not implemented cause it just doesn't work. As I understood, there is no difference between, for example INT and BIGINT
 * in SQLite, cause anyway it would be treated not by container but with more abstract storage class and type affinity and as for me, it is better
 * to use only type affinities, cause if you would put 8-bit value in TINYINT it would work. I suppose all this types were added
 * only for implementation of SQL language. And anyway all would be stored as text in db. Also, length restrictions just ignored
 * in SQLite. You can set check constraint for checking type of value between insertion it db table, but I don't think it is needed. 
 * 
 * @see <a href="https://sqlite.org/lang_createtable.html">SQL As Understood By SQLite#CREATE TABLE</a>
 * @see <a href="http://www.sqlitetutorial.net/sqlite-autoincrement/">SQLite AUTOINCREMENT : Why You Should Avoid Using It</a>
 * 
 * @author akaish (Denis Bogomolov)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Column {
	
	/**
	 * Returns associated with this variable column name of sql table
	 * @return
	 */
	String name() default ZERO_LENGTH_STRING;
	
	/**
	 * Returns associated with this variable column type of sql table
	 * @return
	 */
	TypeAffinities affinity() default TypeAffinities.NOT_SET_USE_DEFAULT_MAPPING;
	
	/**
	 * Returns field order
	 * @return
	 */
	int order();

	/**
	 * Returns true if this field is part of primary key and this value should be generated on databaseClass insert
	 * <br> What is idea of this field? {@link KittyMapper#save(KittyModel)} method should understand what operation
	 * on model should be performed, insert or update. Firstly this method checks availability of rowid, if mapper has
	 * rowid support turned off or table was created with WITHOUT ROWID, than method would try to check all columns of primary
	 * key and fields of model that represents table on NULL values.
	 * <br> So, if all of fields of model that are representations of columns of PK that are marked as generated on insert
	 * are NULL than insert operation would be performed, otherwise update operation would be run.
	 * <br> To enable this feature you have to implement triggers for those values manually at create\migrate scripts
	 */
	boolean isValueGeneratedOnInsert() default false;


	/**
	 * Returns if this field should be used as simple index column for this table
	 * <br> If true than resulting column would automatically get two constraints:
	 * <br> {@link NotNull} @NOT_NULL
	 * <br> {@link PrimaryKey} @PRIMARY_KEY{orderAscDesc = ASCENDING}
	 * <br> This would result in something like 'COLNAME INTEGER NOT NULL PRIMARY KEY ASC'
	 * <br> Also, notice that this would rewrite {@link #affinity()} to INTEGER and
	 * any {@link NotNull} and {@link PrimaryKey} would be skipped.
	 * <br><b>And make sure that field has {@link Long} type in order that {@link KittyMapper}
	 * would process {@link KittyMapper#save(KittyModel)} operation effectively</b>
	 * @return
	 */
	boolean isIPK() default false;
}
