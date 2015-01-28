package net.mtrop.doom;

import java.io.*;
import java.util.Iterator;

import net.mtrop.doom.exception.DataConversionException;
import net.mtrop.doom.exception.WadException;
import net.mtrop.doom.util.NameUtils;

import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * The class that reads WadFile information and provides random access to Wad files.
 * <p>
 * Use of this class is recommended for reading WAD information or small additions of data, as the overhead needed to
 * do so is minimal in this class. Bulk reads/additions/writes/changes are best left for the {@link WadBuffer} class. 
 * Many writing I/O operations will cause the opened file to be changed many times, the length of time of 
 * which being dictated by the length of the entry list (as the list grows, so does the time it takes to write/change it).
 * @author Matthew Tropiano
 */
public class WadFile extends RandomAccessFile implements Wad, Closeable
{
	/** WAD File's name (equivalent to File.getName()). */
	private String fileName;
	/** WAD File's path (equivalent to File.getPath()). */
	private String filePath;
	/** WAD File's absolute path (equivalent to File.getAbsolutePath()). */
	private String fileAbsolutePath;
	
	/** List of this Wad's entries. */
	private List<WadEntry> entries;

	/** Type of Wad File (IWAD or PWAD). */
	private Type type;

	/** Offset of the beginning of the entry list. */
	private int entryListOffset;
	
	/**
	 * Opens a WadFile from a file specified by "path."
	 * @param path	the path to the File;
	 * @throws IOException if the file can't be read.
	 * @throws FileNotFoundException if the file can't be found.
	 * @throws SecurityException if you don't have permission to access the file.
	 * @throws WadException if the file isn't a Wad file.
	 * @throws NullPointerException if "path" is null.
	 */
	public WadFile(String path) throws IOException, WadException
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
	 * @throws NullPointerException if "f" is null.
	 */
	public WadFile(File f) throws IOException, WadException
	{
		super(f,"rws");
		byte[] buffer = new byte[4];

		// read header
		read(buffer);
		String head = new String(buffer,"ASCII");
		if (!head.equals(Type.IWAD.toString()) && !head.equals(Type.PWAD.toString()))
			throw new WadException("Not a Wad file or supported Wad file type.");

		if (head.equals(Type.IWAD.toString()))
			type = Type.IWAD;
			
		if (head.equals(Type.PWAD.toString()))
			type = Type.PWAD;
		
		fileName = f.getName();
		filePath = f.getPath();
		fileAbsolutePath = f.getAbsolutePath();
		
		read(buffer);
		int size = SuperReader.bytesToInt(buffer,SuperReader.LITTLE_ENDIAN);

		read(buffer);
		entryListOffset = SuperReader.bytesToInt(buffer,SuperReader.LITTLE_ENDIAN);
		
		entries = new List<WadEntry>((size + 1) * 2);
		
		// seek to entry list.
		seek(entryListOffset);
		
		// read entries.
		byte[] entrybytes = new byte[16];
		for (int i = 0; i < size; i++)
		{
			read(entrybytes);
			WadEntry entry = WadEntry.create(entrybytes);
			if (entry.getName().length() > 0 || entry.getSize() > 0)
				entries.add(entry);
		}
	}

	private void writeEntryList() throws IOException
	{
		seek(entryListOffset);
		for (WadEntry wfe : entries)
			write(wfe.getBytes());
		if (getFilePointer() < length())
			setLength(getFilePointer());
	}

	private void writeHeader() throws IOException
	{
		seek(4);
		write(SuperWriter.intToBytes(entries.size(),SuperWriter.LITTLE_ENDIAN));
		write(SuperWriter.intToBytes(entryListOffset,SuperWriter.LITTLE_ENDIAN));
	}

	/**
	 * Creates a new, empty WadFile and returns a reference to it.
	 * @param path	the path of the new file in the form of a String.
	 * @return		a reference to the newly created WadFile, already open.
	 * @throws IOException if the file can't be written.
	 * @throws NullPointerException if "path" is null.
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
	 * @throws NullPointerException if "f" is null.
	 */
	public static WadFile createWadFile(File f) throws IOException
	{
		FileOutputStream fo = new FileOutputStream(f);
		SuperWriter sw = new SuperWriter(fo,SuperWriter.LITTLE_ENDIAN);
		sw.writeASCIIString(Type.PWAD.toString());
		sw.writeInt(0);		// number of entries.
		sw.writeInt(12);	// offset to entry list.
		fo.close();
		try{
			return new WadFile(f);
		// never reached (HOPEFULLY. if this happens, holy internal errors, Batman!)
		} catch (WadException e) {return null;}
	}
	
	/**
	 * Returns this Wad's file name. 
	 */
	public String getFileName()
	{
		return fileName;
	}
	
	/**
	 * Returns this Wad's file path. 
	 */
	public String getFilePath()
	{
		return filePath;
	}

	/**
	 * Returns this Wad's file absolute path. 
	 */
	public String getFileAbsolutePath()
	{
		return fileAbsolutePath;
	}

	/**
	 * Closes the file once it is cleaned up by gc().
	 */
	public void finalize() throws Throwable
	{
		try{
		close();
		super.finalize();
		} catch (IOException e){}
	}

	@Override
	public boolean isIWAD()
	{
		return type == Type.IWAD;
	}
	
	@Override
	public boolean isPWAD()
	{
		return type == Type.PWAD;
	}
	
	@Override
	public int getSize()
	{
		return entries.size();
	}

