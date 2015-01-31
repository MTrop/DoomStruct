package net.mtrop.doom.exception;

/**
 * Exception thrown on a data export error of a Doom data structure.
 * Commonly occurs when a data structure contains a value that cannot
 * be properly exported in such a way that data loss or loss of unit
 * significance will occur.
 * @author Matthew Tropiano
 */
public class DataExportException extends RuntimeException
{
	private static final long serialVersionUID = -2097234334808643177L;

	public DataExportException()
	{
		super();
	}

	public DataExportException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DataExportException(String message)
	{
		super(message);
	}

	public DataExportException(Throwable cause)
	{
		super(cause);
	}
}
