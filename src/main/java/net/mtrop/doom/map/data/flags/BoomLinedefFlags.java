package net.mtrop.doom.map.data.flags;

/**
 * Linedef flag constants for Doom/Boom/MBF/SMMU.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
public interface BoomLinedefFlags extends DoomLinedefFlags
{
	/** Linedef flag: Pass USE through this line. */
	public static final int PASSTHRU = 9;
}
