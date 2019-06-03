/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

/**
 * Interface for graphic data.
 * @author Matthew Tropiano
 */
public interface GraphicObject
{
	
	/**
	 * @return the offset from the center, horizontally, in pixels.
	 */
	public int getOffsetX();

	/**
	 * @return the offset from the center, vertically, in pixels.
	 */
	public int getOffsetY();

	/**
	 * @return the width of this graphic in pixels.
	 */
	public int getWidth();
	
	/**
	 * @return the height of this graphic in pixels.
	 */
	public int getHeight();

}
