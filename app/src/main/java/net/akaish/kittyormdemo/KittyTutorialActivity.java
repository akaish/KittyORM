
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

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.akaish.kittyormdemo.fabmenu.FabActivityI;
import net.akaish.kittyormdemo.fabmenu.FabAnimationOpenListener;
import net.akaish.kittyormdemo.fabmenu.HostFabOnclickListener;
import net.akaish.kittyormdemo.lessons.FabContainer;
import net.akaish.kittyormdemo.lessons.InAppTutorialSitePathHelper;
import net.akaish.kittyormdemo.lessons.KittyWebDialog;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import static net.akaish.kittyormdemo.Constants.LOG_TAG_APPLICATION;
import static net.akaish.kittyormdemo.fabmenu.FabMenuConstants.FAB_MENU_ANIMATION_DIMENSIONS_STEPS;

/**
 * Created by akaish on 06.11.18.
 * @author akaish (Denis Bogomolov)
 */

public abstract class KittyTutorialActivity extends AppCompatActivity implements FabActivityI {

    private KittyWebDialog wvDialog;
    private Dialog warningDialog;

    protected ViewGroup fabCoordinatorParentView;


    protected boolean isFABOpenI;

    protected Snackbar snackbar;

    protected LinkedList<FloatingActionButton> fabMenus;
    protected LinkedList<LinearLayout> fabMenuItemsLayouts;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public boolean twoPane;

    public boolean initialFragmentLoadedByActivity = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void setFabCoordinatorParentView();

    public void showWebViewDialog(String pageName) {
        wvDialog = new KittyWebDialog(this);
        wvDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        wvDialog.setContentView(R.layout.web_view_custom_dialog);
        WebView dialogwebView = wvDialog.findViewById(R.id.custom_dialog_web_view);
        dialogwebView.loadUrl(InAppTutorialSitePathHelper.getPathToLocalizedInAppTutorialSitePage(pageName, Locale.getDefault().getLanguage(), this, LOG_TAG_APPLICATION));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wvDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        wvDialog.show();

        wvDialog.getWindow().setAttributes(lp);
    }

    public void showWarningDialog(int titleRes, int warningRes, int okButtonTextRes) {
        warningDialog = new Dialog(this);
        warningDialog.setContentView(R.layout.warning_dialog);
        TextView title = warningDialog.findViewById(R.id.warning_dialog_title);
        title.setText(titleRes);
        TextView warning = warningDialog.findViewById(R.id.warning_dialog_text);
        warning.setText(warningRes);
        Button ok = warningDialog.findViewById(R.id.warning_dialog_ok_button);
        ok.setText(okButtonTextRes);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(warningDialog!=null) warningDialog.dismiss();
            }
        });
        warningDialog.show();
    }

    @Override
    public boolean isFabOpen() {
        return isFABOpenI;
    }

    @Override
    public void setFabMenuState(boolean isOpen) {
        this.isFABOpenI = isOpen;
    }

    @Override
    public void showFabMenu() {
        this.setFabMenuState(true);
        if(fabMenus == null) return;
        if(fabMenus.size() < 2) return;
        fabCoordinatorParentView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkTransparent));
        fabCoordinatorParentView.setClickable(true);
        fabMenus.getFirst().animate().rotation(180);

        Iterator<LinearLayout> fabLayoutsIterator = fabMenuItemsLayouts.iterator();

        int step = 0; long animationDelay = 200l;

        while (fabLayoutsIterator.hasNext()) {
            LinearLayout fabLayout = fabLayoutsIterator.next();
            fabLayout.animate().translationY(-getResources().getDimension(FAB_MENU_ANIMATION_DIMENSIONS_STEPS[step])).setListener(
                    new FabAnimationOpenListener(fabLayout, true)
            ).setStartDelay(animationDelay).setDuration(200l);
            animationDelay+=200l;
            step++;
        }
    }

    public void notifyToChangeFabMenu(Integer fabResource, LinkedList<FabContainer> actions, LinkedList<Integer> fabLayouts) {
        fabCoordinatorParentView.removeAllViews();
        if (actions == null) {
            setFabMenuState(false);
            return;
        }
        int newFabResource = fabResource;
        fabCoordinatorParentView.addView(getLayoutInflater().inflate(newFabResource, fabCoordinatorParentView, false), 0);

        Iterator<FabContainer> fabContainerIterator = actions.iterator();
        fabMenus = new LinkedList<>();
        Iterator<Integer> fabLayoutsIterator = fabLayouts.iterator();
        fabMenuItemsLayouts = new LinkedList<>();
        boolean hostFab = true;
        while (fabContainerIterator.hasNext()) {
            FabContainer fabContainer = fabContainerIterator.next();
            FloatingActionButton fab = (FloatingActionButton) fabCoordinatorParentView.findViewById(fabContainer.getResourceId());
            if(hostFab) {
                hostFab = false;
                HostFabOnclickListener hostFabOnclickListener = new HostFabOnclickListener(fabContainer.getOnClickListener(), this);
                fab.setOnClickListener(hostFabOnclickListener);
            } else {
                fab.setOnClickListener(fabContainer.getOnClickListener());
            }
            fabMenus.addLast(fab);
        }
        while (fabLayoutsIterator.hasNext()) {
            LinearLayout fabLayout = (LinearLayout) findViewById(fabLayoutsIterator.next().intValue());
            fabMenuItemsLayouts.addLast(fabLayout);
        }
    }

    public void dismissSnackbarIfOpen() {
        if(snackbar != null)
            snackbar.dismiss();
    }

    public void showSnackbar(int textId) {
        dismissSnackbarIfOpen();
        if(twoPane) {
            snackbar = Snackbar.make(findViewById(R.id.lesson_list_fab_parent), getText(textId), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null);
        } else {
            snackbar = Snackbar.make(findViewById(R.id.lesson_detail_fab_parent), getText(textId), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null);
        }
        snackbar.show();
    }

    public void fireShareLinkIntent(String uri, String subject, String shareChooserTitle) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        share.putExtra(Intent.EXTRA_TEXT, uri);

        startActivity(Intent.createChooser(share, shareChooserTitle));
    }

    public void fireWebIntent(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    public void fireMailIntent(String address, String subject, String message) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ address});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, getString(R.string._menu_choose_mail_client)));
    }

    @Override
    public void closeFabMenu() {
        this.setFabMenuState(false);
        if(fabMenus == null) return;
        if(fabMenus.size() < 2) return;
        fabCoordinatorParentView.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        fabCoordinatorParentView.setClickable(false);


        Iterator<LinearLayout> fabLayoutsIterator = fabMenuItemsLayouts.iterator();

        while (fabLayoutsIterator.hasNext()) {
            LinearLayout fabLayout = fabLayoutsIterator.next();
            fabLayout.setVisibility(View.GONE);
            fabLayout.setTranslationY(0f);
        }

        fabMenus.getFirst().animate().rotation(0f);
    }
}
