/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import static net.mtrop.doom.test.TestUtils.assertEqual;

import java.io.File;

import net.mtrop.doom.test.TestUtils.AfterAllTests;
import net.mtrop.doom.test.TestUtils.BeforeAllTests;
import net.mtrop.doom.test.TestUtils.Test;

public final class WadTest
{
	private static final File TEST_DIR = new File("testjunk");

	@BeforeAllTests
	public static void beforeAllTests() throws Exception
	{
		assertEqual(TEST_DIR.mkdirs(), true);
	}

	@AfterAllTests
	public static void afterAllTests() throws Exception
	{
		assertEqual(TEST_DIR.delete(), true);
	}
	
	@Test
	public void isWadTest() throws Exception
	{
		assertEqual(Wad.isWAD(new File("src/test/resources/doommap.wad")), true);
		assertEqual(Wad.isWAD(new File("src/test/resources/hexenmap.wad")), true);
		assertEqual(Wad.isWAD(new File("src/test/resources/udmfmap.wad")), true);
		assertEqual(Wad.isWAD(new File("src/test/resources/viscerus.pk3")), false);
		assertEqual(Wad.isWAD(new File("src/test/resources/does-not-exist.wad")), false);
		assertEqual(Wad.isWAD(new File("src/test/resources")), false); // is dir
	}

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

	@Test
	public void getFileData() throws Exception
	{
		WadFile wad = new WadFile("src/test/resources/doommap.wad");
		wad.getData("THINGS");
		wad.close();
	}
	
}
