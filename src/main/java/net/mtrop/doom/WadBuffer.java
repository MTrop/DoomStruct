/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.io.IOUtils;
import net.mtrop.doom.io.SerialReader;
import net.mtrop.doom.io.SerialWriter;
import net.mtrop.doom.struct.DataList;
import net.mtrop.doom.util.NameUtils;

/**
 * An implementation of DoomWad where any and all WAD information is manipulated in memory.
 * This loads everything in the WAD into memory as uninterpreted raw bytes.
 * <p>WadBuffer operations are not thread-safe!
 * @author Matthew Tropiano
 */
public class WadBuffer implements Wad
{
	/** Type of Wad File (IWAD or PWAD). */
	private Type type;
	/** The data itself. */
	protected DataList content;
	/** The list of entries. */
	protected List<WadEntry> entries;
	
	/**
	 * Creates an empty WadBuffer (as a PWAD).
	 */
	public WadBuffer()
	{
		this(Type.PWAD);
	}
	
	/**
	 * Creates an empty WadBuffer with a specific type.
	 * @param type the type to set.
	 */
	public WadBuffer(Type type)
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
			type = Type.valueOf(sr.readString(in, 4, "ASCII"));
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
		IOUtils.close(fos);
	}
	
	@Override
	public boolean isIWAD()
	{
		return getType() == Type.IWAD;
	}

	@Override
	public boolean isPWAD()
	{
		return getType() == Type.PWAD;
	}

	@Override
	public int getSize()
	{
		return entries.size();
	}

	@Override	
	public WadEntry getEntry(int n)
	{
		return entries.get(n);
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
	public InputStream getInputStream(WadEntry entry) throws IOException
	{
		return new WadBufferInputStream(entry.offset, entry.size);
	}

	@Override
	public WadEntry removeEntry(int n) throws IOException
	{
		return entries.remove(n);
	}

	@Override
	public WadEntry deleteEntry(int n) throws IOException
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
		return wfe;
	}

	@Override
	public void renameEntry(int index, String newName) throws IOException
	{
		WadEntry entry = (WadEntry)getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		NameUtils.checkValidEntryName(newName);
		
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
	public void unmapEntries(int startIndex, WadEntry... entryList) throws IOException
	{
		for (int i = 0; i < entryList.length; i++)
			entries.set(startIndex + i, entryList[i]);
	}

	@Override
	public void setEntries(WadEntry... entryList) throws IOException
	{
		entries.clear();
		for (WadEntry WadEntry : entryList)
			entries.add(WadEntry);
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

	/**
	 * Sets the type of WAD that this is.
	 * @param type the new type.
	 */
	public void setType(Type type)
	{
		this.type = type;
	}
	
	/**
	 * Gets the type of WAD that this is.
	 * @return the wad type.
	 */
	public Type getType()
	{
		return type;
	}

	@Override
	public Iterator<WadEntry> iterator()
	{
		return entries.iterator();
	}

	@Override
	public void close() throws IOException
	{
		// Do nothing.
	}

	/**
	 * Input stream for an entry. 
	 */
	private class WadBufferInputStream extends InputStream
	{
		private int offset;
		private int amount;
		private int marked;
		private int markedAmount;
		private int readlimit;
		
		private WadBufferInputStream(int offset, int length)
		{
			this.offset = offset;
			this.amount = length;
			this.marked = -1;
			this.markedAmount = -1;
			this.readlimit = -1;
		}
		
		@Override
		public int read() throws IOException
		{
			if (amount <= 0)
				return -1;
			
			byte b = content.getData(offset++);
			amount--;
			if (--readlimit < 0)
			{
				marked = -1;
				markedAmount = -1;
			}
			return b;
		}

		@Override
		public int available() throws IOException
		{
			return amount;
		}
		
		@Override
		public synchronized void mark(int limit)
		{
			marked = offset;
			markedAmount = amount;
			readlimit = limit;
		}
		
		@Override
		public synchronized void reset() throws IOException
		{
			if (marked < 0)
				throw new IOException("mark() not called or read limit expired.");
			
			offset = marked;
			amount = markedAmount;

			marked = -1;
			markedAmount = -1;
			readlimit = -1;
		}
		
		@Override
		public boolean markSupported()
		{
			return true;
		}
		
	}
	
}
