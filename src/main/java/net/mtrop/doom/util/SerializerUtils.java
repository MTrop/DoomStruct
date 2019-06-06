/*******************************************************************************
 * Copyright (c) 2019 Black Rook Software
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.util;

/**
 * Contains utility methods for serializing values. 
 * @author Matthew Tropiano
 */
public final class SerializerUtils
{
	/** The size of an int in bytes. */
	public static final int SIZEOF_INT = Integer.SIZE/Byte.SIZE;
	/** The size of a byte in bytes (This should always be 1, or Sun screwed up).*/
	public static final int SIZEOF_BYTE = Byte.SIZE/Byte.SIZE;
	/** The size of a short in bytes. */
	public static final int SIZEOF_SHORT = Short.SIZE/Byte.SIZE;
	/** The size of a long in bytes. */
	public static final int SIZEOF_LONG = Long.SIZE/Byte.SIZE;

	public static final boolean
	LITTLE_ENDIAN =	true,
	BIG_ENDIAN = false;

	private SerializerUtils() {}

	/**
	 * Converts a series of bytes to a short.
	 * @param b				the bytes to convert.
	 * @param endianMode	the endian mode of the bytes.
	 */
	public static short bytesToShort(byte[] b, boolean endianMode)
	{
		short out = 0;
	
		int stop = Math.min(b.length,SIZEOF_SHORT);
		for (int x = 0; x < stop; x++)
			out |= (b[x]&0xFF) << Byte.SIZE*(endianMode ? x : SIZEOF_SHORT-1-x);
	
		return out;
	}

	/**
	 * Converts a series of bytes to an integer.
	 * @param b				the bytes to convert.
	 * @param endianMode	the endian mode of the bytes.
	 */
	public static int bytesToInt(byte[] b, boolean endianMode)
	{
		int out = 0;
	
		int stop = Math.min(b.length,SIZEOF_INT);
		for (int x = 0; x < stop; x++)
			out |= (b[x]&0xFF) << Byte.SIZE*(endianMode ? x : SIZEOF_INT-1-x);
	
		return out;
	}

	/**
	 * Converts a series of bytes to a long.
	 * @param b				the bytes to convert.
	 * @param endianMode	the endian mode of the bytes.
	 */
	public static long bytesToLong(byte[] b, boolean endianMode)
	{
		long out = 0;
	
		int stop = Math.min(b.length,SIZEOF_LONG);
		for (int x = 0; x < stop; x++)
			out |= (long)(b[x]&0xFFL) << (long)(Byte.SIZE*(endianMode ? x : SIZEOF_LONG-1-x));
	
		return out;
	}

	/**
	 * Converts a series of bytes to a 32-bit float.
	 * @param b				the bytes to convert.
	 * @param endianMode	the endian mode of the bytes.
	 */
	public static float bytesToFloat(byte[] b, boolean endianMode)
	{
	    return Float.intBitsToFloat(bytesToInt(b, endianMode));
	}

	/**
	 * Converts a series of bytes to a 64-bit float.
	 * @param b				the bytes to convert.
	 * @param endianMode	the endian mode of the bytes.
	 */
	public static double bytesToDouble(byte[] b, boolean endianMode)
	{
	    return Double.longBitsToDouble(bytesToLong(b, endianMode));
	}

	/**
	 * Converts a short to a series of bytes.
	 * @param s				the short to convert.
	 * @param endianMode	the endian mode of the bytes.
	 * @param out			the output array.
	 * @param offset		the offset into the array to write.
	 * @return the next array offset after the write. 
	 */
	public static int shortToBytes(short s, boolean endianMode, byte[] out, int offset)
	{
		for (int x = endianMode ? 0 : SIZEOF_SHORT-1; endianMode ? (x < SIZEOF_SHORT) : (x >= 0); x += endianMode ? 1 : -1)
			out[endianMode ? x : SIZEOF_SHORT-1 - x] = (byte)((s & (0xFF << Byte.SIZE*x)) >> Byte.SIZE*x); 
		return offset + SIZEOF_SHORT;
	}

	/**
	 * Converts an integer to a series of bytes.
	 * @param i				the integer to convert.
	 * @param endianMode	the endian mode of the bytes.
	 * @param out			the output array.
	 * @param offset		the offset into the array to write.
	 * @return the next array offset after the write. 
	 */
	public static int intToBytes(int i, boolean endianMode, byte[] out, int offset)
	{
		for (int x = endianMode ? 0 : SIZEOF_INT-1; endianMode ? (x < SIZEOF_INT) : (x >= 0); x += endianMode ? 1 : -1)
			out[offset + (endianMode ? x : SIZEOF_INT-1 - x)] = (byte)((i & (0xFF << Byte.SIZE*x)) >> Byte.SIZE*x);
		return offset + SIZEOF_INT;
	}

	/**
	 * Converts a long to a series of bytes.
	 * @param l				the long to convert.
	 * @param endianMode	the endian mode of the bytes.
	 * @param out			the output array.
	 * @param offset		the offset into the array to write.
	 * @return the next array offset after the write. 
	 */
	public static int longToBytes(long l, boolean endianMode, byte[] out, int offset)
	{
		for (int x = endianMode ? 0 : SIZEOF_LONG-1; endianMode ? (x < SIZEOF_LONG) : (x >= 0); x += endianMode ? 1 : -1)
			out[offset + (endianMode ? x : SIZEOF_LONG-1 - x)] = (byte)((l & (0xFFL << Byte.SIZE*x)) >> Byte.SIZE*x); 
		return offset + SIZEOF_LONG;
	}

	/**
	 * Converts a float to a series of bytes.
	 * @param f				the float to convert.
	 * @param endianMode	the endian mode of the bytes.
	 * @param out			the output array.
	 * @param offset		the offset into the array to write.
	 * @return the next array offset after the write. 
	 */
	public static int floatToBytes(float f, boolean endianMode, byte[] out, int offset)
	{
		return intToBytes(Float.floatToRawIntBits(f), endianMode, out, offset);
	}
	
	/**
	 * Converts a double to a series of bytes.
	 * @param d				the double to convert.
	 * @param endianMode	the endian mode of the bytes.
	 * @param out			the output array.
	 * @param offset		the offset into the array to write.
	 * @return the next array offset after the write. 
	 */
	public static int doubleToBytes(double d, boolean endianMode, byte[] out, int offset)
	{
		return longToBytes(Double.doubleToRawLongBits(d), endianMode, out, offset);
	}

	/**
	 * Converts a series of boolean values to bits,
	 * going from least-significant to most-significant.
	 * TRUE booleans set the bit, FALSE ones do not.
	 * @param bool list of booleans. cannot exceed 32.
	 * @return the resultant bitstring in an integer.
	 */
	public static int booleansToInt(boolean ... bool)
	{
		int out = 0;
		for (int i = 0; i < Math.min(bool.length, 32); i++)
			if (bool[i])
				out |= (1 << i);
		return out;
	}
	
}
