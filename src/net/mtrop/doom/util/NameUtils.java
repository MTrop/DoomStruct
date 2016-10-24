/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

import com.blackrook.commons.Common;

/**
 * Name utility methods.
 * @author Matthew Tropiano
 */
public final class NameUtils
{
	/** A regex pattern that matches valid entry names. */
	public static final Pattern ENTRY_NAME = Pattern.compile("[A-Z0-9\\[\\]\\-\\_\\\\]{1,8}");
	/** A regex pattern that matches valid texture names. */
	public static final Pattern TEXTURE_NAME = Pattern.compile("(\\-|[A-Z0-9\\-\\_]{1,8})");

	/** The name of the "blank" texture. */
	public static final String EMPTY_TEXTURE_NAME = "-";

	private NameUtils() {}

	/**
	 * Cuts a string at the first null character.
	 * @param s the input string.
	 * @return the resultant string, after the trim.
	 */
	public static String nullTrim(String s)
	{
		int n = s.indexOf('\0');
		return n >= 0 ? s.substring(0, n) : s;
	}

	/**
	 * Converts a String to an ASCII-encoded, byte-length-aligned vector.
	 * If the string length is less than <code>bytelen</code> it is null-byte padded to the length.
	 * @param s the input string.
	 * @param bytelen the output byte array length.
	 * @return the resultant byte array.
	 */
	public static byte[] toASCIIBytes(String s, int bytelen)
	{
		byte[] out = new byte[bytelen];
		byte[] source = null;
		try {source = s.getBytes("ASCII");} catch (UnsupportedEncodingException e) { /* Shouldn't happen. */ }
		System.arraycopy(source, 0, out, 0, Math.min(out.length, source.length));
		return out;
	}

	/**
	 * Tests if an input string is a valid entry name.
	 * <p>
	 * A WadEntry must have a name that is up to 8 characters long, and can only contain
	 * A-Z (uppercase only), 0-9, and [ ] - _, plus the backslash ("\"). 
	 * @param name the input name to test.
	 * @return true if so, false if not.
	 */
	public static boolean isValidEntryName(String name)
	{
		return name != null && ENTRY_NAME.matcher(name).matches();
	}

	/**
	 * Tests if an input string is a valid entry name, and if not, converts it into a valid one.
	 * <p>
	 * All characters must be A-Z (uppercase only), 0-9, and [ ] - _ plus the backslash ("\").
	 * <p>
	 * Lowercase letters are made uppercase and unknown characters are converted to dashes.
	 * Latin characters with diacritical marks are converted to their normalized forms.
	 * Names are truncated to 8 characters.
	 * The entry will also be cut at the first null character, if any.
	 * @param name the input name to test.
	 * @return true if so, false if not.
	 */
	public static String toValidEntryName(String name)
	{
		if (isValidEntryName(name))
			return name;
		
		if (Common.isEmpty(name))
			return "";
			
		// remove diacritics
		name = Normalizer.normalize(name, Form.NFC);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8 && i < name.length(); i++)
		{
			char c = name.charAt(i);
			if (c == '\0')
				break;
			else if (Character.isLetter(c))
			{
				if (Character.isLowerCase(c))
					sb.append(Character.toUpperCase(c));
				else
					sb.append(c);
			}
			else if (Character.isDigit(c))
				sb.append(c);
			else if (c == '[')
				sb.append(c);
			else if (c == ']')
				sb.append(c);
			else if (c == '-')
				sb.append(c);
			else if (c == '_')
				sb.append(c);
			else if (c == '\\')
				sb.append(c);
			else
				sb.append('-');
		}
		
		return sb.toString();
	}
	
	/**
	 * Tests if an input string is a valid texture name.
	 * <p>
	 * A Texture must have an alphanumeric name that is up to 8 characters long, and can only contain
	 * A-Z (uppercase only), 0-9, and - and _, or just "-" 
	 * @param name the input name to test.
	 * @return true if so, false if not.
	 */
	public static boolean isValidTextureName(String name)
	{
		return name != null && TEXTURE_NAME.matcher(name).matches();
	}

	/**
	 * Tests if an input string is a valid entry name, and if not, converts it into a valid one.
	 * <p>
	 * All characters must be A-Z (uppercase only), 0-9, and - and _.
	 * <p>
	 * Blank/null names are changed to "-".
	 * <p>
	 * Lowercase letters are made uppercase and unknown characters are converted to dashes.
	 * Latin characters with diacritical marks are converted to their normalized forms.
	 * Names are truncated to 8 characters.
	 * The entry will also be cut at the first null character, if any.
	 * @param name the input name to test.
	 * @return true if so, false if not.
	 */
	public static String toValidTextureName(String name)
	{
		if (isValidTextureName(name))
			return name;
		
		if (Common.isEmpty(name))
			return EMPTY_TEXTURE_NAME;
			
		// remove diacritics
		name = Normalizer.normalize(name, Form.NFC);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8 && i < name.length(); i++)
		{
			char c = name.charAt(i);
			if (c == '\0')
				break;
			else if (Character.isLetter(c))
			{
				if (Character.isLowerCase(c))
					sb.append(Character.toUpperCase(c));
				else
					sb.append(c);
			}
			else if (Character.isDigit(c))
				sb.append(c);
			else if (c == '-')
				sb.append(c);
			else if (c == '_')
				sb.append(c);
			else
				sb.append('-');
		}
		
		return sb.toString();
	}

	
}
