package net.mtrop.doom.map;

/**
 * A piece of map definition that describes a line.
 * <p>Linedefs connect vertices and attach sidedef definitions.
 * @author Matthew Tropiano
 */
public interface Linedef extends MapObject
{
	/** Linedef flag: blocks things (players/monsters) a.k.a. "Impassable". */
	public static final String ATTRIB_FLAG_BLOCKING = "blocking";
	/** Linedef flag: blocks monsters. */
	public static final String ATTRIB_FLAG_BLOCK_MONSTERS = "blockmonsters";
	/** Linedef flag: two sided. */
	public static final String ATTRIB_FLAG_TWO_SIDED = "twosided";
	/** Linedef flag: upper texture unpegged. */
	public static final String ATTRIB_FLAG_UNPEG_TOP = "dontpegtop";
	/** Linedef flag: lower texture unpegged. */
	public static final String ATTRIB_FLAG_UNPEG_LOWER = "dontpegbottom";
	/** Linedef flag: Secret (shows up as 1-sided, blocking on automap). */
	public static final String ATTRIB_FLAG_SECRET = "secret";
	/** Linedef flag: Block sound propagation. */
	public static final String ATTRIB_FLAG_BLOCK_SOUND = "blocksound";
	/** Linedef flag: Don't draw on automap. */
	public static final String ATTRIB_FLAG_DONT_DRAW = "dontdraw";
	/** Linedef flag: Already revealed on automap. */
	public static final String ATTRIB_FLAG_MAPPED = "mapped";
	/** Linedef flag: Linedef passes its activation through to another line. */
	public static final String ATTRIB_FLAG_PASSTHRU = "passuse";

	/** Linedef Special type. */
	public static final String ATTRIB_SPECIAL = "special";
	/** Linedef first vertex. */
	public static final String ATTRIB_VERTEX_START = "v1";
	/** Linedef second vertex. */
	public static final String ATTRIB_VERTEX_END = "v2";
	/** Linedef Front Sidedef Reference. */
	public static final String ATTRIB_SIDEDEF_FRONT = "sidefront";
	/** Linedef Back Sidedef Reference. */
	public static final String ATTRIB_SIDEDEF_BACK = "sideback";

	
	/**
	 * Gets the attribute to use as a "linking tag" to another sector.
	 * On some linedefs, this is a "tag". On other implementations, this can be
	 * an argument on a special, dependent on the special itself.
	 * <p>
	 * The implementations may have a more direct method to get this, but this provides
	 * an implementation-agnostic way to find it.
	 * @return the name of the attribute. May be null if not a linking attribute.
	 */
	public String getTaggingAttribute();

	/**
	 * @return the linedef's special.
	 */
	public int getSpecial();
	
	/**
	 * @return the linedef's starting vertex reference index (in loaded maps).
	 */
	public int getVertexStartReference();
	
	/**
	 * @return the linedef's ending vertex reference index (in loaded maps).
	 */
	public int getVertexEndReference();
	
	/**
	 * @return the linedef's starting vertex reference index (in loaded maps).
	 */
	public int getSidedefFrontReference();
	
	/**
	 * @return the linedef's ending vertex reference index (in loaded maps).
	 */
	public int getSidedefEndReference();
	
	/**
	 * This references the specific flag. 
	 * There may be separate flags that affect this.
	 * @return true if this blocks the player, false if not.
	 */
	public boolean isImpassable();
	
	/**
	 * This a general attribute. 
	 * There may be separate flags that affect this.
	 * @return true if this blocks the player, false if not.
	 */
	public boolean isPlayerBlocking();
	
	/**
	 * This a general attribute. 
	 * There may be separate flags that affect this.
	 * @return true if this blocks monsters, false if not.
	 */
	public boolean isMonsterBlocking();
	
	/**
	 * @return true if this line is two-sided, false if not.
	 */
	public boolean isTwoSided();
	
	/**
	 * @return true if this line's upper texture is unpegged, false if not.
	 */
	public boolean isUpperUnpegged();
	
	/**
	 * @return true if this line's lower texture is unpegged, false if not.
	 */
	public boolean isLowerUnpegged();
	
	/**
	 * @return true if this line is shown as one-sided on the automap, false if not.
	 */
	public boolean isSecret();
	
	/**
	 * @return true if this line is always drawn on the automap, false if not.
	 */
	public boolean isMapped();

	/**
	 * @return true if this line is not drawn on the automap, false if so.
	 */
	public boolean isNotDrawn();
	
	/**
	 * @return true if this line blocks sound (must be doubled-up to block sound completely), false if not.
	 */
	public boolean isSoundBlocking();
	
	/**
	 * @return true if this line's activated special does not block the activation search, false if so.
	 */
	public boolean isPassThru();
	
}
