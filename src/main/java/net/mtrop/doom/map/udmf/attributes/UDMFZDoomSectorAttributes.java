/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf.attributes;

/**
 * Contains sector attributes for ZDoom namespaces.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public interface UDMFZDoomSectorAttributes extends UDMFDoomSectorAttributes
{
	/*
	 * TODO: All of these.
	 * scalex_top = <float>;     // X scale for upper texture, Default = 1.0.
	 * scaley_top = <float>;     // Y scale for upper texture, Default = 1.0.
	 * scalex_mid = <float>;     // X scale for mid texture, Default = 1.0.
	 * scaley_mid = <float>;     // Y scale for mid texture, Default = 1.0.
	 * scalex_bottom = <float>;  // X scale for lower texture, Default = 1.0.
	 * scaley_bottom = <float>;  // Y scale for lower texture, Default = 1.0.
	 * offsetx_top = <float>;    // X offset for upper texture, Default = 0.0.
	 * offsety_top = <float>;    // Y offset for upper texture, Default = 0.0.
	 * offsetx_mid = <float>;    // X offset for mid texture, Default = 0.0.
	 * offsety_mid = <float>;    // Y offset for mid texture, Default = 0.0.
	 * offsetx_bottom = <float>; // X offset for lower texture, Default = 0.0.
	 * offsety_bottom = <float>; // Y offset for lower texture, Default = 0.0.
	 *                           // When global texture offsets are used they will
	 *                           // be added on top of these values.
	 * light = <integer>;        // This side's light level. Default is 0.
	 * lightabsolute = <bool>;   // true = 'light' is an absolute value. Default is 
	 *                           // relative to the owning sector's light level.
	 * lightfog = <bool>;        // true = This side's relative lighting is used even in
	 *                           // foggy sectors. Default is to disable relative
	 *                           // lighting in foggy sectors.
	 * nofakecontrast = <bool>;  // Disables use of fake contrast on this sidedef.
	 * smoothlighting = <bool>;  // Use smooth fake contrast.
	 * clipmidtex = <bool>;      // Side's mid textures are clipped to floor and ceiling.
	 * wrapmidtex = <bool>;      // Side's mid textures are wrapped.
	 * nodecals = <bool>;        // Disables decals on the sidedef.
	 */
}
