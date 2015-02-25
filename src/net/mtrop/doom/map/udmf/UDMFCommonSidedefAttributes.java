package net.mtrop.doom.map.udmf;

/**
 * Contains common sidedef attributes on some UDMF structures.
 * @author Matthew Tropiano
 */
public interface UDMFCommonSidedefAttributes extends UDMFCommonAttributes
{
	/** Sidedef base texture offset X. */
	public static final String ATTRIB_OFFSET_X = "offsetx";
	/** Sidedef base texture offset Y. */
	public static final String ATTRIB_OFFSET_Y = "offsety";
	/** Sidedef top texture. */
	public static final String ATTRIB_TEXTURE_TOP = "texturetop";
	/** Sidedef bottom texture. */
	public static final String ATTRIB_TEXTURE_BOTTOM = "texturebottom";
	/** Sidedef middle texture. */
	public static final String ATTRIB_TEXTURE_MIDDLE = "texturemiddle";
	/** Sidedef sector reference. */
	public static final String ATTRIB_SECTOR_INDEX = "sector";

}
