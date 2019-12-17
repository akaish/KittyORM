
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

package net.akaish.kitty.orm.configuration.adc;

import android.content.Context;

import net.akaish.kitty.orm.KittyDatabase;
import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.configuration.KittyConfigurator;
import net.akaish.kitty.orm.configuration.conf.KittyDatabaseConfiguration;

import java.util.Map;

/**
 * Created by akaish on 14.03.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittyConfiguratorADC extends KittyConfigurator {

    protected final Class databaseClass;


    public <DB extends KittyDatabase, M extends KittyModel, D extends KittyMapper<M>> KittyConfiguratorADC(Context context,
                                                           Map<Class<M>, Class<D>> registry,
                                                           Class<DB> kittyDatabase, String databaseFilePath,
                                                           int databaseVersion) {
        super(context, registry, databaseFilePath, databaseVersion);
        this.databaseClass = kittyDatabase;
    }

    @Override
    public KittyDatabaseConfiguration generateDatabaseConfiguration() {
        return KittyAnnoDatabaseConfigurationUtil.generateDatabaseConfiguration(
                databaseClass, context, registry, databaseFilePath, databaseVersion
        );
    }
}
