
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

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import net.akaish.kitty.orm.enums.TypeAffinities;
import net.akaish.kitty.orm.exceptions.KittyRuntimeException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static net.akaish.kitty.orm.util.KittyConstants.*;
import static net.akaish.kitty.orm.util.KittyNamingUtils.ASSETS_URI_START;

/**
 * @author akaish (Denis Bogomolov)
 */
public class KittyUtils {
	
	/**
	 * Implodes provided array's value in string with provided separator.
	 * Returns first element in array without changes if array's size = 1.
	 * Returns null it empty array or null array.
	 * @param toImplode
	 * @param separator
	 * @return
	 */
	public static String implode(String[] toImplode, String separator) {
		if(toImplode == null) return null;
		if(toImplode.length == 1) return toImplode[0];
		if(toImplode.length == 0) return null;
		int strBufferCapacity = 0;
		for(int i=0; i<toImplode.length; i++) {
			strBufferCapacity+=toImplode[i].length();
			strBufferCapacity+=separator.length();
		}
		StringBuffer sb = new StringBuffer(strBufferCapacity);
		for(int i=0;i<toImplode.length-1;i++) {
			sb.append(toImplode[i]);
			sb.append(separator);
		}
		sb.append(toImplode[toImplode.length-1]);
		return sb.toString();
	}

	public static boolean endsWithIgnoreCase(String str, String suffix) {
		int suffixLength = suffix.length();
		return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
	}

	public static String implode(String[] toImlpode, char separatorChar) {
		return implode(toImlpode, Character.toString(separatorChar));
	}

	/**
	 * Implodes elements of provided array into string with comma + space separator and surrounded
	 * <br> with bkt's. If array null or empty than empty string would be returned.
	 * @param toImplode
	 * @return
	 */
	public static String implodeWithCommaInBKT(String[] toImplode) {
		if(toImplode == null) return EMPTY_STRING;
		if(toImplode.length > 0) {
			StringBuffer sb = new StringBuffer(32);
			sb.append(LEFT_BKT);
			sb.append(implode(toImplode, COMMA_SEPARATOR));
			sb.append(RIGHT_BKT);
			return sb.toString();
		} else {
			return EMPTY_STRING;
		}
	}

	/**
	 * Implodes elements of provided list into string with comma + space separator and surrounded
	 * <br> with bkt's. If array null or empty than empty string would be returned.
	 * @param toImplode
	 * @return
	 */
	public static String implodeWithCommaInBKT(List<Object> toImplode) {
		String[] toImplodeS = new String[toImplode.size()];
		int counter = 0;
		for(Object o : toImplode) {
			toImplodeS[counter] = o.toString();
			counter++;
		}
		return implodeWithCommaInBKT(toImplodeS);
	}

	/**
	 * Simple method to remove duplicate spaces from statements
	 * @param generatedStatement
	 * @return
	 */
	public static final String statementPrettyPrinting(String generatedStatement) {
		String toPrint = generatedStatement.trim();
		if(toPrint.length()==0) return "";
		StringBuffer out = new StringBuffer(toPrint.length());
		out.append(toPrint.charAt(0));
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		for(int i = 1; i < toPrint.length(); i++) {
			if(i == toPrint.length() - 1) {
				out.append(toPrint.charAt(toPrint.length()-1));
				break;
			}
			if(toPrint.charAt(i)==DOUBLE_QUOTE)
				inDoubleQuotes = !inDoubleQuotes;
			if(toPrint.charAt(i)==QUOTE)
				inSingleQuotes = !inSingleQuotes;
			if(inDoubleQuotes || inSingleQuotes) {
				if (toPrint.charAt(i) == WHITESPACE && toPrint.charAt(i + 1) == WHITESPACE)
					continue;
				if (toPrint.charAt(i) == WHITESPACE && toPrint.charAt(i + 1) == RIGHT_BKT)
					continue;
				if (toPrint.charAt(i) == WHITESPACE && toPrint.charAt(i + 1) == COMMA)
					continue;
				if (toPrint.charAt(i) == WHITESPACE && toPrint.charAt(i - 1) == LEFT_BKT)
					continue;
			}
			out.append(toPrint.charAt(i));
		}
		return out.toString();
	}

	/**
	 * Converts input string (designed for camel case variable names)
	 * into lower case underscored string
	 * @param fieldName
	 * @return
	 */
	public static String fieldNameToLowerCaseUnderScore(String fieldName) {
		return fieldName.replaceAll("[^a-zA-Z0-9]","")
				.replaceAll("(?=[A-Z])","_")
				.replaceAll("^_","")
				.toLowerCase();
	}

	public static final String T_TO_A_EXCEPTION_STRING_PATTERN = "Unable to autodetect type affinity for field {0}!";

