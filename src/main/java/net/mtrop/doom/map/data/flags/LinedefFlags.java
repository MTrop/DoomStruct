package net.mtrop.doom.map.data.flags;

/**
 * Linedef flag constants.
 * The constant value is how many places to bit shift 1 to equal the flag bit.  
 * @author Matthew Tropiano
 */
interface LinedefFlags
{
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
