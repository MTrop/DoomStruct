/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

/**
 * Common elements of all objects that are loaded from text data.
 * This provides a general interface for getting text-based object data.
 * @author Matthew Tropiano
 */
public interface TextObject
{
	/**
	 * Gets the textual representation of this object. 
	 * @return this object as a String.
	 */
	default String toText()
	{
		StringWriter sw = new StringWriter();
		try { writeText(sw); } catch (IOException e) { /* Shouldn't happen. */ }
		return sw.toString();
	}

	/**
	 * Reads in the textual representation of this object and sets its fields.
	 * @param string the string array to read from. 
	 * @throws IOException if a read error occurs.
	 */
	default void fromText(String string) throws IOException
	{
		StringReader sr = new StringReader(string);
		readText(sr);
	}

	/**
	 * Reads from an {@link Reader} and sets this object's fields. 
	 * @param reader the {@link Reader} to read from. 
	 * @throws IOException if a read error occurs.
	 */
	void readText(Reader reader) throws IOException;

	/**
	 * Writes this object to a {@link Writer}.
	 * @param writer the {@link Writer} to write to.
	 * @throws IOException if a write error occurs.
	 */
	void writeText(Writer writer) throws IOException;

	/**
	 * Creates a single object of a specific class from a string.
	 * @param <TO> the object type, a subtype of {@link TextObject}.
	 * @param toClass the class to create.
	 * @param string the string.
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	static <TO extends TextObject> TO create(Class<TO> toClass, String string) throws IOException
	{
		return read(toClass, new StringReader(string));
	}

	/**
	 * Creates a single object of a specific class from from an {@link InputStream}.
	 * @param <TO> the object type, a subtype of {@link TextObject}.
	 * @param toClass the class to create.
	 * @param reader the reader.
	 * @return an array of length <code>count</code> of the created objects.
	 * @throws IOException if an error occurs during the read - most commonly "not enough bytes".
	 */
	static <TO extends TextObject> TO read(Class<TO> toClass, Reader reader) throws IOException
	{
		TO out = (TO)Reflect.create(toClass);
		out.readText(reader);
		return out;
	}

	/**
	 * Transformer interface for transform calls. 
	 * @param <TO> the TextObject type.
	 */
	@FunctionalInterface
	interface Transformer<TO extends TextObject>
	{
		/**
		 * Transforms the provided text object. 
		 * The provided object reference may not be distinct each call.
		 * Do not save the reference passed to this function anywhere.
		 * @param object the object to transform.
		 */
		void transform(TO object);
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
