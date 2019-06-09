package net.mtrop.doom.map.data.flags;

/**
 * Thing flag constants for Doom/Heretic.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface DoomThingFlags extends ThingFlags
{
	/** Thing flag: Ambushes players. */
	public static final int AMBUSH = 3;
	/** Thing flag: Does not appear in single player. */
	public static final int NOT_SINGLEPLAYER = 4;
	
}
