/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.mtrop.doom.enums.WadType;
import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.map.DoomMap;
import net.mtrop.doom.map.HexenMap;
import net.mtrop.doom.map.StrifeMap;
import net.mtrop.doom.util.SerialReader;

/**
 * This is just a basic mapping of WAD entries to a file.
 * The file is NOT kept open after the read, and the file or
 * stream used to gather the WAD metadata is not kept.
 * <p>
 * This may not be added to or changed, and its data may not be read directly,
 * because this is just a mapping of entries. Individual entries may be read
 * for data offset information and then read from the corresponding file or
 * stream.
 * <p>
 * Despite the name, this is not a structure that reads Doom Map information.
 * Use {@link DoomMap}, {@link HexenMap}, or {@link StrifeMap} for that purpose.  
 * @author Matthew Tropiano
 */
public class WadMap implements Wad
{
	/** Type of Wad File (IWAD or PWAD). */
	private WadType type;
	/** The list of entries. */
	protected List<WadEntry> entries;

	private WadMap()
	{
		this.entries = new ArrayList<WadEntry>();
	}
	
	/**
	 * Creates a new WadMap using the contents of a file, denoted by the path.
	 * @param path the path to the file to read.
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if <code>path</code> is null.
	 */
	public WadMap(String path) throws IOException
	{
		this(new File(path));
	}
	
	/**
	 * Creates a new WadMap using the contents of a file.
	 * @param f the file to read.
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if <code>f</code> is null.
	 */
	public WadMap(File f) throws IOException
	{
		this();
		FileInputStream fis = new FileInputStream(f);
		readWad(fis);
		fis.close();
	}
	
	/**
	 * Creates a new WadMap.
	 * @param in the input stream.
	 * @throws IOException if the file can't be read.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if <code>in</code> is null.
	 */
	public WadMap(InputStream in) throws IOException
	{
		this();
		readWad(in);
	}

	/**
	 * Reads in a WAD structure from an InputStream.
	 * @param in
	 */
	private void readWad(InputStream in) throws IOException
	{
		SerialReader sr = new SerialReader(SerialReader.LITTLE_ENDIAN);
		entries.clear();

		try {
			type = WadType.valueOf(sr.readString(in, 4, "ASCII"));
		} catch (IllegalArgumentException e) {
			throw new WadException("Not a WAD file.");
		}
		int entryCount = sr.readInt(in);
		int contentsize = sr.readInt(in) - 12;
		
		// skip content.
		in.skip(contentsize);
		
		byte[] entrybuffer = new byte[16];
		for (int x = 0; x < entryCount; x++)
		{
			sr.readBytes(in, entrybuffer);
			WadEntry entry = BinaryObject.create(WadEntry.class, entrybuffer);
			entries.add(entry);
		}
	}

	@Override
	public boolean isIWAD()
	{
		return type == WadType.IWAD;
	}

	@Override
	public boolean isPWAD()
	{
		return type == WadType.PWAD;
	}

	@Override
	public int getSize()
	{
		return entries.size();
	}

	@Override
	public WadEntry addData(String entryName, byte[] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addData()");
	}

	@Override
	public WadEntry addDataAt(int index, String entryName, byte[] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addDataAt()");
	}

	@Override
	public WadEntry[] addAllData(String[] entryNames, byte[][] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addAllData()");
	}

	@Override
	public WadEntry[] addAllDataAt(int index, String[] entryNames, byte[][] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addAllDataAt()");
	}

	@Override
	public WadEntry addMarker(String name) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addMarker()");
	}

	@Override
	public WadEntry addMarkerAt(int index, String name) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support addMarkerAt()");
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
		throw new UnsupportedOperationException("This class does not support deleteEntry()");
	}

	@Override
	public void renameEntry(int index, String newName) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support renameEntry()");
	}

	@Override
	public void replaceEntry(int index, byte[] data) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support replaceEntry()");
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
		throw new UnsupportedOperationException("This class does not support unmapEntries()");
	}

	@Override
	public void setEntries(WadEntry[] entryList) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support setEntries()");
	}

	@Override	
	public byte[] getData(int n) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getData()");
	}

	@Override	
	public byte[] getData(String entryName) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getData()");
	}

	@Override	
	public byte[] getData(String entryName, int start) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getDataAsStream()");
	}

	@Override	
	public byte[] getData(WadEntry entry) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getData()");
	}

	@Override	
	public InputStream getInputStream(int n) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getInputStream()");
	}

	@Override	
	public InputStream getInputStream(String WadEntry) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getInputStream()");
	}

	@Override	
	public InputStream getInputStream(String WadEntry, int start) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getInputStream()");
	}

	@Override	
	public InputStream getInputStream(WadEntry WadEntry) throws IOException
	{
		throw new UnsupportedOperationException("This class does not support getInputStream()");
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
		Queue<Integer> w = new LinkedList<Integer>();
		
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

	@Override
	public Iterator<WadEntry> iterator()
	{
		return entries.iterator();
	}
}
