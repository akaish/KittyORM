
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.pkey;

import net.akaish.kitty.orm.enums.TypeAffinities;

import java.lang.reflect.Field;

/**
 * Created by akaish on 22.02.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittyPrimaryKeyPart {
    private final boolean isColumnValueGeneratedOnInsert;
    private final String columnName;
    private final Field field;
    private final TypeAffinities affinity;

    public KittyPrimaryKeyPart(boolean isColumnValueGeneratedOnInsert, String columnName, Field field,
                               TypeAffinities affinity) {
        this.isColumnValueGeneratedOnInsert = isColumnValueGeneratedOnInsert;
        this.columnName = columnName;
        this.field = field;
        this.affinity = affinity;
    }

    public boolean isColumnValueGeneratedOnInsert() {
        return isColumnValueGeneratedOnInsert;
    }

    public String getColumnName() {
        return columnName;
    }

    public Field getField() {
        return field;
    }

    public TypeAffinities getAffinity() {
        return affinity;
    }
}
