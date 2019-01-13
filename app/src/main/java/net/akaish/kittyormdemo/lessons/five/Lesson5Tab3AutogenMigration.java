
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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.lessons.adapters.MigAdapter;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.MigOneModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.MigThreeModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.MigTwoModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.MigrationDBv3;
import net.akaish.kittyormdemo.sqlite.migrations.migv3.util.MigV3RandomModelFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T1_SCHEMA;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T1_SOURCE;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T1_TUTORIAL;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T3_SCHEMA;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T3_SOURCE;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T3_TUTORIAL;

/**
 * Created by akaish on 11.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson5Tab3AutogenMigration extends Lesson5BaseFragment {

    private MigrationDBv3 databaseV3;
    private SharedPreferencesMigDB sf;

    private Button insertRandomButton;
    private Button clearTableButton;
    private Button deleteDatabaseButton;

    private ListView eventsListView;

    private TextView statusTV;

    private MigDatabaseState mdbState;

    final static int DB_IMPLEMENTATION_VERSION = 3;
    final static int TABLE_AMOUNT = 3;

    public Lesson5Tab3AutogenMigration() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson5_tab3_auto_migration, container, false);

        insertRandomButton = rootView.findViewById(R.id.l5_t3_go_button);
        clearTableButton = rootView.findViewById(R.id.l5_t3_clear_button);
        deleteDatabaseButton = rootView.findViewById(R.id.l5_t3_delete_database_button);

        eventsListView = rootView.findViewById(R.id.l5_t3_actions);

        insertRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert25RND();
            }
        });

        clearTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTable();
            }
        });

        deleteDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabase();
            }
        });

        statusTV = rootView.findViewById(R.id.l5_t3_status);


        setUpExpandedList(
                rootView,
                R.id._l5_t3_expanded_panel_list,
                R.id._l5_t3_expanded_panel_text,
                R.string._l5_t3_expanded_text_pattern
        );

        reloadTableExpandedList();
        reloadStatus();
        return rootView;
    }

    public MigDatabaseState getMdbState(Context context, int implVersion, String[] tables) {
        if(mdbState != null) return mdbState;
        mdbState = new MigDatabaseState(implVersion, tables, context, getSf());
        return mdbState;
    }

    public void reloadStatus() {
        if(statusTV != null) {
            statusTV.setText(getMdbState(getContext(), DB_IMPLEMENTATION_VERSION, new String[] {M1M1TN, M1M2TN, M1M3TN}).toString());
        }
    }


    @Override
    public void onVisible() {
        reloadTableExpandedList();
        reloadStatus();
    }

    private SharedPreferencesMigDB getSf() {
        if(sf != null) return sf;
        sf = new SharedPreferencesMigDB(getContext());
        return sf;
    }

    private MigrationDBv3 getDatabase() {
        // retrieving existing database after upgrade -> downgrade would cause onUpgrade() script would be run after mapper fetching
        databaseV3 = new MigrationDBv3(getContext());
        return databaseV3;
    }

    private void insert25RND() {
        new InsertRandomAsync().execute(0l);
    }

    private void clearTable() {
        new WipeAsync().execute(0l);
    }

    private void deleteDatabase() {
        new DeleteDatabaseAsync().execute(0l);
    }

    private void reloadTableExpandedList() {
        new ReloadTableAsync().execute(0l);
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l5_t3_snackbar_message;
    }



    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<String>> {

        @Override
        protected List<String> doInBackground(Long... params) {
            LinkedList<String> toListView = new LinkedList<>();
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                // T1
                KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                List<MigOneModel> m1Models = mapper.findAll();
                mapper.close();
                // T2
                KittyMapper mapperT2 = getDatabase().getMapper(MigTwoModel.class);
                List<MigTwoModel> m2Models = mapperT2.findAll();
                mapper.close();
                // T3
                KittyMapper mapperT3 = getDatabase().getMapper(MigThreeModel.class);
                List<MigThreeModel> m3Models = mapperT3.findAll();
                mapper.close();

                if(m1Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m1_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m1_db), m1Models.size()));
                    Iterator<MigOneModel> mI = m1Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m2Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m2_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m2_db), m2Models.size()));
                    Iterator<MigTwoModel> mI = m2Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m3Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m3_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m3_db), m2Models.size()));
                    Iterator<MigThreeModel> mI = m3Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                return toListView;
            } else {
                if(!getSf().isDatabaseCreated() || getSf().isDatabaseDeletedManually()) {
                    toListView.addLast(getString(R.string._l5_t3_m1_db_doesnt_exist));
                    return toListView;
                } else {
                    toListView.addLast(format(getString(R.string._l5_t3_m1_db_has_different_version), getSf().currentMigDBVersion()));
                    return toListView;
                }
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            int tableAmount = TABLE_AMOUNT;
            if(getSf().isDatabaseDeletedManually() || !getSf().isDatabaseCreated() || getSf().currentMigDBVersion() != DB_IMPLEMENTATION_VERSION)
                tableAmount = 0;
            if(result != null) {
                events.setAdapter(new MigAdapter(getContext(), result));
                int recordsAmount = result.size() - tableAmount;
                if(tableAmount == 0)
                    recordsAmount = 0;
                expandedTitle.setText(format(expandeddTitlePattern, recordsAmount, tableAmount));
            } else {
                events.setAdapter(new MigAdapter(getContext(), new LinkedList<String>()));
                expandedTitle.setText(format(expandeddTitlePattern, 0, tableAmount));
            }
        }
    }

    private static final String ERR_STRING_WIPE = "Lesson5tab3WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, WipeAsyncResult> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t3_running_requested_operation_pg_title),
                    getString(R.string._l5_t3_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected WipeAsyncResult doInBackground(Long... params) {
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                try {
                    KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper.countAll() + mapper2.countAll() + mapper3.countAll();
                    long affected = mapper.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    mapper.close(); mapper2.close(); mapper3.close();
                    return new WipeAsyncResult(true, false, DB_IMPLEMENTATION_VERSION, affected, recordsCount);
                } catch (Exception e) {
                    Log.e(MigrationDBv3.LTAG, ERR_STRING_WIPE, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv3.LTAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
                        }
                    }
                    return new WipeAsyncResult(true, false, DB_IMPLEMENTATION_VERSION, -1l, -1l);
                }
            } else {
                return new WipeAsyncResult(
                        getSf().isDatabaseCreated(),
                        getSf().isDatabaseDeletedManually(),
                        getSf().currentMigDBVersion(),
                        -1l, -1l);
            }
        }

        @Override
        protected void onPostExecute(WipeAsyncResult result) {
            dialog.cancel();

            if (eventsListView != null) {
                eventsListView.setAdapter(new BasicArrayAdapter(getContext(), new LinkedList<String>()));
                eventsListView.setOnTouchListener(new View.OnTouchListener() {

                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });

                if(!result.isCreated) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_op_not_existing));
                } else if (result.isDeleted) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_op_deleted));
                } else if (result.dbVersion != DB_IMPLEMENTATION_VERSION) {
                    if(result.dbVersion < 1) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_lower), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    }
                } else if (result.recordsCount > -1 && result.affectedRows > -1) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_count_to_events), result.recordsCount));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_deleted_to_events), result.affectedRows));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t3_error_event));
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }
    }

    class WipeAsyncResult {
        boolean isCreated;
        boolean isDeleted;
        int dbVersion;
        Long affectedRows;
        Long recordsCount;

        public WipeAsyncResult(boolean isCreated, boolean isDeleted, int dbVersion,
                               Long affectedRows, Long recordsCount) {
            this.isCreated = isCreated;
            this.isDeleted = isDeleted;
            this.dbVersion = dbVersion;
            this.affectedRows = affectedRows;
            this.recordsCount = recordsCount;
        }
    }

    static final int INSERT_AMOUNT = 25;
    static final int INSERT_FK_AMOUNT = 10;

    static final String ERR_INSERT_RND = "Lesson5tab3InsertRNDDataError, see exception details!";

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t3_running_requested_operation_pg_title),
                    getString(R.string._l5_t3_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
            if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                return new InsertRandomResults(
                        null,
                        null,
                        null,
                        -1l,
                        -1l,
                        -1l,
                        false,
                        getSf().currentMigDBVersion()
                );
            } else {
                try {
                    KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper.countAll() + mapper2.countAll() + mapper3.countAll();

                    boolean deleteData = getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION;

                    long affected;

                    if(deleteData)
                        affected = mapper.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    else
                        affected = 0l;

                    LinkedList<MigOneModel> modelsToInsert = new LinkedList<>();
                    LinkedList<MigTwoModel> models2ToInsert = new LinkedList<>();
                    LinkedList<MigThreeModel> models3ToInsert = new LinkedList<>();

                    getSf().setDatabaseCreated(true);
                    getSf().setCurrentMigDBVersion(DB_IMPLEMENTATION_VERSION);
                    getSf().setDatabaseDeletedManually(false);

                    MigV3RandomModelFactory factory = new MigV3RandomModelFactory(getContext());

                    for (int i = 0; i < INSERT_AMOUNT; i++) {
                        MigOneModel m = factory.newM1RndModel();
                        modelsToInsert.addLast(m);
                    }
                    mapper.insertInTransaction(modelsToInsert);
                    List<MigOneModel> models = mapper.findAll();
                    Iterator<MigOneModel> mI = models.iterator();
                    LinkedList<String> out = new LinkedList<>();
                    while (mI.hasNext()) {
                        out.addLast(mI.next().toString());
                    }

                    for (int i = 0; i < INSERT_FK_AMOUNT; i++) {
                        MigTwoModel m = factory.newM2RndModel((ArrayList<MigOneModel>) models);
                        models2ToInsert.addLast(m);
                    }
                    mapper2.insertInTransaction(models2ToInsert);
                    List<MigTwoModel> models2 = mapper2.findAll();
                    LinkedList<String> out2 = new LinkedList<>();
                    Iterator<MigTwoModel> mI2 = models2.iterator();
                    while (mI2.hasNext()) {
                        out2.addLast(mI2.next().toString());
                    }

                    for (int i = 0; i < INSERT_AMOUNT; i++) {
                        MigThreeModel m = factory.newM3RndModel();
                        models3ToInsert.addLast(m);
                    }
                    mapper3.insertInTransaction(models3ToInsert);
                    List<MigThreeModel> models3 = mapper3.findAll();
                    LinkedList<String> out3 = new LinkedList<>();
                    Iterator<MigThreeModel> mI3 = models3.iterator();
                    while (mI3.hasNext()) {
                        out3.addLast(mI3.next().toString());
                    }

                    long recordsCountAfter = mapper.countAll() + mapper2.countAll() + mapper3.countAll();
                    mapper.close(); mapper2.close(); mapper3.close();
                    return new InsertRandomResults(out, out2, out3, affected, recordsCount, recordsCountAfter, true, getSf().currentMigDBVersion());
                } catch (Exception e) {
                    Log.e(MigrationDBv3.LTAG, ERR_INSERT_RND, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv3.LTAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
                        }
                    }
                    return new InsertRandomResults(
                            null,
                            null,
                            null,
                            -1l,
                            -1l,
                            -1l,
                            false,
                            getSf().currentMigDBVersion()
                    );
                }
            }
        }

        @Override
        protected void onPostExecute(InsertRandomResults result) {
            dialog.cancel();
            if (eventsListView != null) {
                eventsListView.setAdapter(new BasicArrayAdapter(getContext(), new LinkedList<String>()));
                eventsListView.setOnTouchListener(new View.OnTouchListener() {

                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                if (result.operationSuccess) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_deleted_to_events), result.deletedModelsAffectedRows));
                    for (String modelString : result.modelInsertionsM1) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_inserted_to_events), M1M1TN, modelString));
                    }
                    for (String modelString2 : result.modelInsertionsM2) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_inserted_to_events), M1M2TN, modelString2));
                    }
                    for (String modelString3 : result.modelInsertionsM3) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_inserted_to_events), M1M3TN, modelString3));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_count_to_events), result.modelsCountAfter));
                } else {
                    if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t3_error_event));
                    }
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }


    }

    class InsertRandomResults {
        List<String> modelInsertionsM1;
        List<String> modelInsertionsM2;
        List<String> modelInsertionsM3;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;
        int dbVersion;

        public InsertRandomResults(List<String> modelInsertionsM1, List<String> modelInsertionsM2,
                                   List<String> modelInsertionsM3, long deletedModelsAffectedRows,
                                   long modelsCountBefore, long modelsCountAfter, boolean opSuccess,
                                   int dbVersion) {
            this.modelInsertionsM1 = modelInsertionsM1;
            this.modelInsertionsM2 = modelInsertionsM2;
            this.modelInsertionsM3 = modelInsertionsM3;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
            this.dbVersion = dbVersion;
        }
    }

    static final String ERR_DELETION = "Lesson5tab3DBDeleteError, see exception details!";

    class DeleteDatabaseAsync extends AsyncTask<Long, Long, Integer> {
        ProgressDialog dialog;

        final int DELETED = 1;
        final int NOT_DELETED = 2;
        final int ERROR = 3;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t3_running_requested_operation_pg_title),
                    getString(R.string._l5_t3_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected Integer doInBackground(Long... strings) {
            try {
                boolean deleted = getDatabase().deleteDatabase();
                getSf().setDatabaseDeletedManually(true);
                getSf().setDatabaseCreated(false);
                getSf().setCurrentMigDBVersion(-1);
                if(deleted)
                    return DELETED;
                else
                    return NOT_DELETED;
            } catch (Exception e) {
                Log.e(MigrationDBv3.LTAG, ERR_DELETION, e);
                if (e instanceof KittyRuntimeException) {
                    if (((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(MigrationDBv3.LTAG, ERR_DELETION, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.cancel();
            if (eventsListView != null) {
                eventsListView.setAdapter(new BasicArrayAdapter(getContext(), new LinkedList<String>()));
                eventsListView.setOnTouchListener(new View.OnTouchListener() {

                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                switch (result) {
                    case DELETED:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_delete_db_success)));
                        break;
                    case NOT_DELETED:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_delete_db_fail)));
                        break;
                    case ERROR:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_error_event)));
                        break;
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }
    }

    // Expanded list
    MigAdapter migAdapter;

    @Override
    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandeddTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandeddTitlePattern, 0));

        if(expandedAdapter == null) {
            migAdapter = new MigAdapter(getContext(), new LinkedList<String>());
        }

        events.setAdapter(migAdapter);
        events.setOnTouchListener(new View.OnTouchListener() {

            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T3_TUTORIAL);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T3_SOURCE);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T3_SCHEMA);
            }
        };
    }
}


