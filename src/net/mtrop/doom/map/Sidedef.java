package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a line side.
 * <p>These essentially tell the engine how to render lines and what sectors they belong to. 
 * @author Matthew Tropiano
 */
public interface Sidedef
{
	/** Blank texture name. */
	public static final String TEXTURE_BLANK = "-";
	
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
	public int getSectorIndex();

}
