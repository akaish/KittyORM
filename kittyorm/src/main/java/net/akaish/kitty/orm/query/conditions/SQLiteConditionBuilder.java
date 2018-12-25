
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

package net.akaish.kitty.orm.query.conditions;

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
	public SQLiteConditionBuilder addField(String fieldName, Class modelClass) {
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
	 * Adds column name to condition (e.g. ), if you want to use Java variables described
	 * with {@link KITTY_COLUMN}, use {@link SQLiteConditionBuilder#addField(String, Class)} instead
	 * @param columnName
	 * @return
	 */
	public SQLiteConditionBuilder addField(String columnName) {
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
		selectionArguments.add(KittyReflectionUtils.getStringRepresentationOfObject(value));
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
}
