/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.texture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.List;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.NameUtils;
import net.mtrop.doom.util.RangeUtils;

/**
 * Common contents of texture definitions.
 * @author Matthew Tropiano
 */
public abstract class CommonTexture<P extends CommonPatch> implements BinaryObject, Iterable<P>, Comparable<CommonTexture<?>>
{
	/** Texture name. */
	protected String name;
	/** Width of texture. */
	protected int width;
	/** Height of texture. */
	protected int height;
	/** List of patches. */
	protected List<P> patches;
	
	/**
	 * Creates a new texture.
	 */
	public CommonTexture()
	{
		name = "UNNAMED";
		width = 0;
		height = 0;
		patches = new List<P>(2);
	}
	
	/**
	 * @return the name of this texture.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of this texture.
	 * @throws IllegalArgumentException if the texture name is invalid.
	 */
	public void setName(String name)
	{
		if (!NameUtils.isValidTextureName(name))
			throw new IllegalArgumentException("Invalid texture name.");
		this.name = name;
	}
	
	/**
	 * @return the width of this texture in pixels.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Sets the width of this texture in pixels.
	 * @throws IllegalArgumentException if the width is outside the range 0 to 65535.
	 */
	public void setWidth(int width)
	{
		RangeUtils.checkShortUnsigned("Width", width);
		this.width = width;
	}
	
	/**
	 * @return the height of this texture in pixels.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Sets the height of this texture in pixels.
	 * @throws IllegalArgumentException if the height is outside the range 0 to 65535.
	 */
	public void setHeight(int height)
	{
		RangeUtils.checkShortUnsigned("Height", height);
		this.height = height;
	}
	
	/**
	 * Adds a patch entry to this texture.
	 * @param p	the patch to add.
	 * @throws IllegalArgumentException if the patch would make the patch count exceeds 65535.
	 */
	public void addPatch(P p)
	{
		RangeUtils.checkShortUnsigned("Number of patches", patches.size());
		patches.add(p);
	}
	
	/**
	 * Removes a patch entry from this texture.
	 * @param p	the patch to remove.
	 */
	public boolean removePatch(P p)
	{
		return patches.remove(p);
	}
	
	/**
	 * Removes a patch entry from this texture by index.
	 * @param i	the index of the patch to remove.
	 */
	public P removePatch(int i)
	{
		return patches.removeIndex(i);
	}
	
	/**
	 * Gets a patch from this texture.
	 * @param i		the index of the patch.
	 */
	public P getPatch(int i)
	{
		return patches.getByIndex(i);
	}
	
	/**
	 * Returns the amount of patches on this texture.
	 */
	public int getPatchCount()
	{
		return patches.size();
	}

	@Override
	public Iterator<P> iterator()
	{
		return patches.iterator();
	}

	@Override
	public int compareTo(CommonTexture<?> o)
	{
		return name.compareTo(o.name);
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
	
	
	
}
