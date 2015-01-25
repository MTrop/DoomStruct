package net.mtrop.doom.exception;

import java.io.IOException;

/**
 * An exception thrown when an operation on a WAD file would violate its
 * structural integrity in some way.
 * @author Matthew Tropiano
 */
public class WadException extends IOException
{
	private static final long serialVersionUID = 7393763909497049387L;

	public WadException()
	{
		super();
	}

	public WadException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WadException(String message)
	{
		super(message);
	}

	public WadException(Throwable cause)
	{
		super(cause);
	}
	
}
