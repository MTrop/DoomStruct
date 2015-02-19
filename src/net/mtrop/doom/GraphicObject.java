package net.mtrop.doom;

/**
 * Interface for graphic data.
 * @author Matthew Tropiano
 */
public interface GraphicObject
{
	
	/**
	 * Gets the offset from the center, horizontally, in pixels.
	 */
	public int getOffsetX();

	/**
	 * Gets the offset from the center, vertically, in pixels.
	 */
	public int getOffsetY();

	/**
	 * Returns the width of this graphic in pixels.
	 */
	public int getWidth();
	
	/**
	 * Returns the height of this graphic in pixels.
	 */
	public int getHeight();

}
