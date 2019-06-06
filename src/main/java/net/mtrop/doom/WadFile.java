/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mtrop.doom.enums.WadType;
import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.util.NameUtils;
import net.mtrop.doom.util.SerialWriter;
import net.mtrop.doom.util.SerializerUtils;
import net.mtrop.doom.util.Utils;

/**
 * The class that reads WadFile information and provides random access to Wad files.
 * <p>
 * Use of this class is recommended for reading WAD information or small additions of data, as the overhead needed to
 * do so is minimal in this class. Bulk reads/additions/writes/changes are best left for the {@link WadBuffer} class. 
 * Many writing I/O operations will cause the opened file to be changed many times, the length of time of 
 * which being dictated by the length of the entry list (as the list grows, so does the time it takes to write/change it).
 * @author Matthew Tropiano
 */
public class WadFile implements Wad, AutoCloseable
{
	/** File handle. */
	private RandomAccessFile file;
	
	/** WAD File's name (equivalent to File.getName()). */
	private String fileName;
	/** WAD File's path (equivalent to File.getPath()). */
	private String filePath;
	/** WAD File's absolute path (equivalent to File.getAbsolutePath()). */
	private String fileAbsolutePath;
	
	/** List of this Wad's entries. */
	private List<WadEntry> entries;

	/** Type of Wad File (IWAD or PWAD). */
	private WadType type;

	/** Offset of the beginning of the entry list. */
	private int entryListOffset;
	
	/**
	 * Opens a WadFile from a file specified by "path."
	 * @param path	the path to the File;
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if <code>path</code> is null.
	 */
	public WadFile(String path) throws IOException
	{
		this(new File(path));
	}

	/**
	 * Opens a WadFile from a file.
	 * @param f	the file.
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if <code>f</code> is null.
	 */
	public WadFile(File f) throws IOException
	{
		if (!f.exists())
			throw new FileNotFoundException(f.getPath() + " does not exist!");
		
		file = new RandomAccessFile(f,"rws");
		byte[] buffer = new byte[4];

		// read header
		file.read(buffer);
		String head = new String(buffer,"ASCII");
		if (!head.equals(WadType.IWAD.toString()) && !head.equals(WadType.PWAD.toString()))
			throw new WadException("Not a Wad file or supported Wad file type.");

		if (head.equals(WadType.IWAD.toString()))
			type = WadType.IWAD;
			
		if (head.equals(WadType.PWAD.toString()))
			type = WadType.PWAD;
		
		fileName = f.getName();
		filePath = f.getPath();
		fileAbsolutePath = f.getAbsolutePath();
		
		file.read(buffer);
		int size = SerializerUtils.bytesToInt(buffer, Utils.LITTLE_ENDIAN);

		file.read(buffer);
		entryListOffset = SerializerUtils.bytesToInt(buffer, Utils.LITTLE_ENDIAN);
		
		this.entries = new ArrayList<WadEntry>((size + 1) * 2);
		
		// seek to entry list.
		file.seek(entryListOffset);
		
		// read entries.
		byte[] entrybytes = new byte[16];
		for (int i = 0; i < size; i++)
		{
			file.read(entrybytes);
			WadEntry entry = WadEntry.create(entrybytes);
			if (entry.getName().length() > 0 || entry.getSize() > 0)
				entries.add(entry);
		}
	}

	private void writeEntryList() throws IOException
	{
		file.seek(entryListOffset);
		for (WadEntry wfe : entries)
			file.write(wfe.toBytes());
		if (file.getFilePointer() < file.length())
			file.setLength(file.getFilePointer());
	}

	private void writeHeader() throws IOException
	{
		file.seek(4);
		byte[] b = new byte[4];
		SerializerUtils.intToBytes(entries.size(), SerializerUtils.LITTLE_ENDIAN, b, 0);
		file.write(b);
		SerializerUtils.intToBytes(entryListOffset, SerializerUtils.LITTLE_ENDIAN, b, 0);
		file.write(b);
	}

	/**
	 * Creates a new, empty WadFile and returns a reference to it.
	 * @param path	the path of the new file in the form of a String.
	 * @return		a reference to the newly created WadFile, already open.
	 * @throws IOException if the file can't be written.
	 * @throws NullPointerException if <code>path</code> is null.
	 */
	public static WadFile createWadFile(String path) throws IOException
	{
		return createWadFile(new File(path));
	}

	/**
	 * Creates a new, empty WadFile (PWAD Type) and returns a reference to it.
	 * @param f		the file object referring to the new Wad.
	 * @return		a reference to the newly created WadFile, already open.
	 * @throws IOException if the file can't be written.
	 * @throws NullPointerException if <code>f</code> is null.
	 */
	public static WadFile createWadFile(File f) throws IOException
	{
		FileOutputStream fo = new FileOutputStream(f);
		SerialWriter sw = new SerialWriter(SerialWriter.LITTLE_ENDIAN);
		sw.writeBytes(fo, WadType.PWAD.name().getBytes("ASCII"));
		sw.writeInt(fo, 0);		// number of entries.
		sw.writeInt(fo, 12);	// offset to entry list.
		fo.close();
		try{
			return new WadFile(f);
		} catch (WadException e) {
			throw new RuntimeException("INTERNALL ERROR.");
		}
	}
	
	/**
	 * Returns this Wad's file name. 
	 * @return this file's name (and just the name).
	 * @see File#getName()
	 */
	public String getFileName()
	{
		return fileName;
	}
	
