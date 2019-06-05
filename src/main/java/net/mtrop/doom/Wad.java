/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Base interface for all WAD file type implementations for reading and writing to WAD structures, either in memory or
 * on disk.
 * <p>
 * All entries in a WAD are abstracted as WadEntry objects, which contain the name and offsets for the corresponding
 * data in a WAD file. Note that there may be several entries in a WAD that have the same name; entry "equality" should
 * be determined by name, size and offset.
 * <p>
 * Entries are by no means attached to their source WADs. Attempting to retrieve content from one WAD using entry data
 * from another WAD may have unintended consequences!
 * <p>
 * There may be some implementations of this structure that do not support certain operations, so in those cases, those
 * methods may throw an {@link UnsupportedOperationException}. Also, certain implementations may be more suited for
 * better tasks, so be sure to figure out which implementation suits your needs!
 * <p>
 * Most of the common methods are "defaulted" here. Implementors are encouraged to override these if your implementation
 * can provide a more performant version than the one-size-fits-all methods here.
 * 
 * @author Matthew Tropiano
 */
public interface Wad extends Iterable<WadEntry>
{
	static final byte[] NO_DATA = new byte[0];

	/**
	 * Checks if this WAD is an Information WAD.
	 * @return true if so, false if not.
	 */
	boolean isIWAD();
	
	/**
	 * Checks if this WAD is a Patch WAD.
	 * @return true if so, false if not.
	 */
	boolean isPWAD();
	
	/**
	 * @return the number of entries in this Wad.
	 */
	int getSize();

	/**
	 * Gets the WadEntry at index n.
	 * 
	 * @param n the index of the entry in the entry list.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 * @return the entry at <code>n</code>.
	 */
	WadEntry getEntry(int n);

	/**
	 * Gets the first WadEntry named <code>entryName</code>.
	 * 
	 * @param entryName the name of the entry.
	 * @return the first entry named <code>entryName</code> or <code>null</code> if not found.
	 */
	default WadEntry getEntry(String entryName)
	{
		int i = getIndexOf(entryName, 0);
		return i != -1 ? getEntry(i) : null;
	}

	/**
	 * Gets the first WadEntry named <code>entryName</code>, starting from a particular index.
	 * 
	 * @param entryName the name of the entry.
	 * @param startIndex the starting index to search from.
	 * @return the first entry named <code>entryName</code> or <code>null</code> if not found.
	 */
	default WadEntry getEntry(String entryName, int startIndex)
	{
		int i = getIndexOf(entryName, startIndex);
		return i != -1 ? getEntry(i) : null;
	}

	/**
	 * Gets the n-th WadEntry named <code>entryName</code>.
	 * 
	 * @param entryName the name of the entry.
	 * @param n the n-th occurrence to find, 0-based (0 is first, 1 is second, and so on).
	 * @return the n-th entry named <code>entryName</code> or <code>null</code> if not found.
	 */
	default WadEntry getNthEntry(String entryName, int n)
	{
		int x = 0;
		int s = getSize();
		for (int i = 0; i < s; i++)
		{
			WadEntry entry = getEntry(i);
			if (entry.getName().equals(entryName))
			{
				if (x++ == n)
					return entry;
			}
		}
		return null;
	}

	/**
	 * Gets the last WadEntry named <code>entryName</code>.
	 * 
	 * @param entryName the name of the entry.
	 * @return the last entry named <code>entryName</code> or <code>null</code> if not found.
	 */
	default WadEntry getLastEntry(String entryName)
	{
		int s = getSize();
		for (int i = s - 1; i >= 0; i--)
		{
			WadEntry entry = getEntry(i);
			if (entry.getName().equals(entryName))
				return entry;
		}
		return null;
	}


	/**
	 * Returns all WadEntry objects.
	 * 
	 * @return an array of all of the WadEntry objects.
	 */
	default WadEntry[] getAllEntries()
	{
		WadEntry[] out = new WadEntry[getSize()];
		for (int i = 0; i < out.length; i++)
			out[i] = getEntry(i);
		return out;
	}

