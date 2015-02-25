package net.mtrop.doom.map.udmf;

/**
 * Contains common sector attributes on some UDMF structures.
 * @author Matthew Tropiano
 */
public interface UDMFCommonSectorAttributes extends UDMFCommonAttributes
{
	/** Sector floor height. */
	public static final String ATTRIB_HEIGHT_FLOOR = "heightfloor";
	/** Sector ceiling height. */
	public static final String ATTRIB_HEIGHT_CEILING = "heightceiling";
	/** Sector floor texture. */
	public static final String ATTRIB_TEXTURE_FLOOR = "texturefloor";
	/** Sector ceiling texture. */
	public static final String ATTRIB_TEXTURE_CEILING = "textureceiling";
	/** Sector light level. */
	public static final String ATTRIB_LIGHT_LEVEL = "lightlevel";
	/** Sector special. */
	public static final String ATTRIB_SPECIAL = "special";
	/** Sector tag/id. */
	public static final String ATTRIB_TAG = "id";

}
