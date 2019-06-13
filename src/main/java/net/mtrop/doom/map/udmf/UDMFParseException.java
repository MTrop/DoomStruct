package net.mtrop.doom.map.udmf;

/**
 * An exception thrown on UDMF parse errors.
 * @author Matthew Tropiano
 */
public class UDMFParseException extends RuntimeException
{
	private static final long serialVersionUID = 1102498826055072221L;
	
	public UDMFParseException()
	{
		super();
	}
	
	public UDMFParseException(String message)
	{
		super(message);
	}

}
