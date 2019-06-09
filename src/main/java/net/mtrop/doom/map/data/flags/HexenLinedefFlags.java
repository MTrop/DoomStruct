package net.mtrop.doom.map.data.flags;

/**
 * Linedef flag constants for Hexen.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface HexenLinedefFlags extends DoomLinedefFlags
{
	/** Linedef flag: Line's special is repeatable (special is not cleared). */
	public static final int REPEATABLE = 9;
	
}
