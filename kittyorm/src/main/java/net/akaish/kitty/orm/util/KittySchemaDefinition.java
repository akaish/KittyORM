
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

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Model used for checking schema
 * Created by akaish on 1.6.19.
 * @author akaish (Denis Bogomolov)
 */
public class KittySchemaDefinition {
    final Map<String, SparseArray<KittySchemaColumnDefinition>> definitions = new HashMap<>();

    public void addDefinition(String name, SparseArray<KittySchemaColumnDefinition> definition) {
        definitions.put(name, definition);
    }

    public SparseArray<KittySchemaColumnDefinition> getTableDefinition(String name) {
        return definitions.get(name);
    }

    public Set<String> getExpectedTableNames() {
        return definitions.keySet();
    }
}
