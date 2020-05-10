
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

package net.akaish.kitty.orm.annotations.column.constraints;

import net.akaish.kitty.orm.annotations.column.Column;
import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.enums.ConflictClauses;
import net.akaish.kitty.orm.enums.TypeAffinities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for PRIMARY KEY column constraint.
 * <br>
 * <br><b>How to set synthetic primary key here, through field types and affinities:</b>
 * <br>
 * <br>If you want to use this field as primary key associated, use {@link TypeAffinities#INTEGER}
 * as affinity in {@link Column#affinity()} without {@link #autoincrement()}
 * flag and without {@link AscDesc#DESCENDING} as value for {@link #orderAscDesc()}, cause:
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
 * Created by akaish on 30.04.2018.
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface PrimaryKey {

    /**
     * Order (ASC\DESC), if default value set than it would be skipped at column PK constraint generation
     * <br> Default value is {@link AscDesc#NOT_SET_SKIP_OR_DEFAULT}
     * @return
     */
    AscDesc orderAscDesc() default  AscDesc.NOT_SET_SKIP_OR_DEFAULT;

    /**
     * AUTOINCREMENT keyword flag, if false than it would be skipped at column PK constraint generation
     * <br> Default value is false
     * @return
     */
    boolean autoincrement() default false;

    /**
     * Action that should be performed on conflict, if default value set than it would be skipped at column PK constraint generation
     * <br> Default value is {@link ConflictClauses#CONFLICT_CLAUSE_NOT_SET}
     * @return
     */
    ConflictClauses onConflictAction() default ConflictClauses.CONFLICT_CLAUSE_NOT_SET;
}
