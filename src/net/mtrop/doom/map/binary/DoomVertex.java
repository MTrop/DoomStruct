package net.mtrop.doom.map.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

import net.mtrop.doom.exception.DataExportException;
import net.mtrop.doom.map.BinaryMapObject;
import net.mtrop.doom.map.Vertex;
import net.mtrop.doom.util.RangeUtils;

public class DoomVertex implements BinaryMapObject, Vertex
{
	/** Vertex: X-coordinate. */
	private int x;
	/** Vertex: X-coordinate. */
	private int y;
	
	/**
	 * Creates a new vertex with default values set.
	 * @see #reset()
	 */
	public DoomVertex()
	{
		reset();
	}

	/**
	 * Reads and creates a new DoomVertex from an array of bytes.
	 * This reads from the first 4 bytes of the stream.
	 * The stream is NOT closed at the end.
	 * @param bytes the byte array to read.
	 * @return a new DoomVertex with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomVertex create(byte[] bytes) throws IOException
	{
		DoomVertex out = new DoomVertex();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomVertex from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link DoomVertex} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomVertex with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomVertex read(InputStream in) throws IOException
	{
		DoomVertex out = new DoomVertex();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Resets this vertex's value to (0, 0).
	 */
	public void reset()
	{
		x = 0;
		y = 0;
	}
	
	/**
	 * Sets the coordinates of this vertex.
	 * @param x the x-coordinate value.
	 * @param y the y-coordinate value.
	 */
	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the X-coordinate value of this vertex.
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 * Sets the Y-coordinate value of this vertex.
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	
	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public byte[] toBytes() throws DataExportException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try { writeBytes(bos); } catch (IOException e) { /* Shouldn't happen. */ }
		return bos.toByteArray();
	}

	@Override
	public void fromBytes(byte[] data) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		readBytes(bin);
		Common.close(bin);
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		x = sr.readShort();
		y = sr.readShort();
	}

	@Override
	public void writeBytes(OutputStream out) throws DataExportException, IOException
	{
		RangeUtils.checkShortUnsigned("X-coordinate", x);
		RangeUtils.checkShortUnsigned("Y-coordinate", y);

		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeShort((short)x);
		sw.writeShort((short)y);
	}

	@Override
	public String toString()
	{
		return "Vertex (" + x + ", " + y + ")";
	}
	
}
