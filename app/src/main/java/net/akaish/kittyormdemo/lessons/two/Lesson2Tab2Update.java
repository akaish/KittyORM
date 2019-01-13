
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

package net.akaish.kittyormdemo.lessons.two;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.lessons.adapters.BasicArrayAdapter;
import net.akaish.kittyormdemo.lessons.five.Lesson5Tab4FilescriptMigration;
import net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomMapper;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigFourModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigThreeModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigTwoModel;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.MigrationDBv4;
import net.akaish.kittyormdemo.sqlite.migrations.migv4.util.MigV4RandomModelFactory;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 03.08.18.
 * @author akaish (Denis Bogomolov)
 */
public class Lesson2Tab2Update extends Lesson2BaseFragment {

    public Lesson2Tab2Update(){}

    Button loadModelButton;
    Button updateButton;

    EditText loadModelIdET;

    EditText randomIntET;
    EditText randomIntegerET;
    EditText randomAnimalNameET;
    EditText randomAnimalSaysET;

    TextView loadedModelTW;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson2_tab2_update, container, false);
        setAnimalSpinner(rootView, R.id.l2_t2_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String enumStringName = (String) animalSpinner.getAdapter().getItem(position);
                if(enumStringName.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
                    // do nothing, skip spinner hint
                } else {
                    Animals animal = Animals.valueOf(enumStringName);
                    randomAnimalNameET.setText(Animals.getLocalizedAnimalNameResource(animal));
                    randomAnimalSaysET.setText(Animals.getLocalizedAnimalSaysResource(animal));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadedModelTW = rootView.findViewById(R.id.l2_t2_tw_current_model);
        loadModelButton = rootView.findViewById(R.id.l2_t2_load_button);

        loadModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadModel();
            }
        });

        updateButton = rootView.findViewById(R.id.l2_t2_update_model_button);

        updateButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                updateModel();
            }
        });

        randomIntET = rootView.findViewById(R.id.l2_t2_et_random_int);
        randomIntegerET = rootView.findViewById(R.id.l2_t2_et_random_integer);

        randomAnimalNameET = rootView.findViewById(R.id.l2_t2_et_animal_localised_name);
        randomAnimalSaysET = rootView.findViewById(R.id.l2_t2_et_animal_says);

        loadModelIdET = rootView.findViewById(R.id.l2_t2_et_id);

        setUpExpandedList(
                rootView,
                R.id._l2_t2_expanded_panel_lw,
                R.id._l2_t2_expanded_panel_text,
                R.string._l2_t2_expanded_text_pattern
        );

        return rootView;
    }

    void updateModel() {
        String randomInt = randomIntET.getText().toString();
        String randomInteger = randomIntegerET.getText().toString();
        String animalEnumStringValue = animalSpinner.getSelectedItem().toString();

        if(animalEnumStringValue.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(randomInt == null || randomInteger == null) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(randomInt.length() == 0 || randomInteger.length() == 0) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        int rndInt = 0; Integer rndInteger = null;
        try {
            rndInt = Integer.parseInt(randomInt);
            rndInteger = Integer.valueOf(randomInteger);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_bad_input, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(isLoadedModelExists()) {
            RandomMapper mapper = getMapper();
            RandomModel toUpdate = mapper.findByIPK(getLoadedModelId());
            RandomModel originalModel = toUpdate.clone(RandomModel.class);
            toUpdate.randomInt = rndInt;
            toUpdate.randomInteger = rndInteger;
            Animals animal = Animals.valueOf(animalEnumStringValue);
            toUpdate.randomAnimal = animal;
            toUpdate.randomAnimalName = getString(Animals.getLocalizedAnimalNameResource(animal));
            toUpdate.randomAnimalSays = getString(Animals.getLocalizedAnimalSaysResource(animal));
            long updateStatus = mapper.update(toUpdate);
            if(updateStatus > 0) {
                addNewEventToExpandedPanel(format(getString(R.string._l2_t2_expanded_added), toUpdate.getRowID(), originalModel, toUpdate, mapper.countAll()));
            } else {
                addNewEventToExpandedPanel(format(getString(R.string._l2_t2_expanded_error), toUpdate));
            }
            mapper.close();
            if(isLoadedModelExists()) {
                modelExists();
            } else {
                noModel();
            }
        } else {
            addNewEventToExpandedPanel(format(getString(R.string._l2_t2_expanded_error_unable), getLoadedModelId()));
        }
    }

    void loadModel() {
        String modelIdFromLoadET = loadModelIdET.getText().toString();
        if(modelIdFromLoadET == null) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_bad_id_input,
                    R.string._warning_dialog_ok_button_text);
            return;
        }
        if(modelIdFromLoadET.length() == 0) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_bad_id_input,
                    R.string._warning_dialog_ok_button_text);
            return;
        }
        Long modelToLoad = null;
        try {
            modelToLoad = Long.valueOf(modelIdFromLoadET);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_bad_id_input,
                    R.string._warning_dialog_ok_button_text);
            return;
        }
        setLoadedModelId(modelToLoad);
        if(isLoadedModelExists())
            modelExists();
        else {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_no_record_with_id,
                    R.string._warning_dialog_ok_button_text);
            noModel();
        }
    }

    @Override
    public void onVisible() {
        new LoadSelectedModel().execute(0l);
    }

    void modelExists() {
        setUpdateFieldsActive(true);
        loadModelData(getLoadedModelId());
    }

    void noModel() {
        setUpdateFieldsActive(false);
        setLoadedModelTWText(null);
    }

    void loadModelData(Long IPK) {
        RandomMapper mapper = getMapper();
        RandomModel model = mapper.findByIPK(IPK);
        if(model == null) {
            setUpdateFieldsActive(false);
        } else {
            loadModelValues(model);
        }
        setLoadedModelTWText(model);
    }

    void setLoadedModelTWText(RandomModel model) {
        if(model == null) {
            loadedModelTW.setText(
                    format(
                            getString(R.string._l2_t2_current_model_pattern),
                            getString(R.string._l2_t2_current_model_not_set)
                    )
            );
        } else {
            loadedModelTW.setText(
                    format(
                            getString(R.string._l2_t2_current_model_pattern),
                            model
                    )
            );
        }
    }

    void loadModelValues(RandomModel model) {
        loadModelIdET.setText(Long.toString(model.id));
        randomIntET.setText(Integer.toString(model.randomInt));
        randomIntegerET.setText(Integer.toString(model.randomInteger));
        randomAnimalSaysET.setText(model.randomAnimalSays);
        randomAnimalNameET.setText(model.randomAnimalName);
        animalSpinner.setSelection(animalAdapter.getPosition(model.randomAnimal.name()), true);
    }

    void setUpdateFieldsActive(boolean isActive) {
        //randomIntET.setFocusable(isActive);
        randomIntET.setEnabled(isActive);

        //randomIntegerET.setFocusable(isActive);
        randomIntegerET.setEnabled(isActive);

        //updateButton.setFocusable(isActive);
        updateButton.setEnabled(isActive);

        //animalSpinner.setFocusable(isActive);
        animalSpinner.setEnabled(isActive);
        if(!isActive)
            animalSpinner.setVisibility(View.GONE);
        else
            animalSpinner.setVisibility(View.VISIBLE);

        randomAnimalSaysET.setEnabled(isActive);
        randomAnimalNameET.setEnabled(isActive);
    }

    // Fab menu section

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T2_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T2_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T2_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l2_t2_snackbar_message;
    }

    // Assyncs
    private static String LOG_E_EXCEPTION_ON_LOAD = "Error on loading initial model at Lesson2Tab2Update, see exception details: ";
    private static String LOG_E_EXCEPTION_ON_LOAD_NESTED = "Error on loading initial model at Lesson2Tab2Update, see exception details (nested exception): ";

    class LoadSelectedModel extends AsyncTask<Long, Long, RandomModel> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
