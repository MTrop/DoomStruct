package net.mtrop.doom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class PNGContainerReader implements AutoCloseable
{
	/** PNG Header. */
	private static final byte[] PNG_HEADER = {
		(byte)0x089, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
	};
	
	private InputStream in;
	
	/**
	 * Creates a new PNG container reader from a file.
	 */
	public PNGContainerReader(File f) throws IOException
	{
		this(new FileInputStream(f));
	}
	
	/**
	 * Creates a new PNG container reader using an input stream.
	 */
	public PNGContainerReader(InputStream i) throws IOException
	{
		this.in = i;
		checkHeader();
	}
	
	/** Checks the PNG header. Throws an Exception if bad. */
	protected void checkHeader() throws IOException
	{
		if (!Arrays.equals(PNG_HEADER, (new SerialReader(SerialReader.BIG_ENDIAN)).readBytes(in, 8)))
			throw new IOException("Not a PNG file. Header may be corrupt.");
	}

	/**
	 * Reads the next chunk in this container stream.
	 */
	public Chunk nextChunk() throws IOException
	{
		Chunk chunk = null;
		try {chunk = new Chunk(in);} catch (IOException e) {}
		return chunk;
	}

	@Override
	public void close() throws IOException
	{
		in.close();
	}

	/**
	 * PNG Chunk data.
	 */
	public static class Chunk
	{
		/** Chunk name. */
		private String name;
		/** CRC number. */
		private int crcNumber;
		/** Data. */
		private byte[] data;
		
		Chunk(InputStream in) throws IOException
		{
			SerialReader sr = new SerialReader(SerialReader.BIG_ENDIAN);
			int len = sr.readInt(in);
			name = sr.readString(in, 4, "ASCII").trim();
			data = sr.readBytes(in, len);
			crcNumber = sr.readInt(in);
		}

		/**
		 * Gets this chunk's identifier.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Gets this chunk's CRC value.
		 */
		public int getCRCNumber()
		{
			return crcNumber;
		}

		/**
		 * Gets the data in this chunk.
		 */
		public byte[] getData()
		{
			return data;
		}
		
		@Override
		public String toString()
		{
			return name + " Length: " + data.length + " CRC: " + String.format("%08x", crcNumber);
		}
		
		/**
		 * Is this chunk not a part of the required image chunks?
		 */
		public boolean isAncillary()
		{
			return Character.isLowerCase(name.charAt(0));
		}
		
		/**
		 * Is this chunk part of a non-public specification?
		 */
		public boolean isPrivate()
		{
			return Character.isLowerCase(name.charAt(1));
		}
		
		/**
		 * Does this chunk have the reserved bit set?
		 */
		public boolean isReserved()
		{
			return Character.isLowerCase(name.charAt(2));
		}

		/**
		 * Is this chunk safe to blindly copy, requiring no
		 * other chunks and contains no image-centric data?
		 */
		public boolean isSafeToCopy()
		{
			return Character.isLowerCase(name.charAt(3));
		}
	}
	
}