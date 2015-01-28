package net.mtrop.doom.exception;

/**
 * An exception thrown when Doom Map information is unavailable or malformed.
 * @author Matthew Tropiano
 */
public class MapException extends RuntimeException
{
	private static final long serialVersionUID = 4553734950678544532L;

	public MapException()
	{
		super();
	}

	public MapException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MapException(String message)
	{
		super(message);
	}

	public MapException(Throwable cause)
	{
		super(cause);
	}
	
}
