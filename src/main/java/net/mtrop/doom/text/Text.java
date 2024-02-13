package net.mtrop.doom.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import net.mtrop.doom.object.TextObject;
import net.mtrop.doom.struct.io.IOUtils;

/**
 * A generic text object.
 * Contains only a StringBuilder that can be written to or read from.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class Text implements TextObject, CharSequence
{
	private StringBuilder data;
	
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
