package net.mtrop.doom;

public interface Thing
{

	/**
	 * @return the position X-coordinate.
	 */
	public int getX();

	/**
	 * @return the position Y-coordinate.
	 */
	public int getY();

	/**
	 * @return the Z position relative to sector plane.
	 */
	public int getZ();

	/**
	 * @return the angle (in degrees).
	 */
	public int getAngle();

	/**
	 * @return thing type (a.k.a. editor number).
	 */
	public int getType();

	/**
	 * @return true if this appears on skills considered "easy," false if not.
	 */
	public boolean isEasy();

	/**
	 * @return true if this appears on skills considered "medium," false if not.
	 */
	public boolean isMedium();

	/**
	 * @return true if this appears on skills considered "hard," false if not.
	 */
	public boolean isHard();

	/**
	 * @return true if this ambushes players, false if not.
	 */
	public boolean isAmbush();

	/**
	 * @return true if this is flagged as friendly, false if not.
	 */
	public boolean isFriendly();

	/**
	 * @return true if this is dormant, false if not.
	 */
	public boolean isDormant();

	/**
	 * @return true if this appears on single player, false if not.
	 */
	public boolean isSinglePlayer();

	/**
	 * @return true if this appears on cooperative, false if not.
	 */
	public boolean isCooperative();

	/**
	 * @return true if this appears on deathmatch, false if not.
	 */
	public boolean isDeathmatch();

	/**
	 * @return true if this appears for Fighters, false if not.
	 */
	public boolean isFighter();

	/**
	 * @return true if this appears for Clerics, false if not.
	 */
	public boolean isCleric();

	/**
	 * @return true if this appears for Mages, false if not.
	 */
	public boolean isMage();

	/**
	 * @return true if this is flagged as 25% translucent, false if not.
	 */
	public boolean isTranslucent25();

	/**
	 * @return true if this is flagged as 75% translucent, false if not.
	 */
	public boolean isTranslucent75();

	/**
	 * @return the special action for this thing.
	 */
	public int getSpecial();

	/**
	 * @return gets the array of special arguments.
	 */
	public int[] getArguments();

}
