/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import java.io.File;

import net.mtrop.doom.map.MapFormat;
import net.mtrop.doom.test.TestUtils.Test;

import static net.mtrop.doom.test.TestUtils.assertEqual;

public final class MapUtilsTest
{
	private static final File TEST_DOOM = new File("src/test/resources/doommap.wad");
	private static final File TEST_HEXEN = new File("src/test/resources/hexenmap.wad");
	private static final File TEST_UDMF = new File("src/test/resources/udmfmap.wad");

	@Test
	public void doMapFormatDoomTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_DOOM, (wad) -> 
			MapUtils.getMapFormat(wad, 0)
		), MapFormat.DOOM);
	}

	@Test
	public void doMapFormatHexenTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_HEXEN, (wad) -> 
			MapUtils.getMapFormat(wad, 0)
		), MapFormat.HEXEN);
	}
	
	@Test
	public void doMapFormatUDMFTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_UDMF, (wad) -> 
			MapUtils.getMapFormat(wad, 0)
		), MapFormat.UDMF);
	}
	
	@Test
	public void doMapEntryCountDoomTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_DOOM, (wad) -> 
			MapUtils.getMapEntryCount(wad, 0)
		), 11);
	}
	
	@Test
	public void doMapEntryCountHexenTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_HEXEN, (wad) -> 
			MapUtils.getMapEntryCount(wad, 0)
		), 13);
	}
	
	@Test
	public void doMapEntryCountUDMFTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_UDMF, (wad) -> 
			MapUtils.getMapEntryCount(wad, 0)
		), 7);
	}

	@Test
	public void doMapEntryDoomTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_DOOM, (wad) -> 
			MapUtils.getAllMapHeaders(wad)[0]
		), "MAP07");
	}
	
	@Test
	public void doMapEntryHexenTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_HEXEN, (wad) -> 
			MapUtils.getAllMapHeaders(wad)[0]
		), "MAP01");
	}
	
	@Test
	public void doMapEntryUDMFTest() throws Exception
	{
		assertEqual(WadUtils.openWadAndGet(TEST_UDMF, (wad) -> 
			MapUtils.getAllMapHeaders(wad)[0]
		), "DM01");
	}

}
