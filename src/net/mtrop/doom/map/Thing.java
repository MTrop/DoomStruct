package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a thing.
 * @author Matthew Tropiano
 */
public interface Thing extends MapObject
{
	/** Thing position: x-coordinate. */
	public static final String ATTRIB_POSITION_X = "x";
	/** Thing position: y-coordinate. */
	public static final String ATTRIB_POSITION_Y = "y";
	/** Thing angle in degrees. */
	public static final String ATTRIB_ANGLE = "angle";
	/** Thing type. */
	public static final String ATTRIB_TYPE = "type";
	
	/** Thing flag: Appears on skill 1. */
	public static final String ATTRIB_FLAG_SKILL1 = "skill1";
	/** Thing flag: Appears on skill 2. */
	public static final String ATTRIB_FLAG_SKILL2 = "skill2";
	/** Thing flag: Appears on skill 3. */
	public static final String ATTRIB_FLAG_SKILL3 = "skill3";
	/** Thing flag: Appears on skill 4. */
	public static final String ATTRIB_FLAG_SKILL4 = "skill4";
	/** Thing flag: Appears on skill 5. */
	public static final String ATTRIB_FLAG_SKILL5 = "skill5";
	/** Thing flag: Ambushes players ("deaf" flag). */
	public static final String ATTRIB_FLAG_AMBUSH = "ambush";
	/** Thing flag: Single player. */
	public static final String ATTRIB_FLAG_SINGLE_PLAYER = "single";
	/** Thing flag: Co-operative. */
	public static final String ATTRIB_FLAG_COOPERATIVE = "coop";
	/** Thing flag: Deathmatch. */
	public static final String ATTRIB_FLAG_DEATHMATCH = "dm";
	
	/** Thing flag: Friendly (Marine's Best Friend-style). */
	public static final String ATTRIB_FLAG_FRIENDLY = "friend";
	
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
