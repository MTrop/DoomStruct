/*******************************************************************************
 * Copyright (c) 2015-2022 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import static net.mtrop.doom.test.TestUtils.assertEqual;

import net.mtrop.doom.test.TestUtils.Test;
import net.mtrop.doom.util.NameUtils;

public final class NameUtilsTest
{
	@Test
	public void isValidEntryName() throws Exception
	{
		assertEqual(NameUtils.isValidEntryName("-"), true);
		assertEqual(NameUtils.isValidEntryName("WALL00_3"), true);
		assertEqual(NameUtils.isValidEntryName("SFALL1"), true);
		assertEqual(NameUtils.isValidEntryName("_SFALL1"), true);
		assertEqual(NameUtils.isValidEntryName("SFALL[1]"), true);
		assertEqual(NameUtils.isValidEntryName("^SFALL1"), true);
		assertEqual(NameUtils.isValidEntryName("METAL-2"), true);
		assertEqual(NameUtils.isValidEntryName("--------"), true);
		assertEqual(NameUtils.isValidEntryName("\\MARKER"), true);
		assertEqual(NameUtils.isValidEntryName("\\\\MARKER"), true);
		assertEqual(NameUtils.isValidEntryName("O2V+35_0"), true);
		assertEqual(NameUtils.isValidEntryName("12345678"), true);

		assertEqual(NameUtils.isValidEntryName("123456789"), false);
		assertEqual(NameUtils.isValidEntryName("wall00_3"), false);
		assertEqual(NameUtils.isValidEntryName(" "), false);
		assertEqual(NameUtils.isValidEntryName(""), false);
		assertEqual(NameUtils.isValidEntryName("NOUMLÄUT"), false);
	}

	@Test
	public void isValidTextureName() throws Exception
	{
		assertEqual(NameUtils.isValidTextureName("-"), true);
		assertEqual(NameUtils.isValidTextureName("AASTINKY"), true);
		assertEqual(NameUtils.isValidTextureName("SFALL1"), true);
		assertEqual(NameUtils.isValidTextureName("_SFALL1"), true);
		assertEqual(NameUtils.isValidTextureName("METAL-2"), true);
		assertEqual(NameUtils.isValidTextureName("--------"), true);
		assertEqual(NameUtils.isValidTextureName("O2V+35_0"), true);
		assertEqual(NameUtils.isValidTextureName("12345678"), true);

		assertEqual(NameUtils.isValidTextureName("123456789"), false);
		assertEqual(NameUtils.isValidTextureName("sfall1"), false);
		assertEqual(NameUtils.isValidTextureName("SFALL[1]"), false);
		assertEqual(NameUtils.isValidTextureName("^SFALL1"), false);
		assertEqual(NameUtils.isValidTextureName("\\MARKER"), false);
		assertEqual(NameUtils.isValidTextureName(" "), false);
		assertEqual(NameUtils.isValidTextureName(""), false);
		assertEqual(NameUtils.isValidTextureName("NOUMLÄUT"), false);
	}
}
