package net.mtrop.doom.map.udmf;

/**
 * Contains linedef attributes for Doom namespaces.
 * @author Matthew Tropiano
 */
public interface UDMFDoomLinedefAttributes extends UDMFCommonLinedefAttributes
{
	/** Linedef flag: Linedef passes its activation through to another line. */
	public static final String ATTRIB_FLAG_PASSTHRU = "passuse";

}
