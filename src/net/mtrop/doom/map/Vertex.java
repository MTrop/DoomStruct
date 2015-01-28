package net.mtrop.doom.map;

/**
 * Map vertex information.
 * @author Matthew Tropiano
 */
public interface Vertex extends MapObject
{
	/** Vertex position: x-coordinate. */
	public static final String ATTRIB_POSITION_X = "x";
	/** Vertex position: y-coordinate. */
	public static final String ATTRIB_POSITION_Y = "y";
	
	/**
	 * @return vertex position, x-coordinate.
	 */
	public int getX();
	
	/**
	 * @return vertex position, x-coordinate.
	 */
	public int getY();
	
}
