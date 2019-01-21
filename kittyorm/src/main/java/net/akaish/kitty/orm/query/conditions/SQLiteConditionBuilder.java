
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
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm.query.conditions;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.annotations.column.KITTY_COLUMN;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;
import net.akaish.kitty.orm.util.KittyReflectionUtils;

import static net.akaish.kitty.orm.util.KittyConstants.*;

import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * @author akaish (Denis Bogomolov)
 */
public class SQLiteConditionBuilder {
	private StringBuffer condition = new StringBuffer(256);
	private LinkedList<String> selectionArguments = new LinkedList<String>();
	
	/**
	 * Makes this builder ready for new sql condition
	 * If you are crazy about memory usage optimization,
	 * use {@link SQLiteConditionBuilder#newSQLiteCondition(int)}
	 * for using buffer capacity you actually need
	 */
	public void newSQLiteCondition() {
		condition = new StringBuffer(256);
		selectionArguments = new LinkedList<String>();
	}
	
	/**
	 * Makes this builder ready for new sql condition
	 * @param conditionStringBufferCapacity - size of {@link StringBuffer} that would
	 * be used 
	 */
	public void newSQLiteCondition(int conditionStringBufferCapacity) {
		condition = new StringBuffer(conditionStringBufferCapacity);
		selectionArguments = new LinkedList<String>();
	}
	
