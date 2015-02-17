package net.mtrop.doom.map.udmf;

public class UDMFParseException extends RuntimeException
{
	private static final long serialVersionUID = -5463002855611154475L;

	public UDMFParseException()
	{
		super("An error occurred while parsing the UDMF.");
	}

	public UDMFParseException(String s)
	{
		super(s);
	}
}
