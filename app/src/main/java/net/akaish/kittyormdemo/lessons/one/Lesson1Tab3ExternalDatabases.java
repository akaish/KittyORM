
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
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.akaish.kitty.orm.CVUtils;
import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.pkey.KittyPrimaryKey;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.enums.SQLiteOperator;
import net.akaish.kitty.orm.util.KittyLog;
import net.akaish.kitty.orm.util.KittySchemaColumnDefinition;
import net.akaish.kitty.orm.util.KittySchemaDefinition;
import net.akaish.kitty.orm.util.KittyUtils;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonBaseFragment;
import net.akaish.kittyormdemo.lessons.LessonTabFragmentOnVisibleAction;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.sqlite.external.SimpleDatabase;
import net.akaish.kittyormdemo.sqlite.external.SimpleExampleModel;
import net.akaish.kittyormdemo.sqlite.external.util.RandomSimpleExampleModelUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittySchemaColumnDefinition.PragmaType.integer;
import static net.akaish.kitty.orm.util.KittySchemaColumnDefinition.PragmaType.text;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T3_SCHEMA;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T3_SOURCE;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L1_T3_TUTORIAL;

public class Lesson1Tab3ExternalDatabases extends LessonBaseFragment implements LessonTabFragmentOnVisibleAction {

    private TextView externalDbStatusTV;
    private Button copyButton;
    private Button openButton;

    private ListView actionsLW;
    private Button goButton;
    private Button clearButton;

    private ListView expandedLW;
    private TextView expandedText;
    private String expandedTextPattern;

    public Lesson1Tab3ExternalDatabases() {}

    private SimpleDatabase database;

    private SimpleDatabase getDatabase() {
        if(database == null) {
            KittySchemaDefinition expectedSchema  = new KittySchemaDefinition();
            SparseArray<KittySchemaColumnDefinition> siEx = new SparseArray<>();
            KittySchemaColumnDefinition siExID = new KittySchemaColumnDefinition.Builder().setName("id").setPk().setNotNull().setType(integer).build();
            KittySchemaColumnDefinition siExRanInt = new KittySchemaColumnDefinition.Builder().setName("random_integer").setType(integer).build();
            KittySchemaColumnDefinition siExFN = new KittySchemaColumnDefinition.Builder().setName("first_name").setType(text).build();
            siEx.put(0,siExID);
            siEx.put(1,siExRanInt);
            siEx.put(2,siExFN);
            expectedSchema.addDefinition("simple_example", siEx);
            database = new SimpleDatabase(getContext(), getDBFile().getAbsolutePath(), expectedSchema);
        }
        return database;
    }

    private void closeDatabase() {
        database = null;
    }

