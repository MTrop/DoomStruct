/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
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

import net.mtrop.doom.util.Utils;

/**
 * Common elements of all objects that are loaded from binary data.
 * This provides a general interface for getting serialized object data.
 * @author Matthew Tropiano
 */
public interface BinaryObject
{
	/**
	 * Creates a single object of a specific class from a serialized byte array.
	 * @param <BO> the object type, a subtype of {@link BinaryObject}.
	 * @param boClass the class to create.
	 * @param b the array of bytes.
	 * @param count the (maximum) amount of objects to read. 
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
	 * @param count the (maximum) amount of objects to read. 
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
	
}
