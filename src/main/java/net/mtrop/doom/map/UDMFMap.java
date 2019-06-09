package net.mtrop.doom.map;

import net.mtrop.doom.map.udmf.UDMFObject;

/**
 * A map that contains full UDMF map data.
 * @author Matthew Tropiano
 */
public class UDMFMap extends CommonMap<UDMFObject, UDMFObject, UDMFObject, UDMFObject, UDMFObject>
{
	/** Type name: Vertex. */
	public static final String VERTEX = "vertex";
	/** Type name: Linedef. */
	public static final String LINEDEF = "linedef";
	/** Type name: Sidedef. */
	public static final String SIDEDEF = "sidedef";
	/** Type name: Sector. */
	public static final String SECTOR = "sector";
	/** Type name: Thing. */
	public static final String THING = "thing";
	
	/** The namespace for this UDMF map. */
	private String namespace;
	
	/**
	 * Creates a blank map.
	 */
	public UDMFMap()
	{
		super();
		this.namespace = null;
	}
	
	/**
	 * Gets this UDMF namespace.
	 * @return the namespace for this UDMF map.
	 */
	public String getNamespace()
	{
		return namespace;
	}
	
	/**
	 * Sets the UDMF namespace for this map.
	 * @param namespace the new namespace.
	 */
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}
	
}