	/**
	 * Returns type affinity depends on java field type
	 * @param fType
	 * @return
	 */
	public static final TypeAffinities typeToAffinity(Type fType) {
		if(fType.equals(String.class) || ((Class<?>)fType).isEnum() || fType.equals(BigDecimal.class)
				|| fType.equals(BigInteger.class) || fType.equals(Uri.class) || fType.equals(File.class)
				|| fType.equals(Currency.class))
			return TypeAffinities.TEXT;
		if(fType.equals(byte.class) || fType.equals(int.class) || fType.equals(long.class) ||
				fType.equals(Byte.class) || fType.equals(Integer.class) || fType.equals(Long.class) ||
				fType.equals(Calendar.class) || fType.equals(Date.class) || fType.equals(Timestamp.class) ||
				fType.equals(boolean.class) || fType.equals(Boolean.class) || fType.equals(short.class) ||
				fType.equals(Short.class))
			return TypeAffinities.INTEGER;
		if(fType.equals(float.class) || fType.equals(double.class) || fType.equals(Float.class) ||
				fType.equals(Double.class))
			return TypeAffinities.REAL;
		if(((Class<?>) fType).isAssignableFrom(byte[].class) || ((Class<?>) fType).isAssignableFrom(Byte[].class))
			return TypeAffinities.NONE;
		throw new KittyRuntimeException(MessageFormat.format(T_TO_A_EXCEPTION_STRING_PATTERN, fType));
	}

	private static String LS_PROPERTY = "line.separator";
	private static String RFTS_EXC_MSG_IS_DIR = "Provided File ({0}) is a directory!";
	private static String RFTS_EXC_MSG_NO_R_ACCESS = "Have no permission for read access for provided File {0}!";

