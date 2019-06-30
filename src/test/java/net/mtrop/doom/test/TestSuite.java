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
