
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.pkey;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.enums.TypeAffinities;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Primary key class
 * Created by akaish on 04.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyPrimaryKey {
    private Map<String, String> primaryKeyColumnValues;
    private Map<String, KittyPrimaryKeyPart> primaryKeyColumnKeyParts;
    private final String tableName;
    private final String databaseName;
    private final Class<? extends KittyModel> modelClass;

    private static String EXC_MSG_NO_PKPART = "Unable to add value to primary key column field {0}.{1}.{2} at {3}! Reason: this field not defined!";

    public KittyPrimaryKey(Map<String, String> primaryKeyColumnValues,
                           Map<String, KittyPrimaryKeyPart> primaryKeyColumnKeyParts,
                           String tableName, String databaseName,
                           Class<? extends KittyModel> modelClass) {
        this.primaryKeyColumnValues = primaryKeyColumnValues;
        this.primaryKeyColumnKeyParts = primaryKeyColumnKeyParts;
        this.tableName = tableName;
        this.databaseName = databaseName;
        this.modelClass = modelClass;
    }

    public Map<String, String> getPrimaryKeyColumnValues() {
        return primaryKeyColumnValues;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Class<? extends KittyModel> getModelClass() {
        return modelClass;
    }

    public String[] getPrimaryKeyColumnNames() {
        return primaryKeyColumnKeyParts.keySet().toArray(new String[primaryKeyColumnKeyParts.keySet().size()]);
    }

    public Set<String> getPrimaryKeyColumnNamesSet() {
        return primaryKeyColumnKeyParts.keySet();
    }

    public KittyPrimaryKey setpKeyValue(String pKeyColumn, String pKeyValue) {
        if(!primaryKeyColumnKeyParts.keySet().contains(pKeyColumn)) {
            throw new KittyRuntimeException(MessageFormat.format(EXC_MSG_NO_PKPART,
                    databaseName,
                    tableName,
                    pKeyColumn,
                    modelClass.getCanonicalName()
                    ));
        } else {
            primaryKeyColumnValues.put(pKeyColumn, pKeyValue);
        }
        return this;
    }

    public String getPKeyColumnValue(String pKeyColumn) {
        return primaryKeyColumnValues.get(pKeyColumn);
    }

    public KittyPrimaryKeyPart getPrimaryKeyPart(String pKeyColumn) {
        return primaryKeyColumnKeyParts.get(pKeyColumn);
    }

    public boolean isIPK() {
        if(primaryKeyColumnKeyParts.size() == 1) {
            KittyPrimaryKeyPart keyPart = primaryKeyColumnKeyParts.values().iterator().next();
            if(keyPart.isColumnValueGeneratedOnInsert() && keyPart.getAffinity().equals(TypeAffinities.INTEGER)) {
                return true;
            }
        }
        return false;
    }

    public static class Builder {
        private HashMap<String, String> primaryKeyColumnValues = new HashMap<>();
        private HashMap<String, KittyPrimaryKeyPart> primaryKeyColumnKeyParts = new HashMap<>();
        private String databaseName;
        private String tableName;
        private Class<? extends KittyModel> modelClass;

        public Builder addKeyColumnValue(String pkeyColumn, String pkeyValue) {
            primaryKeyColumnValues.put(pkeyColumn, pkeyValue);
            return this;
        }

        public Builder addPKeyPart(String pkeyColumnName, KittyPrimaryKeyPart pkeyPart) {
            primaryKeyColumnKeyParts.put(pkeyColumnName, pkeyPart);
            return this;
        }

        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder setModelClass(Class<? extends KittyModel> modelClass) {
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
}
