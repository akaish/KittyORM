
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

package net.akaish.kittyormdemo.lessons.three;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.lessons.adapters.CAIModelAdapter;
import net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase;
import net.akaish.kittyormdemo.sqlite.basicdb.IndexesAndConstraintsModel;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 10.09.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson3Tab2Constraints extends Lesson3BaseFragment {

    private BasicDatabase database;

    protected ArrayAdapter<String> animalAdapter;
    protected Spinner animalSpinner;

    public Lesson3Tab2Constraints() {}

    EditText rndIdFkET;
    EditText defaultIntET;
    EditText creationDateET;
    EditText creationTmstmpET;

    Button saveNewModelButton;
    Button wipeAllButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson3_tab2_constraints_and_indexes, container, false);

        rndIdFkET = rootView.findViewById(R.id.l3_t2_et_fk);
        defaultIntET = rootView.findViewById(R.id.l3_t2_et_default_number);
        creationDateET = rootView.findViewById(R.id.l3_t2_et_creation_date);
        creationTmstmpET = rootView.findViewById(R.id.l3_t2_et_current_timestamp);

        saveNewModelButton = rootView.findViewById(R.id._l3_t2_save_button);
        saveNewModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndexesAndConstraintsModel model = getModelFromInput();
                if(model==null) return;
                new InsertNewAsync().execute(model);
            }
        });

        wipeAllButton = rootView.findViewById(R.id._l3_t2_wipe_button);
        wipeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WipeAsync().execute(0l);
            }
        });


        setUpExpandedList(
                rootView,
                R.id._l3_t2_expanded_panel_lw,
                R.id._l3_t2_expanded_panel_text,
                R.string._l3_t2_expanded_text_pattern
        );

        setAnimalSpinner(rootView, R.id.l3_t2_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        reloadTableExpandedList();
        return rootView;
    }

    private void reloadTableExpandedList() {
        new ReloadTableAsync().execute(0l);
    }

    private IndexesAndConstraintsModel getModelFromInput() {
        IndexesAndConstraintsModel model = new IndexesAndConstraintsModel();
        String rndId = rndIdFkET.getText().toString();
        if(rndId == null) model.rndId = null;
        else if(rndId.trim().length() == 0) model.rndId = null;
        else {
            try {
                model.rndId = Long.parseLong(rndId);
            } catch (Exception e) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_rnd_id_cant_be_treated_as_null_or_long_only,
                        R.string._warning_dialog_ok_button_text);
                return null;
            }
        }
        String animalEnumStringValue = animalSpinner.getSelectedItem().toString();
        if(!animalEnumStringValue.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
            model.animal = Animals.valueOf(animalEnumStringValue);
        }
        String defInteger = defaultIntET.getText().toString();
        if(defInteger == null) model.setFieldExclusion("defaultNumber");
        else if(defInteger.trim().length() == 0) model.setFieldExclusion("defaultNumber");
        else {
            try {
                model.defaultNumber = Integer.parseInt(defInteger);
            } catch (Exception e) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_default_number_can_be_treated_as_null_or_long_only,
                        R.string._warning_dialog_ok_button_text);
                return null;
            }
        }
        String creationDate = creationDateET.getText().toString();
        if(creationDate == null) model.setFieldExclusion("creationDate");
        else if(creationDate.trim().length() == 0) model.setFieldExclusion("creationDate");
        else model.creationDate = creationDate;
        String creationTimestamp = creationTmstmpET.getText().toString();
        if(creationTimestamp == null) model.setFieldExclusion("creationTmstmp");
        else if(creationTimestamp.trim().length() == 0) model.setFieldExclusion("creationTmstmp");
        else {
            Long creationTimestampLong = null;
            try {
                creationTimestampLong = Long.parseLong(creationTimestamp);
            } catch (Exception e) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_creation_timestamp_can_be_treated_as_null_or_long_only,
                        R.string._warning_dialog_ok_button_text);
                return null;
            }
            model.creationTmstmp = new Timestamp(creationTimestampLong);
        }

        return model;
    }

    private BasicDatabase getDatabase() {
        if(database != null) return database;
        database = new BasicDatabase(getContext());
        return database;
    }

    @Override
    public void onVisible() {
        reloadTableExpandedList();
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l3_t2_snackbar_message;
    }

    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<IndexesAndConstraintsModel>> {

        @Override
        protected List<IndexesAndConstraintsModel> doInBackground(Long... params) {
            KittyMapper mapper = Lesson3Tab2Constraints.this.getDatabase().getMapper(IndexesAndConstraintsModel.class);
            List<IndexesAndConstraintsModel> out = mapper.findAll();
            mapper.close();
            return out;
        }

        @Override
        protected void onPostExecute(List<IndexesAndConstraintsModel> result) {
            if(result != null) {
                events.setAdapter(new CAIModelAdapter(getContext(), result));
                expandedTitle.setText(format(expandeddTitlePattern, result.size()));
            } else {
                events.setAdapter(new CAIModelAdapter(getContext(), new LinkedList<IndexesAndConstraintsModel>()));
                expandedTitle.setText(format(expandeddTitlePattern, 0));
            }
        }
    }

    private static final String ERR_STRING_WIPE = "Lesson3tab2WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, Long> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    Lesson3Tab2Constraints.this.getLessonActivity(),
                    Lesson3Tab2Constraints.this.getString(R.string._l3_t2_running_requested_operation_pg_title),
                    Lesson3Tab2Constraints.this.getString(R.string._l3_t2_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected Long doInBackground(Long... params) {
            try {
                KittyMapper mapper = Lesson3Tab2Constraints.this.getDatabase().getMapper(IndexesAndConstraintsModel.class);
                long affected = mapper.deleteAll();
                mapper.close();
                return affected;
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return -1l;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            dialog.cancel();
            if (result <= -1l) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_some_error_on_deleting,
                        R.string._warning_dialog_ok_button_text);
            } else {
                reloadTableExpandedList();
            }
        }
    }

    static final String IA_EXPECTED_ONLY_ONE = "Lesson3Tab2Constraints$InsertNewAsync expects array with one element as parameter for doInBackground";
    static final String ERR_ON_INSERTION = "Lesson3Tab2Constraints$InsertNewAsync error on insertion, see exception details!";

    class InsertNewAsync extends AsyncTask<IndexesAndConstraintsModel, Long, InsertNewAsyncResult> {
        @Override
        protected InsertNewAsyncResult doInBackground(IndexesAndConstraintsModel... params) {
            if(params.length > 1)
                throw new IllegalArgumentException(IA_EXPECTED_ONLY_ONE);
            try {
                KittyMapper mapper = getDatabase().getMapper(IndexesAndConstraintsModel.class);
                long insert = mapper.insert(params[0]);
                mapper.close();
                if(insert > -1l)
                    return new InsertNewAsyncResult(true, null, insert);
                else
                    return new InsertNewAsyncResult(false, null, insert);
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_ON_INSERTION, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_ON_INSERTION, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return new InsertNewAsyncResult(false, e, -1l);
            }
        }

        protected void onPostExecute(InsertNewAsyncResult result) {
            if(result.success)
                reloadTableExpandedList();
            else {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_some_error_on_insertion,
                        R.string._warning_dialog_ok_button_text);
            }
        }
    }

    class InsertNewAsyncResult {
        boolean success;
        Exception exception;
        long insertId;

        public InsertNewAsyncResult(boolean success, Exception exception, long insertId) {
            this.success = success;
            this.exception = exception;
            this.insertId = insertId;
        }
    }

    // Animal spinner stuff
    protected ArrayAdapter<String> newAnimalAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1;
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] adapterStrings = getContext().getResources().getStringArray(R.array.animal_enum);
        for(int i = 0; i < adapterStrings.length; i++) {
            adapter.add(adapterStrings[i]);
        }
        adapter.add(getContext().getString(R.string._l2_t1_random_animal_hint));
        return adapter;
    }

    protected void setAnimalSpinner(View rootView, int spinnerId, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        animalSpinner = (Spinner) rootView.findViewById(spinnerId);
        animalAdapter = newAnimalAdapter();
        animalSpinner.setAdapter(animalAdapter);
        animalSpinner.setSelection(animalAdapter.getCount()); //display hint
        animalSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    // expanded list
    CAIModelAdapter caiModelAdapter;

    @Override
    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandeddTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandeddTitlePattern, 0));

        if(expandedAdapter == null) {
            caiModelAdapter = new CAIModelAdapter(getContext(), new LinkedList<IndexesAndConstraintsModel>());
        }

        events.setAdapter(caiModelAdapter);
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
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T2_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T2_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T2_SCHEMA);
            }
        };
    }
}
