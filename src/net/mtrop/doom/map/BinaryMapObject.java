package net.mtrop.doom.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.exception.DataExportException;

/**
 * Common elements of all map objects that are loaded from binary data.
 * This provides a general interface for getting map object data.
 * @author Matthew Tropiano
 */
public interface BinaryMapObject extends MapObject
{
	/**
	 * Gets the byte representation of this object. 
	 * @return this object as a series of bytes.
	 * @throws DataExportException if a field cannot be reliably exported.
	 */
	public byte[] toBytes() throws DataExportException;

	/**
	 * Reads in the byte representation of this object and sets its fields.
	 * @param data the byte array to read from. 
	 * @throws IOException if a read error occurs.
	 */
	public void fromBytes(byte[] data) throws IOException;
	
	/**
	 * Reads from an {@link InputStream} and sets this object's fields. 
	 * @param in the {@link InputStream} to read from. 
	 * @throws IOException if a read error occurs.
	 */
	public void readBytes(InputStream in) throws IOException;

	/**
	 * Writes this object to an {@link OutputStream}.
	 * @param out the {@link OutputStream} to write to.
	 * @throws DataExportException if a field cannot be reliably exported.
	 * @throws IOException if a write error occurs.
	 */
	public void writeBytes(OutputStream out) throws DataExportException, IOException;
	
}
