
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

package net.akaish.kittyormdemo.lessons;

import android.view.View;

/**
 * Created by akaish on 07.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class FabContainer {
    final int weight;
    final int resourceId;
    final View.OnClickListener onClickListener;

    public FabContainer(int weight, int resourceId, View.OnClickListener onClickListener) {
        this.weight = weight;
        this.resourceId = resourceId;
        this.onClickListener = onClickListener;
    }

    public int getWeight() {
        return weight;
    }

    public int getResourceId() {
        return resourceId;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
