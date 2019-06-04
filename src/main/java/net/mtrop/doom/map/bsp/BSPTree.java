/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.bsp;

import java.util.ArrayList;
import java.util.List;

/**
 * BSP Tree Abstraction.
 * @author Matthew Tropiano
 */
public class BSPTree
{
	/** Nodes: List of Segments. */
	private List<BSPSegment> segs;
	/** Nodes: List of Subsectors. */
	private List<BSPSubsector> subsectors;
	/** Nodes: List of Nodes. */
	private List<BSPNode> nodes;
	
	public BSPTree()
	{
		segs = new ArrayList<BSPSegment>(100);
		subsectors = new ArrayList<BSPSubsector>(100);
		nodes = new ArrayList<BSPNode>(100);
	}

	public List<BSPSegment> getSegs()
	{
		return segs;
	}

	public void setSegs(BSPSegment ... segs)
	{
		this.segs.clear();
		for (BSPSegment obj : segs)
			this.segs.add(obj);
	}

	public List<BSPSubsector> getSubsectors()
	{
		return subsectors;
	}

	public void setSubsectors(BSPSubsector ... subsectors)
	{
		this.subsectors.clear();
		for (BSPSubsector obj : subsectors)
			this.subsectors.add(obj);
	}

	public List<BSPNode> getNodes()
	{
		return nodes;
	}

	public void setNodes(BSPNode ... nodes)
	{
		this.nodes.clear();
		for (BSPNode obj : nodes)
			this.nodes.add(obj);
	}

}
