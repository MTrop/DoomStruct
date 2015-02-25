package net.mtrop.doom.map.udmf;

/**
 * Contains vertex attributes for ZDoom namespaces.
 * @author Matthew Tropiano
 */
public interface UDMFZDoomVertexAttributes extends UDMFCommonVertexAttributes
{
	/** Vertex Z position (floor height). */
	public static final String ATTRIB_POSITION_Z_FLOOR = "zfloor";
	/** Vertex Z position (ceiling height). */
	public static final String ATTRIB_POSITION_Z_CEILING = "zceiling";
	
}
