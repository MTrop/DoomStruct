/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf.attributes;

/**
 * Contains thing attributes for ZDoom namespaces.
 * @author Matthew Tropiano
 * @since [NOW]
 */
public interface UDMFZDoomThingAttributes extends UDMFHexenThingAttributes
{
	/*
	 * TODO: All of these.
      skill# = <bool>			// Unlike the base spec, # can range from 1-16.
      class# = <bool>			// Unlike the base spec, # can range from 1-16.
      conversation = <int>		// Assigns a conversation dialogue to this thing.
                                // Parameter is the conversation ID, 0 meaning none.
      countsecret = <bool>;     // Picking up this actor counts as a secret.
      arg0str = <string>;       // Alternate string-based version of arg0
	  gravity = <float>;		// Set per-actor gravity. Positive values are multiplied with the class's property, 
	                            // negative values are used as their absolute. Default = 1.0.
								
	  health = <int>;			// Set per-actor health. Positive values are multiplied with the class's property,
								// negative values are used as their absolute. Default = 1.
								
	  renderstyle = <string>;	// Set per-actor render style, overriding the class default. Possible values can be "normal",
								// "none", "add" or "additive", "subtract" or "subtractive", "stencil", "translucentstencil", 
								// "addstencil", "shaded", "addshaded", "translucent", "fuzzy", "optfuzzy", "soultrans" and "shadow". 
								// Default is an empty string for no change.
	  fillcolor = <integer>;    // Fill color used by the "stencil", "addstencil" and "translucentstencil" rendestyles, as RRGGBB value, default = 0x000000.
	  alpha = <float>;          // Translucency of this actor (if applicable to renderstyle), default is 1.0.
	  score = <int>;			// Score value of this actor, overriding the class default if not null. Default = 0.
      pitch = <integer>; 		// Pitch of thing in degrees. Default = 0 (horizontal).
      roll = <integer>; 		// Pitch of thing in degrees. Default = 0 (horizontal).
	  scalex = <float>;         // Vertical scaling on thing. Default = 0 (ignored).
	  scaley = <float>;         // Horizontal scaling on thing. Default = 0 (ignored).
	  scale = <float>;        	// Vertical and horizontal scaling on thing. Default = 0 (ignored).
	  floatbobphase = <int>;	// Sets the thing's floatbobphase. Valid phase values are 0-63. Default = -1 (use actor class default).	 
	 */
}
