
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.akaish.kitty.orm.enums.AscDesc;
import net.akaish.kitty.orm.query.QueryParameters;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.enums.SQLiteOperator;
import net.akaish.kittyormdemo.KittyTutorialActivity;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.lessons.adapters.BasicRandomModelAdapter;
import net.akaish.kittyormdemo.sqlite.basicdb.AbstractRandomModel;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomMapper;
import net.akaish.kittyormdemo.sqlite.basicdb.RandomModel;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * Created by akaish on 03.08.18.
 * @author akaish (Denis Bogomolov)
 */
public class Lesson2Tab4Find extends Lesson2BaseFragment {

    // Pagination start
    Button firstPage;
    Button pageUp;
    Button pageDown;
    Button lastPage;

    SQLiteCondition currentCondition;
    FindResultsPager pager;

    ListView pagerEntitiesLW;
    TextView expandedTitleTW;
    TextView paginationCounterTW;

    BasicRandomModelAdapter entitiesAdapter;

    String expandedPanelTitlePattern;
    String expandedPanelPageCounterTitlePattern;
    // Pagination end

    // Fragment controls
    EditText findByIdET;
    Button findByIdButton;

    EditText findByRangeStartET;
    EditText findByRangeEndET;
    Button findByRangeButton;

    Button findByAnimalButton;

    Button findAllButton;

    public Lesson2Tab4Find(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson2_tab4_find, container, false);

