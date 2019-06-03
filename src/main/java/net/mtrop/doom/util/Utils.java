/*******************************************************************************
 * Copyright (c) 2019 Black Rook Software
 * From Black Rook Base https://github.com/BlackRookSoftware/Base 
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;

/**
 * A utility class.
 * @author Matthew Tropiano
 */
public final class Utils
{
	public static final boolean LITTLE_ENDIAN = true;
	public static final boolean BIG_ENDIAN = false;
	/** The size of an int in bytes. */
	public static final int SIZEOF_INT = Integer.SIZE/Byte.SIZE;
	/** The relay buffer size, used by relay(). */
	private static int RELAY_BUFFER_SIZE = 8192;

	/**
	 * Reads from an input stream, reading in a consistent set of data
	 * and writing it to the output stream. The read/write is buffered
	 * so that it does not bog down the OS's other I/O requests.
	 * This method finishes when the end of the source stream is reached.
	 * Note that this may block if the input stream is a type of stream
	 * that will block if the input stream blocks for additional input.
	 * This method is thread-safe.
	 * @param in the input stream to grab data from.
	 * @param out the output stream to write the data to.
	 * @return the total amount of bytes relayed.
	 * @throws IOException if a read or write error occurs.
	 */
	public static int relay(InputStream in, OutputStream out) throws IOException
	{
		return relay(in, out, RELAY_BUFFER_SIZE, -1);
	}

	/**
	 * Reads from an input stream, reading in a consistent set of data
	 * and writing it to the output stream. The read/write is buffered
	 * so that it does not bog down the OS's other I/O requests.
	 * This method finishes when the end of the source stream is reached.
	 * Note that this may block if the input stream is a type of stream
	 * that will block if the input stream blocks for additional input.
	 * This method is thread-safe.
	 * @param in the input stream to grab data from.
	 * @param out the output stream to write the data to.
	 * @param bufferSize the buffer size for the I/O. Must be &gt; 0.
	 * @param maxLength the maximum amount of bytes to relay, or a value &lt; 0 for no max.
	 * @return the total amount of bytes relayed.
	 * @throws IOException if a read or write error occurs.
	 */
	public static int relay(InputStream in, OutputStream out, int bufferSize, int maxLength) throws IOException
	{
		int total = 0;
		int buf = 0;
			
		byte[] RELAY_BUFFER = new byte[bufferSize];
		
		while ((buf = in.read(RELAY_BUFFER, 0, Math.min(maxLength < 0 ? Integer.MAX_VALUE : maxLength, bufferSize))) > 0)
		{
			out.write(RELAY_BUFFER, 0, buf);
			total += buf;
			if (maxLength >= 0)
				maxLength -= buf;
		}
		return total;
	}

