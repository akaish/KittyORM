
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

import android.animation.Animator;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by akaish on 06.11.18.
 * @author akaish (Denis Bogomolov)
 */
public class FabAnimationOpenListener implements Animator.AnimatorListener {
    private final LinearLayout layout;
    private final boolean isFABOpenA;
    private boolean hotfix_via_hack = false;

    public FabAnimationOpenListener(LinearLayout layout, boolean isFABOpen) {
        this.layout = layout;
        this.isFABOpenA = isFABOpen;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (isFABOpenA && !hotfix_via_hack) {
            layout.setVisibility(View.VISIBLE);
            hotfix_via_hack = !hotfix_via_hack;
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!isFABOpenA && !hotfix_via_hack) {
            layout.setVisibility(View.GONE);
            hotfix_via_hack = !hotfix_via_hack;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}
