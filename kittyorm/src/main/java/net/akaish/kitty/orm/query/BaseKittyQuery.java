
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

package net.akaish.kitty.orm.query;

import java.text.MessageFormat;

import static net.akaish.kitty.orm.util.KittyConstants.SEMICOLON;

/**
 * Created by akaish on 06.02.2018.
 * @author akaish (Denis Bogomolov)
 */

public abstract class BaseKittyQuery {

    //protected final String mainClause;
    protected final String tableName;

    public BaseKittyQuery(String tableName) {
        this.tableName = tableName;
        //mainClause = getMainClause();
    }

    abstract String getMainClause();

    public boolean canBeBuffered() {
        return true;
    }

    public String getQueryStart() {
        return MessageFormat.format(getMainClause(), tableName);
    }

    public KittySQLiteQuery getSQLQuery() {
        return new KittySQLiteQuery(getQueryStart() + SEMICOLON, null);
    }
}
