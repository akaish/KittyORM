
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

package net.akaish.kitty.orm.query;

/**
 * Created by akaish on 06.02.2018.
 * @author akaish (Denis Bogomolov)
 */

public class SelectQuery extends BaseKittyQuery {

    static final String SELECT_FROM = "SELECT * FROM {0}";
    boolean rowIdOn = false;
    static final String SELECT_FROM_WITH_ROWID = "SELECT rowid, * FROM {0}";

    public SelectQuery(String tableName) {
        super(tableName);
    }

    void setRowIdSupport(boolean rowIdSupport) {
        rowIdOn = rowIdSupport;
    }

    @Override
    String getMainClause() {
        if(rowIdOn)
            return SELECT_FROM_WITH_ROWID;
        return SELECT_FROM;
    }
}
