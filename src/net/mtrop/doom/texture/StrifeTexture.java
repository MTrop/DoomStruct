package net.mtrop.doom.texture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.util.NameUtils;

import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class represents a single texture entry in a TEXTURE1/TEXTURE2/TEXTURES lump.
 * Strife Textures use Strife's texture representation. 
 * @author Matthew Tropiano
 */
public class StrifeTexture extends Texture<StrifeTexture.Patch>
{
	/**
	 * Reads and creates a new StrifeTexture object from an array of bytes.
	 * This reads until it reaches the end of the texture.
	 * @param bytes the byte array to read.
	 * @return a new DoomTexture object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static StrifeTexture create(byte[] bytes) throws IOException
	{
		StrifeTexture out = new StrifeTexture();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new StrifeTexture from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a single {@link StrifeTexture} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new DoomTexture object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static StrifeTexture read(InputStream in) throws IOException
	{
		StrifeTexture out = new StrifeTexture();
		out.readBytes(in);
		return out;
	}
	
	@Override
	public void readBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		name = NameUtils.toValidTextureName(NameUtils.nullTrim(sr.readASCIIString(8)));
		sr.readShort();
		sr.readShort();
		width = sr.readUnsignedShort();
		height = sr.readUnsignedShort();
		
		int n = sr.readUnsignedShort();
		while (n-- > 0)
		{
			Patch p = new Patch();
			p.readBytes(in);
			patches.add(p);
		}
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeBytes(NameUtils.toASCIIBytes(name, 8));
		sw.writeUnsignedShort(0);
		sw.writeUnsignedShort(0);
		sw.writeUnsignedShort(width);
		sw.writeUnsignedShort(height);
		sw.writeUnsignedShort(patches.size());
		for (Patch p : patches)
			p.writeBytes(out);
	}

	/**
	 * Singular patch entry for a texture.
	 */
	public static class Patch extends net.mtrop.doom.texture.Patch
	{
		@Override
		public void readBytes(InputStream in) throws IOException
		{
			SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
			originX = sr.readShort();
			originY = sr.readShort();
			patchIndex = sr.readUnsignedShort();
		}

		@Override
		public void writeBytes(OutputStream out) throws IOException
		{
			SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
			sw.writeShort((short)originX);
			sw.writeShort((short)originY);
			sw.writeUnsignedShort(patchIndex);
		}

	}
	
}
