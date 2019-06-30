/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import net.mtrop.doom.test.TestUtils.Test;

public final class WadTest
{
	@Test
	public void openWadMap() throws Exception
	{
		WadMap wad = new WadMap("src/test/resources/doommap.wad");
		wad.close();
	}

	@Test
	public void openWadFile() throws Exception
	{
		WadFile wad = new WadFile("src/test/resources/doommap.wad");
		wad.close();
	}

	@Test
	public void openWadBuffer() throws Exception
	{
		WadBuffer wad = new WadBuffer("src/test/resources/doommap.wad");
		wad.close();
	}

}
