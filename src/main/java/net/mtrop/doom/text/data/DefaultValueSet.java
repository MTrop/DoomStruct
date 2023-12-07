package net.mtrop.doom.text.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A single value set for a map, skill, or other section of textual data. 
 * @author Matthew Tropiano
 * @since [NOW]
 */
public class DefaultValueSet implements ValueSet<DefaultValueSet>
{
	/** This set's name. */
	private String name;
	/** The properties and values. */
	private Map<String, String[]> properties;
	/** The properties and value sets. */
	private Map<String, ValueSet<?>> propertySets;
	
	/**
	 * Creates a new type-less ValueSet.
	 */
	public DefaultValueSet()
	{
		this(null);
	}
	
	/**
	 * Creates a new ValueSet.
	 * @param name this set's name.
	 */
	public DefaultValueSet(String name)
	{
		this.name = name;
		this.properties = new HashMap<>(8);
		this.propertySets = new HashMap<>(2);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String[] getValues(String property) 
	{
		return properties.get(property);
	}

	@Override
	public boolean hasProperty(String property) 
	{
		return properties.containsKey(property);
	}

	@Override
	public Set<String> properties() 
	{
		return properties.keySet();
	}

	@Override
	public Set<String> valueSetProperties() 
	{
		return propertySets.keySet();
	}

	@Override
	public DefaultValueSet setValues(String property, Object... values)
	{
		String[] stringValues = new String[values.length];
		for (int i = 0; i < values.length; i++)
			stringValues[i] = String.valueOf(values[i]);
		properties.put(property, stringValues);
		propertySets.remove(property);
		return this;
	}

	@Override
	public ValueSet<?> getValueSet(String property)
	{
		return propertySets.get(property);
	}

	@Override
	public DefaultValueSet setValueSet(String property, DefaultValueSet valueSet) 
	{
		propertySets.put(property, valueSet);
		properties.remove(property);
		return this;
	}
	
}
