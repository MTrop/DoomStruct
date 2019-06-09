package net.mtrop.doom.map.data.flags;

/**
 * Thing flag constants for Hexen things.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface HexenThingFlags extends ThingFlags
{
	/** Thing flag: Appears on ambush difficulty. */
	public static final int AMBUSH = 3;
	/** Thing flag: Starts dormant. */
	public static final int DORMANT = 4;
	/** Thing flag: Appears for fighter. */
	public static final int FIGHTER = 5;
	/** Thing flag: Appears for cleric. */
	public static final int CLERIC = 6;
	/** Thing flag: Appears for mage. */
	public static final int MAGE = 7;
	/** Thing flag: Appears in Single Player. */
	public static final int SINGLEPLAYER = 8;
	/** Thing flag: Appears in Cooperative. */
	public static final int COOPERATIVE = 9;
	/** Thing flag: Appears in DeathMatch. */
	public static final int DEATHMATCH = 10;

}
