/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Iterator;

import net.mtrop.doom.util.Utils;

/**
 * Common elements of all objects that are loaded from binary data.
 * This provides a general interface for getting serialized object data.
 * @author Matthew Tropiano
 */
public interface BinaryObject
{
	/**
	 * Gets the byte representation of this object. 
	 * @return this object as a series of bytes.
	 */
	default byte[] toBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(128);
		try { writeBytes(bos); } catch (IOException e) { /* Shouldn't happen. */ }
		return bos.toByteArray();
	}

	/**
	 * Reads in the byte representation of this object and sets its fields.
	 * @param data the byte array to read from. 
	 * @throws IOException if a read error occurs.
	 */
	default void fromBytes(byte[] data) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		readBytes(bin);
	}

	/**
	 * Reads from an {@link InputStream} and sets this object's fields. 
	 * @param in the {@link InputStream} to read from. 
	 * @throws IOException if a read error occurs.
	 */
	void readBytes(InputStream in) throws IOException;

	/**
	 * Writes this object to an {@link OutputStream}.
	 * @param out the {@link OutputStream} to write to.
	 * @throws IOException if a write error occurs.
	 */
	void writeBytes(OutputStream out) throws IOException;

	/**
	 * Creates a single object of a specific class from a serialized byte array.
	 * @param <BO> the object type, a subtype of {@link BinaryObject}.
	 * @param boClass the class to create.
	 * @param b the array of bytes.
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	static <BO extends BinaryObject> BO create(Class<BO> boClass, byte[] b) throws IOException
	{
		return read(boClass, new ByteArrayInputStream(b));
	}

	/**
	 * Creates a single object of a specific class from from an {@link InputStream}.
	 * @param <BO> the object type, a subtype of {@link BinaryObject}.
	 * @param boClass the class to create.
	 * @param in the input stream.
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	static <BO extends BinaryObject> BO read(Class<BO> boClass, InputStream in) throws IOException
	{
		BO out = (BO)Utils.create(boClass);
		out.readBytes(in);
		return out;
	}

	/**
	 * Creates an amount of objects of a specific class from a serialized byte array.
	 * @param <BO> the object type, a subtype of {@link BinaryObject}.
	 * @param boClass the class to create.
	 * @param b the array of bytes.
	 * @param count the (maximum) amount of objects to read. 
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	static <BO extends BinaryObject> BO[] create(Class<BO> boClass, byte[] b, int count) throws IOException
	{
		return read(boClass, new ByteArrayInputStream(b), count);
	}

	/**
	 * Creates an amount of objects of a specific class from an {@link InputStream}.
	 * @param <BO> the object type, a subtype of {@link BinaryObject}.
	 * @param boClass the class to create.
	 * @param in the input stream.
	 * @param count the (maximum) amount of objects to read. 
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	@SuppressWarnings("unchecked")
	static <BO extends BinaryObject> BO[] read(Class<BO> boClass, InputStream in, int count) throws IOException
	{
		BO[] out = (BO[])Array.newInstance(boClass, count);
		int i = 0;
		while (count-- > 0)
		{
			out[i] = Utils.create(boClass);
			out[i].readBytes(in);
			i++;
		}
		return (BO[])out;
	}

	/**
	 * Creates a deserializing scanner iterator that returns independent instances of objects.
	 * @param <BO> the object type, a subtype of {@link BinaryObject}.
	 * @param boClass the class to create.
	 * @param in the input stream.
	 * @param length the length of each object to read. 
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	static <BO extends BinaryObject> Scanner<BO> scanner(Class<BO> boClass, InputStream in, int length) throws IOException
	{
		return new Scanner<>(boClass, in, length);
	}

	/**
	 * Creates a deserializing scanner iterator that returns the same object instance with its contents changed.
	 * This is useful for when you would want to quickly scan through a set of serialized objects while
	 * ensuring low memory use. Do NOT store the references returned by <code>next()</code> anywhere as the contents
	 * of that reference will be changed by the next call to <code>next()</code>.
	 * @param <BO> the object type, a subtype of {@link BinaryObject}.
	 * @param boClass the class to create.
	 * @param in the input stream.
	 * @param length the length of each object to read. 
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	static <BO extends BinaryObject> InlineScanner<BO> inlineScanner(Class<BO> boClass, InputStream in, int length) throws IOException
	{
		return new InlineScanner<>(boClass, in, length);
	}

	/**
	 * A deserializing scanner iterator that returns independent instances of objects.
	 * @param <BO> the BinaryObject type.
	 */
	class Scanner<BO extends BinaryObject> implements Iterator<BO>
	{
		/** The input stream. */
		private InputStream in;
		/** The byte buffer. */
		private byte[] buffer;
		/** The object class. */
		private Class<BO> objClass;
		
		private Scanner(Class<BO> clz, InputStream in, int len)
		{
			this.in = in;
			this.buffer = new byte[len];
			this.objClass = clz;
		}
		
		@Override
		public boolean hasNext()
		{
	        int b;
			try {
				b = in.read(buffer);
		        if (b < buffer.length)
		        {
		        	in.close();
		            return false;
		        }
		        else
		            return true;
			} catch (IOException e) {
				throw new RuntimeException("Could not read bytes for " + objClass.getSimpleName(), e);
			}
		}

		@Override
		public BO next()
		{
			try {
				return create(objClass, buffer);
			} catch (IOException e) {
				throw new RuntimeException("Could not deserialize " + objClass.getSimpleName(), e);
			}
		}
		
	}
	
	/**
	 * A deserializing scanner iterator that returns the same object instance with its contents changed.
	 * @param <BO> the BinaryObject type.
	 */
	class InlineScanner<BO extends BinaryObject> implements Iterator<BO>
	{
		/** The input stream. */
		private InputStream in;
		/** The byte buffer. */
		private byte[] buffer;
		/** The object class. */
		private Class<BO> objClass;
		/** The object class. */
		private BO outObject;
		
		private InlineScanner(Class<BO> clz, InputStream in, int len)
		{
			this.in = in;
			this.buffer = new byte[len];
			this.objClass = clz;
			this.outObject = Utils.create(clz);
		}
		
		@Override
		public boolean hasNext()
		{
	        int b;
			try {
				b = in.read(buffer);
		        if (b < buffer.length)
		        {
		        	in.close();
		            return false;
		        }
		        else
		            return true;
			} catch (IOException e) {
				throw new RuntimeException("Could not read bytes for " + objClass.getSimpleName(), e);
			}
		}

		@Override
		public BO next()
		{
			try {
				outObject.readBytes(in);
				return outObject;
			} catch (IOException e) {
				throw new RuntimeException("Could not deserialize " + objClass.getSimpleName(), e);
			}
		}
		
	}
	
}
