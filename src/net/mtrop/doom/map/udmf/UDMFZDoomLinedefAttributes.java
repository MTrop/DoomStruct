package net.mtrop.doom.map.udmf;

/**
 * Contains linedef attributes for ZDoom namespaces.
 * @author Matthew Tropiano
 */
public interface UDMFZDoomLinedefAttributes extends UDMFDoomLinedefAttributes, UDMFStrifeLinedefAttributes
{
	/** Linedef activation: Player Crosses. */
	public static final String ATTRIB_ACTIVATE_PLAYER_CROSS = "playercross";
	/** Linedef activation: Player Uses. */
	public static final String ATTRIB_ACTIVATE_PLAYER_USE = "playeruse";
	/** Linedef activation: Monster Crosses. */
	public static final String ATTRIB_ACTIVATE_MONSTER_CROSS = "monstercross";
	/** Linedef activation: Monster Crosses. */
	public static final String MONSTER = "monsteruse";
	/** Linedef activation: Projectile Impact. */
	public static final String ATTRIB_ACTIVATE_IMPACT = "impact";
	/** Linedef activation: Player Pushes (collide). */
	public static final String ATTRIB_ACTIVATE_PLAYER_PUSH = "playerpush";
	/** Linedef activation: Monster Pushes (collide). */
	public static final String ATTRIB_ACTIVATE_MONSTER_PUSH = "monsterpush";
	/** Linedef activation: Projectile Crosses. */
	public static final String ATTRIB_ACTIVATE_PROJECTILE_CROSS = "missilecross";
	/** Linedef activation: Anything Crosses. */
	public static final String ATTRIB_ACTIVATE_ANY_CROSS = "anycross";

	/** Linedef flag: Special is repeatable. */
	public static final String ATTRIB_FLAG_REPEATABLE = "repeatspecial";
	/** Linedef flag: Player can use the back of the linedef for specials. */
	public static final String ATTRIB_FLAG_USEBACK = "playeruseback";
	/** Linedef flag: Activates front-side only. */
	public static final String ATTRIB_FLAG_FIRST_SIDE_ONLY = "firstsideonly";
	/** Linedef flag: Blocks players. */
	public static final String ATTRIB_FLAG_BLOCK_PLAYERS = "blockplayers";
	/** Linedef flag: Blocks everything. */
	public static final String ATTRIB_FLAG_BLOCK_EVERYTHING = "blockeverything";
	/** Linedef flag: Blocks sound environment propagation. */
	public static final String ATTRIB_FLAG_ZONE_BOUNDARY = "zoneboundary";
	/** Linedef flag: Blocks projectiles. */
	public static final String ATTRIB_FLAG_BLOCK_PROJECTILES = "blockprojectiles";
	/** Linedef flag: Blocks line use. */
	public static final String ATTRIB_FLAG_BLOCK_USE = "blockuse";
	/** Linedef flag: Blocks monster sight. */
	public static final String ATTRIB_FLAG_BLOCK_SIGHT = "blocksight";
	/** Linedef flag: Blocks hitscan. */
	public static final String ATTRIB_FLAG_BLOCK_HITSCAN = "blockhitscan";
	/** Linedef flag: Clips the rendering of the middle texture. */
	public static final String ATTRIB_FLAG_MIDTEX_CLIP = "clipmidtex";
	/** Linedef flag: Wraps/tiles the rendering of the middle texture. */
	public static final String ATTRIB_FLAG_MIDTEX_WRAP = "wrapmidtex";
	/** Linedef flag: 3D middle texture collision. */
	public static final String ATTRIB_FLAG_MIDTEX_3D = "midtex3d";
	/** Linedef flag: 3D middle texture collision acts only blocks creatures. */
	public static final String ATTRIB_FLAG_MIDTEX_3D_IMPASSABLE = "midtex3dimpassable";
	/** Linedef flag: Switch activation checks activator height. */
	public static final String ATTRIB_FLAG_CHECK_SWITCH_RANGE = "checkswitchrange";

	/** Linedef special argument 0. */
	public static final String ATTRIB_ARG0 = "arg0";
	/** Linedef special argument 1. */
	public static final String ATTRIB_ARG1 = "arg1";
	/** Linedef special argument 2. */
	public static final String ATTRIB_ARG2 = "arg2";
	/** Linedef special argument 3. */
	public static final String ATTRIB_ARG3 = "arg3";
	/** Linedef special argument 4. */
	public static final String ATTRIB_ARG4 = "arg4";

	/** Linedef special argument 0, string type. */
	public static final String ATTRIB_ARG0STR = "arg0str";

	/** Linedef id. */
	public static final String ATTRIB_ID = "id";
	/** Linedef alpha component value. */
	public static final String ATTRIB_ALPHA = "alpha";
	/** Linedef rendering style. */
	public static final String ATTRIB_RENDERSTYLE = "renderstyle";
	/** Linedef special lock type. */
	public static final String ATTRIB_LOCKNUMBER = "locknumber";
	
}
