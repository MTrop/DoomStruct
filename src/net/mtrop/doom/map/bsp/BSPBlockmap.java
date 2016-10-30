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
import net.mtrop.doom.util.RangeUtils;

import com.blackrook.commons.Common;
import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.hash.HashedQueueMap;
import com.blackrook.commons.index.SparseQueueGridIndex;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.math.Pair;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * Representation of the Blockmap lump for a map.
 * This aids in collision detection for linedefs.
 * @author Matthew Tropiano
 */
public class BSPBlockmap implements BinaryObject
{
	private static final Queue<Integer> EMPTY_QUEUE = new Queue<>();
	
	/** Grid origin X-coordinate. */
	private int startX;
	/** Grid origin Y-coordinate. */
	private int startY;

	/** Grid mapping to linedef indices. */
	private SparseQueueGridIndex<Integer> innerMap;
	
	/**
	 * Creates a new Blockmap, startX and startY set to 0.
	 */
	public BSPBlockmap()
	{
		this(0, 0);
	}
	
	/**
	 * Creates a new Blockmap.
	 * @param startX the grid lower-left start position (x-axis).
	 * @param startY the grid lower-left start position (y-axis).
	 * @throws IllegalArgumentException if <code>startX</code> or <code>startY</code> is outside the range of -32768 to 32767.
	 */
	public BSPBlockmap(int startX, int startY)
	{
		RangeUtils.checkShort("Grid start X", startX);
		RangeUtils.checkShort("Grid start Y", startY);
		this.startX = startX;
		this.startY = startY;
		
		innerMap = new SparseQueueGridIndex<Integer>();
	}
	
	/**
	 * Reads and creates a new BSPBlockmap object from an array of bytes.
	 * This reads until it reaches the end of the BSPBlockmap.
	 * @param bytes the byte array to read.
	 * @return a new BSPBlockmap object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPBlockmap create(byte[] bytes) throws IOException
	{
		BSPBlockmap out = new BSPBlockmap();
		out.fromBytes(bytes);
		return out;
	}
	
	/**
	 * Reads and creates a new BSPBlockmap from an {@link InputStream} implementation.
	 * This reads from the stream until enough bytes for a {@link BSPBlockmap} are read.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new BSPBlockmap object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static BSPBlockmap read(InputStream in) throws IOException
	{
		BSPBlockmap out = new BSPBlockmap();
		out.readBytes(in);
		return out;
	}
	
	/**
	 * Adds a linedef index to this blockmap.
	 * @param x	the grid row.
	 * @param y	the grid column.
	 * @param linedefIndex	the linedef index to add.
	 * @throws IllegalArgumentException if <code>x</code> or <code>y</code> is outside the range 0 to 512 
	 * 		or <code>linedefIndex</code> is outside the range 0 to 65535.
	 */
	public void addIndex(int x, int y, int linedefIndex)
	{
		RangeUtils.checkRange("Block X", 0, 512, x);
		RangeUtils.checkRange("Block Y", 0, 512, y);
		RangeUtils.checkShortUnsigned("Linedef Index", linedefIndex);
		innerMap.enqueue(x, y, linedefIndex);
	}
	
	/**
	 * Removes a linedef index to this blockmap.
	 * @param x	the grid column.
	 * @param y	the grid row.
	 * @param linedefIndex	the linedef index to remove.
	 * @return true if removed, false if not.
	 * @throws IllegalArgumentException if <code>x</code> or <code>y</code> is outside the range 0 to 512 
	 * 		or <code>linedefIndex</code> is outside the range 0 to 65535.
	 */
	public boolean removeIndex(int x, int y, int linedefIndex)
	{
		RangeUtils.checkRange("Block X", 0, 512, x);
		RangeUtils.checkRange("Block Y", 0, 512, y);
		RangeUtils.checkShortUnsigned("Linedef Index", linedefIndex);
		Queue<Integer> queue = innerMap.get(x, y);
		return queue != null ? queue.remove(linedefIndex) : false;
	}

	/**
	 * @return the map position start, X coordinate.
	 */
	public float getStartX()
	{
		return startX;
	}

	/**
	 * @return the map position start, Y coordinate.
	 */
	public float getStartY()
	{
		return startY;
	}

	/**
	 * Returns the column index used by a particular map position,
	 * according to this grid's startX value.
	 * If posX is less than startX, this returns -1.
	 * @param posX the map position x-coordinate.
	 * @return the corresponding block column or if <code>posX</code> is less than {@link #getStartX()}, this returns -1.
	 */
	public int getColumnByMapPosition(int posX)
	{
		if (posX < startX)
			return -1;
		return (posX - startX) / 128;
	}
	