	/**
	 * Returns all WadEntry objects named <code>entryName</code>.
	 * 
	 * @param entryName the name of the entry.
	 * @return an array of all of the WadEntry objects with the name <code>entryName</code>.
	 */
	default WadEntry[] getAllEntries(String entryName)
	{
		Queue<WadEntry> w = new LinkedList<>();
		
		int s = getSize();
		for (int i = 0; i < s; i++)
		{
			WadEntry entry = getEntry(i);
			if (entry.getName().equals(entryName))
				w.add(entry);
		}
		
		WadEntry[] out = new WadEntry[w.size()];
		w.toArray(out);
		return out;
	}

	/**
	 * Gets the indices of all WadEntry objects named <code>entryName</code>.
	 * 
	 * @param entryName the name of the entry.
	 * @return an array of all of the WadEntry objects with the name <code>entryName</code>.
	 */
	default int[] getAllEntryIndices(String entryName)
	{
		Queue<Integer> w = new LinkedList<Integer>();
		
		int s = getSize();
		for (int i = 0; i < s; i++)
		{
			WadEntry entry = getEntry(i);
			if (entry.getName().equals(entryName))
				w.add(i);
		}
		
		int[] out = new int[w.size()];
		for (int i = 0; i < out.length; i++)
			out[i] = w.poll();
		return out;
	}

