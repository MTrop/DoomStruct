/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import net.mtrop.doom.map.udmf.listener.UDMFTypeListener;

/**
 * A mechanism for reading UDMF data, element by element, in a <i>pull-oriented</i> way.
 * compared to {@link UDMFReader}, which is push-oriented.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public final class UDMFScanner
{
	/**
	 * The element type returned on each element scanned.
	 */
	public enum ElementType
	{
		/** Element is a global attribute - a [name, value] pair. */
		GLOBAL_ATTRIBUTE,
		/** Element is a UDMFObject. */
		OBJECT;
	}
	
	/**
	 * A single scanned element from the scanner.
	 * @since [NOW]
	 */
	public static class Element
	{
		private ElementType type;
		private String name;
		private Object value;
		
		/**
		 * @return the element type.
		 */
		public ElementType getType()
		{
			return type;
		}
		
		/**
		 * Returns this element's name / object type.
		 * @return the scanned element name.
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 * Returns this element's attribute value / UDMFObject.
		 * @return the scanned element value.
		 */
		public Object getValue()
		{
			return value;
		}
		
		/**
		 * Returns this element's UDMFObject.
		 * @return the object, or null if the element type is not {@link ElementType#OBJECT}.
		 */
		public UDMFObject getUDMFObject()
		{
			if (type == ElementType.OBJECT)
				return (UDMFObject)value;
			else
				return null;
		}
		
	}
	
	/** The current id. */
	private UDMFParser parser;
	/** The current id. */
	private String currentId;
	/** The listener for capturing events. */
	private ScannerListener listener;
	
	private UDMFScanner(UDMFParser parser)
	{
		this.parser = parser;
		this.currentId = null;
		this.listener = new ScannerListener();
	}
	
	/**
	 * Creates a UDMFScanner for reading UDMF-formatted data from an {@link InputStream}.
	 * @param in the InputStream to read from.
	 * @return a new scanner for controlled reading.
	 * @throws IOException if the data can't be read.
	 */
	public static UDMFScanner createScanner(InputStream in) throws IOException
	{
		return createScanner(new InputStreamReader(in, "UTF8"));
	}
	
	/**
	 * Creates a UDMFScanner for reading UDMF-formatted data from a String.
	 * @param data the String to read from.
	 * @return a UDMFTable containing the structures.
	 * @throws UDMFParseException if a parsing error occurs.
	 * @throws IOException if the data can't be read.
	 */
	public static UDMFScanner createScanner(String data) throws IOException
	{
		return createScanner(new StringReader(data));
	}
	
	/**
	 * Creates a UDMFScanner for reading UDMF-formatted data from a {@link Reader}.
	 * @param reader the reader to read from.
	 * @return a UDMFTable containing the parsed structures.
	 * @throws UDMFParseException if a parsing error occurs.
	 * @throws IOException if the data can't be read.
	 */
	public static UDMFScanner createScanner(Reader reader) throws IOException
	{
		UDMFScanner out = new UDMFScanner(new UDMFParser(reader));
		out.scanNext();
		return out;
	}
	
	private static class ScannerListener extends UDMFTypeListener
	{
		private String scannedError;
		private String scannedName;
		private Object scannedValue;
		private UDMFObject scannedObject;

		private ScannerListener()
		{
			super();
		}
		
		@Override
		public void onParseError(String error)
		{
			scannedError = error;
			scannedName = null;
			scannedValue = null;
			scannedObject = null;
		}
		
		@Override
		public void onGlobalAttribute(String name, Object value)
		{
			scannedError = null;
			scannedName = name;
			scannedValue = value;
			scannedObject = null;
		}
		
		@Override
		public void onType(String type, UDMFObject object)
		{
			scannedError = null;
			scannedName = type;
			scannedValue = null;
			scannedObject = object;
		}
	}

	private void scanNext()
	{
		currentId = parser.NextId(listener);
	}
	
	/**
	 * Checks if there are more elements to read.
	 * @return true if more elements can be read, false if not.
	 */
	public boolean hasNext()
	{
		return currentId != null;
	}

	/**
	 * Checks if there are more elements to read.
	 * @return the next element read, null if no more elements.
	 * @throws UDMFParseException if the UDMF document cannot be parsed during read.
	 */
	public Element next()
	{
		if (!hasNext())
			return null;

		if (!parser.AttributeOrObjectPredicate(listener, currentId))
			throw new UDMFParseException(listener.scannedError);
		
		Element out = new Element();
		out.name = listener.scannedName;
		
		if (listener.scannedValue != null)
		{
			out.type = ElementType.GLOBAL_ATTRIBUTE;
			out.value = listener.scannedValue;
		}
		else if (listener.scannedObject != null)
		{
			out.type = ElementType.OBJECT;
			out.value = listener.scannedObject;
		}
		else
		{
			throw new UDMFParseException("INTERNAL ERROR - Not parsed attribute nor object.");
		}

		scanNext();
		return out;
	}

}
