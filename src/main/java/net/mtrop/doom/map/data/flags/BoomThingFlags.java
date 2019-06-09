package net.mtrop.doom.map.data.flags;

/**
 * Thing flag constants for Boom things.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface BoomThingFlags extends DoomThingFlags
{
	/** Thing flag: Does not appear in cooperative. */
	public static final int NOT_COOPERATIVE = 5;
	/** Thing flag: Does not appear in deathmatch. */
	public static final int NOT_DEATHMATCH = 6;
	
}
