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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.struct.DataList;
import net.mtrop.doom.struct.io.IOUtils;
import net.mtrop.doom.struct.io.SerialReader;
import net.mtrop.doom.util.NameUtils;

/**
 * An implementation of Wad where any and all WAD information is manipulated in memory.
 * This loads everything in the WAD into memory as uninterpreted raw bytes.
 * <p>WadBuffer operations are not thread-safe!
 * @author Matthew Tropiano
 */
public class WadBuffer implements Wad
{
	private static final Charset ASCII = Charset.forName("ASCII");
	
	/** Type of Wad File (IWAD or PWAD). */
	private Type type;
	/** Header buffer. */
	private ByteBuffer headerBuffer;
	/** The data itself (including header). */
	private DataList content;
	/** The list of entries. */
	private List<WadEntry> entries;
	
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
		this.headerBuffer = ByteBuffer.allocate(12);
		this.headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
		this.content = new DataList();
		this.entries = new ArrayList<WadEntry>();
		
		headerBuffer.put(type.name().getBytes(ASCII));
		headerBuffer.putInt(0);			// no entries.
		headerBuffer.putInt(12);		// entry list offset (12).
		content.append(headerBuffer.array());	
	}
	
	private void updateHeader()
	{
		headerBuffer.rewind();
		headerBuffer.put(type.name().getBytes(ASCII));
		headerBuffer.putInt(entries.size());
		headerBuffer.putInt(content.size());
		content.setData(0, headerBuffer.array());
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
	 * Creates a new WadBuffer from a subset of entries (and their data) from another Wad.
	 * @param source the the source Wad.
	 * @param startIndex the starting entry index.
	 * @param maxLength the maximum amount of entries from the starting index to copy.
	 * @return a new WadBuffer that only contains the desired entries, plus their data.
	 * @throws IOException if an error occurs on read from the source Wad.
	 * @since 2.1.0
	 */
	public static WadBuffer extract(Wad source, int startIndex, int maxLength) throws IOException
	{
		return extract(source, source.mapEntries(startIndex, maxLength));
	}

	/**
	 * Creates a new WadBuffer from a subset of entries (and their data) from another Wad. 
	 * @param source the the source Wad.
	 * @param entries the entries to copy over.
	 * @return a new WadBuffer that only contains the desired entries, plus their data.
	 * @throws IOException if an error occurs on read from the source Wad.
	 * @since 2.1.0
	 */
	public static WadBuffer extract(Wad source, WadEntry ... entries) throws IOException
	{
		WadBuffer out = new WadBuffer(Type.PWAD);
		for (int i = 0; i < entries.length; i++)
		{
			try (InputStream in = source.getInputStream(entries[i]))
			{
				out.addDataAt(out.getEntryCount(), entries[i].getName(), in);
			}
		}
		return out;
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

		// Add offset dummy data - 12 bytes, updated later.
		content.append(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});
		
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
			WadEntry entry = WadEntry.create(entrybuffer);
			entries.add(entry);
		}
		updateHeader();
	}
	
	/**
	 * Sets the type of WAD that this is.
	 * @param type the new type.
	 */
	public final void setType(Type type)
	{
		this.type = type;
		updateHeader();
	}

	/**
	 * Gets the type of WAD that this is.
	 * @return the wad type.
	 */
	public final Type getType()
	{
		return type;
	}

	/**
	 * Writes the contents of this buffer out to an output stream in Wad format.
	 * Does not close the stream.
	 * @param out the output stream to write to.
	 * @throws IOException if a problem occurs during the write.
	 * @throws NullPointerException if <code>out</code> is null.
	 */
	public final void writeToStream(OutputStream out) throws IOException
	{
		// write content (contains header).
		out.write(content.toByteArray(), 0, content.size());

		// write entry list.
		for (WadEntry entry : entries)
			entry.writeBytes(out);
	}
	
	/**
	 * Writes the contents of this buffer out to a file in Wad format.
	 * The target file will be overwritten.
	 * @param path the file path to write to.
	 * @throws IOException if a problem occurs during the write.
	 * @throws SecurityException if you don't have permission to write the file.
	 * @throws NullPointerException if <code>out</code> is null.
	 * @since 2.6.0
	 */
	public final void writeToFile(String path) throws IOException
	{
		writeToFile(new File(path));
	}
	
	/**
	 * Writes the contents of this buffer out to a file in Wad format.
	 * The target file will be overwritten.
	 * @param path the file to write to.
	 * @throws IOException if a problem occurs during the write.
	 * @throws SecurityException if you don't have permission to write the file.
	 * @throws NullPointerException if <code>out</code> is null.
	 */
	public final void writeToFile(File path) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(path);
		writeToStream(fos);
		IOUtils.close(fos);
	}
	
	@Override
	public int getContentLength()
	{
		return content.size() - 12;
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
	public int getEntryCount()
	{
		return entries.size();
	}

	@Override	
	public WadEntry getEntry(int n)
	{
		return entries.get(n);
	}

	@Override
	public void fetchContent(int offset, int length, byte[] dest, int destOffset) throws IOException
	{
		content.getData(offset, dest, destOffset, length);
	}

	@Override
	public InputStream getInputStream(WadEntry entry) throws IOException
	{
		return new WadBufferInputStream(entry.getOffset(), entry.getSize());
	}

	@Override
	public WadEntry removeEntry(int n) throws IOException
	{
		WadEntry out = entries.remove(n);
		updateHeader();
		return out;		
	}

	@Override
	public WadEntry deleteEntry(int n) throws IOException
	{
		// get removed WadEntry.
		WadEntry entry = removeEntry(n);
		if (entry.getSize() > 0)
		{
			content.delete(entry.getOffset(), entry.getSize());
			
			// adjust offsets.
			for (int i = 0; i < entries.size(); i++)
			{
				WadEntry e = entries.get(i);
				if (e.getOffset() > entry.getOffset())
					entries.set(i, e.withNewOffset(e.getOffset() - entry.getSize()));
			}
			updateHeader();
		}
		return entry;
	}

	@Override
	public void renameEntry(int index, String newName) throws IOException
	{
		WadEntry entry = getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		NameUtils.checkValidEntryName(newName);
		
		entries.set(index, entry.withNewName(newName));
	}

	@Override
	public void replaceEntry(int index, byte[] data) throws IOException
	{
		WadEntry entry = getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		if (data.length != entry.getSize())
		{
			deleteEntry(index);
			String name = entry.getName();
			addDataAt(index, name, data);
			updateHeader();
		}
		else
		{
			content.setData(entry.getOffset(), data);
		}
	}

	@Override
	public void unmapEntries(int startIndex, WadEntry... entryList) throws IOException
	{
		for (int i = 0; i < entryList.length; i++)
		{
			if (startIndex + i >= entries.size())
				entries.add(entryList[i]);
			else
				entries.set(startIndex + i, entryList[i]);
		}
	}

	@Override
	public void setEntries(WadEntry... entryList) throws IOException
	{
		entries.clear();
		for (WadEntry WadEntry : entryList)
			entries.add(WadEntry);
		updateHeader();
	}

	@Override
	public WadEntry addEntry(String entryName, int offset, int length) throws IOException
	{
		WadEntry entry = WadEntry.create(entryName, offset, length);
		entries.add(entry);
		updateHeader();
		return entry;
	}

	@Override
	public WadEntry addDataAt(int index, String entryName, InputStream in, int maxLength) throws IOException
	{
		int offset = content.size();
		int len = IOUtils.relay(in, content, maxLength);
		WadEntry entry = WadEntry.create(entryName, offset, len);
		entries.add(index, entry);
		updateHeader();
		return entry;
	}

	/**
	 * @deprecated [NOW] - The reason why this method was added in the first place was to have a bulk add operation that incurred hopefully
	 * less transaction overhead in implementations. In WadBuffer, the performance overhead was already moot, and WadFile has methods
	 * that delay the writing of the entry list, which, although less "safe," solves this problem by allowing the user
	 * to defer the final write via {@link WadFile#flushEntries()}.
	 */
	@Override
	public WadEntry[] addAllDataAt(int index, String[] entryNames, byte[][] data) throws IOException
	{
		WadEntry[] out = new WadEntry[entryNames.length];
		for (int i = 0; i < entryNames.length; i++)
			out[i] = addDataAt(index + i, entryNames[i], data[i]);
		updateHeader();
		return out;
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
