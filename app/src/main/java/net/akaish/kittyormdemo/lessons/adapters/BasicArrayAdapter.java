
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

package net.akaish.kittyormdemo.lessons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.akaish.kittyormdemo.R;

import java.util.LinkedList;

/**
 * Created by akaish on 22.08.18.
 * @author akaish (Denis Bogomolov)
 */

public class BasicArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final LinkedList<String> values;

    public BasicArrayAdapter(Context context, LinkedList<String> values) {
        super(context, R.layout.lesson_log_list, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.lesson_log_list, parent, false);
        TextView textView = rowView.findViewById(R.id.lesson_log_list_item);
        textView.setText(values.get(position));

        return rowView;
    }

    public void addItemLast(String item) {
        values.addLast(item);
    }

    public void addItemFirst(String item) {
        values.addFirst(item);
    }
}
