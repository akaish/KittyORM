
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
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.query.conditions.SQLiteOperator;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.lessons.adapters.MigAdapter;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigFiveModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigFourModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigThreeModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigTwoModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigrationDBv4;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.util.MigV4RandomModelFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static java.text.MessageFormat.format;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T4_SCHEMA;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T4_SOURCE;
import static net.akaish.kittyormdemo.lessons.LessonsUriConstants.L5_T4_TUTORIAL;

/**
 * Created by akaish on 11.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson5Tab4FilescriptMigration extends Lesson5BaseFragment {

    private MigrationDBv4 databaseV4;
    private SharedPreferencesMigDB sf;

    private Button insertRandomButton;
    private Button clearTableButton;
    private Button deleteDatabaseButton;

    private ListView eventsListView;

    private TextView statusTV;

    private MigDatabaseState mdbState;

    final static int DB_IMPLEMENTATION_VERSION = 4;
    final static int TABLE_AMOUNT = 3;

    public Lesson5Tab4FilescriptMigration() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson5_tab4_file_migrations, container, false);

        insertRandomButton = rootView.findViewById(R.id.l5_t4_go_button);
        clearTableButton = rootView.findViewById(R.id.l5_t4_clear_button);
        deleteDatabaseButton = rootView.findViewById(R.id.l5_t4_delete_database_button);

        eventsListView = rootView.findViewById(R.id.l5_t4_actions);

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

        statusTV = rootView.findViewById(R.id.l5_t4_status);


        setUpExpandedList(
                rootView,
                R.id._l5_t4_expanded_panel_list,
                R.id._l5_t4_expanded_panel_text,
                R.string._l5_t4_expanded_text_pattern
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
            statusTV.setText(getMdbState(getContext(), DB_IMPLEMENTATION_VERSION, new String[] {M1M2TN, M1M3TN, M1M4TN}).toString());
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

    private MigrationDBv4 getDatabase() {
        // retrieving existing database after upgrade -> downgrade would cause onUpgrade() script would be run after mapper fetching
        databaseV4 = new MigrationDBv4(getContext());
        return databaseV4;
    }

    private void insert25RND() {
        new InsertRandomAsync().execute(0l);
        //new TestCPK().execute();
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
        return R.string._l5_t4_snackbar_message;
    }



    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<String>> {

        @Override
        protected List<String> doInBackground(Long... params) {
            LinkedList<String> toListView = new LinkedList<>();
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                // T4
                KittyMapper mapper = getDatabase().getMapper(MigFourModel.class);
                List<MigFourModel> m1Models = mapper.findAll();
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
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m4_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m4_db), m1Models.size()));
                    Iterator<MigFourModel> mI = m1Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m2Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m2_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m2_db), m2Models.size()));
                    Iterator<MigTwoModel> mI = m2Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m3Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m3_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m3_db), m2Models.size()));
                    Iterator<MigThreeModel> mI = m3Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                return toListView;
            } else {
                if(!getSf().isDatabaseCreated() || getSf().isDatabaseDeletedManually()) {
                    toListView.addLast(getString(R.string._l5_t4_m1_db_doesnt_exist));
                    return toListView;
                } else {
                    toListView.addLast(format(getString(R.string._l5_t4_m1_db_has_different_version), getSf().currentMigDBVersion()));
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

    private static final String ERR_STRING_WIPE = "Lesson5tab4WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, WipeAsyncResult> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t4_running_requested_operation_pg_title),
                    getString(R.string._l5_t4_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected WipeAsyncResult doInBackground(Long... params) {
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                try {
                    KittyMapper mapper4 = getDatabase().getMapper(MigFourModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper4.countAll() + mapper2.countAll() + mapper3.countAll();
                    long affected = mapper4.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    mapper4.close(); mapper2.close(); mapper3.close();
                    return new WipeAsyncResult(true, false, DB_IMPLEMENTATION_VERSION, affected, recordsCount);
                } catch (Exception e) {
                    Log.e(MigrationDBv4.LTAG, ERR_STRING_WIPE, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv4.LTAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_count_to_events), result.recordsCount));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_deleted_to_events), result.affectedRows));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t4_error_event));
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

    static final String ERR_INSERT_RND = "Lesson5tab4InsertRNDDataError, see exception details!";

    // TEST COMPLEX PK
    class TestCPK extends AsyncTask<Void, Void, Long> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Long doInBackground(Void... voids) {
            KittyMapper mapper = getDatabase().getMapper(MigFiveModel.class);
            MigFiveModel m1 = new MigFiveModel();
            MigFiveModel m2 = new MigFiveModel();
            MigFiveModel m3 = new MigFiveModel();

            m1.ipkUniqueString = UUID.randomUUID().toString();
            m2.ipkUniqueString = UUID.randomUUID().toString();
            m3.ipkUniqueString = UUID.randomUUID().toString();

            m1.someStr = "STR1";
            m2.someStr = "STR2";
            m3.someStr = "STR3";

            mapper.save(m1);
            mapper.save(m2);
            mapper.save(m3);

            Log.e("CPK test 0", " count = "+mapper.countAll());

            SQLiteCondition sqLiteCondition = new SQLiteConditionBuilder()
                                                .addField("ipk_str")
                                                .addSQLOperator(SQLiteOperator.EQUAL)
                                                .addValue(m3.ipkUniqueString)
                                                .build();
            MigFiveModel m3FromDB = (MigFiveModel) mapper.findWhere(sqLiteCondition).get(0);
            Log.e("CPK test 1", m3FromDB.toString());
            m3FromDB.someStr = "modified";

            mapper.save(m3FromDB);

            SQLiteCondition sqLiteCondition2 = new SQLiteConditionBuilder()
                                                    .addField("some_str")
                                                    .addSQLOperator(SQLiteOperator.EQUAL)
                                                    .addValue("modified")
                                                    .build();
            MigFiveModel m3FromDBM = (MigFiveModel) mapper.findWhere(sqLiteCondition2).get(0);
            Log.e("CPK test 2", m3FromDB.toString());

            Log.e("CPK test 3", " count = "+mapper.countAll());

            mapper.deleteAll();
            return null;
        }
    }

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t4_running_requested_operation_pg_title),
                    getString(R.string._l5_t4_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
//            File destinationFile = getContext().getFilesDir();
//            KittyUtils.copyDirectoryFromAssetsToFS(getContext(), KittyNamingUtils.ASSETS_URI_START + "kittysqliteorm", destinationFile);
//            if(true) return null;
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
                    KittyMapper mapper4 = getDatabase().getMapper(MigFourModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper4.countAll() + mapper2.countAll() + mapper3.countAll();

                    boolean deleteData = getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION;

                    long affected;

                    if(deleteData)
                        affected = mapper4.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    else
                        affected = 0l;

                    LinkedList<MigFourModel> modelsToInsert = new LinkedList<>();
                    LinkedList<MigTwoModel> models2ToInsert = new LinkedList<>();
                    LinkedList<MigThreeModel> models3ToInsert = new LinkedList<>();

                    getSf().setDatabaseCreated(true);
                    getSf().setCurrentMigDBVersion(DB_IMPLEMENTATION_VERSION);
                    getSf().setDatabaseDeletedManually(false);

                    MigV4RandomModelFactory factory = new MigV4RandomModelFactory(getContext());

                    for (int i = 0; i < INSERT_AMOUNT; i++) {
                        MigTwoModel m = factory.newM2RndModel();
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

                    for (int i = 0; i < INSERT_FK_AMOUNT; i++) {
                        MigFourModel m = factory.newM4RndModel(models3, models2);
                        modelsToInsert.addLast(m);
                    }
                    mapper4.insertInTransaction(modelsToInsert);
                    List<MigFourModel> models = mapper4.findAll();
                    Iterator<MigFourModel> mI = models.iterator();
                    LinkedList<String> out4 = new LinkedList<>();
                    while (mI.hasNext()) {
                        out4.addLast(mI.next().toString());
                    }

                    long recordsCountAfter = mapper4.countAll() + mapper2.countAll() + mapper3.countAll();
                    mapper4.close(); mapper2.close(); mapper3.close();
                    return new InsertRandomResults(out4, out2, out3, affected, recordsCount, recordsCountAfter, true, getSf().currentMigDBVersion());
                } catch (Exception e) {
                    Log.e(MigrationDBv4.LTAG, ERR_INSERT_RND, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv4.LTAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_deleted_to_events), result.deletedModelsAffectedRows));
                    for (String modelString : result.modelInsertionsM4) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_inserted_to_events), M1M4TN, modelString));
                    }
                    for (String modelString2 : result.modelInsertionsM2) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_inserted_to_events), M1M2TN, modelString2));
                    }
                    for (String modelString3 : result.modelInsertionsM3) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_inserted_to_events), M1M3TN, modelString3));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_count_to_events), result.modelsCountAfter));
                } else {
                    if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t4_error_event));
                    }
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }


    }

    class InsertRandomResults {
        List<String> modelInsertionsM4;
        List<String> modelInsertionsM2;
        List<String> modelInsertionsM3;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;
        int dbVersion;

        public InsertRandomResults(List<String> modelInsertionsM4, List<String> modelInsertionsM2,
                                   List<String> modelInsertionsM3, long deletedModelsAffectedRows,
                                   long modelsCountBefore, long modelsCountAfter, boolean opSuccess,
                                   int dbVersion) {
            this.modelInsertionsM4 = modelInsertionsM4;
            this.modelInsertionsM2 = modelInsertionsM2;
            this.modelInsertionsM3 = modelInsertionsM3;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
            this.dbVersion = dbVersion;
        }
    }

    static final String ERR_DELETION = "Lesson5tab4DBDeleteError, see exception details!";

    class DeleteDatabaseAsync extends AsyncTask<Long, Long, Integer> {
        ProgressDialog dialog;

        final int DELETED = 1;
        final int NOT_DELETED = 2;
        final int ERROR = 3;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t4_running_requested_operation_pg_title),
                    getString(R.string._l5_t4_running_requested_operation_pg_body)
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
                Log.e(MigrationDBv4.LTAG, ERR_DELETION, e);
                if (e instanceof KittyRuntimeException) {
                    if (((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(MigrationDBv4.LTAG, ERR_DELETION, ((KittyRuntimeException) e).getNestedException());
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
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_delete_db_success)));
                        break;
                    case NOT_DELETED:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_delete_db_fail)));
                        break;
                    case ERROR:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_error_event)));
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T4_TUTORIAL);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T4_SOURCE);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T4_SCHEMA);
            }
        };
    }
}


