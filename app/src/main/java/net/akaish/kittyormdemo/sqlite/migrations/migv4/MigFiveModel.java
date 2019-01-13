
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.sqlite.migrations.migv4;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.annotations.column.constraints.DEFAULT;
import net.akaish.kitty.orm.annotations.column.constraints.NOT_NULL;
import net.akaish.kitty.orm.annotations.column.constraints.PRIMARY_KEY;
import net.akaish.kitty.orm.annotations.column.constraints.UNIQUE;
import net.akaish.kitty.orm.annotations.table.KITTY_TABLE;
import net.akaish.kitty.orm.annotations.table.constraints.PRIMARY_KEY_T;

/**
 * Created by akaish on 30.10.18.
 * @author akaish (Denis Bogomolov)
 */
@KITTY_TABLE(tableName = "mig_five_test_complex_pk")
@PRIMARY_KEY_T(
        name = "ff_complex_pk",
        columns = {"ipk", "ipk_str"}
)
public class MigFiveModel extends KittyModel {

    @KITTY_COLUMN(
            columnOrder = 0,
            isValueGeneratedOnInsert = true
    )
    @NOT_NULL
    public Long ipk;

    @KITTY_COLUMN(
            columnOrder = 1,
            isValueGeneratedOnInsert = false,
            columnName = "ipk_str"
    )
    @UNIQUE
    public String ipkUniqueString;

    @KITTY_COLUMN(
            columnOrder = 2
    )
    @DEFAULT(literalValue = "'Default string'")
    public String someStr;

    public String toString() {
        return new StringBuilder(64)
                .append(ipk).append(" | ")
                .append(ipkUniqueString)
                .append(" | ")
                .append(someStr).toString();
    }
}
