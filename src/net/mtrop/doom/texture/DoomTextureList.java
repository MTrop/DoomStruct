package net.mtrop.doom.texture;

import java.io.IOException;
import java.io.InputStream;

import net.mtrop.doom.BinaryObject;

import com.blackrook.io.SuperReader;

/**
 * This is the lump that contains a collection of Doom-formatted textures.
 * All textures are stored in here, usually named TEXTURE1 or TEXTURE2 in the WAD.
 * @author Matthew Tropiano
 */
public class DoomTextureList extends CommonTextureList<DoomTexture> implements BinaryObject
{
	/**
	 * Creates a new TextureList with a default starting capacity.
	 */
	public DoomTextureList()
	{
		super();
	}

	/**
	 * Creates a new TextureList with a specific starting capacity.
	 */
	public DoomTextureList(int capacity)
	{
		super(capacity);
	}

	/**
	 * Reads and creates a new DoomTextureList from an array of bytes.
	 * This reads a full texture set from the array.
	 * @param bytes the byte array to read.
	 * @return a new DoomTextureList with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomTextureList create(byte[] bytes) throws IOException
	{
		DoomTextureList out = new DoomTextureList();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new DoomTextureList from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a full texture set are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomTextureList with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static DoomTextureList read(InputStream in) throws IOException
	{
		DoomTextureList out = new DoomTextureList();
		out.readBytes(in);
		return out;
	}
	
	@Override
	public void readBytes(InputStream in) throws IOException
	{
		clear();
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		int n = sr.readInt();
		
		in.skip(n*4);
		
		while(n-- > 0)
		{
			DoomTexture t = new DoomTexture();
			t.readBytes(in);
			add(t);
		}
	}

}