	/**
	 * Gets this Wad's file path. 
	 * @return this file's path.
	 * @see File#getPath()
	 */
	public final String getFilePath()
	{
		return filePath;
	}

	/**
	 * Returns this Wad's file absolute path. 
	 * @return this file's name (and just the name).
	 * @see File#getAbsolutePath()
	 */
	public final String getFileAbsolutePath()
	{
		return fileAbsolutePath;
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
	public WadEntry getEntry(int n)
	{
		return entries.get(n);
	}

	@Override	
	public byte[] getData(WadEntry entry) throws IOException
	{
		byte[] out = new byte[entry.getSize()];
		try {
			file.seek(entry.getOffset());
			file.read(out, 0, entry.getSize());
		} catch (IndexOutOfBoundsException e) {
			throw new IOException(e);
		}
		return out;
	}

	@Override
	public void deleteEntry(int n) throws IOException
	{
		// get removed WadEntry.
		WadEntry entry = entries.remove(n);
		if (entry == null)
			throw new IOException("Index is out of range.");
	
		byte[] buffer = new byte[65536];
		int offset = entry.getOffset();
		int dataOffset = entry.getOffset() + entry.getSize();
	
		while (dataOffset < entryListOffset)
		{
			int amount = Math.min(entryListOffset - dataOffset, buffer.length);
			file.seek(dataOffset);
			int readAmount = file.read(buffer, 0, amount);
			file.seek(offset);
			file.write(buffer, 0, readAmount);
			offset += readAmount;
			dataOffset += readAmount;
		}
	
		entryListOffset = dataOffset;
	
		// adjust offsets from last WadEntry.
		for (int i = n; i < entries.size(); i++)
		{
			WadEntry e = entries.get(i);
			e.offset -= entry.getSize();
		}
	
		writeHeader();
		writeEntryList();
	}

	@Override
	public void renameEntry(int index, String newName) throws IOException
	{
		WadEntry entry = getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		if (!NameUtils.isValidEntryName(newName))
			throw new IllegalArgumentException("Entry name \""+newName+"\" does not fit entry requirements.");
		
		entry.name = newName;
	
		// update in file.
		file.seek(entryListOffset + (16 * index) + 8);
		file.write(entry.getNameBytes());
	}

	@Override
	public void replaceEntry(int index, byte[] data) throws IOException
	{
		WadEntry entry = getEntry(index);
		if (entry == null)
			throw new IOException("Index is out of range.");
		
		deleteEntry(index);
		
		String name = entry.getName();
		addDataAt(index, name, data);
	}

	@Override
	public void unmapEntries(int startIndex, WadEntry... entryList) throws IOException
	{
		for (int i = 0; i < entryList.length; i++)
			entries.set(startIndex + i, entryList[i]);
		writeHeader();
		writeEntryList();
	}

	@Override
	public void setEntries(WadEntry... entryList) throws IOException
	{
		entries.clear();
		for (WadEntry WadEntry : entryList)
			entries.add(WadEntry);
		writeHeader();
		writeEntryList();
	}

	@Override
	public WadEntry addData(String entryName, byte[] data) throws IOException
	{
		WadEntry entry = WadEntry.create(entryName, entryListOffset, data.length);
		entries.add(entry);

		file.seek(entryListOffset);
		file.write(data);
		entryListOffset += data.length;
		writeHeader();
		writeEntryList();
		return entry;
	}

	@Override
	public WadEntry addDataAt(int index, String entryName, byte[] data) throws IOException
	{
		WadEntry entry = WadEntry.create(entryName, entryListOffset, data.length);
		entries.add(index, entry);

		file.seek(entryListOffset);
		file.write(data);
		entryListOffset += data.length;
		writeHeader();
		writeEntryList();
		return entry;
	}

	@Override
	public WadEntry[] addAllData(String[] entryNames, byte[][] data) throws IOException
	{
		int curOffs = entryListOffset; 
		WadEntry[] out = new WadEntry[entryNames.length];
		for (int i = 0; i < entryNames.length; i++)
		{
			out[i] = WadEntry.create(entryNames[i], curOffs, data[i].length);
			curOffs += data[i].length;
		}
		
		for (WadEntry we : out)
			entries.add(we);

		file.seek(entryListOffset);
		
		for (int i = 0; i < entryNames.length; i++)
		{
			file.write(data[i]);
			entryListOffset += data[i].length;
		}

		writeHeader();
		writeEntryList();
		return out;
	}

	@Override
	public WadEntry[] addAllDataAt(int index, String[] entryNames, byte[][] data) throws IOException
	{
		int curOffs = entryListOffset; 
		WadEntry[] out = new WadEntry[entryNames.length];
		for (int i = 0; i < entryNames.length; i++)
		{
			out[i] = WadEntry.create(entryNames[i], curOffs, data[i].length);
			curOffs += data[i].length;
		}
		
		for (WadEntry we : out)
			entries.add(index++, we);

		file.seek(entryListOffset);
		
		for (int i = 0; i < entryNames.length; i++)
		{
			file.write(data[i]);
			entryListOffset += data[i].length;
		}

		writeHeader();
		writeEntryList();
		return out;
	}

	/**
	 * Gets the type of WAD that this is.
	 * @return the WAD type.
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

	@Override
	public void close() throws IOException
	{
		file.close();
	}

}