	/**
	 * Adds column name associated with fieldName via {@link KITTY_COLUMN} annotation
	 * May throw {@link KittyRuntimeException}, which is just runtime exception,
	 * if provided fieldName doesn't exists in provided modelClass or if
	 * provided fieldName doesn't described with {@link KITTY_COLUMN} annotation
	 * @param fieldName
	 * @param modelClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public SQLiteConditionBuilder addColumn(String fieldName, Class modelClass) {
		try {
			Field field = modelClass.getField(fieldName);
			field.setAccessible(true);
			if(!field.isAnnotationPresent(KITTY_COLUMN.class)) {
				throw new KittyRuntimeException("There is no KITTY_COLUMN annotation present for field with name "
						+fieldName+" at modelClass class "+modelClass.getCanonicalName());
			}
			if(condition.length()!=0) condition.append(WHITESPACE);
			condition.append(field.getAnnotation(KITTY_COLUMN.class).columnName());
			return this;
		} catch(NoSuchFieldException e) {
			// rethrowing exception
			throw new KittyRuntimeException("There is no field with name "+fieldName+" at modelClass class "+modelClass.getCanonicalName(), e);
		}
	}
	
	/**
	 * Adds some logic operators for SQL condition, described in {@link SQLiteOperator}
	 * {@link SQLiteOperator} has only common used operators, so, for something rare for
	 * common logic use {@link SQLiteConditionBuilder#addArbitrarySQLExpression(String)}
	 * @param logic
	 * @return
	 */
	public SQLiteConditionBuilder addSQLOperator(SQLiteOperator logic) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(logic.toString());
		return this;
	}

	/**
	 * Adds some logic operators for SQL condition, described in {@link SQLiteOperator}
	 * {@link SQLiteOperator} has only common used operators, so, for something rare for
	 * common logic use {@link SQLiteConditionBuilder#addArbitrarySQLExpression(String)}
	 * <br> Case insensitive, so "and" and "AND" and even "AnD" is OK
	 * @param logic
	 * @return
	 */
	public SQLiteConditionBuilder addSQLOperator(String logic) {
		return addSQLOperator(SQLiteOperator.valueOf(logic.toUpperCase()));
	}
	
	/**
	 * Adds column name to condition (e.g. ), if you want to use Java variables described
	 * with {@link KITTY_COLUMN}, use {@link SQLiteConditionBuilder#addColumn(String, Class)} instead
	 * @param columnName
	 * @return
	 */
	public SQLiteConditionBuilder addColumn(String columnName) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(columnName);
		return this;
	}
	
	/**
	 * Adds any arbitrary item to condition
	 * @param ArbitrarySQLExp
	 * @return
	 */
	public SQLiteConditionBuilder addArbitrarySQLExpression(String ArbitrarySQLExp) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(ArbitrarySQLExp);
		return this;
	}
	
	public SQLiteConditionBuilder addValue(String value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(value);
		return this;
	}
	
	public SQLiteConditionBuilder addValue(Boolean value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Integer.toString(value.booleanValue() ? 1 : 0));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(boolean value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Integer.toString(value ? 1 : 0));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(int value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Integer.toString(value));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(Integer value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(value.toString());
		return this;
	}
	
	public SQLiteConditionBuilder addValue(byte value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Integer.toString((int) value));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(Byte value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Integer.toString(value.intValue()));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(double value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Double.toString(value));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(Double value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(value.toString());
		return this;
	}
	
	public SQLiteConditionBuilder addValue(long value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Long.toString(value));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(Long value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(value.toString());
		return this;
	}
	
	public SQLiteConditionBuilder addValue(short value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Short.toString(value));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(Short value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(value.toString());
		return this;
	}

	public SQLiteConditionBuilder addValue(float value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(Float.toString(value));
		return this;
	}
	
	public SQLiteConditionBuilder addValue(Float value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(value.toString());
		return this;
	}

	public SQLiteConditionBuilder addObjectValue(Object value) {
		if(condition.length()!=0) condition.append(WHITESPACE);
		condition.append(QSIGN);
		selectionArguments.add(KittyReflectionUtils.objectToString(value));
		return this;
	}
	
	/**
	 * Returns sql condition String set with setters
	 * @return
	 */
	public String getConditionSQLString() {
		return condition.toString();
	}
	
	/**
	 * Returns string values to be used in prepared sql condition
	 * @return
	 */
	public String[] getConditionValues() {
		return selectionArguments.toArray(new String[selectionArguments.size()]);
	}
	
	/**
	 * Resets builder and return SQL condition
	 * @return
	 */
	public SQLiteCondition build() {
		return new SQLiteCondition(condition.toString(), getConditionValues());
	}

	public static final String WHERE = "where ";
	public static final String MODEL_FIELD_START = "#?";
	public static final String MODEL_FIELD_END = ";";
	public static final String WHSP_STR = " ";

	/**
	 * Generates {@link SQLiteCondition} from SQLite string and provided params.
	 * <br> Example:
	 * <br> fromSQL("column_name_one = ? AND id = ?", null, columnNameOneValue, id)
	 * <br> If you wish use model fieldName, you may use following syntax:
	 * <br> fromSQL("#?columnNameOne; = ? AND id = ?", MyModelImplementation.class, columnNameOneValue, id)
	 * <br> In this case passing not null modelClass parameter is required.
	 * @param conditionStr SQLite condition string with ? placeholders for parameters
	 * @param modelClass model class, required if you pass model field name instead column names using #?modelField; syntax
	 * @param params parameters to be used with query
	 * @return {@link SQLiteCondition} from SQLite string and provided params.
	 */
	public static final SQLiteCondition fromSQL(String conditionStr, Class<? extends KittyModel> modelClass, Object... params) {
		// Getting str collection for parameters
		LinkedList<String> conditionArgs = new LinkedList<String>();
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				conditionArgs.addLast(KittyReflectionUtils.objectToString(params[i]));
			}
		}
		String[] arguments = conditionArgs.toArray(new String[conditionArgs.size()]);
		// OK, trim and check if string condition starts with WHERE ignore case
		conditionStr = conditionStr.trim();
		if(conditionStr.length() >= WHERE.length()) {
			String firstChars = conditionStr.substring(0, WHERE.length() - 1);
			if(firstChars.toLowerCase().startsWith(WHERE))
				conditionStr.replace(firstChars, EMPTY_STRING);
		}
		// Second step, we do the following thing: check occurrences of model field names (starts with #? and ends with ;)
		if(conditionStr.contains(MODEL_FIELD_START)) {
			String[] parts = conditionStr.split(WHSP_STR);
			StringBuilder rebuildCondition = new StringBuilder();
			for(String part : parts) {
				String trimmedPart = part.trim();
				if(trimmedPart == null) continue;
				if(trimmedPart.equals(WHSP_STR)) continue;
				if(trimmedPart.equals(EMPTY_STRING)) continue;
				if(rebuildCondition.length() > 0) rebuildCondition.append(WHITESPACE);
				if(trimmedPart.startsWith(MODEL_FIELD_START)) {
					if(modelClass == null)
						throw new NullPointerException("modelClass param can't be null if you specify #?fieldName; in conditionStr");
					String fieldName = trimmedPart.replaceAll(MODEL_FIELD_START, EMPTY_STRING).replaceAll(MODEL_FIELD_END, EMPTY_STRING);
					try {
						Field field = modelClass.getField(fieldName);
						field.setAccessible(true);
						if(!field.isAnnotationPresent(KITTY_COLUMN.class)) {
							throw new KittyRuntimeException("There is no KITTY_COLUMN annotation present for field with name "
									+fieldName+" at modelClass class "+modelClass.getCanonicalName());
						}
						rebuildCondition.append(field.getAnnotation(KITTY_COLUMN.class).columnName());
					} catch(NoSuchFieldException e) {
						// rethrowing exception
						throw new KittyRuntimeException("There is no field with name "+fieldName+" at modelClass class "+modelClass.getCanonicalName(), e);
					}
				} else {
					rebuildCondition.append(trimmedPart);
				}
			}
			return new SQLiteCondition(rebuildCondition.toString(), arguments);
		}
		return new SQLiteCondition(conditionStr.toString(), arguments);
	}
}
