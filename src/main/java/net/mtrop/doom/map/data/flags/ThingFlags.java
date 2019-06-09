package net.mtrop.doom.map.data.flags;

/**
 * Thing flag constants.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
interface ThingFlags
{
	/** Thing flag: Appears on easy difficulty. */
	public static final int EASY = 0;
	/** Thing flag: Appears on medium difficulty. */
	public static final int MEDIUM = 1;
	/** Thing flag: Appears on hard difficulty. */
	public static final int HARD = 2;

	/**
	 * Gets the bit value of the flag (shifted).
	 * @param flag the input flag constant.
	 * @return the resultant value.
	 */
	default int value(int flag)
	{
		return 1 << flag;
	}
	
}
