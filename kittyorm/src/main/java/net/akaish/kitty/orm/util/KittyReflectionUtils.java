
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

package net.akaish.kitty.orm.util;

import android.net.Uri;

import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import static java.text.MessageFormat.format;
import static net.akaish.kitty.orm.util.KittyConstants.NULL;

/**
 * Created by akaish on 09.04.18.
 * @author akaish (Denis Bogomolov)
 */

public class KittyReflectionUtils {

	private static String KITTY_RNTE_UNABLE_TO_GET_STR_REPRESENT = "Unable to get String representation of {0}.{1} ! Type {2} not supported by ReflectionUtils#getStringRepresentationOfModelField!";

	/**
	 * Returns string representation of provided field for provided kitty model object if possible.
	 * @param model
	 * @param fieldName
	 * @param <M>
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static <M extends KittyModel> String getStringRepresentationOfModelField(M model, String fieldName)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = model.getClass().getField(fieldName);
		field.setAccessible(true);
		Class fieldType = field.getType();

		if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
			Boolean blv = (Boolean) field.get(model);
			if (blv != null)
				return Integer.toString(blv ? 1 : 0);
			return null;
		} else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
			Integer intV = (Integer) field.get(model);
			if (intV != null)
				return Integer.toString(intV);
			return null;
		} else if (byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
			Byte bv = (Byte) field.get(model);
			if (bv != null)
				return Byte.toString(bv);
			return null;
		} else if (double.class.equals(fieldType) || Double.class.equals(fieldType)) {
			Double dv = (Double) field.get(model);
			if (dv != null)
				return Double.toString(dv);
			return null;
		} else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
			Long lv = (Long) field.get(model);
			if (lv != null)
				return Long.toString(lv);
			return null;
		} else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
			Short sv = (Short) field.get(model);
			if (sv != null)
				return Short.toString(sv);
			return null;
		} else if (float.class.equals(fieldType) || Float.class.equals(fieldType)) {
			Float fv = (Float) field.get(model);
			if (fv != null)
				return Float.toString(fv);
			return null;
		} else if (String.class.equals(fieldType)) {
			return (String) field.get(model);
		} else if (BigDecimal.class.equals(fieldType)) {
			BigDecimal bdv = (BigDecimal) field.get(model);
			if (bdv != null)
				return bdv.toString();
			return null;
		} else if (BigInteger.class.equals(fieldType)) {
			BigInteger biv = (BigInteger) field.get(model);
			if (biv != null)
				return biv.toString();
			return null;
		} else if (fieldType.isEnum()) {
			Enum ev = (Enum) field.get(model);
			if (ev != null)
				return ev.name();
			return null;
		} else if (Calendar.class.equals(fieldType)) {
			Calendar cal = (Calendar) field.get(model);
			if (cal != null)
				return Long.toString(cal.getTimeInMillis());
			return null;
		} else if (Date.class.equals(fieldType)) {
			Date dat = (Date) field.get(model);
			if (dat != null)
				return Long.toString(dat.getTime());
		} else if (Timestamp.class.equals(fieldType)) {
			Timestamp tsp = (Timestamp) field.get(model);
			if (tsp != null)
				return Long.toString(tsp.getTime());
		}
			throw new KittyRuntimeException(format(KITTY_RNTE_UNABLE_TO_GET_STR_REPRESENT,
					model.getClass().getCanonicalName(), field.getName(), fieldType.getCanonicalName()));
	}

	public static final String objectToString(Object object) {
		if (object == null) return NULL;
		if (String.class.equals(object.getClass())) {
	    } else if (BigDecimal.class.equals(object.getClass())) {
			return ((BigDecimal)object).toString();
		} else if (BigInteger.class.equals(object.getClass())) {
			return ((BigInteger)object).toString();
		} else if (object.getClass().isEnum()) {
			return ((Enum)object).name();
		} else if (Calendar.class.equals(object.getClass())) {
			return Long.toString(((Calendar) object).getTimeInMillis());
		} else if (Date.class.equals(object.getClass())) {
			return Long.toString(((Date) object).getTime());
		} else if (Timestamp.class.equals(object.getClass())) {
			return Long.toString(((Timestamp) object).getTime());
		} else if (File.class.equals(object.getClass())) {
			return ((File) object).getAbsolutePath();
		} else if (Uri.class.equals(object.getClass())) {
			return object.toString();
		} else if (Currency.class.equals(object.getClass())) {
			return ((Currency) object).getCurrencyCode();
		}
		return NULL;
	}

	private static final String KITTY_RNTE_TO_STRING_NOT_DEFINED = "There is no default mapping to SQLite string defined for java type {0} (KittyORM, KittyReflectionUtils.getSQLiteStringRepresentation(Object object))!";

	public static final String getSQLiteStringRepresentation(Object object) {
		if(object == null) return NULL;
		Class objClass = object.getClass();
		if (boolean.class.equals(objClass) || Boolean.class.equals(objClass)) {
			return Integer.toString((Boolean) object ? 1 : 0);
		} else if (int.class.equals(objClass) || Integer.class.equals(objClass)) {
			return Integer.toString((Integer)object);
		} else if (byte.class.equals(objClass) || Byte.class.equals(objClass)) {
			return Byte.toString((Byte) object);
		} else if (double.class.equals(objClass) || Double.class.equals(objClass)) {
			return Double.toString((Double)object);
		} else if (long.class.equals(objClass) || Long.class.equals(objClass)) {
			return Long.toString((Long)object);
		} else if (short.class.equals(objClass) || Short.class.equals(objClass)) {
			return Short.toString((Short)object);
		} else if (float.class.equals(objClass) || Float.class.equals(objClass)) {
			return Float.toString((Float)object);
		} else if (String.class.equals(objClass)) {
			return (String) object;
		} else if (BigDecimal.class.equals(objClass)) {
			return object.toString();
		} else if (BigInteger.class.equals(objClass)) {
			return object.toString();
		} else if (objClass.isEnum()) {
			return ((Enum) object).name();
		} else if (Calendar.class.equals(objClass)) {
			return Long.toString(((Calendar) object).getTimeInMillis());
		} else if (Date.class.equals(objClass)) {
			return Long.toString(((Date) object).getTime());
		} else if (Timestamp.class.equals(objClass)) {
			return Long.toString(((Timestamp) object).getTime());
		} else if (File.class.equals(objClass)) {
			return ((File) object).getAbsolutePath();
		} else if (Uri.class.equals(objClass)) {
			return object.toString();
		} else if (Currency.class.equals(objClass)) {
			return ((Currency) object).getCurrencyCode();
		}
		throw new KittyRuntimeException(format(KITTY_RNTE_TO_STRING_NOT_DEFINED, objClass.getCanonicalName()));
	}

	/**
	 * Returns accessible field of object
	 * @param obj
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static final Field getField(Object obj, String fieldName) throws NoSuchFieldException {
		Field field = obj.getClass().getField(fieldName);
		field.setAccessible(true);
		return field;
	}
}
