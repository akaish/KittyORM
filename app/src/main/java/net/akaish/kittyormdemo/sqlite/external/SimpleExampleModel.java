
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

package net.akaish.kittyormdemo.sqlite.external;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.annotations.table.KITTY_TABLE;

/**
 * @author akaish (Denis Bogomolov)
 */
@KITTY_TABLE
public class SimpleExampleModel extends KittyModel {
    public SimpleExampleModel() {
        super();
    }

    @KITTY_COLUMN(
            isIPK = true,
            columnOrder = 0
    )
    public Long id;

    @KITTY_COLUMN(columnOrder = 1)
    public int randomInteger;

    @KITTY_COLUMN(columnOrder = 2)
    public String firstName;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        return sb.append("[ rowid = ")
                    .append(getRowID())
                    .append(" ; id = ")
                    .append(id)
                    .append(" ; randomInteger = ")
                    .append(randomInteger)
                    .append(" ; firstName = ")
                    .append(firstName)
                    .append(" ]")
                    .toString();
    }

    public String toLogString() {
        return toString();
    }
}
