package net.mtrop.doom.map.data.flags;

/**
 * Thing flag constants for Strife things.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface StrifeThingFlags extends ThingFlags
{
	/** Thing flag: Thing starts in standing mode. */
	public static final int STANDING = 3;
	/** Thing flag: Appears in multiplayer only. */
	public static final int MULTIPLAYER = 4;
	/** Thing flag: Ambushes players. */
	public static final int AMBUSH = 5;
	/** Thing flag: Thing starts friendly to players. */
	public static final int ALLY = 6;
	/** Thing flag: Appears at 25% translucency. */
	public static final int TRANSLUCENT_25 = 7;
	/** Thing flag: Appears at 75% translucency. */
	public static final int TRANSLUCENT_75 = 8;

}
