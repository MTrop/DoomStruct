package net.mtrop.doom.exception;

import java.io.IOException;

/**
 * An exception that is normally thrown when imported data cannot be converted.
 * @author Matthew Tropiano
 */
public class DataFormatException extends IOException
{

	private static final long serialVersionUID = 4169948362081204093L;

	public DataFormatException()
	{
		super();
	}

	public DataFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DataFormatException(String message)
	{
		super(message);
	}

	public DataFormatException(Throwable cause)
	{
		super(cause);
	}
}