	@Override
	public WadEntry addData(String entryName, byte[] data) throws IOException
	{
		WadEntry entry = WadEntry.create(entryName, data.length, entryListOffset);
		entries.add(entry);
		writeHeader();
		seek(entryListOffset);
		write(data);
		writeEntryList();
		return entry;
	}

	@Override
	public WadEntry addDataAt(int index, String entryName, byte[] data) throws IOException
	{
		WadEntry entry = WadEntry.create(entryName, data.length, entryListOffset);
		entries.add(index, entry);

		seek(entryListOffset);
		write(data);
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
			out[i] = WadEntry.create(entryNames[i], data[i].length, curOffs);
			curOffs += data[i].length;
		}
		
		for (WadEntry we : out)
			entries.add(we);

		seek(entryListOffset);
		
		for (int i = 0; i < entryNames.length; i++)
		{
			write(data[i].length);
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
			out[i] = WadEntry.create(entryNames[i], data[i].length, curOffs);
			curOffs += data[i].length;
		}
		
		for (WadEntry we : out)
			entries.add(index++, we);

		seek(entryListOffset);
		
		for (int i = 0; i < entryNames.length; i++)
		{
			write(data[i].length);
			entryListOffset += data[i].length;
		}

		writeHeader();
		writeEntryList();
		return out;
	}

	@Override
	public WadEntry addMarker(String name) throws IOException
	{
		return addData(name, new byte[0]);
	}

	@Override
	public WadEntry addMarkerAt(int index, String name) throws IOException
	{
		return addDataAt(index, name, new byte[0]);
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
		WadEntry entry = entries.removeIndex(n);
		if (entry == null)
			throw new IOException("Index is out of range.");
	
		byte[] buffer = new byte[65536];
		int offset = entry.getOffset();
		int dataOffset = entry.getOffset() + entry.getSize();

		while (dataOffset < entryListOffset)
		{
			int amount = Math.min(entryListOffset - dataOffset, buffer.length);
			seek(dataOffset);
			int readAmount = read(buffer, 0, amount);
			seek(offset);
			write(buffer, 0, readAmount);
			offset += readAmount;
			dataOffset += readAmount;
		}

		entryListOffset = dataOffset;

		// adjust offsets from last WadEntry.
		for (int i = n; i < entries.size(); i++)
		{
			WadEntry e = entries.getByIndex(i);
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
			throw new DataConversionException("Entry name \""+newName+"\" does not fit entry requirements.");
		
		entry.name = newName;

		// update in file.
		seek(entryListOffset + (16 * index) + 8);
		write(entry.getNameBytes());
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
			entries.replace(startIndex + i, entryList[i]);
		writeHeader();
		writeEntryList();
	}

	@Override
	public void setEntries(WadEntry[] entryList) throws IOException
	{
		entries.clear();
		for (WadEntry WadEntry : entryList)
			entries.add(WadEntry);
		writeHeader();
		writeEntryList();
	}

	@Override	
	public byte[] getData(int n) throws IOException
	{
		return getData(getEntry(n));
	}

	@Override	
	public byte[] getData(String entryName) throws IOException
	{
		return getData(getEntry(entryName));
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
			seek(entry.getOffset());
			read(out, 0, entry.getSize());
		} catch (IndexOutOfBoundsException e) {
			throw new IOException(e);
		}
		return out;
	}

	@Override	
	public InputStream getDataAsStream(int n) throws IOException
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
	public InputStream getDataAsStream(String WadEntry) throws IOException
	{
		WadEntry e = getEntry(WadEntry);
		if (e == null)
			return null;
		byte[] b = getData(e); 
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}

	@Override	
	public InputStream getDataAsStream(String WadEntry, int start) throws IOException
	{
		int i = getIndexOf(WadEntry,start);
		byte[] b = i != -1 ? getData(i) : null;
		if (b == null)
			return null;
		return new ByteArrayInputStream(b);
	}

	@Override	
	public InputStream getDataAsStream(WadEntry WadEntry) throws IOException
	{
		return new ByteArrayInputStream(getData(WadEntry));
	}

	@Override	
	public WadEntry getEntry(int n)
	{
		return entries.getByIndex(n);
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
			WadEntry entry = entries.getByIndex(i);
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
			WadEntry entry = entries.getByIndex(i);
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
		Queue<WadEntry> w = new Queue<WadEntry>();
		
		for (int i = 0; i < entries.size(); i++)
		{
			WadEntry entry = entries.getByIndex(i);
			if (entry.getName().equals(entryName))
				w.enqueue(entry);
		}
		
		WadEntry[] out = new WadEntry[w.size()];
		w.toArray(out);
		return out;
	}

	@Override
	public int[] getAllEntryIndices(String entryName)
	{
		Queue<Integer> w = new Queue<Integer>();
		
		for (int i = 0; i < entries.size(); i++)
		{
			WadEntry entry = entries.getByIndex(i);
			if (entry.getName().equals(entryName))
				w.enqueue(i);
		}
		
		int[] out = new int[w.size()];
		for (int i = 0; i < entries.size(); i++)
			out[i] = w.dequeue();
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
			if (entries.getByIndex(i).getName().equals(entryName))
				return i;
		return -1;
	}

	@Override	
	public int getLastIndexOf(String entryName)
	{
		int out = -1;
		for (int i = 0; i < entries.size(); i++)
			if (entries.getByIndex(i).getName().equals(entryName))
				out = i;
		return out;
	}

	/**
	 * Gets the type of WAD that this is.
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

}
