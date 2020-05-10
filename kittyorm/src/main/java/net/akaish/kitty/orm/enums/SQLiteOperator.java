
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.enums;

/**
 * @author akaish (Denis Bogomolov)
 */
public enum SQLiteOperator {

	AND ("AND"),
	OR ("OR"),
	BETWEEN ("BETWEEN"),
	IN ("IN"),
	OPEN_SUBC ("("),
	CLOSE_SUBC (")"),
	GREATER_THAN(">"),
	GREATER_OR_EQUAL(">="),
	LESS_THAN ("<"),
	LESS_OR_EQUAL("<="),
	EQUAL("="),
	NOT_EQUAL("!="),
	IS("IS"),
	IS_NOT("IS NOT"),
	MULTIPLY("*"),
	DIVIDE("/"),
	PLUS("+"),
	MINUS("-"),
	MODULO("%"),
	LIKE("LIKE"),
	MATCH("MATCH"),
	GLOB("GLOB");
	
	private final String sqltext;
	
	private SQLiteOperator(String sqlTextRep) {
		sqltext = sqlTextRep;
	}
	
    public boolean equalsName(String sqlText) {
        return sqltext.equals(sqlText);
    }

    public static SQLiteOperator fromString(String sqltext) {
		if(sqltext.equalsIgnoreCase("="))
			return EQUAL;
		if(sqltext.equalsIgnoreCase(">="))
			return GREATER_OR_EQUAL;
		if(sqltext.equalsIgnoreCase("=>"))
			return GREATER_OR_EQUAL;
		if(sqltext.equalsIgnoreCase("<="))
			return LESS_OR_EQUAL;
		if(sqltext.equalsIgnoreCase("=<"))
			return LESS_OR_EQUAL;
		if(sqltext.equalsIgnoreCase("!="))
			return NOT_EQUAL;
		if(sqltext.equalsIgnoreCase(">"))
			return GREATER_THAN;
		if(sqltext.equalsIgnoreCase("<"))
			return LESS_THAN;
		if(sqltext.equalsIgnoreCase("("))
			return OPEN_SUBC;
		if(sqltext.equalsIgnoreCase(")"))
			return CLOSE_SUBC;
		if(sqltext.equalsIgnoreCase("*"))
			return MULTIPLY;
		if(sqltext.equalsIgnoreCase("/"))
			return DIVIDE;
		if(sqltext.equalsIgnoreCase("+"))
			return PLUS;
		if(sqltext.equalsIgnoreCase("-"))
			return MINUS;
		if(sqltext.equalsIgnoreCase("%"))
			return MODULO;
		return valueOf(sqltext);
	}

    @Override
    public String toString() {
       return this.sqltext;
    }
}
