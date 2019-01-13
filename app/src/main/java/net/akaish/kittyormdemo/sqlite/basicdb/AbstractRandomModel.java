
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

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.enums.TypeAffinities;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

/**
 * Created by akaish on 25.08.18.
 * @author akaish (Denis Bogomolov)
 */
public abstract class AbstractRandomModel extends KittyModel {

    public static final String RND_INTEGER_CNAME = "rnd_int_custom_column_name";
    public static final String RND_ANIMAL_CNAME = "rndanimal";

    @KITTY_COLUMN(
            isIPK = true,
            columnOrder = 0
    )
    public Long id;

    @KITTY_COLUMN(
            columnOrder = 1
    )
    public int randomInt;

    @KITTY_COLUMN(
            columnOrder = 2,
            columnName = RND_INTEGER_CNAME
    )
    public Integer randomInteger;

    @KITTY_COLUMN(
            columnOrder = 3,
            columnName = RND_ANIMAL_CNAME
    )
    public Animals randomAnimal;

    @KITTY_COLUMN(
            columnOrder = 4,
            columnAffinity = TypeAffinities.TEXT
    )
    public String randomAnimalName;
}
