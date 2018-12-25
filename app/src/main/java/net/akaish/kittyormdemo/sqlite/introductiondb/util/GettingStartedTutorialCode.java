
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
 * This file is a part of KittyORM project (KittyORM Demo), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kittyormdemo.sqlite.introductiondb.util;

import android.content.Context;

import net.akaish.kitty.orm.CVUtils;
import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.pkey.KittyPrimaryKey;
import net.akaish.kitty.orm.pkey.KittyPrimaryKeyBuilder;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.query.conditions.SQLiteOperator;
import net.akaish.kittyormdemo.sqlite.introductiondb.SimpleDatabase;
import net.akaish.kittyormdemo.sqlite.introductiondb.SimpleExampleModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by akaish on 21.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class GettingStartedTutorialCode {
    private final Context context;
    private final SimpleDatabase database;

    public GettingStartedTutorialCode(Context ctx) {
        context = ctx;
        database = new SimpleDatabase(context);
    }

    public void insertNewRecord(int randomInteger, String firstName) {
        SimpleExampleModel m = new SimpleExampleModel();
        m.randomInteger = randomInteger;
        m.firstName = firstName;
        database.getMapper(SimpleExampleModel.class).insert(m);
    }

    public void allInOne() {
        // Creating new instance of SimpleDatabase
        SimpleDatabase simpleDatabase = new SimpleDatabase(context);

        KittyMapper mapper = simpleDatabase.getMapper(SimpleExampleModel.class);

        // Counting records in db table and deleting them if table not empty
        if(mapper.countAll() > 0)
            mapper.deleteAll();

        // Insert new model example
        // Creating and setting three new models
        SimpleExampleModel alex = new SimpleExampleModel();

        alex.randomInteger = 545141;
        alex.firstName = "Alex";

        SimpleExampleModel marina = new SimpleExampleModel();

        marina.randomInteger = 228;
        marina.firstName = "Marina";

        SimpleExampleModel marina2 = new SimpleExampleModel();

        marina2.randomInteger = 445555;
        marina2.firstName = "Marina";

        // Saving those models
        // Saving model with mapper.save(M model)
        mapper.save(alex);
        mapper.save(marina2);

        // Saving model with mapper.insert(M model)
        // Better to use insert(M model) for new DB records cause it is little bit faster
        long marinaRowid = mapper.insert(marina);


        // Finding existing records in DB and mapping them to entities

        int findOperationId = 0;
        // find with condition
        SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
        builder.addField("first_name")
                .addSQLOperator(SQLiteOperator.EQUAL)
                .addValue("Marina");
        List<SimpleExampleModel> marinas = mapper.findWhere(builder.build());


        findOperationId++;
        // find with RowID
        SimpleExampleModel marinaFromTableRowid = mapper.findByRowID(marinaRowid);

        findOperationId++;
        // find with IPK
        SimpleExampleModel marinaFromTableIPK = mapper.findByIPK(marinaFromTableRowid.id);


        findOperationId++;
        // find with KittyPrimaryKey
        KittyPrimaryKey pk = new KittyPrimaryKeyBuilder()
                .addKeyColumnValue("id", marinaFromTableRowid.id.toString())
                .build();
        SimpleExampleModel marinaFromTableKPK = mapper.findByPK(pk);


        // Generating and inserting list of 10 random models
        List<SimpleExampleModel> randomModels = new LinkedList<>();
        for(int i = 0; i < 10; i++)
            randomModels.add(RandomSimpleExampleModelUtil.randomSEModel());
        mapper.save(randomModels);

        // Deleting some models
        // Deleting by entity, make sure that entity has RowID\IPK\PK set
        SQLiteCondition alexCondition = new SQLiteConditionBuilder()
                .addField("first_name")
                .addSQLOperator(SQLiteOperator.EQUAL)
                .addValue("Alex")
                .build();
        SimpleExampleModel alexToDelete = mapper.findFirst(alexCondition);
        mapper.delete(alexToDelete);


        // Deleting with condition
        SQLiteCondition marina445555Condition = new SQLiteConditionBuilder()
                .addField("random_integer")
                .addSQLOperator(SQLiteOperator.EQUAL)
                .addValue(marina2.randomInteger)
                .build();
        mapper.deleteWhere(marina445555Condition);

        // Updating models
        // updating current model
        // if model has RowId or IPK or PrimaryKey values set (3-rd is slowest) just
        SimpleExampleModel newMarina = marinaFromTableIPK.clone(SimpleExampleModel.class);
        newMarina.randomInteger = 1337;
        if(mapper.update(newMarina) > 0) {
            findOperationId++;
            SimpleExampleModel marinaFromTableIPK2 = mapper.findByIPK(marinaFromTableRowid.id);
        }

        // another option is updating with generating query
        SimpleExampleModel updateMarina = new SimpleExampleModel();
        updateMarina.randomInteger = 121212;
        builder = new SQLiteConditionBuilder();
        builder.addField("first_name")
                .addSQLOperator(SQLiteOperator.EQUAL)
                .addValue("Marina");
        if(mapper.update(updateMarina, builder.build(), new String[]{"randomInteger"}, CVUtils.INCLUDE_ONLY_SELECTED_FIELDS) > 0) {
            findOperationId++;
            // find with IPK
            SimpleExampleModel marinaFromTableIPK2 = mapper.findByIPK(marinaFromTableRowid.id);
        }

        // bulk operations in TX mode
        LinkedList<SimpleExampleModel> randModels = new LinkedList<>();
        for(int i = 0; i < 10; i++)
            randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
        mapper.saveInTransaction(randModels);

        // closing mapper
        mapper.close();
    }

    /**
     * Tutorial code
     */
    public void insertNewRecord() {
        SimpleExampleModel alex = new SimpleExampleModel();

        alex.randomInteger = 545141;
        alex.firstName = "Alex";

        SimpleExampleModel marina = new SimpleExampleModel();

        marina.randomInteger = 228;
        marina.firstName = "Marina";

        KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
        mapper.save(alex);
        mapper.save(marina);
        mapper.close();
    }

    public SimpleExampleModel findOneMarina() {
        SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
        builder.addField("first_name")
                .addSQLOperator(SQLiteOperator.EQUAL)
                .addValue("Marina");
        KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
        List<SimpleExampleModel> out = mapper.findWhere(builder.build());
        mapper.close();
        if(out != null)
            if(out.size()>0)
                return out.get(0);
            else
                return null;
        else
            return null;
    }

    public void insertRandom() {
        List<SimpleExampleModel> randModels = new LinkedList<>();
        for(int i = 0; i < 10; i++)
            randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
        KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
        mapper.save(randModels);
        mapper.close();
    }

    public void deleteAlex() {
        KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
        SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
        builder.addField("first_name")
                .addSQLOperator(SQLiteOperator.EQUAL)
                .addValue("Alex");
        mapper.deleteWhere(builder.build());
        mapper.close();
    }

    public void updateMarina() {
        SimpleExampleModel marina = findOneMarina();
        if(marina != null) {
            marina.randomInteger = 1337;
        }
        KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
        mapper.save(marina);
        mapper.close();
    }

    public void rndBulkInTransaction() {
        List<SimpleExampleModel> randModels = new LinkedList<>();
        for(int i = 0; i < 10; i++)
            randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
        KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
        mapper.saveInTransaction(randModels);
        mapper.close();
    }
}
