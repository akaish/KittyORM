
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

package net.akaish.kittyormdemo.lessons.three;

import android.app.ProgressDialog;
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

import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.lessons.adapters.ComplexRandomModelDTAAdapter;
import net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase;
import net.akaish.kittyormdemo.sqlite.basicdb.ComplexRandomMapper;
import net.akaish.kittyormdemo.sqlite.basicdb.ComplexRandomModel;
import net.akaish.kittyormdemo.sqlite.basicdb.util.RNDComplexRandomModelFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 10.09.18.
 * @author akaish (Denis Bogomolov)
 */
public class Lesson3Tab1DatatypesAffinities extends Lesson3BaseFragment {

    private BasicDatabase database;

    public Lesson3Tab1DatatypesAffinities() {}

    private Button insertRandomButton;
    private Button clearTableButton;
    private ListView eventsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson3_tab1_datatype_affinities, container, false);

        insertRandomButton = rootView.findViewById(R.id.l3_t1_go_button);
        clearTableButton = rootView.findViewById(R.id.l3_t1_clear_button);

        eventsListView = rootView.findViewById(R.id.l3_t1_actions);

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

        setUpExpandedList(
                rootView,
                R.id._l3_t1_expanded_panel_list,
                R.id._l3_t1_expanded_panel_text,
                R.string._l3_t1_expanded_text_pattern
        );

        reloadTableExpandedList();
        return rootView;
    }

    private BasicDatabase getDatabase() {
        if(database != null) return database;
        database = new BasicDatabase(getContext());
        return database;
    }

    ComplexRandomModelDTAAdapter complexExpandedAdapter;

    @Override
    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandeddTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandeddTitlePattern, 0));

        if(expandedAdapter == null) {
            complexExpandedAdapter = new ComplexRandomModelDTAAdapter(getContext(), new LinkedList<ComplexRandomModel>());
        }

        events.setAdapter(complexExpandedAdapter);
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

    @Override
    public void onVisible() {
        reloadTableExpandedList();
    }

    private void insert25RND() {
        new InsertRandomAsync().execute(0l);
    }

    private void clearTable() {
        new WipeAsync().execute(0l);
    }

    private void reloadTableExpandedList() {
        new ReloadTableAsync().execute(0l);
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l3_t1_snackbar_message;
    }


    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<ComplexRandomModel>> {

        @Override
        protected List<ComplexRandomModel> doInBackground(Long... params) {
            ComplexRandomMapper mapper = (ComplexRandomMapper) Lesson3Tab1DatatypesAffinities.this.getDatabase().getMapper(ComplexRandomModel.class);
            List<ComplexRandomModel> out = mapper.findAll();
            mapper.close();
            return out;
        }

        @Override
        protected void onPostExecute(List<ComplexRandomModel> result) {
            if(result != null) {
                events.setAdapter(new ComplexRandomModelDTAAdapter(getContext(), (ArrayList<ComplexRandomModel>) result));
                expandedTitle.setText(format(expandeddTitlePattern, result.size()));
            } else {
                events.setAdapter(new ComplexRandomModelDTAAdapter(getContext(), new LinkedList<ComplexRandomModel>()));
                expandedTitle.setText(format(expandeddTitlePattern, 0));
            }
        }
    }

    private static final String ERR_STRING_WIPE = "Lesson3tab1WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, WipeAsyncResult> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    Lesson3Tab1DatatypesAffinities.this.getLessonActivity(),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_title),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected WipeAsyncResult doInBackground(Long... params) {
            try {
                final ComplexRandomMapper mapper = (ComplexRandomMapper) Lesson3Tab1DatatypesAffinities.this.getDatabase().getMapper(ComplexRandomModel.class);
                long recordsCount = mapper.countAll();
                long affected = mapper.deleteAll();
                mapper.close();
                return new WipeAsyncResult(affected, recordsCount);
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return new WipeAsyncResult(-1l, -1l);
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
                if (result.recordsCount > -1 && result.affectedRows > -1) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_count_to_events), result.recordsCount));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_deleted_to_events), result.affectedRows));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l3_t1_error_event));
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
            }
        }
    }

    class WipeAsyncResult {
        Long affectedRows;
        Long recordsCount;

        public WipeAsyncResult(Long affectedRows, Long recordsCount) {
            this.affectedRows = affectedRows;
            this.recordsCount = recordsCount;
        }
    }

    static final int INSERT_AMOUNT = 25;

    static final String ERR_INSERT_RND = "Lesson3tab1InsertRNDDataError, see exception details!";

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    Lesson3Tab1DatatypesAffinities.this.getLessonActivity(),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_title),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
            try {
                ComplexRandomMapper mapper = (ComplexRandomMapper) Lesson3Tab1DatatypesAffinities.this.getDatabase().getMapper(ComplexRandomModel.class);
                long recordsCount = mapper.countAll();
                long affected = mapper.deleteAll();
                LinkedList<ComplexRandomModel> modelsToInsert = new LinkedList<>();
                RNDComplexRandomModelFactory factory = new RNDComplexRandomModelFactory(getContext());
                for(int i = 0; i < INSERT_AMOUNT; i++) {
                    ComplexRandomModel m = factory.newComplexRandomModel();
                    modelsToInsert.addLast(m);
                }
                mapper.insertInTransaction(modelsToInsert);
                List<ComplexRandomModel> models = mapper.findAll();
                long recordsCountAfter = mapper.countAll();
                mapper.close();
                return new InsertRandomResults(models, affected, recordsCount, recordsCountAfter, true);
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_INSERT_RND, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return new InsertRandomResults(null, -1l, -1l, -1l, false);
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_deleted_to_events), result.deletedModelsAffectedRows));
                    for(ComplexRandomModel m : result.modelInsertions) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_inserted_to_events), m.toShortString()));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_count_to_events), result.modelsCountAfter));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l3_t1_error_event));
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
            }
            reloadTableExpandedList();
        }


    }

    class InsertRandomResults {
        List<ComplexRandomModel> modelInsertions;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;

        public InsertRandomResults(List<ComplexRandomModel> modelInsertions, long deletedModelsAffectedRows, long modelsCountBefore, long modelsCountAfter, boolean opSuccess) {
            this.modelInsertions = modelInsertions;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
        }
    }

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T1_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T1_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T1_SCHEMA);
            }
        };
    }
}
