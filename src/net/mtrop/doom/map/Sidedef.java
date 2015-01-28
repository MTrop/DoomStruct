package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a line side.
 * <p>These essentially tell the engine how to render lines and what sectors they belong to. 
 * @author Matthew Tropiano
 */
public interface Sidedef extends MapObject
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

	/**
	 * @return the sidedef's base texture offset X.
	 */
	public int getOffsetX();
	
	/**
	 * @return the sidedef's base texture offset Y.
	 */
	public int getOffsetY();
	
	/**
	 * @return the sidedef's top texture.
	 */
	public String getTextureTop();

	/**
	 * @return the sidedef's bottom texture.
	 */
	public String getTextureBottom();

	/**
	 * @return the sidedef's middle texture.
	 */
	public String getTextureMiddle();

	/**
	 * @return the sidedef's sector reference index (in loaded maps).
	 */
	public int getSectorReference();

}
