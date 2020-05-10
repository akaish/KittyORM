
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.sqlite.basicdb;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.ForeignKeyReference;
import net.akaish.kitty.orm.annotations.column.Column;
import net.akaish.kitty.orm.annotations.column.SingleColumnIndex;
import net.akaish.kitty.orm.annotations.column.constraints.Check;
import net.akaish.kitty.orm.annotations.column.constraints.Default;
import net.akaish.kitty.orm.annotations.column.constraints.NotNull;
import net.akaish.kitty.orm.annotations.column.constraints.PrimaryKey;
import net.akaish.kitty.orm.annotations.column.constraints.Unique;
import net.akaish.kitty.orm.annotations.table.KittyTable;
import net.akaish.kitty.orm.annotations.table.constraints.TableForeignKey;
import net.akaish.kitty.orm.annotations.table.index.TableIndex;
import net.akaish.kitty.orm.annotations.table.index.TableIndexEntree;
import net.akaish.kitty.orm.enums.LiteralValues;
import net.akaish.kitty.orm.enums.OnUpdateDeleteActions;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.sql.Timestamp;

/**
 * Created by akaish on 25.08.18.
 * @author akaish (Denis Bogomolov)
 */
@KittyTable(name = "cai")
@TableForeignKey(
        name = "CAI_FK",
        columns = {IndexesAndConstraintsModel.RANDOM_ID_CNAME},
        reference = @ForeignKeyReference(
                foreignTableName = "random",
                foreignTableColumns = {"id"},
                onUpdate = OnUpdateDeleteActions.CASCADE,
                onDelete = OnUpdateDeleteActions.CASCADE
        )
)
@TableIndex(columns = {@TableIndexEntree(name = "creation_date")})
public class IndexesAndConstraintsModel extends KittyModel {
    static final String RANDOM_ID_CNAME = "rnd_id";

    @Column(order = 0)
    @PrimaryKey
    @NotNull
    public Long id;

    @Column(order = 1)
    @NotNull
    @Unique
    public Long rndId;

    @Column(order = 2)
    @Check(checkExpression = "animal IN (\"CAT\", \"TIGER\", \"LION\")") // only cats allowed to this party
    public Animals animal;

    @Column(order = 3)
    @Default(signedInteger = 28) // You can choose for options for default declaration, if nothing set than 0 value would be used
    @NotNull
    public Integer defaultNumber;

    @Column(order = 4)
    @Default(
            predefinedLiteralValue = LiteralValues.CURRENT_DATE
    )
    @NotNull
    public String creationDate;

    @Column(order = 5)
    @Default(
            predefinedLiteralValue = LiteralValues.CURRENT_TIMESTAMP
    )
    @SingleColumnIndex(unique = true, name = "IAC_unique_index_creation_timestamp")
    @NotNull
    public Timestamp creationTmstmp;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("[ RowID = ").append(getRowID())
                .append(" ; id = ").append(id)
                .append(" ; rndId = ").append(rndId)
                .append(" ; animal = ").append(animal)
                .append(" ; defaultNumber = ").append(defaultNumber)
                .append(" ; creationDate = ").append(creationDate)
                .append(" ; creationTmstmp = ").append(creationTmstmp).append(" ]");
        return sb.toString();
    }
}
