
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.pkey;

import net.akaish.kitty.orm.KittyModel;

import java.util.HashMap;

/**
 * Builder for primary key
 * Created by akaish on 04.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyPrimaryKeyBuilder {
    private HashMap<String, String> primaryKeyColumnValues = new HashMap<>();
    private HashMap<String, KittyPrimaryKeyPart> primaryKeyColumnKeyParts = new HashMap<>();
    private String databaseName;
    private String tableName;
    private Class<? extends KittyModel> modelClass;

    public KittyPrimaryKeyBuilder addKeyColumnValue(String pkeyColumn, String pkeyValue) {
        primaryKeyColumnValues.put(pkeyColumn, pkeyValue);
        return this;
    }

    public KittyPrimaryKeyBuilder addPKeyPart(String pkeyColumnName, KittyPrimaryKeyPart pkeyPart) {
        primaryKeyColumnKeyParts.put(pkeyColumnName, pkeyPart);
        return this;
    }

    public KittyPrimaryKeyBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public KittyPrimaryKeyBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public KittyPrimaryKeyBuilder setModelClass(Class<? extends KittyModel> modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    public KittyPrimaryKey build() {
        return new KittyPrimaryKey(
                primaryKeyColumnValues,
                primaryKeyColumnKeyParts,
                tableName,
                databaseName,
                modelClass
        );
    }
}
