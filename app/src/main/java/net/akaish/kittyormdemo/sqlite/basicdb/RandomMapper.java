
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

package net.akaish.kittyormdemo.sqlite.basicdb;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.query.QueryParameters;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.query.conditions.SQLiteOperator;
import net.akaish.kitty.orm.util.KittyConstants;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import static net.akaish.kitty.orm.query.conditions.SQLiteOperator.AND;
import static net.akaish.kitty.orm.query.conditions.SQLiteOperator.LESS_OR_EQUAL;
import static net.akaish.kitty.orm.query.conditions.SQLiteOperator.LESS_THAN;
import static net.akaish.kitty.orm.query.conditions.SQLiteOperator.GREATER_OR_EQUAL;
import static net.akaish.kitty.orm.query.conditions.SQLiteOperator.GREATER_THAN;
import static net.akaish.kittyormdemo.sqlite.basicdb.AbstractRandomModel.RND_ANIMAL_CNAME;

import java.util.List;


/**
 * Created by akaish on 09.08.18.
 * @author akaish (Denis Bogomolov)
 */
public class RandomMapper extends KittyMapper {

    public <M extends KittyModel> RandomMapper(KittyTableConfiguration tableConfiguration,
                                              M blankModelInstance,
                                              String databasePassword) {
        super(tableConfiguration, blankModelInstance, databasePassword);
    }

    protected SQLiteCondition getAnimalCondition(Animals animal) {
        return new SQLiteConditionBuilder()
                .addColumn(RND_ANIMAL_CNAME)
                .addSQLOperator("=")
                .addObjectValue(animal)
                .build();
    }

    public long deleteByRandomIntegerRange(int start, int end) {
        return deleteWhere("#?randomInt; >= ? AND #?randomInt; <= ?", start, end);
    }

    public long deleteByAnimal(Animals animal) {
        return deleteWhere(getAnimalCondition(animal));
    }

    public List<RandomModel> findByAnimal(Animals animal, long offset, long limit, boolean groupingOn) {
        SQLiteCondition condition = getAnimalCondition(animal);
        QueryParameters qparam = new QueryParameters();
        qparam.setLimit(limit).setOffset(offset);
        if(groupingOn)
            qparam.setGroupByColumns(RND_ANIMAL_CNAME);
        else
            qparam.setGroupByColumns(KittyConstants.ROWID);
        return findWhere(condition, qparam);
    }

    public List<RandomModel> findByIdRange(long fromId, long toId, boolean inclusive, Long offset, Long limit) {
        SQLiteCondition condition = new SQLiteConditionBuilder()
                .addColumn("id")
                .addSQLOperator(inclusive ? GREATER_OR_EQUAL : GREATER_THAN)
                .addValue(fromId)
                .addSQLOperator(AND)
                .addColumn("id")
                .addSQLOperator(inclusive ? LESS_OR_EQUAL : LESS_THAN)
                .addValue(toId)
                .build();
        QueryParameters qparam = new QueryParameters();
        qparam.setLimit(limit).setOffset(offset).setGroupByColumns(KittyConstants.ROWID);
        return findWhere(condition, qparam);
    }

    public List<RandomModel> findAllRandomModels(Long offset, Long limit) {
        QueryParameters qparam = new QueryParameters();
        qparam.setLimit(limit).setOffset(offset).setGroupByColumns(KittyConstants.ROWID);
        return findAll(qparam);
    }

}
