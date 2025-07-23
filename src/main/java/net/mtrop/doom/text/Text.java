package net.mtrop.doom.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.io.IOUtils;

/**
 * A generic text object.
 * Contains only a StringBuilder that can be written to or read from.
 * @author Matthew Tropiano
 * @since 2.19.0
 */
public class Text implements TextObject, CharSequence
{
	private StringBuilder data;
	
	/**
	 * Creates a new Text object.
	 */
	public Text()
	{
		this.data = new StringBuilder(); 
	}
	
	@Override
	public int length()
	{
		return data.length();
	}

	@Override
	public char charAt(int index) 
	{
		return data.charAt(index);
	}
	
	/**
	 * Returns the index of the first occurrence of the provided string.
	 * @param str the string to search for.
	 * @return the index or -1 if not found.
	 */
	public int indexOf(String str)
	{
		return data.indexOf(str);
	}
	
	/**
	 * Returns the index of the first occurrence of the provided string, starting from a provided index.
	 * @param str the string to search for.
	 * @param fromIndex the index to start searching from. 
	 * @return the index or -1 if not found.
	 */
	public int indexOf(String str, int fromIndex)
	{
		return data.indexOf(str, fromIndex);
	}
	
	/**
	 * Returns the index of the last occurrence of the provided string.
	 * @param str the string to search for.
	 * @return the index or -1 if not found.
	 */
	public int lastIndexOf(String str)
	{
		return data.lastIndexOf(str);
	}
	
	/**
	 * Returns the index of the last occurrence of the provided string, starting from a provided index, searching backwards.
	 * @param str the string to search for.
	 * @param fromIndex the index to start searching from. 
	 * @return the index or -1 if not found.
	 */
	public int lastIndexOf(String str, int fromIndex)
	{
		return data.lastIndexOf(str, fromIndex);
	}

	/**
	 * Returns a Matcher for a given RegEx pattern.
	 * @param regex the RegEx pattern to get a matcher for.
	 * @return the matcher for this Text.
	 */
	public Matcher getMatcherForPattern(Pattern regex)
	{
		return regex.matcher(this);
	}
	
	@Override
	public CharSequence subSequence(int start, int end)
	{
		return data.subSequence(start, end);
	}

	/**
	 * @return a reference to the underlying StringBuilder.
	 */
	public StringBuilder getBuilder()
	{
		return data;
	}
	
	/**
	 * Returns a new object, where the contents of this Text is read as a different TextObject-implementing object.
	 * @param <TO> the TextObject subtype.
	 * @param toClass the class to create.
	 * @return the new object.
	 * @throws IOException if an error occurs during the read - most commonly a ParseException.
	 */
	public <TO extends TextObject> TO create(Class<TO> toClass) throws IOException
	{
		return TextObject.create(toClass, toString());
	}

	@Override
	public String toString() 
	{
		return data.toString();
	}
	
	@Override
	public void readText(Reader reader) throws IOException
	{
		StringWriter writer = new StringWriter(length());
		IOUtils.relay(reader, writer, 16384, -1);
		writer.flush();
		this.data = new StringBuilder(writer.toString());
	}

	@Override
	public void writeText(Writer writer) throws IOException 
	{
		StringReader reader = new StringReader(data.toString());
		IOUtils.relay(reader, writer, 16384, -1);
		writer.flush();
	}

}
