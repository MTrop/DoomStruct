/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import com.blackrook.commons.Common;
import com.blackrook.commons.logging.Logger;
import com.blackrook.commons.logging.LoggingFactory;

public final class WadFileTest
{
	private static final Logger out = LoggingFactory.createConsoleLoggerFor(WadFileTest.class); 
	
	public static void main(String[] args) throws Exception
	{
		Wad wad;
		
		wad = new WadBuffer(args[0]);
		for (WadEntry e : wad)
			out.info(e.toString());
		out.info("*************************************************************");
		wad = new WadFile(args[0]);
		for (WadEntry e : wad)
			out.info(e.toString());
		Common.close((WadFile)wad);
		out.info("*************************************************************");
		wad = new WadMap(args[0]);
		for (WadEntry e : wad)
			out.info(e.toString());
	}

}
