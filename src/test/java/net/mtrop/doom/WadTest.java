/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public final class WadTest
{
	private static final File TEST_DIR = new File("testjunk");

	@BeforeAll
	public static void beforeAllTests() throws Exception
	{
		assertEquals(TEST_DIR.mkdirs(), true);
	}

	@AfterAll
	public static void afterAllTests() throws Exception
	{
		assertEquals(TEST_DIR.delete(), true);
	}
	
	@Test
	public void isWadTest() throws Exception
	{
		assertEquals(Wad.isWAD(new File("src/test/resources/doommap.wad")), true);
		assertEquals(Wad.isWAD(new File("src/test/resources/hexenmap.wad")), true);
		assertEquals(Wad.isWAD(new File("src/test/resources/udmfmap.wad")), true);
		assertEquals(Wad.isWAD(new File("src/test/resources/viscerus.pk3")), false);
		assertEquals(Wad.isWAD(new File("src/test/resources/does-not-exist.wad")), false);
		assertEquals(Wad.isWAD(new File("src/test/resources")), false); // is dir
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
