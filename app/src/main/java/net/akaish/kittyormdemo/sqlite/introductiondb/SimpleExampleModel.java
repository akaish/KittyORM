
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

package net.akaish.kittyormdemo.sqlite.introductiondb;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.Column;
import net.akaish.kitty.orm.annotations.table.KittyTable;

/**
 * @author akaish (Denis Bogomolov)
 */
@KittyTable
public class SimpleExampleModel extends KittyModel {
    public SimpleExampleModel() {
        super();
    }

    @Column(
            isIPK = true,
            order = 0
    )
    public Long id;

    @Column(order = 1)
    public int randomInteger;

    @Column(order = 2)
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
