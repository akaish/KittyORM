
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

package net.akaish.kittyormdemo.fabmenu;

import android.view.View;

/**
 * Created by akaish on 07.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class HostFabOnclickListener implements View.OnClickListener {

    private final View.OnClickListener nested;
    private final FabActivityI fabActivityI;

    public HostFabOnclickListener(View.OnClickListener nested, FabActivityI fabActivityI) {
        this.nested = nested;
        this.fabActivityI = fabActivityI;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(nested != null) {
            if(nested instanceof FabOnClickListenerFabMenuStateSpecific) {
                ((FabOnClickListenerFabMenuStateSpecific) nested).setFabMenuOpen(fabActivityI.isFabOpen());
            }
        }
        if(fabActivityI.isFabOpen()) {
            fabActivityI.closeFabMenu();
            if(nested != null) {
                nested.onClick(v);
            }
        } else {
            fabActivityI.showFabMenu();
            if(nested != null) {
                nested.onClick(v);
            }
        }
    }
}
