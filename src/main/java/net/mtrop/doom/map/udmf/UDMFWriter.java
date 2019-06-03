/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map.Entry;

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
	 * @param table the table to write.
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
	 * @param table the table to write.
	 * @param writer the Writer to write to.
	 * @throws IOException if the output stream cannot be written to.
	 */
	public static void writeData(UDMFTable table, Writer writer) throws IOException
	{
		PrintWriter pw = new PrintWriter(writer, true);
		
		writeFields(table.getGlobalFields(), pw, "");

		for (String strName : table.getAllObjectNames())
		{
			int x = 0;
			for (UDMFObject struct : table.getObjects(strName))
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
		Iterator<Entry<String, Object>> it = struct.iterator();
		while (it.hasNext())
		{
			Entry<String, Object> entry = it.next();
			String fieldName = entry.getKey();
			pw.println(lineprefix + fieldName + " = " + renderFieldData(entry.getValue())+";");
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
