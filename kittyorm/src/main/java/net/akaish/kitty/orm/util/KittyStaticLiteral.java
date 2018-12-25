
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

import java.util.LinkedList;

/**
 * Class for getting literals static linked list in order to use in queries with joins
 * Created by akaish on 02.03.18.
 * @author akaish (Denis Bogomolov)
 */
public class KittyStaticLiteral {

    private static final LinkedList<Character> LITERALS = new LinkedList<>();

    static {
        LITERALS.add(new Character('A'));
        LITERALS.add(new Character('B'));
        LITERALS.add(new Character('C'));
        LITERALS.add(new Character('D'));
        LITERALS.add(new Character('E'));
        LITERALS.add(new Character('F'));
        LITERALS.add(new Character('G'));
        LITERALS.add(new Character('H'));
        LITERALS.add(new Character('I'));
        LITERALS.add(new Character('J'));
        LITERALS.add(new Character('K'));
        LITERALS.add(new Character('L'));
        LITERALS.add(new Character('M'));
        LITERALS.add(new Character('N'));
        LITERALS.add(new Character('O'));
        LITERALS.add(new Character('P'));
        LITERALS.add(new Character('Q'));
        LITERALS.add(new Character('R'));
        LITERALS.add(new Character('S'));
        LITERALS.add(new Character('T'));
        LITERALS.add(new Character('U'));
        LITERALS.add(new Character('V'));
        LITERALS.add(new Character('W'));
        LITERALS.add(new Character('X'));
        LITERALS.add(new Character('Y'));
        LITERALS.add(new Character('Z'));
        // Now I know the alphabet
    }


    /**
     * Returns collection ({@link LinkedList}) of literals from A to Z
     * @return
     */
    public static final LinkedList<Character> getLiteralList() {
        return (LinkedList<Character>) LITERALS.clone();
    }
}
