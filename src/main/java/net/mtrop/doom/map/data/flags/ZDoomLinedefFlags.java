package net.mtrop.doom.map.data.flags;

/**
 * Linedef flag constants for ZDoom (Hexen format).
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface ZDoomLinedefFlags extends HexenLinedefFlags
{
	/** Linedef flag: Special can be activated by players and monsters. */
	public static final int ACTIVATED_BY_MONSTERS = 13;
	/** Linedef flag: Blocks players. */
	public static final int BLOCK_PLAYERS = 14;
	/** Linedef flag: Blocks everything (like a one-sided line). */
	public static final int BLOCK_EVERYTHING = 15;
	
}
