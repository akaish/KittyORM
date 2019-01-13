
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

package net.akaish.kitty.orm;

import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.util.ArrayList;
import java.util.Map;

/**
 * KittyORM abstract model class.
 * @author akaish (Denis Bogomolov)
 *
 */
public abstract class KittyModel implements Cloneable {

    public static final String EXCEPTION_UNABLE_TO_CLONE = "Unable to clone model class, see exception details!";

    private Long rowid;
    final ArrayList<String> exclusions = new ArrayList<>();

    final void setRowID(Long rowid) {
        this.rowid = rowid;
    }

    public final Long getRowID() {
        return rowid;
    }

    /**
     * Returns map where keys are names of columns that are part of PK for this model and values are
     * values of those columns for this model
     * <br> Override this method manually if you want achieve more performance for update methods in
     * {@link KittyMapper} (setting this method manually in KittyModel child would avoid generating
     * this map in update operations via reflection in cases when RowId not set and where statement not
     * defined).
     * @return
     */
    public Map<String, String> getPrimaryKeyValues() {
        return null;
    }

    // CLONING IMPLEMENTATIONS
    public <T extends KittyModel> T clone(Class<T> recordClass) {
        try {
            T record = (T) super.clone();
            return record;
        } catch (CloneNotSupportedException e) {
            throw new KittyRuntimeException(EXCEPTION_UNABLE_TO_CLONE, e);
        }
    }

    @Override
    public KittyModel clone() {
        try {
            KittyModel record = (KittyModel) super.clone();
            return record;
        } catch (CloneNotSupportedException e) {
            throw new KittyRuntimeException(EXCEPTION_UNABLE_TO_CLONE, e);
        }
    }

    /**
     * Implement this method in order to use functionality of dev logging model values.
     * <br><b> And switch logging at production cause, you know, nobody wants their private data to be
     * <br> outputted into serial console, lol. Or at least switch production mode to false, cause models
     * <br> logging would be used only when both logging is true and development mode is false!</b>
     * <br> Good approach would be implementing it as kv pairs with comma delimiter via
     * <br> {@link net.akaish.kitty.orm.util.KittyUtils#implodeWithCommaInBKT(String[])}
     * <br> For example, it would be good option to implement like following for this
     * <br> example model with following model fields and table name persons:
     * <br> Model: persons, fields _id, name, surname
     * <br>     public String toLogString() {
     * <br>         return implodeWithCommaInBKT(new String[] {
     * <br>             "[table name = persons]",
     * <br>             "[rowid = " + getRowID() + "]",
     * <br>             "[_id = " + getId() + "]",
     * <br>             "[name = " + getName() + "]",
     * <br>             "[surname = " + getSurname() + "]" });
     * <br>         }
     * <br> Also do not forget for handling NULL's and, to be honest, use such methods as Long.toString() etc.
     * <br>
     * @return
     */
    public String toLogString() {
        return null;
    }

    /**
     * Sets field exclusion for setting it explicitly
     * <br> Usefull for inserting values that should have to be assigned by DEFAULT constraint
     * <br> or for fields that are parts of PK that should be generated automatically by trigger
     * @param fieldName
     */
    public final void setFieldExclusion(String fieldName) {
        exclusions.add(fieldName);
    }

}