	/**
	 * Returns the row index used by a particular map position,
	 * according to this grid's startY value.
	 * @param posY the map position y-coordinate.
	 * @return the corresponding block row or if <code>posY</code> is less than {@link #getStartY()}, this returns -1.
	 */
	public int getRowByMapPosition(int posY)
	{
		if (posY < startY)
			return -1;
		return (posY - startY) / 128;
	}

	/**
	 * Returns an iterable structure for linedef indices for a certain block.
	 * @param x	the grid column.
	 * @param y	the grid row.
	 * @return an iterable structure for a particular block.
	 */
	public Iterable<Integer> getIndexList(int x, int y)
	{
		Queue<Integer> queue = innerMap.get(x, y);
		return queue != null ? queue : EMPTY_QUEUE;
	}

	/**
	 * Returns an iterable structure for linedef indices for a certain block using a map position.
	 * @param posX the map position, X-coordinate.
	 * @param posY the map position, Y-coordinate.
	 * @return an iterable structure for a particular block that corresponds to the map position.
	 */
	public Iterable<Integer> getIndexListForPosition(int posX, int posY)
	{
		int x = getColumnByMapPosition(posX);
		int y = getRowByMapPosition(posY);
		return getIndexList(x, y);
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
		innerMap.clear();
		startX = sr.readShort();
		startY = sr.readShort();
		int maxX = sr.readUnsignedShort();
		int maxY = sr.readUnsignedShort();

		// read offset table
		short[] indices = sr.readShorts(maxX*maxY);
		
		// data must be treated as a stream: find highest short offset so that the reading can stop.
		int offMax = -1;
		for (short s : indices)
		{
			int o = s & 0x0ffff;
			offMax = o > offMax ? o : offMax;
		}
		
		// precache linedef lists at each particular offset: blockmap may be compressed.
		HashedQueueMap<Integer, Integer> indexList = new HashedQueueMap<Integer, Integer>();
		int index = 4 + (maxX*maxY);
		while (index <= offMax)
		{
			int nindex = index;
			short n = sr.readShort();
			nindex++;
			
			if (n != 0)
				throw new IOException("Blockmap list at short index "+index+" should start with 0.");

			n = sr.readShort();
			nindex++;
			while (n != -1)
			{
				indexList.enqueue(index, (n & 0x0ffff));
				n = sr.readShort();
				nindex++;
			}
			
			index = nindex;
		}

		// read into internal blockmap table.
		for (int i = 0; i < maxX; i++)
			for (int j = 0; j < maxY; j++)
			{
				// "touch" entry. this is so the maximum column/row
				// still gets written on call to getDoomBytes()
				innerMap.set(i, j, new Queue<Integer>());			
				
				// add index list to map.
				int ind = indices[(i*maxY)+j];
				Queue<Integer> list = indexList.get(ind);
				if (list != null) for (Integer line : list)
					addIndex(i, j, line);
			}
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		sw.writeShort((short)startX);
		sw.writeShort((short)startY);
		int maxX = 0;
		int maxY = 0;

		for (ObjectPair<Pair, Queue<Integer>> hp : innerMap)
		{
			Pair p = hp.getKey();
			maxX = Math.max(maxX, p.x);
			maxY = Math.max(maxY, p.y);
		}
		maxX++;
		maxY++;

		sw.writeUnsignedShort(maxX);
		sw.writeUnsignedShort(maxY);
		
		// convert linedef indices for offset calculation
		short[][][] shorts = new short[maxX][maxY][];
		
		for (int x = 0; x < maxX; x++)
			for (int y = 0; y < maxY; y++)
			{
				Queue<Integer> list = innerMap.get(x,y);
				if (list != null)
				{
					shorts[x][y] = new short[list.size()];
					int n = 0;
					for (Integer s : list)
						shorts[x][y][n++] = s.shortValue();
				}
				else
					shorts[x][y] = new short[0];
			}
		
		// set starting offset
		short offset = (short)(4 + (maxX*maxY));

		// write offset table
		for (int x = 0; x < maxX; x++)
			for (int y = 0; y < maxY; y++)
			{
				sw.writeShort(offset);
				offset += 2 + shorts[x][y].length;
			}
		
		// write index lists
		for (int x = 0; x < maxX; x++)
			for (int y = 0; y < maxY; y++)
			{
				sw.writeShort((short)0);
				for (int n = 0; n < shorts[x][y].length; n++)
					sw.writeShort(shorts[x][y][n]);
				sw.writeShort((short)-1);
			}

	}
	
}
