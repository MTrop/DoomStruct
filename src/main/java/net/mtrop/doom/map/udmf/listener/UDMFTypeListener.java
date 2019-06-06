package net.mtrop.doom.map.udmf.listener;

import java.util.LinkedList;

import net.mtrop.doom.map.udmf.UDMFObject;
import net.mtrop.doom.map.udmf.UDMFParserListener;

/**
 * A parser listener that listens for specific structure/object types.
 * @author Matthew Tropiano
 */
public abstract class UDMFTypeListener implements UDMFParserListener
{
	/** Current object being read. */
	private UDMFObject current;
	/** Error list. */
	private LinkedList<String> errors;

	@Override
	public void onStart()
	{
		this.current = null;
		this.errors = new LinkedList<>();
	}

	@Override
	public void onEnd()
	{
		this.current = null;
	}

	@Override
	public void onAttribute(String name, Object value)
	{
		if (current != null)
			current.set(name, value);
		else
			onGlobalAttribute(name, value);
	}

	@Override
	public void onObjectStart(String name)
	{
		current = new UDMFObject();
	}

	@Override
	public void onObjectEnd(String name)
	{
		onType(name, current);
		current = null;
	}

	@Override
	public void onParseError(String error)
	{
		errors.add(error);
	}

	/**
	 * @return the list of error messages during parse.
	 */
	public String[] getErrorMessages()
	{
		String[] out = new String[errors.size()];
		errors.toArray(out);
		return out;
	}
	
	/**
	 * Called when a global attribute is encountered.
	 * @param name the name of the attribute.
	 * @param value the parsed value.
	 */
	public abstract void onGlobalAttribute(String name, Object value);
	
	/**
	 * Called when the parser has finished reading an object.
	 * @param type the object type.
	 * @param object the object itself.
	 */
	public abstract void onType(String type, UDMFObject object);
	
}

