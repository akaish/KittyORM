
/*
 * ---
 *
 *  Copyright (c) 2019 Denis Bogomolov (akaish)
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

package net.akaish.kitty.orm.util;

import static net.akaish.kitty.orm.util.KittySchemaColumnDefinition.PRAGMA_TYPES.integer;

/**
 * Created by akaish on 1.6.19.
 * @author akaish (Denis Bogomolov)
 */
public class KittySchemaColumnDefinitionBuilder {
    private String name;
    private KittySchemaColumnDefinition.PRAGMA_TYPES type = integer;
    private int notNull = 0;
    private int pk = 0;

    public KittySchemaColumnDefinitionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public KittySchemaColumnDefinitionBuilder setType(KittySchemaColumnDefinition.PRAGMA_TYPES type) {
        this.type = type;
        return this;
    }

    public KittySchemaColumnDefinitionBuilder setNotNull() {
        this.notNull = 1;
        return this;
    }

    public KittySchemaColumnDefinitionBuilder setPk() {
        this.pk = 1;
        return this;
    }

    public KittySchemaColumnDefinition build() {
        return new KittySchemaColumnDefinition(name, type, notNull, pk);
    }
}