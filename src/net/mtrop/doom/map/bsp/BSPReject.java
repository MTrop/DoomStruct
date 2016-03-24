/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.bsp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mtrop.doom.BinaryObject;

import com.blackrook.commons.Common;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * Represents the Reject lump.
 * <p>
 * The reject lump is a lookup grid that hold information on what sectors can
 * "see" other sectors on the map used for thing sight algorithms. 
 * @author Matthew Tropiano
 */
public class BSPReject implements BinaryObject
{
	/** The reject grid itself. */
	private boolean[][] grid;
	
	/**
	 * Creates a new blank reject grid.
	 * @param sectors the number of sectors.
	 */
	public BSPReject(int sectors)
	{
		grid = new boolean[sectors][sectors];
	}
	
	/**
	 * Checks whether a sector is visible from another.
	 */
	public boolean getSectorIsVisibleTo(int sectorIndex, int targetSectorIndex)
	{
		return grid[targetSectorIndex][sectorIndex];
	}
	
	/**
	 * Sets whether a sector is visible from another.
	 */
	public void setSectorIsVisibleTo(int sectorIndex, int targetSectorIndex, boolean flag)
	{
		grid[targetSectorIndex][sectorIndex] = flag;
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
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length && in.available() > 0; j++)
				grid[i][j] = sr.readBit();
		sr.byteAlign();
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				sw.writeBit(grid[i][j]);
		sw.flushBits();
	}

}
