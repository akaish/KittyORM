
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

package net.akaish.kittyormdemo.lessons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kittyormdemo.R;
import net.akaish.kittyormdemo.sqlite.basicdb.IndexesAndConstraintsModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by akaish on 10.10.18.
 * @author akaish (Denis Bogomolov)
 */

public class MigAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final LinkedList<String> values = new LinkedList<>();

    public MigAdapter(Context context, List<String> values) {
        super(context, R.layout.lesson_log_list, values);
        this.context = context;
        this.values.addAll(values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.lesson_crnd_list_item, parent, false);
        TextView textView = rowView.findViewById(R.id.crnd_item_model);
        textView.setText(values.get(position).toString());

        return rowView;
    }

    public void addItemLast(String item) {
        values.addLast(item);
    }

    public void addItemFirst(String item) {
        values.addFirst(item);
    }

    public void addAll(List<String> items) {
        values.addAll(items);
    }
}
