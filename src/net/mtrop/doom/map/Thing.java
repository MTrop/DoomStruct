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
	
}
