
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

package net.akaish.kitty.orm.enums;

/**
 * Created by akaish on 03.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public enum Keywords {
    TABLE("TABLE"),
    CREATE("CREATE"),
    DROP("DROP"),
    DELETE("DELETE"),
    UNIQUE("UNIQUE"),
    IF_NOT_EXISTS("IF NOT EXISTS"),
    IF_EXISTS("IF EXISTS"),
    PRIMARY_KEY("PRIMARY KEY"),
    FOREIGN_KEY("FOREIGN KEY"),
    AUTOINCREMENT("AUTOINCREMENT"),
    REFERENCES("REFERENCES"),
    ON_UPDATE("ON UPDATE"),
    ON_DELETE("ON DELETE"),
    CHECK("CHECK"),
    DEFAULT("DEFAULT"),
    COLLATE("COLLATE"),
    TEMPORARY("TEMPORARY"),
    CONSTRAINT("CONSTRAINT"),
    NOT("NOT"),
    WHERE("WHERE"),
    NULL("NULL"),
    ON("ON"),
    WITHOUT("WITHOUT"),
    ROWID("ROWID"),
    INDEX("INDEX");

    private final String sqlText;

    Keywords(String sqlTextRep) {
        sqlText = sqlTextRep;
    }

    public boolean equalsName(String sqlText) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return this.sqlText.equals(sqlText);
    }

    @Override
    public String toString() {
        return this.sqlText;
    }
}
