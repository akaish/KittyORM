
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.lessons.five;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by akaish on 10.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class SharedPreferencesMigDB {
    private final Context ctx;
    private final SharedPreferences sf;

    private static final String SF_NAME = "migrationssf";

    public SharedPreferencesMigDB(Context ctx) {
        this.ctx = ctx;
        this.sf = ctx.getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
    }

    private static String CMDBV = "CMDBV";

    public int currentMigDBVersion() {
        return sf.getInt(CMDBV, -1);
    }

    private static final String IA_VERSION_CODE = "Version code can be in range [1; 4] or -1 only!";

    public void setCurrentMigDBVersion(int version) {
        if((version > 0 && version < 5) || version == -1)
            sf.edit().putInt(CMDBV, version).commit();
        else
            throw new IllegalArgumentException(IA_VERSION_CODE);
    }

    private static String DBMD = "DBMD";

    public boolean isDatabaseDeletedManually() {
        return sf.getBoolean(DBMD, false);
    }

    public void setDatabaseDeletedManually(boolean deleted) {
        sf.edit().putBoolean(DBMD, deleted).commit();
    }

    private static final String DBCREATED = "DBCREATED";

    public boolean isDatabaseCreated() {
        return sf.getBoolean(DBCREATED, false);
    }

    public boolean setDatabaseCreated(boolean created) {
        return sf.edit().putBoolean(DBCREATED, created).commit();
    }
}
