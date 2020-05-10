
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

package net.akaish.kitty.orm.configuration.conf;

import net.akaish.kitty.orm.constraints.column.CheckColumnConstraint;
import net.akaish.kitty.orm.constraints.column.CollationColumnConstraint;
import net.akaish.kitty.orm.constraints.column.DefaultColumnConstraint;
import net.akaish.kitty.orm.constraints.column.ForeignKeyColumnConstraint;
import net.akaish.kitty.orm.constraints.column.NotNullColumnConstraint;
import net.akaish.kitty.orm.constraints.column.PrimaryKeyColumnConstraint;
import net.akaish.kitty.orm.constraints.column.UniqueColumnConstraint;
import net.akaish.kitty.orm.enums.TypeAffinities;

import java.lang.reflect.Field;

/**
 * Created by akaish on 07.02.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittyColumnMainConfiguration {
    public final String columnName;
    public final TypeAffinities columnAffinity;
    public final Field columnField;
    public final boolean isIPK;

    public final DefaultColumnConstraint defaultConstraint;
    public final CheckColumnConstraint checkConstraint;
    public final NotNullColumnConstraint notNullConstraint;
    public final UniqueColumnConstraint uniqueColumnConstraint;
    public final CollationColumnConstraint collationColumnConstraint;
    public final PrimaryKeyColumnConstraint primaryKeyColumnConstraint;
    public final ForeignKeyColumnConstraint foreignKeyColumnConstraint;


    public final int columnOrder;
    public final boolean isValueGeneratedOnInsert;

    public KittyColumnMainConfiguration(String columnName,
                                        TypeAffinities columnAffinity, Field columnField,
                                        DefaultColumnConstraint defaultConstraint, boolean isIPK,
                                        CheckColumnConstraint checkConstraint, int columnOrder,
                                        boolean isValueGeneratedOnInsert, NotNullColumnConstraint notNullConstraint,
                                        UniqueColumnConstraint uniqueColumnConstraint,
                                        CollationColumnConstraint collationColumnConstraint,
                                        PrimaryKeyColumnConstraint primaryKeyColumnConstraint,
                                        ForeignKeyColumnConstraint foreignKeyColumnConstraint) {
        this.columnName = columnName;
        this.columnAffinity = columnAffinity;
        this.columnField = columnField;
        this.defaultConstraint = defaultConstraint;
        this.isIPK = isIPK;
        this.checkConstraint = checkConstraint;
        this.columnOrder = columnOrder;
        this.isValueGeneratedOnInsert = isValueGeneratedOnInsert;
        this.notNullConstraint = notNullConstraint;
        this.uniqueColumnConstraint = uniqueColumnConstraint;
        this.collationColumnConstraint = collationColumnConstraint;
        this.primaryKeyColumnConstraint = primaryKeyColumnConstraint;
        this.foreignKeyColumnConstraint = foreignKeyColumnConstraint;
    }
}
