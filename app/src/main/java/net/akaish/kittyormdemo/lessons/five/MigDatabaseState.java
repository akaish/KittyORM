
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.lessons.five;

import android.content.Context;

import net.akaish.kitty.orm.util.KittyUtils;
import net.akaish.kittyormdemo.R;

import java.text.MessageFormat;

/**
 * Created by akaish on 11.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class MigDatabaseState {
    final int implementationVersion;
    final static String DBNAME = "mig";
    final String[] implementationTableNames;
    final Context ctx;
    final SharedPreferencesMigDB sf;

    public MigDatabaseState(int implementationVersion, String[] implementationTableNames, Context ctx, SharedPreferencesMigDB sf) {
        this.implementationVersion = implementationVersion;
        this.implementationTableNames = implementationTableNames;
        this.ctx = ctx;
        this.sf = sf;
    }

    // Database name: {0}; implementation version {1}; database version {2}; database created {3}; database was deleted manually {4}; \r\n Implementation table names: {5}
    @Override
    public String toString() {
        return MessageFormat.format(
                ctx.getString(R.string._l5_op_mig_version),
                DBNAME,
                implementationVersion,
                sf.currentMigDBVersion(),
                sf.isDatabaseCreated(),
                sf.isDatabaseDeletedManually(),
                KittyUtils.implodeWithCommaInBKT(implementationTableNames)
        );
    }
}