//            dialog = ProgressDialog.show(
//                    getLessonActivity(),
//                    getString(R.string._l5_t4_running_requested_operation_pg_title),
//                    getString(R.string._l5_t4_running_requested_operation_pg_body)
//            );
//            dialog.setCancelable(false);
        }

        @Override
        protected RandomModel doInBackground(Long... params) {
            Long modelToLoad = getLoadedModelId();
            if(modelToLoad == null) return null;
            else {
                try {
                    RandomMapper mapper = (RandomMapper) getDb().getMapper(RandomModel.class);
                    return mapper.findByIPK(modelToLoad);
                } catch (Exception e) {
                    Log.e(BasicDatabase.LOG_TAG, LOG_E_EXCEPTION_ON_LOAD, e);
                    if(e instanceof KittyRuntimeException) {
                        if(((KittyRuntimeException) e).getNestedException() != null)
                            Log.e(BasicDatabase.LOG_TAG,
                                    LOG_E_EXCEPTION_ON_LOAD_NESTED,
                                    ((KittyRuntimeException) e).getNestedException());
                    }
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(RandomModel result) {
//            dialog.cancel();
            if (result != null) {
                loadModelValues(result);
                setLoadedModelTWText(result);
            } else {
                setUpdateFieldsActive(false);
                setLoadedModelTWText(null);
            }
        }
    }
}
