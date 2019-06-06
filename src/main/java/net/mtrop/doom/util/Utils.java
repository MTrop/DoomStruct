/*******************************************************************************
 * Copyright (c) 2019 Black Rook Software
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

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

	public static final byte[] BITMASK = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte)0x80};
	
	/**
	 * Wraps an iterator as an iterable.
	 * @param <T> the Iterator type.
	 * @param iter the iterator.
	 * @return an Iterable wrapping the provided Iterator.
	 */
	public static <T> Iterable<T> asIterable(final Iterator<T> iter)
	{
		return ()->iter;
	}
	
	/**
	 * Retrieves the binary contents of a stream until it hits the end of the stream.
	 * @param in	the input stream to use.
	 * @return		an array of len bytes that make up the data in the stream.
	 * @throws IOException	if the read cannot be done.
	 */
	public static byte[] getBinaryContents(InputStream in) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		relay(in, bos);
		return bos.toByteArray();
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
	 * Returns a random boolean.
	 * @param rand the random number generator.
	 * @return true or false.
	 */
	public static boolean randBoolean(Random rand)
	{
	    return rand.nextBoolean();
	}

	/**
	 * @param rand the random number generator.
	 * @return a random double value from [0 to 1) (inclusive/exclusive).
	 */
	public static double randDouble(Random rand)
	{
	    return rand.nextDouble();
	}

	/**
	 * Returns a random double value from -1 to 1 (inclusive).
	 * @param rand the random number generator.
	 * @return the next double.
	 */
	public static double randDoubleN(Random rand)
	{
	    return randDouble(rand) * (randBoolean(rand)? -1.0 : 1.0);
	}

	/**
	 * Gets a scalar factor that equals how "far along" a value is along an interval.
	 * @param value the value to test.
	 * @param lo the lower value of the interval.
	 * @param hi the higher value of the interval.
	 * @return a value between 0 and 1 describing this distance 
	 * 		(0 = beginning or less, 1 = end or greater), or 0 if lo and hi are equal.
	 */
	public static double getInterpolationFactor(double value, double lo, double hi)
	{
		if (lo == hi)
			return 0.0;
		return RangeUtils.clampValue((value - lo) / (hi - lo), 0, 1);
	}

	/**
	 * Gives a value that is the result of a linear interpolation between two values.
	 * @param factor the interpolation factor.
	 * @param x the first value.
	 * @param y the second value.
	 * @return the interpolated value.
	 */
	public static double linearInterpolate(double factor, double x, double y)
	{
		return factor * (y - x) + x;
	}

	/**
	 * Gives a value that is the result of a cosine interpolation between two values.
	 * @param factor the interpolation factor.
	 * @param x the first value.
	 * @param y the second value.
	 * @return the interpolated value.
	 */
	public static double cosineInterpolate(double factor, double x, double y)
	{
		double ft = factor * Math.PI;
		double f = (1 - Math.cos(ft)) * .5;
		return f * (y - x) + x;
	}

	/**
	 * Gives a value that is the result of a cublic interpolation between two values.
	 * Requires two outside values to predict a curve more accurately.
	 * @param factor the interpolation factor between x and y.
	 * @param w the value before the first.
	 * @param x the first value.
	 * @param y the second value.
	 * @param z the value after the second.
	 * @return the interpolated value.
	 */
	public static double cubicInterpolate(double factor, double w, double x, double y, double z)
	{
		double p = (z - y) - (w - x);
		double q = (w - x) - p;
		double r = y - w;
		double s = x;
		return (p*factor*factor*factor) + (q*factor*factor) + (r*factor) + s;
	}

	/**
	 * Creates a new instance of a class from a class type.
	 * This essentially calls {@link Class#newInstance()}, but wraps the call
	 * in a try/catch block that only throws an exception if something goes wrong.
	 * @param <T> the return object type.
	 * @param clazz the class type to instantiate.
	 * @return a new instance of an object.
	 * @throws RuntimeException if instantiation cannot happen, either due to
	 * a non-existent constructor or a non-visible constructor.
	 */
	public static <T> T create(Class<T> clazz)
	{
		Object out = null;
		try {
			out = clazz.getDeclaredConstructor().newInstance();
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		
		return clazz.cast(out);
	}
	
	

}
