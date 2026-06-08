/*******************************************************************************
 * Copyright (c) 2015-2023 Matt Tropiano
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public final class NameUtilsTest
{
	@Test
	public void isValidEntryName() throws Exception
	{
		assertEquals(NameUtils.isValidEntryName("-"), true);
		assertEquals(NameUtils.isValidEntryName("WALL00_3"), true);
		assertEquals(NameUtils.isValidEntryName("SFALL1"), true);
		assertEquals(NameUtils.isValidEntryName("_SFALL1"), true);
		assertEquals(NameUtils.isValidEntryName("SFALL[1]"), true);
		assertEquals(NameUtils.isValidEntryName("^SFALL1"), true);
		assertEquals(NameUtils.isValidEntryName("METAL-2"), true);
		assertEquals(NameUtils.isValidEntryName("--------"), true);
		assertEquals(NameUtils.isValidEntryName("\\MARKER"), true);
		assertEquals(NameUtils.isValidEntryName("\\\\MARKER"), true);
		assertEquals(NameUtils.isValidEntryName("O2V+35_0"), true);
		assertEquals(NameUtils.isValidEntryName("12345678"), true);
		assertEquals(NameUtils.isValidEntryName("\\FLAT**"), true);
		assertEquals(NameUtils.isValidEntryName("!@#$%^&*"), true);

		assertEquals(NameUtils.isValidEntryName("123456789"), false);
		assertEquals(NameUtils.isValidEntryName("wall00_3"), false);
		assertEquals(NameUtils.isValidEntryName(" "), false);
		assertEquals(NameUtils.isValidEntryName(""), false);
		assertEquals(NameUtils.isValidEntryName("NOUMLÄUT"), false);
	}

	@Test
	public void isValidTextureName() throws Exception
	{
		assertEquals(NameUtils.isValidTextureName("-"), true);
		assertEquals(NameUtils.isValidTextureName("AASTINKY"), true);
		assertEquals(NameUtils.isValidTextureName("SFALL1"), true);
		assertEquals(NameUtils.isValidTextureName("_SFALL1"), true);
		assertEquals(NameUtils.isValidTextureName("METAL-2"), true);
		assertEquals(NameUtils.isValidTextureName("--------"), true);
		assertEquals(NameUtils.isValidTextureName("O2V+35_0"), true);
		assertEquals(NameUtils.isValidTextureName("12345678"), true);
		assertEquals(NameUtils.isValidTextureName("SFALL[1]"), true);
		assertEquals(NameUtils.isValidTextureName("^SFALL1"), true);
		assertEquals(NameUtils.isValidTextureName("\\MARKER"), true);
		assertEquals(NameUtils.isValidTextureName("\\FLAT**"), true);
		assertEquals(NameUtils.isValidTextureName("!@#$%^&*"), true);

		assertEquals(NameUtils.isValidTextureName("123456789"), false);
		assertEquals(NameUtils.isValidTextureName("sfall1"), false);
		assertEquals(NameUtils.isValidTextureName(" "), false);
		assertEquals(NameUtils.isValidTextureName(""), false);
		assertEquals(NameUtils.isValidTextureName("NOUMLÄUT"), false);
	}
}
