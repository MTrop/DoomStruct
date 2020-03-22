/*******************************************************************************
 * Copyright (c) 2015-2020 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.test;

import net.mtrop.doom.PK3Test;
import net.mtrop.doom.WadTest;
import net.mtrop.doom.test.TestUtils.TestDependsOn;

@TestDependsOn({
	WadTest.class,
	PK3Test.class,
})
public final class TestSuite
{
}
