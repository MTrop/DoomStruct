package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a sector.
 * <p>Technically, sectors don't really "exist" per se, they're additional info for sidedefs that describe
 * how to draw the attached floor/ceiling and sidedef lighting (and 3D collision detection).
 * @author Matthew Tropiano
 */
public interface Sector
{
	/** Blank texture name. */
	public static final String TEXTURE_BLANK = "-";
	
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