	/**
	 * Gets the first index of an entry of name <code>entryName</code>.
	 * 
	 * @param entryName the name of the entry to find.
	 * @return the index of the entry in this file, or -1 if not found.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default int getIndexOf(String entryName)
	{
		return getIndexOf(entryName, 0);
	}

	/**
	 * Gets the first index of an entry of name "entryName" from a starting point.
	 * 
	 * @param entryName the name of the entry to find.
	 * @param start the index with which to start the search.
	 * @return the index of the entry in this file, or -1 if not found.
	 * @throws ArrayIndexOutOfBoundsException if start &lt; 0 or &gt;= size.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default int getIndexOf(String entryName, int start)
	{
		int s = getSize();
		for (int i = start; i < s; i++)
			if (getEntry(i).getName().equals(entryName))
				return i;
		return -1;
	}

	/**
	 * Gets the last index of an entry of name <code>entryName</code>.
	 * 
	 * @param entryName the name of the entry to find.
	 * @return the index of the entry in this file, or -1 if not found.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default int getLastIndexOf(String entryName)
	{
		int out = -1;
		int s = getSize();
		for (int i = 0; i < s; i++)
			if (getEntry(i).getName().equals(entryName))
				out = i;
		return out;
	}

	/**
	 * Retrieves the data of a particular entry index.
	 * 
	 * @param n the index of the entry in the Wad.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 * @return a byte array of the data.
	 */
	default byte[] getData(int n) throws IOException
	{
		return getData(getEntry(n));
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry.
	 * 
	 * @param entryName the name of the entry to find.
	 * @return a byte array of the data, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default byte[] getData(String entryName) throws IOException
	{
		WadEntry entry = getEntry(entryName, 0);
		return entry != null ? getData(entry) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry from a starting index.
	 * 
	 * @param entryName the name of the entry to find.
	 * @param start the index with which to start the search.
	 * @return a byte array of the data, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @throws ArrayIndexOutOfBoundsException if start &lt; 0 or &gt;= size.
	 */
	default byte[] getData(String entryName, int start) throws IOException
	{
		int i = getIndexOf(entryName, start);
		return i != -1 ? getData(i) : null;
	}

	/**
	 * Retrieves the data of the specified entry.
	 * 
	 * @param entry the entry to use.
	 * @return a byte array of the data.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entry</code> is <code>null</code>.
	 */
	byte[] getData(WadEntry entry) throws IOException;

	/**
	 * Retrieves the data of an entry at a particular index as a deserialized lump.
	 * @param n the index of the entry in the Wad.
	 * @param type the class type to deserialize into.
	 * @return the data, deserialized, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO getDataAs(int n, Class<BO> type) throws IOException
	{
		byte[] data = getData(n);
		return data != null ? BinaryObject.create(type, data) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry as a deserialized lump.
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @return the data, deserialized, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO getDataAs(String entryName, Class<BO> type) throws IOException
	{
		byte[] data = getData(entryName);
		return data != null ? BinaryObject.create(type, data) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry from a starting index as a deserialized lump.
	 * @param entryName the name of the entry to find.
	 * @param start the index with which to start the search.
	 * @param type the class type to deserialize into.
	 * @return the data, deserialized, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO getDataAs(String entryName, int start, Class<BO> type) throws IOException
	{
		byte[] data = getData(entryName, start);
		return data != null ? BinaryObject.create(type, data) : null;
	}

	/**
	 * Retrieves the data of the specified entry as a deserialized lump.
	 * @param entry the entry to use.
	 * @param type the class type to deserialize into.
	 * @return the data, deserialized.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entry</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO getDataAs(WadEntry entry, Class<BO> type) throws IOException
	{
		return BinaryObject.create(type, getData(entry));
	}

	/**
	 * Retrieves the data of an entry at a particular index as a deserialized lump.
	 * @param n the index of the entry in the Wad.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each individual object in bytes.
	 * @return the data, deserialized.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO[] getDataAs(int n, Class<BO> type, int objectLength) throws IOException
	{
		byte[] data = getData(n);
		return BinaryObject.create(type, data, data.length / objectLength);
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry as a deserialized lump.
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each individual object in bytes.
	 * @return the data, deserialized, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO[] getDataAs(String entryName, Class<BO> type, int objectLength) throws IOException
	{
		byte[] data = getData(entryName);
		return data != null ? BinaryObject.create(type, data, data.length / objectLength) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry from a starting index as a deserialized lump.
	 * @param entryName the name of the entry to find.
	 * @param start the index with which to start the search.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each individual object in bytes.
	 * @return the data, deserialized, or null if the entry doesn't exist.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO[] getDataAs(String entryName, int start, Class<BO> type, int objectLength) throws IOException
	{
		byte[] data = getData(entryName, start);
		return data != null ? BinaryObject.create(type, data, data.length / objectLength) : null;
	}

	/**
	 * Retrieves the data of the specified entry as a deserialized lump.
	 * @param entry the entry to use.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each individual object in bytes.
	 * @return the data, deserialized.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entry</code> is <code>null</code>.
	 * @see BinaryObject#create(Class, byte[])
	 */
	default <BO extends BinaryObject> BO[] getDataAs(WadEntry entry, Class<BO> type, int objectLength) throws IOException
	{
		byte[] data = getData(entry);
		return BinaryObject.create(type, data, data.length / objectLength);
	}

	/**
	 * Retrieves the data of a particular entry index and returns it as a stream.
	 * 
	 * @param n the index of the entry in the file.
	 * @return an open input stream of the data, or null if it can't be retrieved.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 */
	default InputStream getInputStream(int n) throws IOException
	{
		return getInputStream(getEntry(n));
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry and returns it as a stream.
	 * 
	 * @param entryName the name of the entry to find.
	 * @return an open input stream of the data, or null if it can't be retrieved.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default InputStream getInputStream(String entryName) throws IOException
	{
		int index = getIndexOf(entryName);
		if (index < 0)
			return null;
		return getInputStream(index);
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry from a starting index and returns it as a stream.
	 * 
	 * @param entryName the name of the entry to find.
	 * @param start the index with which to start the search.
	 * @return an open input stream of the data, or null if it can't be retrieved.
	 * @throws IOException if the data couldn't be retrieved.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 * @throws ArrayIndexOutOfBoundsException if start &lt; 0 or &gt;= size.
	 */
	default InputStream getInputStream(String entryName, int start) throws IOException
	{
		int index = getIndexOf(entryName, start);
		if (index < 0)
			return null;
		return getInputStream(index);
	}

	/**
	 * Retrieves the data of the specified entry from a starting index and returns it as a stream.
	 * 
	 * @param entry the entry to use.
	 * @return an open input stream of the data.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entry</code> is <code>null</code>.
	 */
	default InputStream getInputStream(WadEntry entry) throws IOException
	{
		return new ByteArrayInputStream(getData(entry));
	}

	/**
	 * Retrieves the data of a particular entry at a specific index and returns it as 
	 * a deserializing scanner iterator that returns independent instances of objects.
	 * 
	 * @param n the index of the entry.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 */
	default <BO extends BinaryObject> BinaryObject.Scanner<BO> getScanner(int n, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(n);
		return BinaryObject.scanner(type, in, objectLength);
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry and returns it as 
	 * a deserializing scanner iterator that returns independent instances of objects.
	 * 
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data, or null if the entry can't be found.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default <BO extends BinaryObject> BinaryObject.Scanner<BO> getScanner(String entryName, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(entryName);
		return in != null ? BinaryObject.scanner(type, in, objectLength) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry from a starting index and returns it as 
	 * a deserializing scanner iterator that returns independent instances of objects.
	 * 
	 * @param entryName the name of the entry to find.
	 * @param start the index with which to start the search.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data, or null if the entry can't be found.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default <BO extends BinaryObject> BinaryObject.Scanner<BO> getScanner(String entryName, int start, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(entryName, start);
		return in != null ? BinaryObject.scanner(type, in, objectLength) : null;
	}

	/**
	 * Retrieves the data of the specified entry and returns it as 
	 * a deserializing scanner iterator that returns independent instances of objects.
	 * 
	 * @param entry the entry to use.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entry</code> is <code>null</code>.
	 */
	default <BO extends BinaryObject> BinaryObject.Scanner<BO> getScanner(WadEntry entry, Class<BO> type, int objectLength) throws IOException
	{
		return BinaryObject.scanner(type, getInputStream(entry), objectLength);
	}

	/**
	 * Retrieves the data of a particular entry at a specific index and returns it as 
	 * a deserializing scanner iterator that returns the same object instance with its contents changed.
	 * <p>This is useful for when you would want to quickly scan through a set of serialized objects while
	 * ensuring low memory use. Do NOT store the references returned by <code>next()</code> anywhere as the contents
	 * of that reference will be changed by the next call to <code>next()</code>.
	 * 
	 * @param n the index of the entry.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 */
	default <BO extends BinaryObject> BinaryObject.InlineScanner<BO> getInlineScanner(int n, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(n);
		return BinaryObject.inlineScanner(type, in, objectLength);
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry and returns it as 
	 * a deserializing scanner iterator that returns the same object instance with its contents changed.
	 * <p>This is useful for when you would want to quickly scan through a set of serialized objects while
	 * ensuring low memory use. Do NOT store the references returned by <code>next()</code> anywhere as the contents
	 * of that reference will be changed by the next call to <code>next()</code>.
	 * 
	 * @param entryName the name of the entry to find.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data, or null if the entry can't be found.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default <BO extends BinaryObject> BinaryObject.InlineScanner<BO> getInlineScanner(String entryName, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(entryName);
		return in != null ? BinaryObject.inlineScanner(type, in, objectLength) : null;
	}

	/**
	 * Retrieves the data of the first occurrence of a particular entry from a starting index and returns it as 
	 * a deserializing scanner iterator that returns the same object instance with its contents changed.
	 * <p>This is useful for when you would want to quickly scan through a set of serialized objects while
	 * ensuring low memory use. Do NOT store the references returned by <code>next()</code> anywhere as the contents
	 * of that reference will be changed by the next call to <code>next()</code>.
	 * 
	 * @param entryName the name of the entry to find.
	 * @param start the index with which to start the search.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data, or null if the entry can't be found.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entryName</code> is <code>null</code>.
	 */
	default <BO extends BinaryObject> BinaryObject.InlineScanner<BO> getInlineScanner(String entryName, int start, Class<BO> type, int objectLength) throws IOException
	{
		InputStream in = getInputStream(entryName, start);
		return in != null ? BinaryObject.inlineScanner(type, in, objectLength) : null;
	}

	/**
	 * Retrieves the data of the specified entry and returns it as a 
	 * deserializing scanner iterator that returns the same object instance with its contents changed.
	 * <p>This is useful for when you would want to quickly scan through a set of serialized objects while
	 * ensuring low memory use. Do NOT store the references returned by <code>next()</code> anywhere as the contents
	 * of that reference will be changed by the next call to <code>next()</code>.
	 * 
	 * @param entry the entry to use.
	 * @param type the class type to deserialize into.
	 * @param objectLength the length of each object in the entry in bytes.
	 * @return a scanner for the data.
	 * @throws IOException if the data couldn't be retrieved or the entry's offsets breach the file extents.
	 * @throws NullPointerException if <code>entry</code> is <code>null</code>.
	 */
	default <BO extends BinaryObject> BinaryObject.InlineScanner<BO> getInlineScanner(WadEntry entry, Class<BO> type, int objectLength) throws IOException
	{
		return BinaryObject.inlineScanner(type, getInputStream(entry), objectLength);
	}

	/**
	 * Checks if this Wad contains a particular entry, false otherwise.
	 * The name is case-sensitive. 
	 * @param entryName the name of the entry.
	 * @return true if so, false if not.
	 */
	default boolean contains(String entryName)
	{
		return getIndexOf(entryName, 0) > -1;
	}

	/**
	 * Checks if this Wad contains a particular entry from a starting entry index, false otherwise.
	 * The name is case-sensitive. 
	 * 
	 * @param entryName the name of the entry.
	 * @param index the index 
	 * @return true if so, false if not.
	 */
	default boolean contains(String entryName, int index)
	{
		return getIndexOf(entryName, 0) > -1;
	}

	/**
	 * Adds data to this Wad, using entryName as the name of the entry. The overhead for multiple additions may be
	 * expensive I/O-wise depending on the DoomWad implementation.
	 * 
	 * @param entryName the name of the entry to add this as.
	 * @param data the bytes of data to add as this wad's data.
	 * @return a WadEntry that describes the added data.
	 * @throws IllegalArgumentException if the provided name is not a valid name.
	 * @throws IOException if the data cannot be written.
	 * @throws NullPointerException if <code>entryName</code> or <code>data</code> is <code>null</code>.
	 */
	WadEntry addData(String entryName, byte[] data) throws IOException;

	/**
	 * Adds data to this Wad at a particular entry offset, using entryName as the name of the entry. The rest of the
	 * entries in the wad are shifted down one index. The overhead for multiple additions may be expensive I/O-wise
	 * depending on the DoomWad implementation.
	 * 
	 * @param index the index at which to add the entry.
	 * @param entryName the name of the entry to add this as.
	 * @param data the bytes of data to add as this wad's data.
	 * @return a WadEntry that describes the added data.
	 * @throws IllegalArgumentException if the provided name is not a valid name.
	 * @throws IOException if the data cannot be written.
	 * @throws NullPointerException if <code>entryName</code> or <code>data</code> is <code>null</code>.
	 */
	WadEntry addDataAt(int index, String entryName, byte[] data) throws IOException;

	/**
	 * Adds multiple entries of data to this Wad, using entryNames as the name of the entry, using the same indices
	 * in the data array as the corresponding data.
	 * 
	 * @param entryNames the names of the entries to add.
	 * @param data the bytes of data to add as each entry's data.
	 * @return an array of WadEntry objects that describe the added data.
	 * @throws IOException if the data cannot be written.
	 * @throws ArrayIndexOutOfBoundsException if the lengths of entryNames and data do not match.
	 * @throws NullPointerException if an object if <code>entryNames</code> or <code>data</code> is <code>null</code>.
	 */
	WadEntry[] addAllData(String[] entryNames, byte[][] data) throws IOException;

	/**
	 * Adds multiple entries of data to this Wad at a particular entry offset, using entryNames as the name 
	 * of the entry, using the same indices in the data array as the corresponding data.
	 * 
	 * @param index the index to add these entries at.
	 * @param entryNames the names of the entries to add.
	 * @param data the bytes of data to add as each entry's data.
	 * @return an array of WadEntry objects that describe the added data.
	 * @throws IOException if the data cannot be written.
	 * @throws ArrayIndexOutOfBoundsException if the lengths of entryNames and data do not match.
	 * @throws NullPointerException if an object if <code>entryNames</code> or <code>data</code> is <code>null</code>.
	 */
	WadEntry[] addAllDataAt(int index, String[] entryNames, byte[][] data) throws IOException;

	/**
	 * Adds an entry marker to the Wad (entry with 0 size, arbitrary offset).
	 * 
	 * @param name the name of the entry.
	 * @return the entry that was added.
	 * @throws IllegalArgumentException if the provided name is not a valid name.
	 * @throws IOException if the entry cannot be written.
	 * @throws NullPointerException if <code>name</code> is <code>null</code>.
	 */
	default WadEntry addMarker(String name) throws IOException
	{
		return addData(name, NO_DATA);
	}

	/**
	 * Adds an entry marker to the Wad (entry with 0 size, arbitrary offset).
	 * 
	 * @param index the index at which to add the marker.
	 * @param name the name of the entry.
	 * @return the entry that was added.
	 * @throws IllegalArgumentException if the provided name is not a valid name.
	 * @throws IOException if the entry cannot be written.
	 * @throws NullPointerException if <code>name</code> is <code>null</code>.
	 */
	default WadEntry addMarkerAt(int index, String name) throws IOException
	{
		return addDataAt(index, name, NO_DATA);
	}

	/**
	 * Replaces the entry at an index in the WAD.
	 * 
	 * @param index the index of the entry to replace.
	 * @param data the data to replace the entry with.
	 * @throws IllegalArgumentException if the provided name is not a valid name.
	 * @throws IOException if the entry cannot be written.
	 * @throws NullPointerException if <code>data</code> is <code>null</code>.
	 */
	void replaceEntry(int index, byte[] data) throws IOException;

	/**
	 * Renames the entry at an index in the WAD.
	 * 
	 * @param index the index of the entry to rename.
	 * @param newName the new name of the entry.
	 * @throws IllegalArgumentException if the provided name is not a valid name.
	 * @throws IOException if the entry cannot be renamed.
	 */
	void renameEntry(int index, String newName) throws IOException;

	/**
	 * Deletes a Wad's entry and its contents. The overhead for multiple deletions may be expensive I/O-wise.
	 * 
	 * @param n the index of the entry to delete.
	 * @throws ArrayIndexOutOfBoundsException if n &lt; 0 or &gt;= size.
	 * @throws IOException if the file cannot be altered in such a manner.
	 */
	void deleteEntry(int n) throws IOException;

	/**
	 * Retrieves a contiguous set of entries from this Wad, starting from a desired index. If the amount of entries
	 * desired goes outside the Wad's potential set of entries, this will retrieve up to those entries (for example,
	 * <code>mapEntries(5, 10)</code> in an 8-entry Wad will only return 3 entries: 5, 6, and 7).
	 * 
	 * @param startIndex the starting index to map from (inclusive).
	 * @param maxLength the amount of entries to retrieve from the index position.
	 * @return an array of references to {@link WadEntry} objects.
	 * @throws IllegalArgumentException if startIndex is less than 0.
	 */
	default WadEntry[] mapEntries(int startIndex, int maxLength)
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

	/**
	 * Replaces a series of WadEntry objects in this Wad, using the provided list of entries as the replacement list. If
	 * the list of entries plus the starting index would breach the original list of entries, the excess is appended to
	 * the Wad.
	 * 
	 * @param startIndex the starting index to replace from (inclusive).
	 * @param entryList the set of entries to replace (in order) from the starting index.
	 * @throws IOException if the entries ould not be written.
	 * @throws IllegalArgumentException if startIndex is less than 0.
	 */
	void unmapEntries(int startIndex, WadEntry[] entryList) throws IOException;

	/**
	 * Completely replaces the list of entries in this Wad with a completely different set of entries.
	 * 
	 * @param entryList the set of entries that will make up this Wad.
	 * @throws IOException if the entries ould not be written.
	 * @throws IllegalArgumentException if startIndex is less than 0.
	 */
	void setEntries(WadEntry[] entryList) throws IOException;

}
