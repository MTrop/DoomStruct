package net.mtrop.doom;

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
	public static Pattern ENTRY_NAME = Pattern.compile("[A-Z0-9\\[\\]\\-\\_\\\\]{1,8}");
	/** A regex pattern that matches valid texture names. */
	public static Pattern TEXTURE_NAME = Pattern.compile("[A-Z0-9\\-\\_]{1,8}");

	private NameUtils()
	{
	}

	/**
	 * Cuts a string at the first null character.
	 */
	public static String nullTrim(String s)
	{
		int n = s.indexOf('\0');
		return n >= 0 ? s.substring(0, n) : s;
	}

	/**
	 * Tests if an input string is a valid entry name.
	 * <p>
	 * A WadEntry must have a name that is up to 8 characters long, and can only contain
	 * A-Z (uppercase only), 0-9, and [ ] - _, plus the backslash ("\"). 
	 * @return true if so, false if not.
	 */
	public static boolean isValidEntryName(String name)
	{
		return name.matches(ENTRY_NAME.pattern());
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
	 * @return true if so, false if not.
	 */
	public static String toValidEntryName(String name)
	{
		if (isValidEntryName(name))
			return name;
		
		if (Common.isEmpty(name))
			return "-";
			
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
	
}
