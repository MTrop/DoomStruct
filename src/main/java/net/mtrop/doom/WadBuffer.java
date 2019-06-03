/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.mtrop.doom.enums.WadType;
import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.struct.DataList;
import net.mtrop.doom.util.NameUtils;
import net.mtrop.doom.util.SerialReader;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.Utils;

/**
 * An implementation of DoomWad where any and all WAD information is manipulated in memory.
 * This loads everything in the WAD into memory as uninterpreted raw bytes.
 * @author Matthew Tropiano
 */
public class WadBuffer implements Wad
{
	private static final byte[] NO_DATA = new byte[0];
	
	/** Type of Wad File (IWAD or PWAD). */
	private WadType type;
	/** The data itself. */
	protected DataList content;
	/** The list of entries. */
	protected List<WadEntry> entries;
	
	/**
	 * Creates an empty WadBuffer (as a PWAD).
	 */
	public WadBuffer()
	{
		this(WadType.PWAD);
	}
	
	/**
	 * Creates an empty WadBuffer with a specific type.
	 * @param type the type to set.
	 */
	public WadBuffer(WadType type)
	{
		this.type = type;
		this.content = new DataList();
		this.entries = new ArrayList<WadEntry>();
	}
	
	/**
	 * Creates a new WadBuffer using the contents of a file, denoted by the path.
	 * @param path the path to the file to read.
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if <code>path</code> is null.
	 */
	public WadBuffer(String path) throws IOException
	{
		this(new File(path));
	}
	
	/**
	 * Creates a new WadBuffer using the contents of a file.
	 * @param f the file to read.
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if <code>path</code> is null.
	 */
	public WadBuffer(File f) throws IOException
	{
		this();
		FileInputStream fis = new FileInputStream(f);
		readWad(fis);
		fis.close();
	}
	
	/**
	 * Creates a new WadBuffer.
	 * @param in the input stream.
	 * @throws IOException if the file can't be read.
	 * @throws WadException if the stream contents are not a Wad file.
	 * @throws NullPointerException if <code>path</code> is null.
	 */
	public WadBuffer(InputStream in) throws IOException
	{
		this();
		readWad(in);
	}

	/**
	 * Reads in a wad from an InputStream.
	 * @param in the input stream.
	 */
	private void readWad(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		content.clear();
		entries.clear();

		try {
			type = WadType.valueOf(sr.readString(in, 4, "ASCII"));
		} catch (IllegalArgumentException e) {
			throw new WadException("Not a WAD file.");
		}
		int entryCount = sr.readInt(in);
		int contentsize = sr.readInt(in) - 12;
		
		byte[] buffer = new byte[65536];
		int bytes = 0;
		int n = 0;
		while (bytes < contentsize)
		{
			n = sr.readBytes(in, buffer, Math.min(contentsize - bytes, buffer.length));
			content.append(buffer, 0, n);
			bytes += n;
		}
		
		byte[] entrybuffer = new byte[16];
		for (int x = 0; x < entryCount; x++)
		{
			sr.readBytes(in, entrybuffer);
			WadEntry wadEntry = WadEntry.create(entrybuffer);
			entries.add(wadEntry);
		}
	}
	
	/**
	 * Converts a WadEntry offset to the offset into the data vector.
	 */
	private int getContentOffset(WadEntry WadEntry)
	{
		return WadEntry.getOffset() - 12; 
	}
	
	/**
	 * Converts a content offset to a WadEntry offset.
	 */
	private int toEntryOffset(int contentOffset)
	{
		return contentOffset + 12; 
	}
	
	/**
	 * Removes a WadEntry.
	 */
	private WadEntry removeEntry(int n)
	{
		return entries.remove(n);
	}

	/**
	 * Writes the contents of this buffer out to an output stream in Wad format.
	 * Does not close the stream.
	 * @param out the output stream to write to.
	 * @throws IOException if a problem occurs during the write.
	 * @throws NullPointerException if <code>out</code> is null.
	 */
	public void writeToStream(OutputStream out) throws IOException
	{
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeBytes(out, type.name().getBytes("ASCII"));
		sw.writeInt(out, entries.size());		// number of entries.
		sw.writeInt(out, 12 + content.size());	// offset to WadEntry list.
		sw.writeBytes(out, content.toByteArray());
		for (WadEntry entry : entries)
			entry.writeBytes(out);
	}
	