        setAnimalSpinner(rootView, R.id.l2_t4_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setUpPagination(rootView);

        findByIdET = rootView.findViewById(R.id.l2_t4_et_id);
        findByIdButton = rootView.findViewById(R.id.l2_t4_find_by_id_button);
        findByIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findById();
            }
        });

        findByRangeStartET = rootView.findViewById(R.id.l2_t4_et_id_range_start);
        findByRangeEndET = rootView.findViewById(R.id.l2_t4_et_id_range_end);
        findByRangeButton = rootView.findViewById(R.id.l2_t4_find_by_range_button);
        findByRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findByRange();
            }
        });

        findByAnimalButton = rootView.findViewById(R.id.l2_t4_find_by_animal);
        findByAnimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findByAnimal();
            }
        });

        findAllButton  = rootView.findViewById(R.id._l2_t4_find_all_button);
        findAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAll();
            }
        });

        reloadPager();

        return rootView;
    }

    void findById() {
        String inputId = findByIdET.getText().toString();
        if(inputId == null) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_id_message,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        if(inputId.length() == 0) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_id_message,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        Long idToFind = null;
        try {
            idToFind = Long.valueOf(inputId);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_id_message,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        if(idToFind < 1) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_id_must_be_positive,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
        builder.addColumn("id").addSQLOperator(SQLiteOperator.EQUAL).addValue(idToFind);
        setPaginationResults(builder.build());
    }

    void findByRange() {
        String rangeStart = findByRangeStartET.getText().toString();
        String rangeEnd = findByRangeEndET.getText().toString();
        if(rangeStart == null || rangeEnd == null) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_range_message,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        if(rangeStart.length() == 0 || rangeEnd.length() == 0) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_range_message,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        int rangeStartInt = 0; int rangeEndInt = 0;
        try {
            rangeStartInt = Integer.parseInt(rangeStart);
            rangeEndInt = Integer.parseInt(rangeEnd);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_range_message,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
        builder.addColumn("random_int")
                .addSQLOperator(SQLiteOperator.GREATER_OR_EQUAL)
                .addValue(rangeStartInt)
                .addSQLOperator(SQLiteOperator.AND)
                .addColumn("random_int")
                .addSQLOperator(SQLiteOperator.LESS_OR_EQUAL)
                .addValue(rangeEndInt);
        setPaginationResults(builder.build());
    }

    void findByAnimal() {
        String animalStr = (String) animalSpinner.getSelectedItem();
        if(animalStr.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t4_find_by_animal_message,
                    R.string._warning_dialog_ok_button_text
            );
            return;
        }
        Animals animal = Animals.valueOf(animalStr);
        SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
        builder.addColumn(AbstractRandomModel.RND_ANIMAL_CNAME)
                .addSQLOperator(SQLiteOperator.EQUAL)
                .addValue(animal.name());
        setPaginationResults(builder.build());
    }

    void findAll() {
        currentCondition = new SQLiteConditionBuilder().addValue(1)
                                                       .build();
        setPaginationResults(currentCondition);
    }

    @Override
    public void onVisible() {
        reloadPager();
    }

    void reloadPager() {
        new ReloadPagerTask().execute(0l);
    }

    void setUpPagination(View root) {
        firstPage = root.findViewById(R.id._l2_t4_pagination_at_start);
        pageUp = root.findViewById(R.id._l2_t4_pagination_page_up);
        pageDown = root.findViewById(R.id._l2_t4_pagination_page_down);
        lastPage = root.findViewById(R.id._l2_t4_pagination_end);
        pagerEntitiesLW = root.findViewById(R.id._l2_t4_find_result_enteties_list);
        paginationCounterTW = root.findViewById(R.id._l2_t4_page_counter);
        expandedTitleTW = root.findViewById(R.id._l2_t4_expanded_panel_title);
        entitiesAdapter = new BasicRandomModelAdapter(getContext(), new LinkedList<RandomModel>());
        pagerEntitiesLW.setAdapter(entitiesAdapter);
        pagerEntitiesLW.setOnTouchListener(new View.OnTouchListener() {

            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        pagerEntitiesLW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RandomModel model = entitiesAdapter.getItem(position);
                Toast.makeText(
                        getLessonActivity(),
                        format(
                                getString(R.string._l2_t4_pager_select_entity_pattern),
                                model.id
                        ), Toast.LENGTH_SHORT
                ).show();
                setLoadedModelId(model.id);

            }
        });
        expandedPanelTitlePattern = getString(R.string._l2_t4_expanded_title_pattern);
        expandedPanelPageCounterTitlePattern = getString(R.string._l2_t4_pager_pattern);
        paginationCounterTW.setText(format(expandedPanelPageCounterTitlePattern,0, 0));
        expandedTitleTW.setText(format(expandedPanelTitlePattern, 0));
        firstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage(1l);
            }
        });
        pageUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager != null) {
                    loadPage(pager.currentPage-1);
                }
            }
        });
        pageDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager != null) {
                    loadPage(pager.currentPage+1);
                }
            }
        });
        lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager != null) {
                    loadPage(pager.getPagesCount());
                }
            }
        });
    }

    void setPaginationResults(SQLiteCondition condition) {
        RandomMapper mapper = getMapper();
        this.currentCondition = condition;
        this.pager = new FindResultsPager(mapper.countWhere(condition, null));
        mapper.close();
        loadPage(1l);
    }

    void setPaginationButtonsState() {
        if(pager == null || currentCondition == null) {
            firstPage.setEnabled(false);
            firstPage.setTextColor(getColourForPB(false));
            pageUp.setEnabled(false);
            pageUp.setTextColor(getColourForPB(false));
            pageDown.setEnabled(false);
            pageDown.setTextColor(getColourForPB(false));
            lastPage.setEnabled(false);
            lastPage.setTextColor(getColourForPB(false));
        } else {
            firstPage.setEnabled(!pager.isFirstPage());
            firstPage.setTextColor(getColourForPB(!pager.isFirstPage()));
            pageUp.setEnabled(!pager.isFirstPage());
            pageUp.setTextColor(getColourForPB(!pager.isFirstPage()));
            pageDown.setEnabled(!pager.isLastPage());
            pageDown.setTextColor(getColourForPB(!pager.isLastPage()));
            lastPage.setEnabled(!pager.isLastPage());
            lastPage.setTextColor(getColourForPB(!pager.isLastPage()));
        }
    }

    int getColourForPB(boolean isActive) {
        if(isActive)
            return getResources().getColor(R.color.colorGrayLightest);
        return getResources().getColor(R.color.colorPrimaryDark);
    }

    void loadPage(long pageNumber) {
        if(currentCondition == null || pager == null) {
            entitiesAdapter.notifyDataSetChanged();
            paginationCounterTW.setText(format(expandedPanelPageCounterTitlePattern, 0, 0));
            expandedTitleTW.setText(format(expandedPanelTitlePattern, 0));
            setPaginationButtonsState();
            return;
        }

        pager.setCurrentPage(pageNumber);
        entitiesAdapter.clear();

        RandomMapper mapper = getMapper();
        List<RandomModel> page = mapper.findWhere(currentCondition, pager.getQueryParameter());
        mapper.close();
        if(page == null) {
            entitiesAdapter.notifyDataSetChanged();
            paginationCounterTW.setText(format(expandedPanelPageCounterTitlePattern, 0, 0));
            expandedTitleTW.setText(format(expandedPanelTitlePattern, 0));
        } else {
            entitiesAdapter.addAll(page);
            entitiesAdapter.notifyDataSetChanged();
            paginationCounterTW.setText(format(
                    expandedPanelPageCounterTitlePattern,
                    pager.currentPage,
                    pager.getPagesCount()
            ));
            expandedTitleTW.setText(format(expandedPanelTitlePattern, pager.entitiesAmount));
        }
        setPaginationButtonsState();
    }

    class FindResultsPager {
        long entitiesAmount;
        long currentPage = 1l;
        final long entitiesPerPage = 50l;

        void setCurrentPage(long pageNumber) {
            if(getPagesCount() == 0) {
                currentPage = 1l;
                return;
            }
            if(pageNumber < 1l) {
                currentPage = 1l;
                return;
            }
            if(pageNumber > getPagesCount()) {
                currentPage = getPagesCount();
                return;
            }
            currentPage = pageNumber;
        }

        boolean isFirstPage() {
            if(currentPage == 1l) return true;
            return false;
        }

        boolean isLastPage() {
            if(currentPage == getPagesCount()) return true;
            return false;
        }

        long getPagesCount() {
            if((entitiesAmount % entitiesPerPage) > 0) {
                return (entitiesAmount / entitiesPerPage)+1;
            } else {
                return (entitiesAmount / entitiesPerPage);
            }
        }

        FindResultsPager(long entitiesAmount) {
            this.entitiesAmount = entitiesAmount;
        }

        long getCurrentPage() {
            return currentPage;
        }

        long getOffset(long pageNumber) {
            return entitiesPerPage * pageNumber;
        }

        long getLimit() {
            return entitiesPerPage;
        }

        QueryParameters getQueryParameter() {
            QueryParameters parameters = new QueryParameters();
            parameters.setLimit(entitiesPerPage);
            long offset = (currentPage - 1) * entitiesPerPage;
            parameters.setOffset(offset);
            parameters.setOrderByColumns("id");
            parameters.setOrderAscDesc(AscDesc.ASCENDING);
            return parameters;
        }
    }


    // Fab menu section
    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T4_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T4_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T4_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l2_t4_snackbar_message;
    }

    class ReloadPagerTask extends AsyncTask<Long, Long, ArrayList<RandomModel>> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<RandomModel> doInBackground(Long... params) {
            long oldPage = 1l;
            if(pager != null)
                oldPage = pager.currentPage;
            RandomMapper mapper = getMapper();
            pager = new FindResultsPager(mapper.countWhere(currentCondition, null));
            mapper.close();
            ArrayList<RandomModel> page = getPage(1l);
            if(pager != null) {
                if(oldPage != 1l) {
                    if(oldPage <= pager.getPagesCount()) {
                        page = getPage(oldPage);
                    }
                }
            }
            return page;
        }

        private ArrayList<RandomModel> getPage(Long pageNumber) {
            if(currentCondition == null || pager == null) {
                return null;
            }
            pager.setCurrentPage(pageNumber);
            RandomMapper mapper = getMapper();
            ArrayList<RandomModel> page = (ArrayList)mapper.findWhere(currentCondition, pager.getQueryParameter());
            mapper.close();
            return page;
        }

        @Override
        protected void onPostExecute(ArrayList<RandomModel> result) {
//            dialog.cancel();
            if(currentCondition == null || pager == null) {
                entitiesAdapter.notifyDataSetChanged();
                paginationCounterTW.setText(format(expandedPanelPageCounterTitlePattern, 0, 0));
                expandedTitleTW.setText(format(expandedPanelTitlePattern, 0));
                setPaginationButtonsState();
                return;
            } else {
                entitiesAdapter.clear();
                if(result == null) {
                    entitiesAdapter.notifyDataSetChanged();
                    paginationCounterTW.setText(format(expandedPanelPageCounterTitlePattern, 0, 0));
                    expandedTitleTW.setText(format(expandedPanelTitlePattern, 0));
                } else {
                    entitiesAdapter.addAll(result);
                    entitiesAdapter.notifyDataSetChanged();
                    paginationCounterTW.setText(format(
                            expandedPanelPageCounterTitlePattern,
                            pager.currentPage,
                            pager.getPagesCount()
                    ));
                    expandedTitleTW.setText(format(expandedPanelTitlePattern, pager.entitiesAmount));
                }
                setPaginationButtonsState();
            }
        }
    }
}
