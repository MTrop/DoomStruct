/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.mtrop.doom.util.Utils;
import net.mtrop.doom.util.trie.CaseInsensitiveTrieMap;

/**
 * Doom PK3 file. Contains a whole bunch of Doom resources,
 * however, they are all zipped together. This class contains
 * a bunch of methods for getting file information from the PK3,
 * but if you want to read explicit info from it, see {@link ZipFile}.
 * @author Matthew Tropiano
 */
public class DoomPK3 extends ZipFile
{
	/** File path. */
	private String filePath;
	/** File name. */
	private String fileName;
	
	/** Hashtable of PK3 directories and lists of internal files. */
	private CaseInsensitiveTrieMap<ZipEntry> entryTable;
	
	/**
	 * Opens a DoomPK3 file for reading and caches its contents.
	 * @param path the path to the file to open.		
	 * @throws ZipException if this is not a PK3/ZIP archive.
	 * @throws IOException if the file cannot be read.
	 */
	public DoomPK3(String path) throws ZipException, IOException
	{
		this(new File(path));
	}
	
	/**
	 * Opens a DoomPK3 file for reading and caches its contents.
	 * @param pk3File the file to open.		
	 * @throws ZipException if this is not a PK3/ZIP archive.
	 * @throws IOException if the file cannot be read.
	 */
	public DoomPK3(File pk3File) throws ZipException, IOException
	{
		super(pk3File);
		entryTable = new CaseInsensitiveTrieMap<ZipEntry>();
		filePath = pk3File.getPath();
		fileName = pk3File.getName();
		refreshEntries();
	}
	
	/**
	 * Gets this file's path.
	 * @return this file's path.
	 * @see File#getPath()
	 */
	public final String getFilePath()
	{
		return filePath;
	}

	/**
	 * Gets this file's name (and just the name).
	 * @return this file's name (and just the name).
	 * @see File#getName()
	 */
	public final String getFileName()
	{
		return fileName;
	}

	/**
	 * Refreshes the entry lists, if ZIP contents changed.
	 */
	public final void refreshEntries()
	{
		CaseInsensitiveTrieMap<ZipEntry> entryList = new CaseInsensitiveTrieMap<ZipEntry>();
		Enumeration<? extends ZipEntry> entries = entries();
		while (entries.hasMoreElements())
		{
			ZipEntry ze = entries.nextElement();
			if (ze.isDirectory())
				continue;
			String entryName = ze.getName();
			entryList.put(entryName, ze);
		}
		entryTable = entryList;
	}

	/**
	 * Returns a list of all entries that start with a type of key.
	 * This is treated case-insensitively.
	 * @param key the start of an entry.
	 * @return an array of entry names starting with the key string.  
	 */
	public String[] getEntriesStartingWith(String key)
	{
		List<String> outList = new ArrayList<String>();
		entryTable.getKeysAfterKey(key, outList);
		String[] out = new String[outList.size()];
		outList.toArray(out);
		Arrays.sort(out);
		return out;
	}
	
	/**
	 * @return the amount of entries in this archive. 
	 */
	public int getEntryCount()
	{
		return entryTable.size();
	}
	
	/**
	 * Checks if this Wad contains a particular entry, false otherwise.
	 * <p>The name is case-insensitive.
	 * @param entryName the name of the entry.
	 * @return true if so, false if not.
	 */
	public boolean contains(String entryName)
	{
		return getEntry(entryName) != null;
	}

	/**
	 * Gets the data in one entry in the PK3 by entry name (path and all).
	 * @param entry the entry to extract and return as a byte array.
	 * @return a byte array of the entry's data, or null if no corresponding entry.
	 * @throws IOException if a read error occurs.
	 * @throws ZipException if a ZIP format error has occurred
	 * @throws IllegalStateException if the zip file has been closed 
	 */
	public byte[] getData(String entry) throws IOException
	{
		ZipEntry zentry = getEntry(entry);
		return zentry != null ? getData(zentry) : null;
	}