	/**
	 * Attempts to close an {@link AutoCloseable} object.
	 * If the object is null, this does nothing.
	 * @param c the reference to the AutoCloseable object.
	 */
	public static void close(AutoCloseable c)
	{
		if (c == null) return;
		try { c.close(); } catch (Exception e){}
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
	 * Checks if a value is "empty."
	 * The following is considered "empty":
	 * <ul>
	 * <li><i>Null</i> references.
	 * <li>{@link Array} objects that have a length of 0.
	 * <li>{@link Boolean} objects that are false.
	 * <li>{@link Character} objects that are the null character ('\0', '\u0000').
	 * <li>{@link Number} objects that are zero.
	 * <li>{@link String} objects that are the empty string, or are {@link String#trim()}'ed down to the empty string.
	 * <li>{@link Collection} objects where {@link Collection#isEmpty()} returns true.
	 * </ul> 
	 * @param obj the object to check.
	 * @return true if the provided object is considered "empty", false otherwise.
	 */
	public static boolean isEmpty(Object obj)
	{
		if (obj == null)
			return true;
		else if (isArray(obj.getClass()))
			return Array.getLength(obj) == 0;
		else if (obj instanceof Boolean)
			return !((Boolean)obj);
		else if (obj instanceof Character)
			return ((Character)obj) == '\0';
		else if (obj instanceof Number)
			return ((Number)obj).doubleValue() == 0.0;
		else if (obj instanceof String)
			return ((String)obj).trim().length() == 0;
		else if (obj instanceof Collection<?>)
			return ((Collection<?>)obj).isEmpty();
		else
			return false;
	}

	private static boolean isArray(Class<?> clazz)
	{
		return clazz.getName().startsWith("["); 
	}

	/**
	 * Moves the object at an index in an array to another index,
	 * shifting the contents between the two selected indices in the array back or forward.
	 * <p>If sourceIndex is equal to targetIndex, this does nothing.
	 * <p>If one index is outside the bounds of the array 
	 * (less than 0 or greater than or equal to array length),
	 * this throws an exception. 
	 * @param arr the array to shift the contents of.
	 * @param sourceIndex the first index.
	 * @param targetIndex the second index.
	 * @throws IllegalArgumentException if one index is outside the bounds of the array
	 * (less than 0 or greater than or equal to length).
	 */
	public static <T> void shift(T[] arr, int sourceIndex, int targetIndex)
	{
		if (sourceIndex < 0 || sourceIndex >= arr.length)
			throw new IllegalArgumentException("sourceIndex cannot be outside the range of this array.");
		if (targetIndex < 0 || targetIndex >= arr.length)
			throw new IllegalArgumentException("index1 cannot be outside the range of this array.");
		
		if (sourceIndex == targetIndex)
			return;
	
		T obj = arr[sourceIndex];
		if (targetIndex < sourceIndex)
			System.arraycopy(arr, targetIndex, arr, targetIndex + 1, sourceIndex - targetIndex);
		else if (targetIndex > sourceIndex)
			System.arraycopy(arr, sourceIndex + 1, arr, sourceIndex, targetIndex - sourceIndex);
		arr[targetIndex] = obj;
	}

	/**
	 * Checks if bits are set in a value.
	 * @param value		the value.
	 * @param test		the testing bits.
	 * @return			true if all of the bits set in test are set in value, false otherwise.
	 */
	public static boolean bitIsSet(long value, long test)
	{
		return (value & test) == test;
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

	/**
	 * Coerces an integer to the range bounded by lo and hi.
	 * <br>Example: clampValue(32,-16,16) returns 16.
	 * <br>Example: clampValue(4,-16,16) returns 4.
	 * <br>Example: clampValue(-1000,-16,16) returns -16.
	 * @param val the integer.
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return the value after being "forced" into the range.
	 */
	public static int clampValue(int val, int lo, int hi)
	{
		return Math.min(Math.max(val,lo),hi);
	}

	/**
	 * Coerces a short to the range bounded by lo and hi.
	 * <br>Example: clampValue(32,-16,16) returns 16.
	 * <br>Example: clampValue(4,-16,16) returns 4.
	 * <br>Example: clampValue(-1000,-16,16) returns -16.
	 * @param val the short.
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return the value after being "forced" into the range.
	 */
	public static short clampValue(short val, short lo, short hi)
	{
		return (short)Math.min((short)Math.max(val,lo),hi);
	}

	/**
	 * Coerces a float to the range bounded by lo and hi.
	 * <br>Example: clampValue(32,-16,16) returns 16.
	 * <br>Example: clampValue(4,-16,16) returns 4.
	 * <br>Example: clampValue(-1000,-16,16) returns -16.
	 * @param val the float.
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return the value after being "forced" into the range.
	 */
	public static float clampValue(float val, float lo, float hi)
	{
		return Math.min(Math.max(val,lo),hi);
	}

	/**
	 * Coerces a double to the range bounded by lo and hi.
	 * <br>Example: clampValue(32,-16,16) returns 16.
	 * <br>Example: clampValue(4,-16,16) returns 4.
	 * <br>Example: clampValue(-1000,-16,16) returns -16.
	 * @param val the double.
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return the value after being "forced" into the range.
	 */
	public static double clampValue(double val, double lo, double hi)
	{
		return Math.min(Math.max(val,lo),hi);
	}
	
	

}
