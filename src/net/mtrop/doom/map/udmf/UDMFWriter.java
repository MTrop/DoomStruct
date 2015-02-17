package net.mtrop.doom.map.udmf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * Writes UDMF data.
 * @author Matthew Tropiano
 */
public final class UDMFWriter
{
	private UDMFWriter() {}
	
	/**
	 * Writes UDMF-formatted data into an {@link OutputStream}.
	 * Does not close the OutputStream at the end of the write.
	 * @param out the OutputStream to write to.
	 * @throws IOException if the output stream cannot be written to.
	 */
	public static void writeData(UDMFTable table, OutputStream out) throws IOException
	{
		writeData(table, new OutputStreamWriter(out, "UTF8"));
	}
	
	/**
	 * Writes UDMF-formatted data into a {@link Writer}.
	 * Does not close the OutputStream at the end of the write.
	 * @param writer the Writer to write to.
	 * @throws IOException if the output stream cannot be written to.
	 */
	public static void writeData(UDMFTable table, Writer writer) throws IOException
	{
		PrintWriter pw = new PrintWriter(writer, true);
		
		writeFields(table.getGlobalFields(), pw, "");

		for (String strName : table.getAllStructNames())
		{
			int x = 0;
			for (UDMFObject struct : table.getStructs(strName))
			{
				writeStructStart(strName, pw, x, "");
				writeFields(struct, pw, "\t");
				writeStructEnd(strName, pw, x, "");
				x++;
			}
		}
		
	}

	/**
	 * Writes the fields out to the stream.
	 */
	private static void writeFields(UDMFObject struct, PrintWriter pw, String lineprefix)
	{
		Iterator<String> it = struct.keyIterator();
		while(it.hasNext())
		{
			String fieldName = it.next();
			pw.println(lineprefix + fieldName + " = " + renderFieldData(struct.get(fieldName))+";");
		}
	}
	
	/**
	 * Starts the structure.
	 */
	private static void writeStructStart(String name, PrintWriter pw, int count, String lineprefix)
	{
		pw.println(lineprefix + name + " // " + count);
		pw.println(lineprefix + "{");
	}
	
	/**
	 * Ends the structure.
	 */
	private static void writeStructEnd(String name, PrintWriter pw, int count, String lineprefix)
	{
		pw.println(lineprefix + "}");
		pw.println();
	}
	
	private static String renderFieldData(Object data)
	{
		if (data instanceof Boolean || data instanceof Number)
			return String.valueOf(data);
		else
			return '\"' + String.valueOf(data) + '\"';
	}
	
}
