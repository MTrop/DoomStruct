package net.mtrop.doom.exception;

/**
 * An exception that is normally thrown when data cannot be easily converted.
 * This should be thrown when conversion is expected to be correct, i.e. initiated
 * from code an not an external file.
 * @author Matthew Tropiano
 */
public class DataConversionException extends RuntimeException
{
	private static final long serialVersionUID = -3385983010870832191L;

	public DataConversionException()
	{
		super();
	}

	public DataConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DataConversionException(String message)
	{
		super(message);
	}

	public DataConversionException(Throwable cause)
	{
		super(cause);
	}
}
