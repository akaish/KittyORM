
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

package net.akaish.kitty.orm.constraints;

import net.akaish.kitty.orm.annotations.FOREIGN_KEY_REFERENCE;
import net.akaish.kitty.orm.enums.DeferrableOptions;
import net.akaish.kitty.orm.enums.OnUpdateDeleteActions;

import static net.akaish.kitty.orm.enums.Keywords.ON_DELETE;
import static net.akaish.kitty.orm.enums.Keywords.ON_UPDATE;
import static net.akaish.kitty.orm.enums.Keywords.REFERENCES;
import static net.akaish.kitty.orm.util.KittyConstants.WHITESPACE;
import static net.akaish.kitty.orm.util.KittyUtils.implodeWithCommaInBKT;

/**
 * Wrapper for foreign key reference
 * Created by akaish on 01.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public class ForeignKeyReference {

    protected final String foreignTable;
    protected final String foreignColumns;
    protected final String onDeleteAction;
    protected final String onUpdateAction;
    protected final String deferrableOption;

    public ForeignKeyReference(String foreignTableName, String[] foreignColumnNames,
                               OnUpdateDeleteActions onDeleteAction,
                               OnUpdateDeleteActions onUpdateAction,
                               DeferrableOptions deferrableOption) {
        String onUpdate = null;
        String onDelete = null;
        String deferrable = null;
        if(!onDeleteAction.equals(OnUpdateDeleteActions.NOT_SET_SKIP_THIS_FIELD))
            onDelete = onDeleteAction.toString();
        if(!onUpdateAction.equals(OnUpdateDeleteActions.NOT_SET_SKIP_THIS_FIELD))
            onUpdate = onUpdateAction.toString();
        if(!deferrableOption.equals(DeferrableOptions.NOT_SET_IGNORE_OPTION))
            deferrable = deferrableOption.toString();
        this.foreignTable = foreignTableName;
        this.foreignColumns = implodeWithCommaInBKT(foreignColumnNames);
        if(onDelete!=null) {
            this.onDeleteAction = new StringBuffer(16)
                    .append(ON_DELETE).append(WHITESPACE)
                    .append(onDelete).toString();
        } else {
            this.onDeleteAction = null;
        }
        if(onUpdate!=null) {
            this.onUpdateAction = new StringBuffer(16)
                    .append(ON_UPDATE).append(WHITESPACE)
                    .append(onUpdate).toString();
        } else {
            this.onUpdateAction = null;
        }
        this.deferrableOption = deferrable;
    }

    public ForeignKeyReference(FOREIGN_KEY_REFERENCE fkrAnnotation) {
        this(fkrAnnotation.foreignTableName(),
                fkrAnnotation.foreignTableColumns(),
                fkrAnnotation.onDelete(),
                fkrAnnotation.onUpdate(),
                fkrAnnotation.deferrableOption());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(32);
        sb.append(REFERENCES);
        sb.append(WHITESPACE);
        sb.append(foreignTable);
        if(foreignColumns.length()>0) {
            sb.append(WHITESPACE);
            sb.append(foreignColumns);
        }
        if(onUpdateAction!=null) {
            sb.append(WHITESPACE);
            sb.append(onUpdateAction);
        }
        if(onDeleteAction!=null) {
            sb.append(WHITESPACE);
            sb.append(onDeleteAction);
        }
        if(deferrableOption!=null) {
            sb.append(WHITESPACE);
            sb.append(deferrableOption);
        }
        return sb.toString().trim();
    }
}
