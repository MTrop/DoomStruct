/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf.attributes;

/**
 * Contains sidedef attributes for ZDoom namespaces.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public interface UDMFZDoomSidedefAttributes extends UDMFDoomSidedefAttributes
{
	/*
	 * TODO: All of these.
	 * xpanningfloor = <float>;        // X texture offset of floor texture, Default = 0.0.
	 * ypanningfloor = <float>;        // Y texture offset of floor texture, Default = 0.0.
	 * xpanningceiling = <float>;      // X texture offset of ceiling texture, Default = 0.0.
	 * ypanningceiling = <float>;      // Y texture offset of ceiling texture, Default = 0.0.
	 * xscalefloor = <float>;          // X texture scale of floor texture, Default = 1.0.
	 * yscalefloor = <float>;          // Y texture scale of floor texture, Default = 1.0.
	 * xscaleceiling = <float>;        // X texture scale of ceiling texture, Default = 1.0.
	 * yscaleceiling = <float>;        // Y texture scale of ceiling texture, Default = 1.0.
	 * rotationfloor = <float>;        // Rotation of floor texture in degrees, Default = 0.0.
	 * rotationceiling = <float>;      // Rotation of ceiling texture in degrees, Default = 0.0.
	 * ceilingplane_a = <float>;       // Define the plane equation for the sector's ceiling. Default is a horizontal plane at 'heightceiling'.
	 * ceilingplane_b = <float>;       // 'heightceiling' will still be used to calculate texture alignment.
	 * ceilingplane_c = <float>;       // The plane equation will only be used if all 4 values are given.
	 * ceilingplane_d = <float>;
	 * floorplane_a = <float>;         // Define the plane equation for the sector's floor. Default is a horizontal plane at 'heightfloor'.
	 * floorplane_b = <float>;         // 'heightfloor' will still be used to calculate texture alignment.
	 * floorplane_c = <float>;         // The plane equation will only be used if all 4 values are given.
	 * floorplane_d = <float>;
	 * lightfloor = <integer>;         // The floor's light level. Default is 0.
	 * lightceiling = <integer>;       // The ceiling's light level. Default is 0.
	 * lightfloorabsolute = <bool>;    // true = 'lightfloor' is an absolute value. Default is 
	 *                                 // relative to the owning sector's light level.
	 * lightceilingabsolute = <bool>;  // true = 'lightceiling' is an absolute value. Default is 
	 *                                 // relative to the owning sector's light level.
	 * alphafloor = <float>;           // translucency of floor plane (only has meaning with Sector_SetPortal) Default is 1.0.
	 * alphaceiling = <float>;         // translucency of ceiling plane (only has meaning with Sector_SetPortal) Default is 1.0.
	 * renderstylefloor = <string>;    // floor plane renderstyle (only has meaning with Sector_SetPortal); not implemented yet in software renderer
	 *                                 // can be "translucent" or "add", default is "translucent".
	 * renderstyleceiling = <string>;  // ceiling plane renderstyle (only has meaning with Sector_SetPortal); not implemented yet in software renderer
	 *                                 // can be "translucent" or "add", default is "translucent".
	 * gravity = <float>;              // Sector's gravity. Default is 1.0.
	 * lightcolor = <integer>;         // Sector's light color as RRGGBB value, default = 0xffffff.
	 * fadecolor = <integer>;          // Sector's fog color as RRGGBB value, default = 0x000000.
	 * desaturation = <float>;         // Color desaturation factor. 0 = none, 1 = full, default = 0.
	 * silent = <bool>;                // Actors in this sector make no sound,
	 * nofallingdamage = <bool>;       // Falling damage is disabled in this sector
	 * dropactors = <bool>;            // Actors drop with instantly moving floors (*)
	 * norespawn = <bool>;             // Players can not respawn in this sector
	 * soundsequence = <string>;       // The sound sequence to play when this sector moves. Placing a
	 *                                 // sound sequence thing in the sector will override this property.
	 * hidden = <bool>;                // if true this sector will not be drawn on the textured automap.
	 * waterzone = <bool>;             // Sector is under water and swimmable
	 * moreids = <string>;             // Additional sector IDs/tags, specified as a space separated list of numbers (e.g. "2 666 1003 4505")
	 * damageamount = <int>;           // Amount of damage inflicted by this sector, default = 0. If this is 0, all other damage properties will be ignored.
	 *                                 // Setting damage through these properties will override any damage set through 'special'.
	 *                                 // Setting damageamount to a negative value will create a healing sector.
	 * damagetype = <string>;          // Damage type for sector damage, Default = "None". (generic damage)
	 * damageinterval = <int>;         // Interval in tics between damage application, default = 32.
	 * leakiness = <int>;              // Probability of leaking through radiation suit (0 = never, 256 = always), default = 0.
	 * damageterraineffect = <bool>;   // Will spawn a terrain splash when damage is inflicted. Default = false.
	 * damagehazard = <bool>;          // Changes damage model to Strife's delayed damage for the given sector. Default = false.
	 * floorterrain = <string>;        // Sets the terrain for the sector's floor. Default = 'use the flat texture's terrain definition.'
	 * ceilingterrain = <string>;      // Sets the terrain for the sector's ceiling. Default = 'use the flat texture's terrain definition.'
	 * 
	 * portal_ceil_blocksound = <bool> // ceiling portal blocks sound.
	 * portal_ceil_disabled = <bool>   // ceiling portal disabled.
	 * portal_ceil_nopass = <bool>     // ceiling portal blocks movement if true.
	 * portal_ceil_norender = <bool>   // ceiling portal not rendered.
	 * portal_ceil_overlaytype = <string> // defines translucency style, can either be "translucent" or "additive". Default is "translucent".
	 * portal_floor_blocksound = <bool> // floor portal blocks sound.
	 * portal_floor_disabled = <bool>   // floor portal disabled.
	 * portal_floor_nopass = <bool>     // ceiling portal blocks movement if true.
	 * portal_floor_norender = <bool>   // ceiling portal not rendered.
	 * portal_floor_overlaytype = <string> // defines translucency style, can either be "translucent" or "additive". Default is "translucent".
	 */
}
