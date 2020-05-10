
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

package net.akaish.kitty.orm.annotations;

/**
 * Created by akaish on 30.04.2018.
 */

import net.akaish.kitty.orm.enums.DeferrableOptions;
import net.akaish.kitty.orm.enums.OnUpdateDeleteActions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for foreign key reference.
 * <br>TODO add MATCH support as described in <a href="https://www.sqlite.org/syntax/foreign-key-clause.html">foreign-key-clause</a>
 * @author akaish (Denis Bogomolov)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface ForeignKeyReference {
    String foreignTableName();
    String[] foreignTableColumns() default {};
    OnUpdateDeleteActions onUpdate() default OnUpdateDeleteActions.NO_ACTION;
    OnUpdateDeleteActions onDelete() default OnUpdateDeleteActions.NO_ACTION;
    DeferrableOptions deferrableOption() default DeferrableOptions.NOT_SET_IGNORE_OPTION;
}
