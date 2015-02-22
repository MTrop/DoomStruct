package net.mtrop.doom.map.bsp;

import com.blackrook.commons.list.List;

/**
 * 
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
	/** Map Blockmap. */
	private BSPBlockmap blockmap;
	/** Map Reject. */
	private BSPReject reject;
	
	public BSPTree()
	{
		segs = new List<BSPSegment>(100);
		subsectors = new List<BSPSubsector>(100);
		nodes = new List<BSPNode>(100);
		blockmap = null;
		reject = null;
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

	public BSPBlockmap getBlockmap()
	{
		return blockmap;
	}

	public void setBlockmap(BSPBlockmap blockmap)
	{
		this.blockmap = blockmap;
	}

	public BSPReject getReject()
	{
		return reject;
	}

	public void setReject(BSPReject reject)
	{
		this.reject = reject;
	}

}
