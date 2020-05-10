
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

package net.akaish.kittyormdemo.lessons.one;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.akaish.kitty.orm.CVUtils;
import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.pkey.KittyPrimaryKey;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.enums.SQLiteOperator;
import net.akaish.kitty.orm.util.KittyLog;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonTabFragmentOnVisibleAction;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.lessons.LessonBaseFragment;
import net.akaish.kittyormdemo.sqlite.introductiondb.SimpleDatabase;
import net.akaish.kittyormdemo.sqlite.introductiondb.SimpleExampleModel;
import net.akaish.kittyormdemo.sqlite.introductiondb.util.RandomSimpleExampleModelUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T2_SCHEMA;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T2_SOURCE;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T2_TUTORIAL;

/**
 * Created by akaish on 21.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson1Tab2GettingStarted extends LessonBaseFragment implements LessonTabFragmentOnVisibleAction {

    private ListView actionsLW;
    private Button goButton;

    private ListView expandedLW;
    private TextView expandedText;
    private String expandedTextPattern;

    public Lesson1Tab2GettingStarted() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson1_tab2_getting_started, container, false);
        actionsLW = rootView.findViewById(R.id.l1_t2_actions);
        goButton = rootView.findViewById(R.id.l1_t2_go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });
        expandedLW = rootView.findViewById(R.id._l1_t2_expanded_panel_list);
        expandedText = rootView.findViewById(R.id._l1_t2_expanded_panel_text);
        expandedTextPattern = getString(R.string._l1_t2_expanded_text_pattern);
        rootView.findViewById(R.id.l1_t2_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateExpandPanelList();
    }

    void clear() {
        SimpleDatabase simpleDatabase = new SimpleDatabase(getContext());
        KittyMapper mapper = simpleDatabase.getMapper(SimpleExampleModel.class);
        if(actionsLW != null) {
            actionsLW.setAdapter(new BasicArrayAdapter(getContext(), new LinkedList<String>()));
            actionsLW.setOnTouchListener(new View.OnTouchListener() {

                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));
            addActionListItem(format(getString(R.string._l1_t2_clear), mapper.deleteAll()));
            updateExpandPanelList();
        }
        mapper.close();
    }

    private static final String NST_LOGTAG = "NST_LOGTAG";

    private void newSyntaxTest() {
        SimpleDatabase simpleDatabase = new SimpleDatabase(getContext());
        KittyMapper mapper = simpleDatabase.getMapper(SimpleExampleModel.class);
        Log.e(NST_LOGTAG, "1: adding ten rgen models");
        LinkedList<SimpleExampleModel> randModels = new LinkedList<>();
        for(int i = 0; i < 10; i++)
            randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
        mapper.saveInTransaction(randModels);
        Log.e(NST_LOGTAG, "2: adding at least two Pavels");
        SimpleExampleModel p1 = new SimpleExampleModel(); p1.randomInteger = 1; p1.firstName = "pavel";
        SimpleExampleModel p2 = new SimpleExampleModel(); p2.randomInteger = 2; p2.firstName = "pavel";
        Log.e(NST_LOGTAG, "#" + mapper.insert(p1));
        Log.e(NST_LOGTAG, "#" + mapper.insert(p2));
        mapper.save(p2);
        List<SimpleExampleModel> pavels = mapper.findWhere("#?firstName = ?", "pavel");
        Iterator<SimpleExampleModel> pavelsI = pavels.iterator();
        while (pavelsI.hasNext())
            Log.e(NST_LOGTAG, "3: " + pavelsI.next().toString());
        Log.e(NST_LOGTAG, "4: adding at least one Morty");
        SimpleExampleModel d1 = new SimpleExampleModel(); d1.randomInteger = 228; d1.firstName = "Morty";
        mapper.save(d1);
        SQLiteConditionBuilder sqb = new SQLiteConditionBuilder();
        sqb.addColumn("first_name").addSQLOperator("=").addValue("Morty");
        List<SimpleExampleModel> mortys = mapper.findWhere(sqb.build());
        Iterator<SimpleExampleModel> mI = mortys.iterator();
        while (mI.hasNext())
            Log.e(NST_LOGTAG, "5: " + mI.next().toString());
        Log.e(NST_LOGTAG, "6: " + mapper.countWhere("first_name = ?", "pavel"));
        Log.e(NST_LOGTAG, "7: " + mapper.countAll());
        Log.e(NST_LOGTAG, "8: " + mapper.deleteWhere("first_name = ?", "pavel"));
        Log.e(NST_LOGTAG, "9: " + mapper.countAll());
        Log.e(NST_LOGTAG, "0: " + mapper.deleteAll());
        mapper.close();
    }

    void go() {
        if(actionsLW != null) {
            actionsLW.setAdapter(new BasicArrayAdapter(getContext(), new LinkedList<String>()));
            actionsLW.setOnTouchListener(new View.OnTouchListener() {

                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            // Creating new instance of SimpleDatabase
            SimpleDatabase simpleDatabase = new SimpleDatabase(getContext());

            // Printing generated by KittyORM schema create and drop scripts
            simpleDatabase.printPregeneratedCreateSchemaToLog("KITTY_ORM_DEMO_L1T2");
            simpleDatabase.printPregeneratedDropSchemaToLog("KITTY_ORM_DEMO_L1T2");

            // Printing registry to log (e.g. collection of KittyModels->KittyMappers that would be used)
            simpleDatabase.printRegistryToLog(KittyLog.LOG_LEVEL.E);
            KittyMapper<SimpleExampleModel> mapper = simpleDatabase.getMapper(SimpleExampleModel.class);

            // Counting records in db table and deleting them if table not empty
            if(mapper.countAll() > 0) {
                addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));
                addActionListItem(format(getString(R.string._l1_t2_clear), mapper.deleteAll()));
            }

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

            addActionListItem(format(getString(R.string._l1_t2_inserting), alex));
            addActionListItem(format(getString(R.string._l1_t2_inserting), marina));
            addActionListItem(format(getString(R.string._l1_t2_inserting), marina2));

            // Saving those models
            // Saving model with mapper.save(M model)
            mapper.save(alex);
            mapper.save(marina2);

            // Saving model with mapper.insert(M model)
            // Better to use insert(M model) for new DB records cause it is little bit faster
            long marinaRowid = mapper.insert(marina);


            addActionListItem(format(getString(R.string._l1_t2_inserted), alex));
            addActionListItem(format(getString(R.string._l1_t2_inserted), marina));
            addActionListItem(format(getString(R.string._l1_t2_inserted), marina2));

            addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));


            // Finding existing records in DB and mapping them to entities

            int findOperationId = 0;
            // find with condition
            addActionListItem(format(getContext().getString(R.string._l1_t2_retrieving), "mapper.findWhere", "WHERE first_name = Marina", findOperationId));
            SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
            builder.addColumn("first_name")
                    .addSQLOperator(SQLiteOperator.EQUAL)
                    .addValue("Marina");
            List<SimpleExampleModel> marinas = mapper.findWhere(builder.build());
            // Also you may define conditions in alternative way
            marinas = mapper.findWhere(SQLiteConditionBuilder.fromSQL("first_name = ?", null, "Marina"));
            // Or specify field name instead column name using following syntax
            marinas = mapper.findWhere(SQLiteConditionBuilder.fromSQL("#?firstName; = ?", SimpleExampleModel.class, "Marina"));
            if(marinas != null) {
                addActionListItem(format(getString(R.string._l1_t2_retrieved),  marinas.size(), findOperationId));
            }
            int marinasCounter = 0;
            for(SimpleExampleModel m : marinas) {
                addActionListItem(format(getString(R.string._l1_t2_retrieved_model_show),  findOperationId, marinasCounter, m));
                marinasCounter++;
            }

            findOperationId++;
            // find with RowID
            addActionListItem(format(getString(R.string._l1_t2_retrieving), "mapper.findByRowID", "RowID = "+marinaRowid, findOperationId));
            SimpleExampleModel marinaFromTableRowid = mapper.findByRowID(marinaRowid);
            if(marinaFromTableRowid != null) {
                addActionListItem(format(getString(R.string._l1_t2_retrieved),  1, findOperationId));
                addActionListItem(format(getString(R.string._l1_t2_retrieved_model_show),  findOperationId, 0, marinaFromTableRowid));
            }

            findOperationId++;
            // find with IPK
            addActionListItem(format(getContext().getString(R.string._l1_t2_retrieving), "mapper.findByIPK", "IPK = "+marinaFromTableRowid.id, findOperationId));
            SimpleExampleModel marinaFromTableIPK = mapper.findByIPK(marinaFromTableRowid.id);
            if(marinaFromTableIPK != null) {
                addActionListItem(format(getString(R.string._l1_t2_retrieved),  1, findOperationId));
                addActionListItem(format(getString(R.string._l1_t2_retrieved_model_show),  findOperationId, 0, marinaFromTableIPK));
            }

            findOperationId++;
            // find with KittyPrimaryKey
            addActionListItem(format(format(getString(R.string._l1_t2_retrieving), "mapper.findByPK", "KittyPrimaryKey [ id = "+marinaFromTableRowid.id, findOperationId)));
            KittyPrimaryKey pk = new KittyPrimaryKey.Builder()
                                            .addKeyColumnValue("id", marinaFromTableRowid.id.toString())
                                            .build();
            SimpleExampleModel marinaFromTableKPK = mapper.findByPK(pk);
            if(marinaFromTableKPK != null) {
                addActionListItem(format(getString(R.string._l1_t2_retrieved),  1, findOperationId));
                addActionListItem(format(getString(R.string._l1_t2_retrieved_model_show),  findOperationId, 0, marinaFromTableKPK));
            }

            // Generating and inserting list of 10 random models
            List<SimpleExampleModel> randomModels = new LinkedList<>();
            for(int i = 0; i < 10; i++)
                randomModels.add(RandomSimpleExampleModelUtil.randomSEModel());
            mapper.save(randomModels);
            addActionListItem(getString(R.string._l1_t2_random_save));

            addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));

            // Deleting some models
            // Deleting by entity, make sure that entity has RowID\IPK\PK set
            SQLiteCondition alexCondition = new SQLiteConditionBuilder()
                                                        .addColumn("first_name")
                                                        .addSQLOperator(SQLiteOperator.EQUAL)
                                                        .addValue("Alex")
                                                        .build();
            SimpleExampleModel alexToDelete = mapper.findFirst(alexCondition);
            if(alexToDelete!=null) {
                addActionListItem(getString(R.string._l1_t2_one_alex_to_delete_found));
                if(mapper.delete(alexToDelete) > 0) {
                    addActionListItem(getString(R.string._l1_t2_one_alex_deleted));
                    addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));
                }
            }

            // Deleting with condition
            SQLiteCondition marina445555Condition = new SQLiteConditionBuilder()
                                                            .addColumn("random_integer")
                                                            .addSQLOperator(SQLiteOperator.EQUAL)
                                                            .addValue(marina2.randomInteger)
                                                            .build();
            addActionListItem(getString(R.string._l1_2_one_marina_deleting));
            if(mapper.deleteWhere(marina445555Condition) > 0) {
                addActionListItem(getString(R.string._l1_2_one_marina_deleted));
                addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));
            }

            // Updating models
            // updating current model
            // if model has RowId or IPK or PrimaryKey values set (3-rd is slowest) just
            SimpleExampleModel oldMarina = marinaFromTableIPK.clone(SimpleExampleModel.class);
            SimpleExampleModel newMarina = marinaFromTableIPK.clone(SimpleExampleModel.class);
            newMarina.randomInteger = 1337;
            addActionListItem(format(getString(R.string._l1_t2_updating_entity), oldMarina, newMarina));
            if(mapper.update(newMarina) > 0) {
                addActionListItem(format(getString(R.string._l1_t2_updated), oldMarina, newMarina));
                findOperationId++;
                // find with IPK
                addActionListItem(format(getContext().getString(R.string._l1_t2_retrieving), "mapper.findByIPK", "IPK = "+marinaFromTableRowid.id, findOperationId));
                SimpleExampleModel marinaFromTableIPK2 = mapper.findByIPK(marinaFromTableRowid.id);
                if(marinaFromTableIPK != null) {
                    addActionListItem(format(getString(R.string._l1_t2_retrieved),  1, findOperationId));
                    addActionListItem(format(getString(R.string._l1_t2_retrieved_model_show),  findOperationId, 0, marinaFromTableIPK2));
                    addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));
                }
            }

            // another option is updating with generating query
            SimpleExampleModel updateMarina = new SimpleExampleModel();
            updateMarina.randomInteger = 121212;
            addActionListItem(format(getString(R.string._l1_t2_updating_query_like), updateMarina));
            builder = new SQLiteConditionBuilder();
            builder.addColumn("first_name")
                    .addSQLOperator(SQLiteOperator.EQUAL)
                    .addValue("Marina");
            if(mapper.update(updateMarina, builder.build(), new String[]{"randomInteger"}, CVUtils.INCLUDE_ONLY_SELECTED_FIELDS) > 0) {
                addActionListItem(format(getString(R.string._l1_t2_updating_query_like_updated), updateMarina));
                findOperationId++;
                // find with IPK
                addActionListItem(format(getContext().getString(R.string._l1_t2_retrieving), "mapper.findByIPK", "IPK = "+marinaFromTableRowid.id, findOperationId));
                SimpleExampleModel marinaFromTableIPK2 = mapper.findByIPK(marinaFromTableRowid.id);
                if(marinaFromTableIPK != null) {
                    addActionListItem(format(getString(R.string._l1_t2_retrieved),  1, findOperationId));
                    addActionListItem(format(getString(R.string._l1_t2_retrieved_model_show),  findOperationId, 0, marinaFromTableIPK2));
                    addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));
                }
            }

            // bulk operations in TX mode
            LinkedList<SimpleExampleModel> randModels = new LinkedList<>();
            for(int i = 0; i < 10; i++)
                randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
            mapper.saveInTransaction(randModels);
            addActionListItem(getString(R.string._l1_t2_random_save));

            addActionListItem(format(getString(R.string._l1_t2_count), mapper.countAll()));

            // closing mapper
            mapper.close();

            updateExpandPanelList();
        }
    }

    void updateExpandPanelList() {
        if (expandedText != null && expandedLW != null && expandedTextPattern != null) {
            SimpleDatabase sdb = new SimpleDatabase(getContext());
            KittyMapper mapper = sdb.getMapper(SimpleExampleModel.class);
            expandedText.setText(format(expandedTextPattern, mapper.countAll()));
            List<SimpleExampleModel> models = mapper.findAll();
            if (models == null) {
                models = new LinkedList<>();
            }
            LinkedList<String> modelsToString = new LinkedList<>();
            Iterator<SimpleExampleModel> modelIterator = models.iterator();
            while (modelIterator.hasNext()) {
                modelsToString.addLast(modelIterator.next().toString());
            }
            expandedLW.setAdapter(new BasicArrayAdapter(getContext(), modelsToString));
            expandedLW.setOnTouchListener(new View.OnTouchListener() {

                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            mapper.close();
        }
    }

    void addActionListItem(String item) {
        if(actionsLW != null) {
            ((BasicArrayAdapter)actionsLW.getAdapter()).addItemLast(item);
            ((BasicArrayAdapter) actionsLW.getAdapter()).notifyDataSetChanged();
        }
    }

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T2_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T2_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T2_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l1_t2_snackbar_message;
    }

    @Override
    public void onVisible() {

    }
}
