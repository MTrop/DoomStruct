/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

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
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
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
	 * Converts an array of BinaryObjects into bytes.
	 * @param <BO> the BinaryObject type.
	 * @param data the objects to convert.
	 * @return the data bytes.
	 * @since 2.4.0
	 */
	static <BO extends BinaryObject> byte[] toBytes(BO[] data)
	{
		ByteArrayOutputStream bos = Shared.CONVERSIONBUFFER.get();
		bos.reset();
		for (BO bo : data)
			try {bo.writeBytes(bos);} catch (IOException e) {/* Should not happen. */}
		return bos.toByteArray();
	}

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
		BO out = (BO)Reflect.create(boClass);
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
			out[i] = Reflect.create(boClass);
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
	 * Transformer interface for transform calls. 
	 * @param <BO> the BinaryObject type.
	 * @since 2.1.0
	 */
	@FunctionalInterface
	interface Transformer<BO extends BinaryObject>
	{
		/**
		 * Transforms the provided object. 
		 * The provided object reference may not be distinct each call.
		 * Do not save the reference passed to this function anywhere.
		 * @param object the object to transform.
		 * @param index the sequence index of the object. 
		 */
		void transform(BO object, int index);
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
			this.outObject = Reflect.create(clz);
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
				outObject.fromBytes(buffer);
				return outObject;
			} catch (IOException e) {
				throw new RuntimeException("Could not deserialize " + objClass.getSimpleName(), e);
			}
		}
		
	}

	static class Shared
	{
		private static ThreadLocal<ByteArrayOutputStream> CONVERSIONBUFFER = 
			ThreadLocal.withInitial(()->new ByteArrayOutputStream(16384));
	}
	
	static class Reflect
	{
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
		private static <T> T create(Class<T> clazz)
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
	
}
