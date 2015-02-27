/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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
