
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.sqlite.migrations.migv2;

/**
 * Created by akaish on 03.10.18.
 */

import net.akaish.kitty.orm.annotations.column.ONE_COLUMN_INDEX;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.annotations.column.constraints.DEFAULT;
import net.akaish.kitty.orm.annotations.column.constraints.NOT_NULL;
import net.akaish.kitty.orm.annotations.table.KITTY_TABLE;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.enums.LiteralValues;

import java.sql.Timestamp;

/**
 * Created by akaish on 03.10.18.
 * @author akaish (Denis Bogomolov)
 */
@KITTY_TABLE(
        tableName = "mig_one"
)
public class MigOneModel extends KittyModel {
    @KITTY_COLUMN(
            columnOrder = 0,
            isIPK = true)
    public Long id;

    @KITTY_COLUMN(columnOrder = 1)
    @NOT_NULL
    @DEFAULT(predefinedLiteralValue = LiteralValues.CURRENT_DATE)
    public String creationDate;

    @KITTY_COLUMN(columnOrder = 2)
    @DEFAULT(signedInteger = 228)
    @ONE_COLUMN_INDEX(indexName = "m1_di_index")
    public Integer defaultInteger;

    @KITTY_COLUMN(columnOrder = 3)
    public Timestamp currentTimestamp;

    public String toString() {
        return new StringBuilder(64)
                .append("[ id = ")
                .append(id)
                .append(" ; creationDate = ")
                .append(creationDate)
                .append(" ; someInteger = ")
                .append(defaultInteger)
                .append(" ; currentTimestamp = ")
                .append(currentTimestamp)
                .append(" ]").toString();
    }
}