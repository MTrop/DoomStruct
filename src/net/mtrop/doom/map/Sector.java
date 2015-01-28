package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a sector.
 * <p>Technically, sectors don't really "exist" per se, they're additional info for sidedefs that describe
 * how to draw the attached floor/ceiling and sidedef lighting (and 3D collision detection).
 * @author Matthew Tropiano
 */
public interface Sector extends MapObject
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

	/**
	 * @return the sector's floor height.
	 */
	public int getFloorHeight();
	
	/**
	 * @return the sector's ceiling height.
	 */
	public int getCeilingHeight();
	
	/**
	 * @return the sector's floor texture.
	 */
	public String getFloorTexture();

	/**
	 * @return the sector's ceiling texture.
	 */
	public String getCeilingTexture();

	/**
	 * @return the sector's light level.
	 */
	public int getLightLevel();

	/**
	 * @return the sector's special type.
	 */
	public int getSpecial();

	/**
	 * @return the sector's tag.
	 */
	public int getTag();

}
