/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.List;
import com.blackrook.commons.trie.CaseInsensitiveTrieMap;


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
	public void refreshEntries()
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
		List<String> outList = new List<String>();
		entryTable.getKeysAfterKey(key, outList);
		outList.sort(0, outList.size());
		String[] out = new String[outList.size()];
		outList.toArray(out);
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
	 * Gets the data in one entry in the PK3 by entry name (path and all).
	 * @param entry the entry to extract and return as a byte array.
	 * @return a byte array of the entry's data.
	 * @throws IOException if a read error occurs. 
	 */
	public byte[] getData(String entry) throws IOException
	{
		return getData(getEntry(entry));
	}

	/**
	 * Gets the data in one entry in the PK3.
	 * @param entry the entry to extract and return as a byte array.
	 * @return a byte array of the entry's data.
	 * @throws IOException if a read error occurs. 
	 */
	public byte[] getData(ZipEntry entry) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in = getInputStream(entry);
		Common.relay(in, bos);
		Common.close(in);
		return bos.toByteArray();
	}

	/**
	 * Gets the data in one entry in the PK3 as an input stream.
	 * The data is extracted fully before it is returned as a stream.
	 * @param entry the entry to extract and return as a byte array.
	 * @return an InputStream of the entry's data.
	 * @throws IOException if a read error occurs. 
	 */
	public InputStream getInputStream(ZipEntry entry) throws IOException
	{
		return new ByteArrayInputStream(getData(entry));
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
		return new ByteArrayInputStream(getData(entry));
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
