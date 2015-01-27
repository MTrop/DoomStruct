package net.mtrop.doom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import net.mtrop.doom.exception.DataConversionException;

/**
 * Abstraction of a single entry from a WAD.
 * This entry contains NO DATA - this is a descriptor for the data in the originating WAD.
 * @author Matthew Tropiano
 */
public class WadEntry
{
	/** The name of the entry. */
	String name;
	/** The offset into the original WAD for the start of the data. */
	int offset;
	/** The size of the entry content in bytes. */
	int size;
	
	private WadEntry(String name, int offset, int size)
	{
		this.name = name;
		this.offset = offset;
		this.size = size;
	}

	/**
	 * Creates a WadEntry.
	 * @param name the name of the entry.
	 * @param offset the offset into the WAD in bytes.
	 * @param size the size of the entry in bytes.
	 * @return the constructed WadEntry.
	 * @throws DataConversionException if the name is invalid or the offset or size is negative.
	 */
	public static WadEntry create(String name, int offset, int size)
	{
		if (!NameUtils.isValidEntryName(name))
			throw new DataConversionException("Entry name \""+name+"\" does not fit entry requirements.");
		if (offset < 0)
			throw new DataConversionException("Entry offset is negative.");
		if (size < 0)
			throw new DataConversionException("Entry size is negative.");
		
		return new WadEntry(name, offset, size); 
	}
	
	/**
	 * Creates a WadEntry from a piece of raw entry data from a WAD.
	 * Reads the first 16 bytes.
	 * @param data the byte representation of the entry.
	 * @return the constructed WadEntry.
	 * @throws DataConversionException if the name is invalid or the offset or size is negative.
	 * @throws IOException if the data cannot be read for some reason.
	 */
	public static WadEntry create(byte[] data) throws IOException
	{
		SuperReader sr = new SuperReader(new ByteArrayInputStream(data), SuperReader.LITTLE_ENDIAN);
		int offset = sr.readInt();
		int size = sr.readInt();
		String name = NameUtils.toValidEntryName(sr.readASCIIString(8));
		return new WadEntry(name, offset, size); 
	}
	
	/**
	 * @return the name of the entry.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the offset into the original WAD for the start of the data.
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * @return the size of the entry content in bytes.
	 */
	public int getSize()
	{
		return size;
	}
	
	/**
	 * Tests if this entry is a "marker" entry. Marker entries have 0 size.
	 * @return true if size = 0, false if not.
	 */
	public boolean isMarker()
	{
		return size == 0;
	}

	/**
	 * Returns this entry's name as how it is represented in a WAD.
	 * @return a byte array of length 8 containing the output data.
	 */
	public byte[] getNameBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);

		try {
			sw.writeASCIIString(name);
			// null pad out to 8
			for (int n = name.length(); n < 8; n++)
				sw.writeByte((byte)0x00);
		} catch (IOException e) {
			// Should not happen.
		}
		
		return bos.toByteArray();
	}

	/**
	 * Returns this entry as a set of serialized bytes - how it is represented in a WAD.
	 * @return a byte array of length 16 containing the output data.
	 */
	public byte[] getBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos, SuperWriter.LITTLE_ENDIAN);
		try {
			sw.writeInt(offset);
			sw.writeInt(size);
			sw.writeASCIIString(name);
			// null pad out to 8
			for (int n = name.length(); n < 8; n++)
				sw.writeByte((byte)0x00);
		} catch (IOException e) {
			// Should not happen.
		}
		
		return bos.toByteArray();
	}
	
	@Override
	public String toString()
	{
		return String.format("WadEntry %-8s Offset: %d, Size: %d", name, offset, size);
	}
	
}