	/**
	 * Writes the contents of this buffer out to a file in Wad format.
	 * The target file will be overwritten.
	 * @param f the file to write to.
	 * @throws IOException if a problem occurs during the write.
	 * @throws SecurityException if you don't have permission to write the file.
	 * @throws NullPointerException if <code>out</code> is null.
	 */
	public void writeToFile(File f) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(f);
		writeToStream(fos);
		Utils.close(fos);
	}
	
	@Override
	public boolean isIWAD()
	{
		return getType() == WadType.IWAD;
	}

	@Override
	public boolean isPWAD()
	{
		return getType() == WadType.PWAD;
	}

	@Override
	public int getSize()
	{
		return entries.size();
	}

	@Override
	public WadEntry addData(String entryName, byte[] data) throws IOException
	{
		WadEntry entry = WadEntry.create(entryName, toEntryOffset(content.size()), data.length);
		content.append(data);
		entries.add(entry);
		return entry;
	}

	@Override
	public WadEntry addDataAt(int index, String entryName, byte[] data) throws IOException
	{
		WadEntry entry = WadEntry.create(entryName, toEntryOffset(content.size()), data.length);
		content.append(data);
		entries.add(index, entry);
		return entry;
	}
	
	@Override
	public WadEntry[] addAllData(String[] entryNames, byte[][] data) throws IOException
	{
		WadEntry[] out = new WadEntry[entryNames.length];
		for (int i = 0; i < entryNames.length; i++)
			out[i] = addData(entryNames[i], data[i]);
		return out;
	}

	@Override
	public WadEntry[] addAllDataAt(int index, String[] entryNames, byte[][] data)
			throws IOException
	{
		WadEntry[] out = new WadEntry[entryNames.length];
		for (int i = 0; i < entryNames.length; i++)
			out[i] = addDataAt(index + i, entryNames[i], data[i]);
		return out;
	}

	@Override
	public WadEntry addMarker(String name) throws IOException
	{
		return addData(name, NO_DATA);
	}

	@Override
	public WadEntry addMarkerAt(int index, String name) throws IOException
	{
		return addDataAt(index, name, NO_DATA);
	}

	@Override	
	public boolean contains(String entryName)
	{
		return getIndexOf(entryName, 0) > -1;
	}
	
	@Override	
	public boolean contains(String entryName, int index)
	{
		return getIndexOf(entryName, index) > -1;
	}
	
	@Override
	public void deleteEntry(int n) throws IOException
	{
		// get removed WadEntry.
		WadEntry wfe = removeEntry(n);
		if (wfe == null)
			throw new IOException("Index is out of range.");

		int cofs = getContentOffset(wfe);

		content.delete(cofs, wfe.getSize());
		
		// adjust offsets from last WadEntry.
		for (int i = n; i < entries.size(); i++)
		{
			WadEntry e = entries.get(i);
			e.offset -= wfe.getSize();
		}
	}

	@Override
	public void renameEntry(int index, String newName) throws IOException
	{
		WadEntry entry = (WadEntry)getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		if (!NameUtils.isValidEntryName(newName))
			throw new IllegalArgumentException("Entry name \""+newName+"\" does not fit entry requirements.");
		
		entry.name = newName;
	}

	@Override
	public void replaceEntry(int index, byte[] data) throws IOException
	{
		WadEntry WadEntry = removeEntry(index);
		if (WadEntry == null)
			throw new IOException("Index is out of range.");
		
		String name = WadEntry.getName();
		addDataAt(index, name, data);
	}

	@Override
	public WadEntry[] mapEntries(int startIndex, int maxLength)
	{
		if (startIndex < 0)
			throw new IllegalArgumentException("Starting index cannot be less than 0.");

		int len = Math.min(maxLength, getSize() - startIndex);
		if (len <= 0)
			return new WadEntry[0];
		WadEntry[] out = new WadEntry[len];
		for (int i = 0; i < len; i++)
			out[i] = getEntry(startIndex + i);
		return out;
	}

	@Override
	public void unmapEntries(int startIndex, WadEntry[] entryList) throws IOException
	{
		for (int i = 0; i < entryList.length; i++)
			entries.set(startIndex + i, entryList[i]);
	}

	@Override
	public void setEntries(WadEntry[] entryList) throws IOException
	{
		entries.clear();
		for (WadEntry WadEntry : entryList)
			entries.add(WadEntry);
	}

	@Override	
	public byte[] getData(int n) throws IOException
	{
		return getData(getEntry(n));
	}
	
	@Override	
	public byte[] getData(String entryName) throws IOException
	{
		WadEntry entry = getEntry(entryName);
		return entry != null ? getData(entry) : null;
	}

	@Override	
	public byte[] getData(String entryName, int start) throws IOException
	{
		int i = getIndexOf(entryName, start);
		return i != -1 ? getData(i) : null;
	}
	
	@Override	
	public byte[] getData(WadEntry entry) throws IOException
	{
		byte[] out = new byte[entry.getSize()];
		try {
			content.getData(getContentOffset(entry), out);
		} catch (IndexOutOfBoundsException e) {
			throw new IOException(e);
		}
		return out;
	}
	
	@Override	
	public InputStream getInputStream(int n) throws IOException
	{
		WadEntry e = getEntry(n);
		if (e == null)
			return null;
		byte[] b = getData(e); 
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}
	
	@Override	
	public InputStream getInputStream(String entryName) throws IOException
	{
		WadEntry e = getEntry(entryName);
		if (e == null)
			return null;
		byte[] b = getData(e); 
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}

	@Override	
	public InputStream getInputStream(String entryName, int start) throws IOException
	{
		int i = getIndexOf(entryName,start);
		byte[] b = i != -1 ? getData(i) : null;
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}
	
	@Override	
	public InputStream getInputStream(WadEntry entry) throws IOException
	{
		return new ByteArrayInputStream(getData(entry));
	}

	@Override	
	public WadEntry getEntry(int n)
	{
		return entries.get(n);
	}

	@Override	
	public WadEntry getEntry(String entryName)
	{
		int i = getIndexOf(entryName, 0);
		return i != -1 ? getEntry(i) : null;
	}

	@Override	
	public WadEntry getEntry(String entryName, int startingIndex)
	{
		int i = getIndexOf(entryName, startingIndex);
		return i != -1 ? getEntry(i) : null;
	}

	@Override	
	public WadEntry getNthEntry(String entryName, int n)
	{
		int x = 0;
		for (int i = 0; i < entries.size(); i++)
		{
			WadEntry entry = entries.get(i);
			if (entry.getName().equals(entryName))
			{
				if (x++ == n)
					return entry;
			}
		}
		return null;
	}

	@Override	
	public WadEntry getLastEntry(String entryName)
	{
		for (int i = entries.size() - 1; i >= 0; i--)
		{
			WadEntry entry = entries.get(i);
			if (entry.getName().equals(entryName))
				return entry;
		}
		return null;
	}

	@Override	
	public WadEntry[] getAllEntries()
	{
		WadEntry[] out = new WadEntry[entries.size()];
		entries.toArray(out);
		return out;
	}
	
	@Override	
	public WadEntry[] getAllEntries(String entryName)
	{
		Queue<WadEntry> w = new LinkedList<>();
		
		for (int i = 0; i < entries.size(); i++)
		{
			WadEntry entry = entries.get(i);
			if (entry.getName().equals(entryName))
				w.add(entry);
		}
		
		WadEntry[] out = new WadEntry[w.size()];
		w.toArray(out);
		return out;
	}

	@Override
	public int[] getAllEntryIndices(String entryName)
	{
		Queue<Integer> w = new LinkedList<>();
		
		for (int i = 0; i < entries.size(); i++)
		{
			WadEntry entry = entries.get(i);
			if (entry.getName().equals(entryName))
				w.add(i);
		}
		
		int[] out = new int[w.size()];
		for (int i = 0; i < entries.size(); i++)
			out[i] = w.poll();
		return out;
	}

	@Override	
	public int getIndexOf(String entryName)
	{
		return getIndexOf(entryName, 0);
	}

	@Override	
	public int getIndexOf(String entryName, int start)
	{
		for (int i = start; i < entries.size(); i++)
			if (entries.get(i).getName().equals(entryName))
				return i;
		return -1;
	}
	
	@Override	
	public int getLastIndexOf(String entryName)
	{
		int out = -1;
		for (int i = 0; i < entries.size(); i++)
			if (entries.get(i).getName().equals(entryName))
				out = i;
		return out;
	}
	
	/**
	 * Sets the type of WAD that this is.
	 * @param type the new type.
	 */
	public void setType(WadType type)
	{
		this.type = type;
	}
	
	/**
	 * Gets the type of WAD that this is.
	 * @return the wad type.
	 */
	public WadType getType()
	{
		return type;
	}

	@Override
	public Iterator<WadEntry> iterator()
	{
		return entries.iterator();
	}

}