    private boolean dbStateCanOpen = false;
    private boolean dbOpened = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson1_tab3_external, container, false);
        actionsLW = rootView.findViewById(R.id.l1_t3_actions);
        goButton = rootView.findViewById(R.id.l1_t3_go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });
        expandedLW = rootView.findViewById(R.id._l1_t3_expanded_panel_list);
        expandedText = rootView.findViewById(R.id._l1_t3_expanded_panel_text);
        expandedTextPattern = getString(R.string._l1_t3_expanded_text_pattern);
        clearButton = rootView.findViewById(R.id.l1_t3_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        externalDbStatusTV = rootView.findViewById(R.id.l1_t3_database_file);
        copyButton = rootView.findViewById(R.id.l1_t3_copy_from_assets_button);
        openButton = rootView.findViewById(R.id.l1_t3_open_db_button);

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatabase();
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyDatabaseFromAssets();
            }
        });

        File dbFile = getDBFile();
        if(dbFile.exists()) {
            externalDbStatusTV.setText(format(
                    getString(R.string._l1_t3_database_pattern),
                    format(
                            getString(R.string._l1_t3_state_waiting),
                            dbFile.getAbsolutePath()
                    )
            ));
            dbStateCanOpen = true;
            copyButton.setEnabled(false);
            clearButton.setEnabled(true);
            goButton.setEnabled(true);
            openButton.setEnabled(true);
        } else {
            externalDbStatusTV.setText(format(
                    getString(R.string._l1_t3_database_pattern),
                    format(
                            getString(R.string._l1_t3_state_no_file),
                            dbFile.getAbsolutePath()
                    )
            ));
            copyButton.setEnabled(true);
            clearButton.setEnabled(false);
            goButton.setEnabled(false);
            openButton.setEnabled(false);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateExpandPanelList();
        if(dbStateCanOpen && dbOpened)
            getDatabase();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dbStateCanOpen)
            closeDatabase();
    }

    void clear() {
        if(database == null) {
            Log.e(NEW_FEATURE_LTAG, "No database initiated!");
            return;
        }
        KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
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
            addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));
            addActionListItem(format(getString(R.string._l1_t3_clear), mapper.deleteAll()));
            updateExpandPanelList();
        }

    }

    void openDatabase() {
        if(dbStateCanOpen) {
            getDatabase();
            dbOpened = true;
        }
    }

    void toast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
    }

    private File getAppDir() {
        return new File( Environment.getExternalStorageDirectory() + "/kitty_orm_demo" );
    }

    private File getDBFile() {
        return new File( getAppDir(), "EDB_TEST.db" );
    }

    // Actually database is light, so in main ui thread
    void copyDatabaseFromAssets() {
        if (isExternalStorageWritable() ) {

            File appDirectory = getAppDir();
            File dbFile =getDBFile();
            if(dbFile.exists() && dbFile.length() > 0) {
                toast(format(getString(R.string._l1_t3_toasts_db_already_exists_at), dbFile.getAbsolutePath()));
                return;
            }
            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdirs();
            }

            try {
                KittyUtils.copyFileFromAssets(getContext(), "external_db/EDB_TEST.db", appDirectory);
                toast(format(getString(R.string._l1_t3_toasts_db_copied), dbFile.getAbsolutePath()));
                externalDbStatusTV.setText(format(
                        getString(R.string._l1_t3_database_pattern),
                        format(
                                getString(R.string._l1_t3_state_waiting),
                                dbFile.getAbsolutePath()
                        )
                ));
                copyButton.setEnabled(false);
                dbStateCanOpen = true;
                openButton.setEnabled(true);
                goButton.setEnabled(true);
                clearButton.setEnabled(true);

                return;
            } catch (IOException e) {
                Log.e(NEW_FEATURE_LTAG, "Unable to copy external_db/EDB_TEST.db to "+dbFile.getAbsolutePath(), e);
                toast(getString(R.string._l1_t3_toasts_db_already_io_exception));
            }


        } else if ( isExternalStorageReadable() ) {
            Log.e(NEW_FEATURE_LTAG, "Unable to copy database from assets, reason: external storage mounted is read only!");
            toast(getString(R.string._l1_t3_toasts_db_already_read_only));
        } else {
            Log.e(NEW_FEATURE_LTAG, "Unable to copy database from assets, reason: external storage is not accessible!");
            toast(getString(R.string._l1_t3_toasts_db_already_not_available));
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    private static final String NEW_FEATURE_LTAG = "NEW_FEATURE_LTAG";

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

            if(database == null) {
                Log.e(NEW_FEATURE_LTAG, "No database initiated!");
                return;
            }

            // Printing generated by KittyORM schema create and drop scripts
            database.printPregeneratedCreateSchemaToLog("KITTY_ORM_DEMO_L1T3");
            database.printPregeneratedDropSchemaToLog("KITTY_ORM_DEMO_L1T3");

            // Printing registry to log (e.g. collection of KittyModels->KittyMappers that would be used)
            database.printRegistryToLog(KittyLog.LOG_LEVEL.E);
            KittyMapper<SimpleExampleModel>  mapper = database.getMapper(SimpleExampleModel.class);

            // Counting records in db table and deleting them if table not empty
            if(mapper.countAll() > 0) {
                addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));
                addActionListItem(format(getString(R.string._l1_t3_clear), mapper.deleteAll()));
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

            addActionListItem(format(getString(R.string._l1_t3_inserting), alex));
            addActionListItem(format(getString(R.string._l1_t3_inserting), marina));
            addActionListItem(format(getString(R.string._l1_t3_inserting), marina2));

            // Saving those models
            // Saving model with mapper.save(M model)
            mapper.save(alex);
            mapper.save(marina2);

            // Saving model with mapper.insert(M model)
            // Better to use insert(M model) for new DB records cause it is little bit faster
            long marinaRowid = mapper.insert(marina);


            addActionListItem(format(getString(R.string._l1_t3_inserted), alex));
            addActionListItem(format(getString(R.string._l1_t3_inserted), marina));
            addActionListItem(format(getString(R.string._l1_t3_inserted), marina2));

            addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));


            // Finding existing records in DB and mapping them to entities

            int findOperationId = 0;
            // find with condition
            addActionListItem(format(getContext().getString(R.string._l1_t3_retrieving), "mapper.findWhere", "WHERE first_name = Marina", findOperationId));
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
                addActionListItem(format(getString(R.string._l1_t3_retrieved),  marinas.size(), findOperationId));
            }
            int marinasCounter = 0;
            for(SimpleExampleModel m : marinas) {
                addActionListItem(format(getString(R.string._l1_t3_retrieved_model_show),  findOperationId, marinasCounter, m));
                marinasCounter++;
            }

            findOperationId++;
            // find with RowID
            addActionListItem(format(getString(R.string._l1_t3_retrieving), "mapper.findByRowID", "RowID = "+marinaRowid, findOperationId));
            SimpleExampleModel marinaFromTableRowid = mapper.findByRowID(marinaRowid);
            if(marinaFromTableRowid != null) {
                addActionListItem(format(getString(R.string._l1_t3_retrieved),  1, findOperationId));
                addActionListItem(format(getString(R.string._l1_t3_retrieved_model_show),  findOperationId, 0, marinaFromTableRowid));
            }

            findOperationId++;
            // find with IPK
            addActionListItem(format(getContext().getString(R.string._l1_t3_retrieving), "mapper.findByIPK", "IPK = "+marinaFromTableRowid.id, findOperationId));
            SimpleExampleModel marinaFromTableIPK = mapper.findByIPK(marinaFromTableRowid.id);
            if(marinaFromTableIPK != null) {
                addActionListItem(format(getString(R.string._l1_t3_retrieved),  1, findOperationId));
                addActionListItem(format(getString(R.string._l1_t3_retrieved_model_show),  findOperationId, 0, marinaFromTableIPK));
            }

            findOperationId++;
            // find with KittyPrimaryKey
            addActionListItem(format(format(getString(R.string._l1_t3_retrieving), "mapper.findByPK", "KittyPrimaryKey [ id = "+marinaFromTableRowid.id, findOperationId)));
            KittyPrimaryKey pk = new KittyPrimaryKey.Builder()
                    .addKeyColumnValue("id", marinaFromTableRowid.id.toString())
                    .build();
            SimpleExampleModel marinaFromTableKPK = mapper.findByPK(pk);
            if(marinaFromTableKPK != null) {
                addActionListItem(format(getString(R.string._l1_t3_retrieved),  1, findOperationId));
                addActionListItem(format(getString(R.string._l1_t3_retrieved_model_show),  findOperationId, 0, marinaFromTableKPK));
            }

            // Generating and inserting list of 10 random models
            List<SimpleExampleModel> randomModels = new LinkedList<>();
            for(int i = 0; i < 10; i++)
                randomModels.add(RandomSimpleExampleModelUtil.randomSEModel());
            mapper.save(randomModels);
            addActionListItem(getString(R.string._l1_t3_random_save));

            addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));

            // Deleting some models
            // Deleting by entity, make sure that entity has RowID\IPK\PK set
            SQLiteCondition alexCondition = new SQLiteConditionBuilder()
                    .addColumn("first_name")
                    .addSQLOperator(SQLiteOperator.EQUAL)
                    .addValue("Alex")
                    .build();
            SimpleExampleModel alexToDelete = mapper.findFirst(alexCondition);
            if(alexToDelete!=null) {
                addActionListItem(getString(R.string._l1_t3_one_alex_to_delete_found));
                if(mapper.delete(alexToDelete) > 0) {
                    addActionListItem(getString(R.string._l1_t3_one_alex_deleted));
                    addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));
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
                addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));
            }

            // Updating models
            // updating current model
            // if model has RowId or IPK or PrimaryKey values set (3-rd is slowest) just
            SimpleExampleModel oldMarina = marinaFromTableIPK.clone(SimpleExampleModel.class);
            SimpleExampleModel newMarina = marinaFromTableIPK.clone(SimpleExampleModel.class);
            newMarina.randomInteger = 1337;
            addActionListItem(format(getString(R.string._l1_t3_updating_entity), oldMarina, newMarina));
            if(mapper.update(newMarina) > 0) {
                addActionListItem(format(getString(R.string._l1_t3_updated), oldMarina, newMarina));
                findOperationId++;
                // find with IPK
                addActionListItem(format(getContext().getString(R.string._l1_t3_retrieving), "mapper.findByIPK", "IPK = "+marinaFromTableRowid.id, findOperationId));
                SimpleExampleModel marinaFromTableIPK2 = mapper.findByIPK(marinaFromTableRowid.id);
                if(marinaFromTableIPK != null) {
                    addActionListItem(format(getString(R.string._l1_t3_retrieved),  1, findOperationId));
                    addActionListItem(format(getString(R.string._l1_t3_retrieved_model_show),  findOperationId, 0, marinaFromTableIPK2));
                    addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));
                }
            }

            // another option is updating with generating query
            SimpleExampleModel updateMarina = new SimpleExampleModel();
            updateMarina.randomInteger = 121212;
            addActionListItem(format(getString(R.string._l1_t3_updating_query_like), updateMarina));
            builder = new SQLiteConditionBuilder();
            builder.addColumn("first_name")
                    .addSQLOperator(SQLiteOperator.EQUAL)
                    .addValue("Marina");
            if(mapper.update(updateMarina, builder.build(), new String[]{"randomInteger"}, CVUtils.INCLUDE_ONLY_SELECTED_FIELDS) > 0) {
                addActionListItem(format(getString(R.string._l1_t3_updating_query_like_updated), updateMarina));
                findOperationId++;
                // find with IPK
                addActionListItem(format(getContext().getString(R.string._l1_t3_retrieving), "mapper.findByIPK", "IPK = "+marinaFromTableRowid.id, findOperationId));
                SimpleExampleModel marinaFromTableIPK2 = mapper.findByIPK(marinaFromTableRowid.id);
                if(marinaFromTableIPK != null) {
                    addActionListItem(format(getString(R.string._l1_t3_retrieved),  1, findOperationId));
                    addActionListItem(format(getString(R.string._l1_t3_retrieved_model_show),  findOperationId, 0, marinaFromTableIPK2));
                    addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));
                }
            }

            // bulk operations in TX mode
            LinkedList<SimpleExampleModel> randModels = new LinkedList<>();
            for(int i = 0; i < 10; i++)
                randModels.add(RandomSimpleExampleModelUtil.randomSEModel());
            mapper.saveInTransaction(randModels);
            addActionListItem(getString(R.string._l1_t3_random_save));

            addActionListItem(format(getString(R.string._l1_t3_count), mapper.countAll()));

            updateExpandPanelList();
        }
    }

    void updateExpandPanelList() {
        if (expandedText != null && expandedLW != null && expandedTextPattern != null) {
            if(database == null) {
                Log.e(NEW_FEATURE_LTAG, "No database initiated!");
                return;
            }
            KittyMapper mapper = database.getMapper(SimpleExampleModel.class);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T3_TUTORIAL);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T3_SOURCE);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L1_T3_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l1_t3_snackbar_message;
    }

    @Override
    public void onVisible() {

    }
}