	/**
	 * Gets the data in one entry in the PK3.
	 * @param entry the entry to extract and return as a byte array.
	 * @return a byte array of the entry's data.
	 * @throws IOException if a read error occurs.
	 * @throws ZipException if a ZIP format error has occurred
	 * @throws IllegalStateException if the zip file has been closed 
	 */
	public byte[] getData(ZipEntry entry) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in = getInputStream(entry);
		Utils.relay(in, bos);
		Utils.close(in);
		return bos.toByteArray();
	}

	/**
	 * Gets the data in one entry in the PK3 as an input stream by entry name (path and all).
	 * The data is extracted fully before it is returned as a stream.
	 * @param entry the entry to extract and return as a byte array.
	 * @return an InputStream of the entry's data.
	 * @throws IOException if a read error occurs. 
	 */
	public InputStream getInputStream(String entry) throws IOException
	{
		ZipEntry zentry = getEntry(entry);
		return zentry != null ? getInputStream(zentry) : null;
	}
	
	/**
	 * Retrieves the data of the first occurrence of a particular entry as a decoded string of characters.
	 * <p>The name is case-insensitive.
	 * @param entryName the name of the entry to find.
	 * @param charset the source charset.
	 * @return the data, decoded, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	public String getTextData(String entryName, Charset charset) throws IOException
	{
		byte[] data = getData(entryName);
		return data != null ? new String(data, charset) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry as a deserialized lump.
	 * <p>The name is case-insensitive.
	 * @param <BO> a type that extends BinaryObject.
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @return the data, deserialized, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	public <BO extends BinaryObject> BO getDataAs(String entryName, Class<BO> type) throws IOException
	{
		byte[] data = getData(entryName);
		return data != null ? BinaryObject.create(type, data) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry as a deserialized lump.
	 * <p>The name is case-insensitive.
	 * @param <BO> a type that extends BinaryObject.
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each individual object in bytes.
	 * @return the data, deserialized, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	public <BO extends BinaryObject> BO[] getDataAs(String entryName, Class<BO> type, int objectLength) throws IOException
	{
		byte[] data = getData(entryName);
		return data != null ? BinaryObject.create(type, data, data.length / objectLength) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry and returns it as 
	 * a deserializing scanner iterator that returns independent instances of objects.
	 * <p>The name is case-insensitive.
	 * @param <BO> a type that extends BinaryObject.
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data, or null if the entry can't be found.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 */
	public <BO extends BinaryObject> BinaryObject.Scanner<BO> getScanner(String entryName, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(entryName);
		return in != null ? BinaryObject.scanner(type, in, objectLength) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry and returns it as 
	 * a deserializing scanner iterator that returns the same object instance with its contents changed.
	 * <p>This is useful for when you would want to quickly scan through a set of serialized objects while
	 * ensuring low memory use. Do NOT store the references returned by <code>next()</code> anywhere as the contents
	 * of that reference will be changed by the next call to <code>next()</code>.
	 * <p>The name is case-insensitive.
	 * @param <BO> a type that extends BinaryObject.
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data, or null if the entry can't be found.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 */
	public <BO extends BinaryObject> BinaryObject.InlineScanner<BO> getInlineScanner(String entryName, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(entryName);
		return in != null ? BinaryObject.inlineScanner(type, in, objectLength) : null;
	}

	/**
	 * Gets the "entry name" for a ZipEntry, which is just the filename itself minus extension.
	 * @param ze the ZipEntry to use.
	 * @return the "entry name" for this ZipEntry.
	 */
	public static String getEntryName(ZipEntry ze)
	{
		String name = ze.getName();
		int pindex = name.lastIndexOf("/")+1;
		int eindex = name.lastIndexOf(".");
		if (eindex > -1)
			return name.substring(pindex, eindex);
		else
			return name.substring(pindex);
	}
	
}