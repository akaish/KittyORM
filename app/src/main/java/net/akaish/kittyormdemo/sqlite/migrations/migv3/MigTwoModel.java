
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

package net.akaish.kittyormdemo.sqlite.migrations.migv3;

import com.google.gson.GsonBuilder;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.ForeignKeyReference;
import net.akaish.kitty.orm.annotations.column.Column;
import net.akaish.kitty.orm.annotations.column.SerializationRules;
import net.akaish.kitty.orm.annotations.column.constraints.ForeignKey;
import net.akaish.kitty.orm.annotations.table.KittyTable;
import net.akaish.kitty.orm.enums.OnUpdateDeleteActions;
import net.akaish.kitty.orm.enums.TypeAffinities;
import net.akaish.kittyormdemo.gsonmodels.AnimalSounds;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

/**
 * Created by akaish on 03.10.18.
 * @author akaish (Denis Bogomolov)
 */

@KittyTable(
        name = "mig_two"
)
public class MigTwoModel extends KittyModel {

    @Column(
            order = 0,
            isIPK = true
    )
    public Long id;

    @Column(
            order = 1
    )
    @ForeignKey(
            reference = @ForeignKeyReference(
                    foreignTableName = "mig_one",
                    foreignTableColumns = {"id"},
                    onUpdate = OnUpdateDeleteActions.CASCADE,
                    onDelete = OnUpdateDeleteActions.CASCADE
            )
    )
    public Long migOneReference;

    @Column(order = 2)
    public Animals someAnimal;

    @Column(
            order = 3,
            affinity = TypeAffinities.TEXT
    )
    @SerializationRules
    public AnimalSounds someAnimalSound;

    String someAnimalSoundSerialize() {
        if(someAnimalSound == null) return null;
        return new GsonBuilder().create().toJson(someAnimalSound);
    }

    AnimalSounds someAnimalSoundDeserialize(String cvData) {
        if(cvData == null) return null;
        if(cvData.length() == 0) return null;
        return new GsonBuilder().create().fromJson(cvData, AnimalSounds.class);
    }

    @Override
    public String toString() {
        return new StringBuilder(64)
                .append("[ id = ")
                .append(id)
                .append(" ; migOneReference = ")
                .append(migOneReference)
                .append(" ; someAnimal = ")
                .append(someAnimal)
                .append(" ; someAnimalSound = ")
                .append(someAnimalSoundSerialize())
                .append(" ] ").toString();
    }
}