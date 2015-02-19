package net.mtrop.doom.struct;

import java.io.*;

import net.mtrop.doom.BinaryObject;

import com.blackrook.commons.Common;
import com.blackrook.commons.math.RMath;
import com.blackrook.io.SuperReader;

/**
 * This is a single entry that indexes the palette indices for color lookup.
 * This is NOT the representation of the complete COLORMAP lump, see ColorMapLump
 * for that implementation. This is merely a single entry in that lump. Other commercial
 * IWAD lumps that are colormaps are the TRANTBL lumps in Hexen.
 * @author Matthew Tropiano
 */
public class Colormap implements BinaryObject
{
	/** The number of total indices in a standard Doom color map. */
	public static final int
	NUM_INDICES = 256;
	
	/** The index list in this map. */
	protected int[] indices;

	/**
	 * Creates a new identity colormap where all indices point to their own index.
	 */
	public Colormap()
	{
		indices = new int[NUM_INDICES];
		setIdentity();
	}
	
	/**
	 * Creates a new colormap by copying the contents of another.
	 */
	public Colormap(Colormap map)
	{
		System.arraycopy(map.indices, 0, indices, 0, NUM_INDICES);
	}
	
	/**
	 * Reads and creates a new Colormap object from an array of bytes.
	 * This reads the first 256 bytes from the array.
	 * @param bytes the byte array to read.
	 * @return a new Colormap object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Colormap create(byte[] bytes) throws IOException
	{
		Colormap out = new Colormap();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new Colormap from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link Colormap} are read (256 bytes).
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new Colormap with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Colormap read(InputStream in) throws IOException
	{
		Colormap out = new Colormap();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Reads and creates new Colormap from an array of bytes.
	 * This reads from the first 256 * <code>count</code> bytes of the array.
	 * @param bytes the byte array to read.
	 * @param count the amount of objects to read.
	 * @return an array of Colormap objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Colormap[] create(byte[] bytes, int count) throws IOException
	{
		return read(new ByteArrayInputStream(bytes), count);
	}

	/**
	 * Reads and creates a new Colormap from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for <code>count</code> {@link Colormap}s are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @param count the amount of objects to read.
	 * @return an array of Colormap objects with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Colormap[] read(InputStream in, int count) throws IOException
	{
		Colormap[] out = new Colormap[count];
		for (int i = 0; i < count; i++)
		{
			out[i] = new Colormap();
			out[i].readBytes(in);
		}
		return out;
	}

	/**
	 * Creates a color map where each color is mapped to its own index
	 * (index 0 is palette color 0 ... index 255 is palette color 255).
	 * @return a new color map with the specified indices already mapped.
	 */
	public static Colormap createIdentityMap()
	{
		Colormap out = new Colormap();
		out.setIdentity();
		return out;
	}

	/**
	 * Resets the color map to where each color is mapped to its own index
	 * (index 0 is palette color 0 ... index 255 is palette color 255).
	 */
	public void setIdentity()
	{
		for (int i = 0; i < NUM_INDICES; i++)
			indices[i] = i;
	}

	/**
	 * Sets a colormap translation by remapping groups of contiguous indices.
	 * @param startIndex the starting replacement index.
	 * @param endIndex the ending replacement index.
	 * @param startValue the starting replacement value.
	 * @param endValue the ending replacement value.
	 */
	public void setTranslation(int startIndex, int endIndex, int startValue, int endValue)
	{
		int min = Math.min(startIndex, endIndex);
		int max = Math.max(startIndex, endIndex);
		
		float len = Math.abs(startValue - endValue) + 1f;
		
		for (int i = min; i <= max; i++)
			indices[i] = (int)RMath.linearInterpolate((i - min) / len, startValue, endValue);
	}
	
	/**
	 * Returns the palette index of a specific index in the map.
	 * @param index	the index number of the entry.
	 * @throws ArrayIndexOutOfBoundsException if index > NUM_INDICES or < 0.
	 */
	public int getPaletteIndex(int index)
	{
		return indices[index];
	}
	
	/**
	 * Sets the palette index of a specific index in the map.
	 * @param index	the index number of the entry.
	 * @param paletteIndex the new index.
	 * @throws ArrayIndexOutOfBoundsException if index &gt; NUM_INDICES or &lt; 0.
	 * @throws IllegalArgumentException if paletteIndex &lt; 0 or &gt; 255.
	 */
	public void setPaletteIndex(int index, int paletteIndex)
	{
		if (paletteIndex < 0 || paletteIndex > 255)
			throw new IllegalArgumentException("Palette index is out of range. Must be from 0 to 255.");
		
		indices[index] = paletteIndex;
	}
	
	@Override
	public byte[] toBytes()
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
		for (int i = 0; i < NUM_INDICES; i++)
			indices[i] = sr.readByte() & 0x0ff;
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		for (int i : indices)
			out.write(i & 0x0ff);
	}

	@Override
	public String toString()
	{
		return "Colormap " + java.util.Arrays.toString(indices);
	}
	
	
	
}
