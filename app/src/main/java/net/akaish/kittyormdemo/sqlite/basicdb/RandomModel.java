
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

package net.akaish.kittyormdemo.sqlite.basicdb;

import net.akaish.kitty.orm.annotations.KITTY_EXTENDED_CRUD;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.annotations.table.KITTY_TABLE;
import net.akaish.kitty.orm.annotations.table.index.INDEX;
import net.akaish.kitty.orm.annotations.table.index.INDEX_ENTRY;
import net.akaish.kitty.orm.enums.AscDesc;

/**
 * Created by akaish on 29.07.18.
 * @author akaish (Denis Bogomolov)
 */
@KITTY_TABLE
@KITTY_EXTENDED_CRUD(extendedCrudController = RandomMapper.class)
@INDEX(
        indexName = "random_animal_index",
        indexColumns = {@INDEX_ENTRY(columnName = AbstractRandomModel.RND_ANIMAL_CNAME, sortingOrder = AscDesc.ASCENDING)}
)
public class RandomModel extends AbstractRandomModel {


    public RandomModel() {
        super();
    }

    @KITTY_COLUMN(columnOrder = 5)
    public String randomAnimalSays;

    @Override
    public String toString() {
        return new StringBuffer(64).append("[ id = ")
                                            .append(id)
                                            .append("; randomInt = ")
                                            .append(Integer.toString(randomInt))
                                            .append("; randomInteger = ")
                                            .append(randomInteger)
                                            .append("; randomAnimal = ")
                                            .append(randomAnimal)
                                            .append("; randomAnimnalLocalizedName = ")
                                            .append(randomAnimalName)
                                            .append("; randomAnimalSays = ")
                                            .append(randomAnimalSays).append(" ]").toString();
    }
}
