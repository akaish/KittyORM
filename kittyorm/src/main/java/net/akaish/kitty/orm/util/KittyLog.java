
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.util;

import android.util.Log;

/**
 * Created by akaish on 07.07.2018.
 * @author akaish (Denis Bogomolov)
 */

public class KittyLog {

    public enum LOG_LEVEL {
        D, E, W, I, V, WTF
    }

    public static void kLog(LOG_LEVEL level, String tag, String msg, Throwable tr) {
            switch(level) {
                case E:
                    Log.e(tag, msg, tr);
                    break;
                case D:
                    Log.d(tag, msg, tr);
                    break;
                case I:
                    Log.i(tag, msg, tr);
                    break;
                case V:
                    Log.v(tag, msg, tr);
                    break;
                case W:
                    Log.w(tag, msg, tr);
                    break;
                case WTF:
                    Log.wtf(tag, msg, tr);
                    break;
                default:
                    Log.wtf(tag, msg, tr);
                    break;
            }

    }
}
