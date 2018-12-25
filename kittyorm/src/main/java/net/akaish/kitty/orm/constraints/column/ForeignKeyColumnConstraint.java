
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

package net.akaish.kitty.orm.constraints.column;

import net.akaish.kitty.orm.annotations.column.constraints.FOREIGN_KEY;
import net.akaish.kitty.orm.constraints.ForeignKeyReference;

/**
 * Wrapper for FOREIGN KEY column constraint
 * Created by akaish on 01.05.2018.
 * @author akaish (Denis Bogomolov)
 */

public class ForeignKeyColumnConstraint {

    protected final ForeignKeyReference foreignKeyReference;

    public ForeignKeyColumnConstraint(ForeignKeyReference foreignKeyReference) {
        this.foreignKeyReference = foreignKeyReference;
    }

    public ForeignKeyColumnConstraint(FOREIGN_KEY fkrAnnotation) {
        this(new ForeignKeyReference(fkrAnnotation.reference()));
    }

    public String toString() {
        return foreignKeyReference.toString();
    }
}