	/**
	 * Reads provided file into String
	 * @param file file to read
	 * @return string content of provided file
	 * @throws IOException as usual
	 * @throws IllegalArgumentException when file is directory or has no read access
	 */
	public static final String readFileToString(File file) throws IOException{
		if(file.isDirectory())
			throw new IllegalArgumentException(MessageFormat.format(RFTS_EXC_MSG_IS_DIR, file.getAbsolutePath()));
		if(!file.canRead())
			throw new IllegalArgumentException(MessageFormat.format(RFTS_EXC_MSG_NO_R_ACCESS, file.getAbsolutePath()));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty(LS_PROPERTY);
		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
		} finally {
			if (reader!=null)
				reader.close();
		}
		return stringBuilder.toString();
	}

	/**
	 * Reads provided file into LinkedList filled with Strings, one line per String
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static final LinkedList<String> readFileToLinkedList(File file) throws IOException {
		if(file.isDirectory())
			throw new IllegalArgumentException(MessageFormat.format(RFTS_EXC_MSG_IS_DIR, file.getAbsolutePath()));
		if(!file.canRead())
			throw new IllegalArgumentException(MessageFormat.format(RFTS_EXC_MSG_NO_R_ACCESS, file.getAbsolutePath()));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		LinkedList<String> out = new LinkedList<>();
		try {
			while ((line = reader.readLine()) != null) {
				out.addLast(line);
			}
		} finally {
			if (reader!=null)
				reader.close();
		}
		return out;
	}

	/**
	 * Reads data from provided assets path returns it content as a LinkedList filled with Strings
	 * @param ctx
	 * @param relativeFilePath
	 * @return
	 * @throws IOException
	 */
	public static final LinkedList<String> readFileFromAssetsToLinkedList(Context ctx, String relativeFilePath) throws IOException {
		if(relativeFilePath.startsWith(ASSETS_URI_START))
			relativeFilePath = relativeFilePath.replace(ASSETS_URI_START, EMPTY_STRING);
		BufferedReader reader = null;
		IOException tr = null;
		LinkedList<String> out = new LinkedList<>();
		try {
			reader = new BufferedReader(
					new InputStreamReader(ctx.getAssets().open(relativeFilePath)));
			String lineFromAssetsFile;
			while ((lineFromAssetsFile = reader.readLine()) != null) {
				out.addLast(lineFromAssetsFile);;
			}
		} catch (IOException e) {
			tr = e;
		} finally {
			if (reader != null) {
				reader.close();
				if(tr != null)
					throw tr;
			}
		}
		return out;
	}

	/**
	 * Reads data from provided assets path returns it content as a string
	 * @param ctx
	 * @param relativeFilePath
	 * @return
	 * @throws IOException
	 */
	public static final String readFileFromAssetsToString(Context ctx, String relativeFilePath) throws IOException {
		BufferedReader reader = null;
		IOException tr = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(
					new InputStreamReader(ctx.getAssets().open(relativeFilePath)));
			String lineFromAssetsFile;
			String ls = System.getProperty(LS_PROPERTY);
			while ((lineFromAssetsFile = reader.readLine()) != null) {
				sb.append(lineFromAssetsFile);
				sb.append(ls);
			}
		} catch (IOException e) {
			tr = e;
		} finally {
			if (reader != null) {
				reader.close();
				if(tr != null)
					throw tr;
			}
		}
		return sb.toString();
	}

	/**
	 * Returns part of parameter string before first occurrence of parameter char.
	 * <br> If parameter string doesn't contain parameter char than parameter string would be returned
	 * <br> If parameter flag reverse is true than part of parameter string after last occurrence of parameter char would be returned
	 * @param input
	 * @param Char
	 * @param reverse
	 * @return
	 */
	public static final String getStringPartBeforeFirstOccurrenceOfChar(String input, char Char, boolean reverse) {
		StringBuilder sb = new StringBuilder(64);
		if(!reverse) {
			for (int i = 0; i < input.length(); i++) {
				if(input.charAt(i) == Char)
					break;
				sb.append(input.charAt(i));
			}
		} else {
			StringBuilder reverseSb = new StringBuilder(64);
			for (int i = input.length() - 1; i > -1; i--) {
				if(input.charAt(i) == Char)
					break;
				reverseSb.append(input.charAt(i));
			}
			for(int i = reverseSb.length() - 1; i > -1; i--) {
				sb.append(reverseSb.charAt(i));
			}
		}
		return sb.toString();
	}



	/**
	 * Creates directory in internal memory.
	 * @param ctx
	 * @param dirname
	 */
	public static final void createDirectoryInternalMemory(Context ctx, String dirname) {
		ctx.getDir(dirname, Context.MODE_PRIVATE);
	}

	/**
	 * Writes string to specified path.
	 * @param filepath - where to write file.
	 * @param data - what to write to file.
	 * @param append - append existing data if true, otherwise
	 * @param createIfNotExists - create file if not exists
	 * @throws IOException
	 */
	public static final void writeStringToFile(File filepath, String data, boolean append, boolean createIfNotExists)
			throws IOException {
		BufferedWriter bufferedWriter = null;
		FileWriter writer = null;
		try {
			if (!filepath.exists() && createIfNotExists) {
				filepath.createNewFile();
			}
			writer = new FileWriter(filepath, append);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(data);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
				if (writer != null)
					writer.close();
			} catch (Exception ex) {

			}
		}
	}

	public static final int CODE_UNABLE_TO_COPY_ALREADY_EXISTS = 0;
	public static final int CODE_UNABLE_TO_COPY = 1;
	public static final int CODE_COPIED_SUCCESSFULLY = 2;
	
	public static final void copyDirectoryFromAssetsToFS(Context ctx, String assetsPath, File baseFile) {
		if(assetsPath.startsWith(ASSETS_URI_START)) {
			assetsPath = assetsPath.replace(ASSETS_URI_START, EMPTY_STRING);
		}
		String assetsDirFileName = getStringPartBeforeFirstOccurrenceOfChar(assetsPath, File.separatorChar, true);
		AssetManager manager = ctx.getAssets();
		try {
			String[] fNames = manager.list(assetsPath);
			if (fNames == null) {
				copyFileFromAssets(ctx, assetsPath, baseFile);
				return;
			} else if (fNames.length == 0) {
				copyFileFromAssets(ctx, assetsPath, baseFile);
				return;
			}
			File destinationDir = new File(baseFile.getAbsoluteFile(), assetsDirFileName);
			destinationDir.mkdirs();

			for (String fName : fNames) {
				StringBuilder sb = new StringBuilder(64);
				sb.append(assetsPath).append(File.separatorChar).append(fName);
				copyDirectoryFromAssetsToFS(ctx, sb.toString(), destinationDir);
			}
		} catch (IOException e) {

		}
	}

	public static final void copyFileFromAssets(Context ctx, String assetsPath, File baseFile) throws IOException {
		if(assetsPath.startsWith(ASSETS_URI_START)) {
			assetsPath = assetsPath.replace(ASSETS_URI_START, EMPTY_STRING);
		}
		String assetsFilename = getStringPartBeforeFirstOccurrenceOfChar(assetsPath, File.separatorChar, true);
		File file = new File(baseFile.getAbsolutePath(), assetsFilename);
		InputStream in = ctx.getAssets().open(assetsPath);
		OutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int read = in.read(buffer);
		while (read != -1) {
			out.write(buffer, 0, read);
			read = in.read(buffer);
		}
		out.close();
		in.close();
	}

	public static final void writeStringsToFile(File filepath, List<String> data, boolean append, boolean createIfNotExists)
			throws IOException {
		BufferedWriter bufferedWriter = null;
		FileWriter writer = null;
		String newLine = System.getProperty("line.separator");
		try {
			if (!filepath.exists() && createIfNotExists) {
				filepath.createNewFile();
			}
			writer = new FileWriter(filepath, append);
			bufferedWriter = new BufferedWriter(writer);
			for(String line : data) {
				bufferedWriter.write(line);
				bufferedWriter.write(newLine);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
				if (writer != null)
					writer.close();
			} catch (IOException ex) {
				throw ex;
			}
		}
	}



	private static String LEGAL_CHARACTERS_PATTERN = "[^a-zA-Z0-9.-]";
	private static String LEGAL_UNDERSCORE = "_";

	/**
	 * Replaces all symbols except those that can be used in paths
	 * @param input string to replace illegal symbols
	 * @return string without illegal symbols (they would be changed with underscore)
	 */
	public static final String removeAllIllegalCharactersFromPathString(String input) {
		return input.replaceAll(LEGAL_CHARACTERS_PATTERN, LEGAL_UNDERSCORE);
	}

}
