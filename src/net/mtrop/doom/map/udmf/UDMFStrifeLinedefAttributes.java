package net.mtrop.doom.map.udmf;

/**
 * Contains linedef attributes for Doom namespaces.
 * @author Matthew Tropiano
 */
public interface UDMFStrifeLinedefAttributes extends UDMFCommonLinedefAttributes
{
	/** Linedef flag: Linedef is translucent. */
	public static final String ATTRIB_FLAG_TRANSLUCENT = "translucent";
	/** Linedef flag: Linedef is a railing that can be jumped over. */
	public static final String ATTRIB_FLAG_JUMPOVER = "jumpover";
	/** Linedef flag: Linedef blocks floating enemies. */
	public static final String ATTRIB_FLAG_BLOCK_FLOAT = "blockfloaters";

}
