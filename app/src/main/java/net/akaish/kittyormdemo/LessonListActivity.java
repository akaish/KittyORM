
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

package net.akaish.kittyormdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.akaish.kittyormdemo.fabmenu.FabOnClickListenerFabMenuStateSpecific;
import net.akaish.kittyormdemo.lessons.ContextFabMenus;
import net.akaish.kittyormdemo.lessons.FabContainer;
import net.akaish.kittyormdemo.lessons.LessonHostFragment;
import net.akaish.kittyormdemo.lessons.LessonsUriConstants;
import net.akaish.kittyormdemo.lessons.RegularLessonBaseFragment;
import net.akaish.kittyormdemo.menu.MainMenu;

import java.util.LinkedList;

import static java.text.MessageFormat.format;
import static net.akaish.kittyormdemo.lessons.ContextFabMenus.MAIN_MENU_FAB;
import static net.akaish.kittyormdemo.lessons.ContextFabMenus.getFabLayoutResource;
import static net.akaish.kittyormdemo.menu.MainMenu.LESSON_ID;

/**
 * An activity representing a list of Lessons. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LessonDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * @author akaish (Denis Bogomolov)
 */
public class LessonListActivity extends KittyTutorialActivity {

    Fragment currentFragment;
    Toolbar toolbar;
    private String titlePattern = "{0} : {1}";
    private int currentFragmentId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);
        setFabCoordinatorParentView();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.lesson_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }

        View recyclerView = findViewById(R.id.lesson_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (twoPane) {
            commitFragment(currentFragmentId);
        } else {;
            drawMainMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(twoPane)
            toolbar.setTitle(format(titlePattern, getTitle(), getString(MainMenu.LESSON_ITEMS.get(currentFragmentId).title)));
    }

    @Override
    protected void setFabCoordinatorParentView() {
        fabCoordinatorParentView = findViewById(R.id.lesson_list_fab_parent);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(MainMenu.LESSON_ITEMS, twoPane));
    }

    public void commitFragment(int lessonId) {
        currentFragmentId = lessonId;
        Bundle arguments = new Bundle();
        arguments.putInt(LESSON_ID, lessonId);
        currentFragment = MainMenu.LESSON_ITEMS.get(lessonId).newLessonFragment();
        currentFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lesson_detail_container, currentFragment)
                .commit();
        if(twoPane) {
            notifyToChangeFabMenu(getFabLayoutResource(lessonId), getFabActions(lessonId), getFabLayouts(lessonId));
            initialFragmentLoadedByActivity = true;
            toolbar.setTitle(format(titlePattern, getTitle(), getString(MainMenu.LESSON_ITEMS.get(lessonId).title)));
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final SparseArray<MainMenu.LessonItem> lessons;
        public final boolean twoPane;

        private View oldView;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainMenu.LessonItem item = (MainMenu.LessonItem) view.getTag();
                if (twoPane) {
                    if(oldView != null) {
                        oldView.findViewById(R.id.highlighter).setBackgroundResource(R.color.colorDarkDarkGray);
                    }
                    oldView = view;
                    view.findViewById(R.id.highlighter).setBackgroundResource(R.color.colorAccent);

                    commitFragment(item.id);
                    //((LessonHostFragment)parentActivity.getSupportFragmentManager().findFragmentById(R.id.lesson_detail_container)).notifyFragmentOnViewPagerChanged();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, LessonDetailActivity.class);
                    intent.putExtra(LESSON_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(SparseArray<MainMenu.LessonItem> items,
                                      boolean twoPane) {
            lessons = items;
            this.twoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lesson_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            //holder.mIdView.setText(format(LessonListActivity.this.getString(R.string._menu_lesson), position+1));
            if(position == 0 && twoPane) {
                holder.highlighter.setBackgroundResource(R.color.colorAccent);
                oldView = holder.highlighter;
            }
            holder.mContentView.setText(lessons.get(position).title);

            holder.itemView.setTag(lessons.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return lessons.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            //final TextView mIdView;
            final TextView mContentView;
            final View highlighter;

            ViewHolder(View view) {
                super(view);
                //mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                highlighter = view.findViewById(R.id.highlighter);
            }
        }
    }

    public void onBackPressed() {
        if(isFabOpen() && !initialFragmentLoadedByActivity) {
            if(currentFragment != null) {
                RegularLessonBaseFragment tabFragment = ((LessonHostFragment)currentFragment).getCurrentTabFragment();
                if(tabFragment!=null)
                    dismissSnackbarIfOpen();
                    tabFragment.dismissSnackbarIfOpen();
            }
            closeFabMenu();
        } else if (isFabOpen() && initialFragmentLoadedByActivity) {
            dismissSnackbarIfOpen();
            closeFabMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void drawMainMenu() {
        notifyToChangeFabMenu(getFabLayoutResource(MAIN_MENU_FAB), getFabActions(MAIN_MENU_FAB), getFabLayouts(MAIN_MENU_FAB));
    }

    private View.OnClickListener contactKittyAction() {
        return new MailIntentOnClick(LessonsUriConstants.L7_MAIL, getString(R.string._menu_mail_header), "");
    }

    private View.OnClickListener followKittyAction() {
        return new WebIntentOnClick(LessonsUriConstants.L7_FOLLOW);
    }

    private View.OnClickListener shareKittyAction() {
        return new ShareIntentOnClick(
                LessonsUriConstants.L6_SHARE,
                getString(R.string._menu_share_link_subject),
                getString(R.string._menu_share_link_chooser_title
                ));
    }

    private View.OnClickListener legalInfoAction() {
        return new WebIntentOnClick(LessonsUriConstants.L7_VIEW_LEGAL_INFO_ONLINE);
    }

    private LinkedList<FabContainer> getFabActions(int lessonId) {
        switch (lessonId) {
            case MAIN_MENU_FAB:
                return mainMenuFabs();
            case 0:
                return lessonOneInitialFabs();
            case 1:
                return lessonTwoInitialFabs();
            case 2:
                return lessonThreeInitialFabs();
            case 3:
                return lessonFourInitialFabs();
            case 4:
                return lessonFiveInitialFabs();
            case 5:
                return lessonSixInitialFabs();
            case 6:
                return lessonSevenInitialFabs();
            default:
                return null;
        }
    }

    private LinkedList<Integer> getFabLayouts(int lessonId) {
        return ContextFabMenus.getFabLayout(lessonId);
    }

    private LinkedList<FabContainer> mainMenuFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.main_menu_fab, null));
        out.addLast(new FabContainer(1, R.id.mm_license_fab, legalInfoAction()));
        out.addLast(new FabContainer(2, R.id.mm_share_fab, shareKittyAction()));
        out.addLast(new FabContainer(3, R.id.mm_follow_fab, followKittyAction()));
        out.addLast(new FabContainer(4, R.id.mm_mail_fab, contactKittyAction()));
        return out;
    }

    private LinkedList<FabContainer> lessonOneInitialFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.regular_lesson_fab, baseFabAction(R.string._l1_t1_snackbar_message)));
        out.addLast(new FabContainer(1, R.id.regular_lesson_fab2_sourcecode, new WebDialogOnClick(LessonsUriConstants.L1_T1_SOURCE)));
        out.addLast(new FabContainer(2, R.id.regular_lesson_fab3_dbschema, new WebDialogOnClick(LessonsUriConstants.L1_T1_SCHEMA)));
        out.addLast(new FabContainer(3, R.id.regular_lesson_fab1_help, new WebDialogOnClick(LessonsUriConstants.L1_T1_TUTORIAL)));
        return out;
    }

    private LinkedList<FabContainer> lessonTwoInitialFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.regular_lesson_fab, baseFabAction(R.string._l2_t1_snackbar_message)));
        out.addLast(new FabContainer(1, R.id.regular_lesson_fab2_sourcecode,  new WebDialogOnClick(LessonsUriConstants.L2_T1_SOURCE)));
        out.addLast(new FabContainer(2, R.id.regular_lesson_fab3_dbschema,  new WebDialogOnClick(LessonsUriConstants.L2_T1_SCHEMA)));
        out.addLast(new FabContainer(3, R.id.regular_lesson_fab1_help,  new WebDialogOnClick(LessonsUriConstants.L2_T1_TUTORIAL)));
        return out;
    }

    private LinkedList<FabContainer> lessonThreeInitialFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.regular_lesson_fab, baseFabAction(R.string._l3_t1_snackbar_message)));
        out.addLast(new FabContainer(1, R.id.regular_lesson_fab2_sourcecode,  new WebDialogOnClick(LessonsUriConstants.L3_T1_SOURCE)));
        out.addLast(new FabContainer(2, R.id.regular_lesson_fab3_dbschema,  new WebDialogOnClick(LessonsUriConstants.L3_T1_SCHEMA)));
        out.addLast(new FabContainer(3, R.id.regular_lesson_fab1_help,  new WebDialogOnClick(LessonsUriConstants.L3_T1_TUTORIAL)));
        return out;
    }

    private LinkedList<FabContainer> lessonFourInitialFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.regular_lesson_fab, baseFabAction(R.string._l4_t1_snackbar_message)));
        out.addLast(new FabContainer(1, R.id.regular_lesson_fab2_sourcecode,  new WebDialogOnClick(LessonsUriConstants.L4_T1_SOURCE)));
        out.addLast(new FabContainer(2, R.id.regular_lesson_fab1_help,  new WebDialogOnClick(LessonsUriConstants.L4_T1_TUTORIAL)));
        return out;
    }

    private LinkedList<FabContainer> lessonFiveInitialFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.regular_lesson_fab, baseFabAction(R.string._l5_t1_snackbar_message)));
        out.addLast(new FabContainer(1, R.id.regular_lesson_fab2_sourcecode,  new WebDialogOnClick(LessonsUriConstants.L5_T1_SOURCE)));
        out.addLast(new FabContainer(2, R.id.regular_lesson_fab3_dbschema,  new WebDialogOnClick(LessonsUriConstants.L5_T1_SCHEMA)));
        out.addLast(new FabContainer(3, R.id.regular_lesson_fab1_help,  new WebDialogOnClick(LessonsUriConstants.L5_T1_TUTORIAL)));
        return out;
    }

    private LinkedList<FabContainer> lessonSixInitialFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.make_kitty_great_fab, baseFabAction(R.string._l6_t1_snackbar_message)));

        out.addLast(new FabContainer(1, R.id.donate_fab, new WebIntentOnClick(LessonsUriConstants.L6_SPONSOR)));
        out.addLast(new FabContainer(2, R.id.commit_fab, new WebIntentOnClick(LessonsUriConstants.L6_DEVELOP)));
        out.addLast(new FabContainer(3, R.id.share_fab, new ShareIntentOnClick(
                                                                        LessonsUriConstants.L6_SHARE,
                                                                        getString(R.string._menu_share_link_subject),
                                                                        getString(R.string._menu_share_link_chooser_title)
                                                                        )
        ));
        out.addLast(new FabContainer(4, R.id.use_fab, new WebIntentOnClick(LessonsUriConstants.L6_TRY_URI)));
        return out;
    }

    private LinkedList<FabContainer> lessonSevenInitialFabs() {
        LinkedList<FabContainer> out = new LinkedList<>();
        out.addLast(new FabContainer(0, R.id.contact_and_license_fab, baseFabAction(R.string._l7_t1_snackbar_message)));

        out.addLast(new FabContainer(1, R.id.license_fab, new WebIntentOnClick(LessonsUriConstants.L7_VIEW_LEGAL_INFO_ONLINE)));
        out.addLast(new FabContainer(2, R.id.follow_fab, new WebIntentOnClick(LessonsUriConstants.L7_FOLLOW)));
        out.addLast(new FabContainer(3, R.id.mail_fab, new MailIntentOnClick(LessonsUriConstants.L7_MAIL, getString(R.string._menu_mail_header), "")));
        return out;
    }

    private View.OnClickListener baseFabAction(final int snackbarMessageId) {

        return new FabOnClickListenerFabMenuStateSpecific(snackbarMessageId) {

            @Override
            public void onClick(View v) {
                if(!isFabMenuOpen)
                    showSnackbar(snackbarResId);
                else
                    dismissSnackbarIfOpen();
            }
        };
    }

    class ShareIntentOnClick implements View.OnClickListener {

        private final String uri;
        private final String subject;
        private final String shareChooserTitle;

        public ShareIntentOnClick(String uri, String subject, String shareChooserTitle) {
            this.uri = uri;
            this.subject = subject;
            this.shareChooserTitle = shareChooserTitle;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            fireShareLinkIntent(uri, subject, shareChooserTitle);
        }
    }

    class MailIntentOnClick implements View.OnClickListener {
        private final String address;
        private final String subject;
        private final String message;

        public MailIntentOnClick(String address, String subject, String message) {
            this.address = address;
            this.subject = subject;
            this.message = message;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            fireMailIntent(address, subject, message);
        }
    }

    class WebIntentOnClick implements View.OnClickListener {
        private final String uri;

        WebIntentOnClick(String uri) {
            this.uri = uri;
        }
        @Override
        public void onClick(View v) {
            fireWebIntent(uri);
        }
    }

    class WebDialogOnClick implements View.OnClickListener {

        private final String uri;

        WebDialogOnClick(String uri) {
            this.uri = uri;
        }
        @Override
        public void onClick(View v) {
            showWebViewDialog(uri);
        }
    }
}
