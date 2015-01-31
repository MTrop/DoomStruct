package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a line.
 * <p>Linedefs connect vertices and attach sidedef definitions.
 * @author Matthew Tropiano
 */
public interface Linedef
{
	/**
	 * @return the linedef's special.
	 */
	public int getSpecial();
	
	/**
	 * @return the linedef's starting vertex reference index (in loaded maps).
	 */
	public int getVertexStartIndex();
	
	/**
	 * @return the linedef's ending vertex reference index (in loaded maps).
	 */
	public int getVertexEndIndex();
	
	/**
	 * @return the linedef's front sidedef reference index (in loaded maps).
	 */
	public int getSidedefFrontIndex();
	
	/**
	 * @return the linedef's back sidedef reference index (in loaded maps).
	 */
	public int getSidedefBackIndex();
	
}
