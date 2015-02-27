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

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.RangeUtils;

import com.blackrook.commons.Common;

/**
 * Singular patch entry for a texture.
 */
public abstract class CommonPatch implements BinaryObject
{
	/** Horizontal offset of the patch. */
	protected int originX;
	/** Vertical offset of the patch. */
	protected int originY;
	/** Index of patch in patch names lump to use. */
	protected int patchIndex;

	public CommonPatch()
	{
		originX = 0;
		originY = 0;
		patchIndex = 0;
	}
	
	/** 
	 * Gets the horizontal offset of the patch. 
	 */
	public int getOriginX()
	{
		return originX;
	}

	/** 
	 * Sets the horizontal offset of the patch. 
	 */
	public void setOriginX(int originX)
	{
		RangeUtils.checkShort("Patch Origin X", originX);
		this.originX = originX;
	}

	/** 
	 * Gets the vertical offset of the patch. 
	 */
	public int getOriginY()
	{
		return originY;
	}

	/** 
	 * Sets the vertical offset of the patch. 
	 */
	public void setOriginY(int originY)
	{
		RangeUtils.checkShort("Patch Origin Y", originY);
		this.originY = originY;
	}

	/** 
	 * Gets the patch's index into the patch name lump. 
	 */
	public int getPatchIndex()
	{
		return patchIndex;
	}

	/** 
	 * Sets the patch's index into the patch name lump. 
	 */
	public void setPatchIndex(int patchIndex)
	{
		RangeUtils.checkShortUnsigned("Patch Index", patchIndex);
		this.patchIndex = patchIndex;
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
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Patch ");
		sb.append(patchIndex);
		sb.append(" (");
		sb.append(originX);
		sb.append(", ");
		sb.append(originY);
		sb.append(")");
		return sb.toString();
	}
	
	

}
