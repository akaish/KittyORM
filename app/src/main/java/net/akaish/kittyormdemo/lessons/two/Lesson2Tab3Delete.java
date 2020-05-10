
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

package net.akaish.kittyormdemo.lessons.two;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomMapper;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import static java.text.MessageFormat.format;
import static net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase.LOG_TAG;

/**
 * Created by akaish on 03.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson2Tab3Delete extends Lesson2BaseFragment {

    public Lesson2Tab3Delete(){};

    EditText deleteByIdEt;
    Button deleteByIdButton;

    EditText deleteByRangeStartET;
    EditText deleteByRangeEndET;
    Button deleteByRangeButton;

    Button deleteByAnimalButton;

    Button wipeDataButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson2_tab3_delete, container, false);

        setAnimalSpinner(rootView, R.id.l2_t3_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // do nothing
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        deleteByIdEt = rootView.findViewById(R.id.l2_t3_et_id);
        deleteByIdButton = rootView.findViewById(R.id.l2_t3_delete_by_id_button);

        deleteByRangeStartET = rootView.findViewById(R.id.l2_t3_et_id_range_start);
        deleteByRangeEndET = rootView.findViewById(R.id.l2_t3_et_id_range_end);
        deleteByRangeButton = rootView.findViewById(R.id.l2_t3_delete_by_range_button);

        deleteByAnimalButton = rootView.findViewById(R.id.l2_t3_delete_by_animal_button);

        deleteByIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteById();
            }
        });

        deleteByRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteByRange();
            }
        });

        deleteByAnimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteByAnimal();
            }
        });

        wipeDataButton = rootView.findViewById(R.id.l2_t3_wipe);
        wipeDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wipeData();
            }
        });

        setUpExpandedList(
                rootView,
                R.id._l2_t3_expanded_panel_lw,
                R.id._l2_t3_expanded_panel_text,
                R.string._l2_t3_expanded_text_pattern
        );

        return rootView;
    }

    void wipeData() {
        RandomMapper mapper = getMapper();
        countRecordsToExpandedPanel(mapper);

        DeleteAsync task = new DeleteAsync(new DeleteTask(DELETE_ALL, mapper, null, null, null) {

            @Override
            void publishResultToEventList(Long resultDelete) {
                String result = null;
                StringBuilder operation = new StringBuilder(16).append("\'wipe all\'");
                if(resultDelete > -1) {
                    result = format(getString(R.string._l2_t3_delete_model_completed), resultDelete, operation);
                } else {
                    result = format(getString(R.string._l2_t3_delete_model_error), LOG_TAG, operation);
                }
                addNewEventToExpandedPanel(result);
                countRecordsToExpandedPanel(deleteMapper);
                deleteMapper.close();
            }
        });

        task.execute("");
    }

    void deleteById() {
        RandomMapper mapper = getMapper();
        countRecordsToExpandedPanel(mapper);
        String inputId = deleteByIdEt.getText().toString();
        if(inputId == null) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t3_delete_by_id_message,
                    R.string._warning_dialog_ok_button_text
            );
            mapper.close();
            return;
        }
        if(inputId.length() == 0) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t3_delete_by_id_message,
                    R.string._warning_dialog_ok_button_text
            );
            mapper.close();
            return;
        }
        Long idToDelete = null;
        try {
            idToDelete = Long.valueOf(inputId);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t3_delete_by_id_message,
                    R.string._warning_dialog_ok_button_text
            );
            mapper.close();
            return;
        }
        long affectedRecords = mapper.deleteByIPK(idToDelete);
        String result = null;
        StringBuilder operation = new StringBuilder(16).append("id = ").append(idToDelete);
        if(affectedRecords > -1) {
            result = format(getString(R.string._l2_t3_delete_model_completed), affectedRecords, operation);
        } else {
            result = format(getString(R.string._l2_t3_delete_model_error), LOG_TAG, operation);
        }
        addNewEventToExpandedPanel(result);
        countRecordsToExpandedPanel(mapper);
        mapper.close();
    }

    void deleteByRange() {
        RandomMapper mapper = getMapper();
        countRecordsToExpandedPanel(mapper);
        String rangeStart = deleteByRangeStartET.getText().toString();
        String rangeEnd = deleteByRangeEndET.getText().toString();
        if(rangeStart == null || rangeEnd == null) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t3_delete_by_range_message,
                    R.string._warning_dialog_ok_button_text
            );
            mapper.close();
            return;
        }
        if(rangeStart.length() == 0 || rangeEnd.length() == 0) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t3_delete_by_range_message,
                    R.string._warning_dialog_ok_button_text
            );
            mapper.close();
            return;
        }
        int rangeStartInt = 0; int rangeEndInt = 0;
        try {
            rangeStartInt = Integer.parseInt(rangeStart);
            rangeEndInt = Integer.parseInt(rangeEnd);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t3_delete_by_range_message,
                    R.string._warning_dialog_ok_button_text
            );
            mapper.close();
            return;
        }

        DeleteAsync task = new DeleteAsync(new DeleteTask(DELETE_BY_RANGE, mapper, null, rangeStartInt, rangeEndInt) {

            @Override
            void publishResultToEventList(Long resultDelete) {
                String result = null;
                StringBuilder operation = new StringBuilder(32).append("randomInt range [")
                        .append(deleteRangeStart)
                        .append("; ")
                        .append(deleteRangeEnd)
                        .append("]");
                if(resultDelete > -1) {
                    result = format(getString(R.string._l2_t3_delete_model_completed), resultDelete, operation);
                } else {
                    result = format(getString(R.string._l2_t3_delete_model_error), LOG_TAG, operation);
                }
                addNewEventToExpandedPanel(result);
                countRecordsToExpandedPanel(deleteMapper);
                deleteMapper.close();
            }
        });

        task.execute("");
    }

    void deleteByAnimal() {
        RandomMapper mapper = getMapper();
        countRecordsToExpandedPanel(mapper);
        String animalStr = (String) animalSpinner.getSelectedItem();
        if(animalStr.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t3_delete_by_animal_message,
                    R.string._warning_dialog_ok_button_text
            );
            mapper.close();
            return;
        }
        Animals animal = Animals.valueOf(animalStr);

        DeleteAsync task = new DeleteAsync(new DeleteTask(DELETE_BY_ANIMAL, mapper, animal, null, null) {

            @Override
            void publishResultToEventList(Long resultDelete) {
                String result = null;
                StringBuilder operation = new StringBuilder(16).append("deleteAnimal = ").append(deleteAnimal.name());
                if(resultDelete > -1) {
                    result = format(getString(R.string._l2_t3_delete_model_completed), resultDelete, operation);
                } else {
                    result = format(getString(R.string._l2_t3_delete_model_error), LOG_TAG, operation);
                }
                addNewEventToExpandedPanel(result);
                countRecordsToExpandedPanel(deleteMapper);
                deleteMapper.close();
            }
        });

        task.execute("");

    }

    void countRecordsToExpandedPanel(RandomMapper mapper) {
        addNewEventToExpandedPanel(format(getString(R.string._l2_t3_delete_model_count_completed), mapper.countAll()));
    }

    private static final int DELETE_BY_RANGE = 1;
    private static final int DELETE_BY_ANIMAL = 2;
    private static final int DELETE_ALL = 3;

    abstract class DeleteTask {

        private int operation;
        RandomMapper deleteMapper;
        Animals deleteAnimal;
        Integer deleteRangeStart;
        Integer deleteRangeEnd;

        public DeleteTask(int operation, RandomMapper mapper, Animals animal, Integer rangeStart, Integer rangeEnd) {
            this.operation = operation;
            this.deleteMapper = mapper;
            this.deleteAnimal = animal;
            this.deleteRangeStart = rangeStart;
            this.deleteRangeEnd = rangeEnd;
        }

        Long deleteInBackground() {
            switch (operation) {
                case DELETE_BY_RANGE:
                    return deleteMapper.deleteByRandomIntegerRange(deleteRangeStart, deleteRangeEnd);
                case DELETE_BY_ANIMAL:
                    return deleteMapper.deleteByAnimal(deleteAnimal);
                case DELETE_ALL:
                    return deleteMapper.deleteAll();
            }
            return -1l;
        }

        abstract void publishResultToEventList(Long resultDelete);
    }

    class DeleteAsync extends AsyncTask<String, String, Long> {
        private DeleteTask deleteTask;

        ProgressDialog dialog;

        DeleteAsync(DeleteTask task) {
            deleteTask = task;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    Lesson2Tab3Delete.this.getLessonActivity(),
                    Lesson2Tab3Delete.this.getString(R.string._l2_t3_delete_dialog_title),
                    Lesson2Tab3Delete.this.getString(R.string._l2_t3_delete_message)
            );
            dialog.setCancelable(false);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Long doInBackground(String... strings) {
            try {
                return deleteTask.deleteInBackground();
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, "Exception caught on delete, see details", e);
                if(e instanceof KittyRuntimeException)
                    if(((KittyRuntimeException) e).getNestedException() != null)
                        Log.e(BasicDatabase.LOG_TAG, "Nested exception: ", ((KittyRuntimeException) e).getNestedException());
            }
            return -1l;
        }

        @Override
        protected void onPostExecute(Long result) {
            deleteTask.publishResultToEventList(result);
            dialog.cancel();
        }


    }

    // Fab menu section

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T3_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T3_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T3_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l2_t3_snackbar_message;
    }
}
