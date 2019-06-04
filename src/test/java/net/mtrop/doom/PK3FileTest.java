/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import net.mtrop.doom.LoggingFactory.Logger;
import net.mtrop.doom.util.Utils;

public final class PK3FileTest
{
	private static final Logger out = LoggingFactory.createConsoleLoggerFor(PK3FileTest.class); 
	
	public static void main(String[] args) throws Exception
	{
		DoomPK3 pk3 = new DoomPK3(args[0]);
		for (String path : pk3.getEntriesStartingWith("sprites/"))
			out.info(path);
		
		Utils.close(pk3);
	}

}
