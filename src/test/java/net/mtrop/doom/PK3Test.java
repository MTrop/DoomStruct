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
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.mtrop.doom.sound.DMXSound;

public final class PK3Test
{
	private static final File TEST_DIR = new File("testjunk");
	private static final File TEST_PK3 = new File("src/test/resources/viscerus.pk3");
	
	private static DoomPK3 pk3;
	
	@BeforeAll
	public static void beforeAllTests() throws Exception
	{
		pk3 = new DoomPK3(TEST_PK3);
		assertEquals(TEST_DIR.mkdirs(), true);
	}

	@AfterAll
	public static void afterAllTests() throws Exception
	{
		assertEquals(TEST_DIR.delete(), true);
		pk3.close();
	}
	
	@BeforeEach
	public void beforeEachTest() throws Exception
	{
		// Nothing.
	}
	
	@AfterEach
	public void afterEachTest() throws Exception
	{
		for (File f : TEST_DIR.listFiles())
			f.delete();
	}
	
	@Test
	public void getFilePath() throws Exception
	{
		assertEquals(pk3.getFilePath(), TEST_PK3.getPath());
	}

	@Test
	public void getFileName() throws Exception
	{
		assertEquals(pk3.getFileName(), TEST_PK3.getName());
	}

	@Test
	public void getEntriesStartingWith() throws Exception
	{
		List<String> entries = pk3.getEntriesStartingWith("maps/");
		assertEquals(entries.get(0), "maps/map01.wad");
		assertEquals(entries.get(1), "maps/map02.wad");
		assertEquals(entries.get(2), "maps/map03.wad");
		assertEquals(entries.get(3), "maps/map04.wad");
		assertEquals(entries.get(4), "maps/map05.wad");
		assertEquals(entries.get(5), "maps/map06.wad");
		assertEquals(entries.get(6), "maps/map07.wad");
	}

	@Test
	public void getEntryCount() throws Exception
	{
		assertEquals(pk3.getEntryCount(), 82);
	}

	@Test
	public void containsEntry() throws Exception
	{
		assertEquals(pk3.contains("maps/map01.wad"), true);
		assertEquals(pk3.contains("maps/map08.wad"), false);
	}

	@Test
	public void getData() throws Exception
	{
		byte[] data = pk3.getData("maps/map02.wad");
		assertEquals(data.length, 250034);
	}

	@Test
	public void getDataAs() throws Exception
	{
		pk3.getDataAs("sounds/dsflamst.lmp", DMXSound.class);
	}

	@Test
	public void getDataAsWadMap() throws Exception
	{
		WadMap wad = pk3.getDataAsWadMap("maps/map01.wad");
		assertEquals(wad.getEntryCount(), Integer.valueOf(13));
		wad.close();
	}

	@Test
	public void getDataAsTempWadFile() throws Exception
	{
		File tempFile = new File("testjunk/tempwad.wad");
		WadFile wad = pk3.getDataAsTempWadFile("maps/map01.wad", tempFile);
		assertEquals(tempFile.exists(), true);
		assertEquals(wad.getEntryCount(), Integer.valueOf(13));
		wad.close();
		assertEquals(tempFile.exists(), false);
	}

	@Test
	public void getDataAsWadBuffer() throws Exception
	{
		WadBuffer wad = pk3.getDataAsWadBuffer("maps/map01.wad");
		assertEquals(wad.getEntryCount(), Integer.valueOf(13));
		assertEquals(wad.getContentLength(), Integer.valueOf(442359));
		wad.close();
	}

	@Test
	public void getFile() throws Exception
	{
		File tempFile = new File("testjunk/tempwad.wad");
		File file = pk3.getFile("maps/map01.wad", tempFile);
		assertEquals(file.exists(), true);
		file.delete();
		assertEquals(file.exists(), false);
	}

	@Test
	public void getInputStream() throws Exception
	{
		InputStream in = pk3.getInputStream("maps/map02.wad");
		byte[] buf = new byte[8192];
		int b = 0;
		int out = 0;
		while ((b = in.read(buf)) > 0)
			out += b;
		in.close();
		assertEquals(out, Integer.valueOf(250034));
	}

	@Test
	public void getTextData() throws Exception
	{
		pk3.getTextData("decorate.txt", Charset.forName("ASCII"));
	}

	@Test
	public void getReader() throws Exception
	{
		int c = 0;
		char[] buf = new char[1024];
		
		Reader r = pk3.getReader("decorate.txt", Charset.forName("ASCII"));
		StringBuilder sb = new StringBuilder();
		while ((c = r.read(buf)) > 0)
			sb.append(buf, 0, c);
		assertEquals(sb.toString(), pk3.getTextData("decorate.txt", Charset.forName("ASCII")));
	}

}
