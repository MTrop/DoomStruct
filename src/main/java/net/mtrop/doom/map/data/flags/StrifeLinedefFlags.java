package net.mtrop.doom.map.data.flags;

/**
 * Linedef flag constants for Strife.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface StrifeLinedefFlags extends DoomLinedefFlags
{
	/** Linedef flag: Line is a jump-over railing. */
	public static final int RAILING = 9;
	/** Linedef flag: Line blocks floating actors. */
	public static final int BLOCK_FLOATERS = 10;
	
}
