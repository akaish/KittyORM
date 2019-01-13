
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

package net.akaish.kittyormdemo.sqlite.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.akaish.kittyormdemo.R;

import java.util.Random;

/**
 * Created by akaish on 29.07.18.
 * @author akaish (Denis Bogomolov)
 */

public enum SomeColours {

    WHITE ("WHITE"),
    BLACK ("BLACK"),
    RED ("RED"),
    ORANGE ("ORANGE"),
    YELLOW ("YELLOW"),
    GREEN ("GREEN"),
    LIGHT_BLUE ("LIGHT_BLUE"),
    BLUE ("BLUE"),
    PURPLE ("PURPLE");

    SomeColours(String colourName) {
        this.colourName = colourName;
    }

    private final String colourName;

    public boolean equalsName(String sqlText) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return this.colourName.equals(sqlText);
    }

    public static final int getSomeColoursBitmapResource(SomeColours colour) {
        switch (colour) {
            case RED:
                return R.drawable.c_red;
            case BLUE:
                return R.drawable.c_blue;
            case BLACK:
                return R.drawable.c_black;
            case GREEN:
                return R.drawable.c_green;
            case WHITE:
                return R.drawable.c_white;
            case ORANGE:
                return R.drawable.c_orange;
            case PURPLE:
                return R.drawable.c_purple;
            case YELLOW:
                return R.drawable.c_yellow;
            case LIGHT_BLUE:
                return R.drawable.c_light_blue;
        }
        return -1;
    }

    public static final Bitmap getSomeColourBitmpap(int colourResource, Context ctx) {
        return BitmapFactory.decodeResource(ctx.getResources(), colourResource);
    }

    public static final SomeColours rndColour(Random rnd) {
        switch (rnd.nextInt(9)) {
            case 0:
                return WHITE;
            case 1:
                return BLACK;
            case 2:
                return RED;
            case 3:
                return ORANGE;
            case 4:
                return YELLOW;
            case 5:
                return GREEN;
            case 6:
                return LIGHT_BLUE;
            case 7:
                return BLUE;
            case 8:
                return PURPLE;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.colourName;
    }
}
