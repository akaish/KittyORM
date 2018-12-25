
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

package net.akaish.kittyormdemo.lessons.two;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.sqlite.basicdb.BasicDatabase;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomMapper;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomModel;
import net.akaish.kittyormdemo.sqlite.basicdb.util.RNDRandomModelFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 03.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class Lesson2Tab5Random extends Lesson2BaseFragment {

    public Lesson2Tab5Random(){}

    CheckBox newInstanceCheckbox;
    Button new10Button;
    Button new500Button;
    Button new500TXButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson2_tab5_rnd, container, false);
        newInstanceCheckbox = rootView.findViewById(R.id.l2_t5_check_new_database_instance_for_operation);
        new10Button = rootView.findViewById(R.id.l2_t5_insert_10);
        new500Button = rootView.findViewById(R.id.l2_t5_insert_50k);
        new500TXButton = rootView.findViewById(R.id.l2_t5_insert_50k_tx);
        new10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert50RandomForProfiling();
                countRecordsToExpandedPanel(getMapper());
                InsertInAsync ten = new InsertInAsync(OPERATION_TEN_NEW, newInstanceCheckbox.isChecked());
                ten.execute("");
            }
        });
        new500Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countRecordsToExpandedPanel(getMapper());
                InsertInAsync fh = new InsertInAsync(OPERATION_FIFTY_K_NEW, newInstanceCheckbox.isChecked());
                fh.execute("");
            }
        });
        new500TXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countRecordsToExpandedPanel(getMapper());
                InsertInAsync fhtx = new InsertInAsync(OPERATION_FIFTY_K_TX_NEW, newInstanceCheckbox.isChecked());
                fhtx.execute("");
            }
        });
        setUpExpandedList(
                rootView,
                R.id._l2_t5_expanded_panel_lw,
                R.id._l2_t5_expanded_panel_text,
                R.string._l2_t5_expanded_text_pattern
        );
        return rootView;
    }

    void countRecordsToExpandedPanel(RandomMapper mapper) {
        addNewEventToExpandedPanel(format(getString(R.string._l2_t5_count_completed), mapper.countAll()));
    }

    void insert50RandomForProfiling() {
        RandomMapper m = getMapper();
        List<RandomModel> genRnd = new LinkedList<>();
        RNDRandomModelFactory factory = new RNDRandomModelFactory(getContext());
        for(int i = 0; i<50; i++) {
            genRnd.add(factory.newRandomModel());
        }
        m.insertInTransaction(genRnd);
    }

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T5_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T5_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T5_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l2_t5_snackbar_message;
    }

    final static int OPERATION_TEN_NEW = 0;
    final static int OPERATION_FIFTY_K_NEW = 1;
    final static int OPERATION_FIFTY_K_TX_NEW = 2;

    class InsertInAsync extends AsyncTask<String, String, Integer> {

        // It is not profiling (!)
        private long nanosStart;
        private long nanosGetDatabaseAnaMapper;
        private long nanosGenerateRandomModelsToInsert;
        private long nanosInsert;

        private final int operation;

        private final boolean useNewDatabaseObject;

        ProgressDialog dialog;

        InsertInAsync(int operation, boolean useNewDatabaseObject) {
            this.operation = operation;
            this.useNewDatabaseObject = useNewDatabaseObject;
        }

        BasicDatabase getDatabase() {
            if(useNewDatabaseObject)
                return new BasicDatabase(Lesson2Tab5Random.this.getContext());
            else
                return Lesson2Tab5Random.this.getDb();
        }

        RandomMapper getRandomMapperForAssync() {
            return (RandomMapper) getDatabase().getMapper(RandomModel.class);
        }

        void insertIntoDB() {
            RNDRandomModelFactory rndFactory = new RNDRandomModelFactory(Lesson2Tab5Random.this.getContext());
            nanosStart = System.nanoTime();
            RandomMapper mapper = getRandomMapperForAssync();
            nanosGetDatabaseAnaMapper = System.nanoTime();
            List<RandomModel> toInsert = new ArrayList<>();
            switch (operation) {
                case OPERATION_TEN_NEW:
                    for(int i = 0; i < 10; i++) {
                        toInsert.add(rndFactory.newRandomModel());
                    }
                    break;
                default:
                    for(int i = 0; i < 50000; i++) {
                        toInsert.add(rndFactory.newRandomModel());
                    }
            }
            nanosGenerateRandomModelsToInsert = System.nanoTime();
            switch (operation) {
                case OPERATION_TEN_NEW:
                    mapper.insert(toInsert);
                    break;
                case OPERATION_FIFTY_K_TX_NEW:
                    mapper.insertInTransaction(toInsert);
                    break;
                case OPERATION_FIFTY_K_NEW:
                    mapper.insert(toInsert);
                    break;
            }
            nanosInsert = System.nanoTime();
            mapper.close();
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
        protected Integer doInBackground(String... strings) {
            try {
                insertIntoDB();
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, "Exception caught on insertion, see details", e);
                if(e instanceof KittyRuntimeException)
                    if(((KittyRuntimeException) e).getNestedException() != null)
                        Log.e(BasicDatabase.LOG_TAG, "Nested exception: ", ((KittyRuntimeException) e).getNestedException());
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPreExecute() {
            int progressDialogStringId = 0;
            switch (operation) {
                case OPERATION_TEN_NEW:
                    progressDialogStringId = R.string._l2_t5_inserting_10;
                    break;
                case OPERATION_FIFTY_K_NEW:
                    progressDialogStringId = R.string._l2_t5_inserting_50k;
                    break;
                case OPERATION_FIFTY_K_TX_NEW:
                    progressDialogStringId = R.string._l2_t5_inserting_50kTX;
                    break;
            }
            dialog = ProgressDialog.show(
                    Lesson2Tab5Random.this.getLessonActivity(),
                    Lesson2Tab5Random.this.getString(R.string._l2_t5_inserting_dialog_title),
                    Lesson2Tab5Random.this.getString(progressDialogStringId)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Integer result) {
            int operationNameStringId = 0;
            switch (operation) {
                case OPERATION_TEN_NEW:
                    operationNameStringId = R.string._l2_t5_inserting_10_OP;
                    break;
                case OPERATION_FIFTY_K_NEW:
                    operationNameStringId = R.string._l2_t5_inserting_50k_OP;
                    break;
                case OPERATION_FIFTY_K_TX_NEW:
                    operationNameStringId = R.string._l2_t5_inserting_50kTX_OP;
                    break;
            }
            if(result > 0) {

                String successfullEvent = format(
                        Lesson2Tab5Random.this.getString(R.string._l2_t5_inserted_event),
                        Lesson2Tab5Random.this.getString(operationNameStringId),
                        useNewDatabaseObject,
                        nanosInsert - nanosStart,
                        nanosGetDatabaseAnaMapper - nanosStart,
                        nanosGenerateRandomModelsToInsert - nanosGetDatabaseAnaMapper,
                        nanosInsert - nanosGenerateRandomModelsToInsert
                );
                Lesson2Tab5Random.this.addNewEventToExpandedPanel(successfullEvent);
            } else {
                String eventErrorMessage = format(
                        Lesson2Tab5Random.this.getString(R.string._l2_t5_inserted_event_error),
                        Lesson2Tab5Random.this.getString(operation),
                        BasicDatabase.LOG_TAG
                );
                Lesson2Tab5Random.this.addNewEventToExpandedPanel(eventErrorMessage);
            }
            dialog.cancel();
            countRecordsToExpandedPanel(getMapper());
        }
    }
}
