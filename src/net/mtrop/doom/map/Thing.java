package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a thing.
 * @author Matthew Tropiano
 */
public interface Thing
{
	/**
	 * @return thing position, x-coordinate.
	 */
	public int getX();
	
	/**
	 * @return thing position, y-coordinate.
	 */
	public int getY();
	
	/**
	 * @return thing type (a.k.a. editor number).
	 */
	public int getType();
	
	/**
	 * @return thing angle in degrees.
	 */
	public int getAngle();
	
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
	 * This may be considered "easy" depending on implementation.
	 * @return true if this appears on skill 1, false if not.
	 * @see #isEasy()
	 */
	public boolean isSkill1();

	/**
	 * This may be considered "easy" depending on implementation.
	 * @return true if this appears on skill 2, false if not.
	 * @see #isEasy()
	 */
	public boolean isSkill2();

	/**
	 * This may be considered "medium" depending on implementation.
	 * @return true if this appears on skill 3, false if not.
	 * @see #isMedium()
	 */
	public boolean isSkill3();

	/**
	 * This may be considered "hard" depending on implementation.
	 * @return true if this appears on skill 4, false if not.
	 * @see #isHard()
	 */
	public boolean isSkill4();

	/**
	 * This may be considered "hard" depending on implementation.
	 * @return true if this appears on skill 5, false if not.
	 * @see #isHard()
	 */
	public boolean isSkill5();

	/**
	 * @return true if this ambushes players, false if not.
	 */
	public boolean isAmbush();

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
	 * @return true if this is flagged as friendly, false if not.
	 */
	public boolean isFriendly();

}
