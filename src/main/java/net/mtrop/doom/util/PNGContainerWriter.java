package net.mtrop.doom.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PNGContainerWriter implements AutoCloseable
{
	/** PNG Header. */
	private static final byte[] PNG_HEADER = {
		(byte)0x089, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
	};
	
	/** PNG CRC32 generator. */
	private static final CRC32 PNG_CRC = new CRC32(CRC32.POLYNOMIAL_IEEE);
	
	/** Did we write the header, yet? */
	private OutputStream out;
	/** Did we write the header, yet? */
	private boolean wroteHeader;
	
	/**
	 * Creates a new PNG container reader from a file.
	 */
	public PNGContainerWriter(File f) throws IOException
	{
		this(new FileOutputStream(f));
	}
	
	/**
	 * Creates a new PNG container reader using an input stream.
	 */
	public PNGContainerWriter(OutputStream out) throws IOException
	{
		this.out = out;
	}
	
	/** Starts the PNG header. Called if not called yet. */
	protected void startHeader() throws IOException
	{
		(new SerialWriter(SerialWriter.BIG_ENDIAN)).writeBytes(out, PNG_HEADER);
	}

	/**
	 * Writes the next chunk in this container stream.
	 * @param name	the name of the chunk. Must be length 4 (excluding whitespace), 
	 * 				and follow the guidelines for naming necessary/private/etc. chunks.
	 * @param data	the data to write.
	 * @throws IOException	if the write could not occur.
	 */
	public void writeChunk(String name, byte[] data) throws IOException
	{
		if (name.trim().length() != 4)
			throw new IllegalArgumentException("Name must be 4 alphabetical characters long.");
		
		if (!wroteHeader)
		{
			startHeader();
			wroteHeader = true;
		}
		
		SerialWriter sw = new SerialWriter(SerialWriter.BIG_ENDIAN);
		
		sw.writeInt(out, data.length);

		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		sw.writeBytes(baout, name.getBytes("ASCII"));
		sw.writeBytes(baout, data);

		byte[] bytes = baout.toByteArray();
		sw.writeBytes(out, bytes);
		sw.writeInt(out, PNG_CRC.createCRC32(bytes));
	}

	@Override
	public void close() throws IOException
	{
		out.close();
	}

}