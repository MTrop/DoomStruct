/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import net.mtrop.doom.BinaryObject;
import net.mtrop.doom.util.RangeUtils;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.List;
import com.blackrook.commons.math.RMath;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * This class is an abstract representation of a DEMO lump in Doom.
 * It stores player controls per game tic, since everything else is deterministic,
 * all things equal.
 * <p>
 * The method {@link #readBytes(InputStream)} will read until it detects the end of the DEMO
 * information.
 * @author Matthew Tropiano
 */
public class Demo implements BinaryObject, Iterable<Demo.Tic[]>
{
	/** Demo version 1.2 byte. */
	public static final int VERSION_12 =			0;  
	/** Demo version 1.4 byte. */
	public static final int VERSION_14 =			104;  
	/** Demo version 1.5 byte. */
	public static final int VERSION_15 =			105;  
	/** Demo version 1.666 byte. */
	public static final int VERSION_1666 =			106;  
	/** Demo version 1.9 byte. */
	public static final int VERSION_19 =			109;  
	/** Demo version 1.9 byte. */
	public static final int VERSION_FINALDOOM =		111;  
	/** Demo version Boom. */
	public static final int VERSION_BOOM =			200;  
	/** Demo version Boom v2.01. */
	public static final int VERSION_BOOM201 =		201;  
	/** Demo version Boom v2.02. */
	public static final int VERSION_BOOM202 =		202;  
	/** Demo version LxDoom. */
	public static final int VERSION_LXDOOM =		203;  
	/** Demo version MBF. */
	public static final int VERSION_MBF =			204;  
	/** Demo version PrBoom v2.1.X. */
	public static final int VERSION_PRBOOM210 =		210;  
	/** Demo version PrBoom v2.2.X. */
	public static final int VERSION_PRBOOM220 =		211;  
	/** Demo version PrBoom v2.3.X. */
	public static final int VERSION_PRBOOM230 =		212;  
	/** Demo version PrBoom v2.4.X. */
	public static final int VERSION_PRBOOM240 =		213;  
	/** Demo version PrBoom v2.5.0.X. */
	public static final int VERSION_PRBOOM250 =		214;  

	/** Demo skill byte (very easy). */
	public static final byte SKILL_VERY_EASY =		0x00;  
	/** Demo skill byte (easy). */
	public static final byte SKILL_EASY =			0x01;  
	/** Demo skill byte (medium). */
	public static final byte SKILL_MEDIUM =			0x02;  
	/** Demo skill byte (hard). */
	public static final byte SKILL_HARD =			0x03;  
	/** Demo skill byte (very hard). */
	public static final byte SKILL_VERY_HARD =		0x04;  

	/** Demo play mode byte (Single/co-op). */
	public static final byte MODE_NORMAL =			0x00;  
	/** Demo play mode byte (Deathmatch). */
	public static final byte MODE_DEATHMATCH =		0x01;  
	/** Demo play mode byte (Alt. Deathmatch). */
	public static final byte MODE_ALTDEATH =		0x02;  
	
	/** Demo insurance - no correction. */
	public static final int DEMO_INSURANCE_NONE =		0;  
	/** Demo insurance - attempt fix. */
	public static final int DEMO_INSURANCE_FIX =		1;  
	/** Demo insurance - only attempt fix during recording. */
	public static final int DEMO_INSURANCE_RECORD_ONLY = 2;  
	/** Demo insurance setting count. */
	public static final int DEMO_INSURANCE_SETTINGS = 	3;  

	/** Demo terminal byte. */
	public static final byte DEMO_END =				(byte)0x80;  

	/** Compatibility Level Doom v1.2 */
	public static final int COMPLEVEL_DOOM_12 =			0;
	/** Compatibility Level Doom v1.666 */
	public static final int COMPLEVEL_DOOM_1666 =		1;
	/** Compatibility Level Doom v1.9 */
	public static final int COMPLEVEL_DOOM_19 =			2;
	/** Compatibility Level Ultimate Doom */
	public static final int COMPLEVEL_UDOOM =			3;
	/** Compatibility Level Final Doom */
	public static final int COMPLEVEL_FINAL_DOOM =		4;
	/** Compatibility Level DOSDoom */
	public static final int COMPLEVEL_DOSDOOM =			5;
	/** Compatibility Level Boom v2.0 */
	public static final int COMPLEVEL_BOOM =			7;
	/** Compatibility Level Boom v2.01 */
	public static final int COMPLEVEL_BOOM201 =			8; 
	/** Compatibility Level Boom v2.02 */
	public static final int COMPLEVEL_BOOM202 =			9;
	/** Compatibility Level LxDoom 1.4.X */
	public static final int COMPLEVEL_LXDOOM =			10;
	/** Compatibility Level MBF */
	public static final int COMPLEVEL_MBF =				11;
	/** Compatibility Level PrBoom v2.03b */
	public static final int COMPLEVEL_PRBOOM_203B =		12;
	/** Compatibility Level PrBoom v2.1.0 */
	public static final int COMPLEVEL_PRBOOM_210 =		13;
	/** Compatibility Level PrBoom v2.1.1-2.2.6 */
	public static final int COMPLEVEL_PRBOOM_211_226 =	14;
	/** Compatibility Level PrBoom v2.3.X */
	public static final int COMPLEVEL_PRBOOM_23X =		15;
	/** Compatibility Level PrBoom v2.4.0 */
	public static final int COMPLEVEL_PRBOOM_240 =		16;
	/** Compatibility Level PrBoom Current */
	public static final int COMPLEVEL_CURRENT_PRBOOM =	17;
	
	/** Compatibility flag: Any monster can telefrag on MAP30. */
	public static final int COMPFLAG_TELEFRAG =			0;
	/** Compatibility flag: Some objects never hang over tall ledges. */
	public static final int COMPFLAG_DROPOFF =			1;
	/** Compatibility flag: Arch-Vile resurrects invincible ghosts. */
	public static final int COMPFLAG_VILEGHOST =		2;
	/** Compatibility flag: Pain Elementals limited to 21 lost souls. */
	public static final int COMPFLAG_PAINLIMIT =		3;
	/** Compatibility flag: Lost souls get stuck behind walls. */
	public static final int COMPFLAG_LOSTSOULWALLS =	4;
	/** Compatibility flag: Blazing doors make double closing sounds. */
	public static final int COMPFLAG_BLAZECLOSESOUND =	5;
	/** Compatibility flag: Tagged doors don't trigger special lighting. */
	public static final int COMPFLAG_DOORLIGHT =		6;
	/** Compatibility flag: Use Doom's linedef trigger model */
	public static final int COMPFLAG_DOOMTRIGGERMODEL =	7;
	/** Compatibility flag: God mode isn't absolute (can still be killed by telefrag). */
	public static final int COMPFLAG_GOD =				8;
	/** Compatibility flag: Objects don't fall under their own weight. */
	public static final int COMPFLAG_FALLOFF =			9;
	/** Compatibility flag: Use Doom's floor motion behavior. */
	public static final int COMPFLAG_DOOMFLOORMOTION =	10;
	/** Compatibility flag: Sky is unaffected by invulnerability. */
	public static final int COMPFLAG_SKYINVUL =			11;
	/** Compatibility flag: Monsters don't give up pursuit of targets. */
	public static final int COMPFLAG_PURSUIT =			12;
	/** Compatibility flag: Monsters get stuck on doortracks. */
	public static final int COMPFLAG_DOORSTUCK =		13;
	/** Compatibility flag: Monsters randomly walk off of moving lifts. */
	public static final int COMPFLAG_STAYLIFT =			14;
	/** Compatibility flag: Dead players can exit levels. */
	public static final int COMPFLAG_DEADPLAYEREXIT =	15;
	/** Compatibility flag: Use Doom's stairbuilding method. */
	public static final int COMPFLAG_BUGGYSTAIRS =		16;
	/** Compatibility flag: Powerup cheats are not infinite duration. */
	public static final int COMPFLAG_INFCHEAT =			17;
	/** Compatibility flag: Linedef effects still work with sector tag = 0. */
	public static final int COMPFLAG_ZEROTAGS =			18;
	/** Compatibility flag: Use Doom's movement clipping code. */
	public static final int COMPFLAG_DOOMCLIPPING =		19;
	/** Compatibility flag: Use Doom's respawn code. */
	public static final int COMPFLAG_DOOMRESPAWN =		20;
	/** Compatibility flag: Use Doom's sound code behavior. */
	public static final int COMPFLAG_BUGGYSOUND =		21;
	/** Compatibility flag: All boss types can trigger tag 666 at ExM8. */
	public static final int COMPFLAG_666 =				22;
	/** Compatibility flag: Lost souls don't bounce off flat surfaces. */
	public static final int COMPFLAG_SOULBOUNCE =		23;
	/** Compatibility flag: 2S middle textures do not animate. */
	public static final int COMPFLAG_MASKEDANIM =		24;
	
	/** Length of compatibility flags. */
	public static final int COMPFLAG_LENGTH =			32;
	/** Length of option flag chunk in Boom DEMOs. */
	public static final int OPTION_FLAGS_LENGTH = 		64;
	/** Maximum players. */
	public static final int MAX_PLAYERS = 				4;
	/** Minimum maximum players in Boom (according to Boom source code). */
	public static final int BOOM_MIN_MAXPLAYERS = 		32;
	
	/** MBF Demo Signature */
	public static final byte[] MBF_SIGNATURE = {0x1d, 'M', 'B', 'F', (byte)0xe6, '\0'};
	/** Boom Demo Signature */
	public static final byte[] BOOM_SIGNATURE = {0x1d, 'B', 'o', 'o', 'm', (byte)0xe6};
	
	/** Number of players. */
	private int players;

	/** Demo version (game version). */
	private int version;
	/** Demo skill level. */
	private int skill;
	/** Demo episode. */
	private int episode;
	/** Demo map/mission number. */
	private int map;
	/** Demo game mode. */
	private int gamemode;
	/** Demo player viewpoint. */
	private int viewpoint;

	/** Demo switch - monsters respawn. */
	private boolean respawn;
	/** Demo switch - fast monsters. */
	private boolean fast;
	/** Demo switch - no monsters. */
	private boolean nomonsters;
	
	/** Monsters remember last target. */
	private boolean monstersRememberTarget;
	/** Friction specials are enabled. */
	private boolean enableFriction; 
	/** Weapon recoil is enabled. */
	private boolean enableWeaponRecoil;
	/** Push/Pull things are enabled. */
	private boolean allowPushers;
	/** Player bobbing is enabled. */
	private boolean enablePlayerBobbing;

	/** Demo insurance setting. */
	private int demoInsurance;
	/** Random number generator seeding value. */
	private int randomSeed; // READ BIG ENDIAN
	/** Follow distance of helpers. */
	private int friendFollowDistance; // READ BIG ENDIAN SHORT UNSIGNED
	
	/** Enable/disable monster infighting. */
	private boolean monsterInfighting;
	/** Enable/disable Dogs (Man's Best Friend). */
	private boolean enableDogs;
	/** Enable/Disable monkeys. */
	private boolean enableMonkeys;
	/** Can dogs jump?. */
	private boolean dogsJump;
	/** Enable/disable monster backing behavior. */
	private boolean monsterBacking;
	/** Monsters try to avoid hazards. */
	private boolean monstersAvoidHazards;
	/** Monsters are affected by friction. */
	private boolean monsterFriction;
	/** Number of helpers. */
	private int helperCount;
	
	/** Force something with BSPs. */
	private boolean	forceOldBSP;
	
	/** Compatibility flags. */
	private boolean[] compatibilityFlags;

	/** Demo compatibility level. */
	private int compatibilityLevel;
	
	/** List of game tics. */
	private List<Tic[]> gameTics;

	/**
	 * Creates a new, blank demo with default values.
	 */
	public Demo()
	{
		this(1);
	}
	
	/**
	 * Creates a new, blank demo with default values
	 * and a set amount of players.
	 */
	public Demo(int players)
	{
		this.players = players;
		compatibilityFlags = new boolean[COMPFLAG_LENGTH];
		reset();
	}
	
	/**
	 * Reads and creates a new Demo object from an array of bytes.
	 * This reads from the array until the end of the demo.
	 * @param bytes the byte array to read.
	 * @return a new Demo object.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Demo create(byte[] bytes) throws IOException
	{
		Demo out = new Demo();
		out.fromBytes(bytes);
		return out;
	}

	/**
	 * Reads and creates a new Demo from an {@link InputStream} implementation.
	 * This reads from the stream until the end of the demo.
	 * The stream is NOT closed at the end.
	 * @param in the open {@link InputStream} to read from.
	 * @return a new Demo with its fields set.
	 * @throws IOException if the stream cannot be read.
	 */
	public static Demo read(InputStream in) throws IOException
	{
		Demo out = new Demo();
		out.readBytes(in);
		return out;
	}

	/**
	 * Resets the contents of this demo.
	 */
	public void reset()
	{
		gameTics = new List<Tic[]>(35*150); // 3 minutes of tics.
		setVersion(VERSION_19);
		setCompatibilityLevel(COMPLEVEL_DOOM_19);
		setSkill(SKILL_MEDIUM);
		setEpisode(1);
		setMap(1);

		setMonsterRespawn(false);
		setFastMonsters(false);
		setNoMonsters(false);
		
		setViewpoint(0);
		
		setMonstersRememberTarget(false);
		setEnableFriction(false);
		setEnableWeaponRecoil(false);
		setAllowPushers(true);
		setEnablePlayerBobbing(true);
		setDemoInsurance(DEMO_INSURANCE_NONE);
		setRandomSeed(0);
		setFriendFollowDistance(0);
		setMonsterInfighting(true);
		setEnableDogs(false);
		setEnableMonkeys(false);
		setDogsJump(false);
		setMonsterBacking(false);
		setMonstersAvoidHazards(false);
		setMonsterFriction(false);
		setHelperCount(0);
		for (int i = 0; i < COMPFLAG_LENGTH; i++)
			setCompatibilityFlag(i, false);
		setForceOldBSP(false);
	}
	
	/**
	 * Gets the version of Doom that with which this demo was made.
	 * See the VERSION macros for the important values.
	 * This affects how the demo is exported if written to
	 * an output stream of some kind.
	 * @return the version value.
	 */
	public int getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of Doom that with which this demo was made.
	 * See the VERSION macros for the important values.
	 * <p>
	 * This affects how the demo is exported if written to an output stream of some kind.
	 * @param version the version value.
	 * @throws IllegalArgumentException if version is outside the range 0 to 255.
	 */
	public void setVersion(int version)
	{
		RangeUtils.checkByteUnsigned("Version", version);
		this.version = version;
	}

	/**
	 * Gets the skill level that this demo is for.
	 * See the SKILL macros for the important values.
	 * @return the skill level value.
	 */
	public int getSkill()
	{
		return skill;
	}

	/**
	 * Sets the skill level that this demo is for.
	 * See the SKILL macros for the important values.
	 * @param skill the skill level value.
	 * @throws IllegalArgumentException if skill is outside the range 0 to 4.
	 */
	public void setSkill(int skill)
	{
		RangeUtils.checkRange("Skill", 0, 4, skill);
		this.skill = skill;
	}

	/**
	 * Gets the episode that this demo is for.
	 * <p>
	 * Doom 2, Hexen, and Strife do not have episodes,
	 * so this is 1 for those games.
	 * @return the episode number.
	 */
	public int getEpisode()
	{
		return episode;
	}

	/**
	 * Sets the episode that this demo is for.
	 * <p>
	 * Doom 2, Hexen, and Strife do not have episodes, so this is 1 for those games.
	 * @param episode the episode number.
	 * @throws IllegalArgumentException if episode is outside the range 0 to 255.
	 */
	public void setEpisode(int episode)
	{
		RangeUtils.checkByteUnsigned("Episode", episode);
		this.episode = episode;
	}

	/**
	 * @return the map/mission number that this demo is for.
	 */
	public int getMap()
	{
		return map;
	}

	/**
	 * Sets the map/mission number that this demo is for.
	 * @param map the map number.
	 * @throws IllegalArgumentException if map is outside the range 0 to 255.
	 */
	public void setMap(int map)
	{
		RangeUtils.checkByteUnsigned("Map", map);
		this.map = map;
	}

	/**
	 * Gets the game mode that this demo is for.
	 * See the MODE constants.
	 * @return the game mode value.
	 */
	public int getGameMode()
	{
		return gamemode;
	}

	/**
	 * Sets the game mode that this demo is for.
	 * See the MODE constants.
	 * @param mode the game mode value.
	 */
	public void setGameMode(int mode)
	{
		this.gamemode = mode;
	}

	/**
	 * Is monster respawning set for this demo?
	 * @return true if so, false if not.
	 */
	public boolean getMonsterRespawn()
	{
		return respawn;
	}

	/**
	 * Sets if monster respawning is set for this demo.
	 */
	public void setMonsterRespawn(boolean respawn)
	{
		this.respawn = respawn;
	}

	/**
	 * Is fast monsters set for this demo?
	 * @return true if so, false if not.
	 */
	public boolean getFastMonsters()
	{
		return fast;
	}

	/**
	 * Sets if fast monsters are set for this demo.
	 * @param fast true if so, false if not.
	 */
	public void setFastMonsters(boolean fast)
	{
		this.fast = fast;
	}

	/**
	 * Is "no monsters" set for this demo?
	 * @return true if so, false if not.
	 */
	public boolean getNoMonsters()
	{
		return nomonsters;
	}

	/**
	 * Sets the "no monsters" flag for this demo.
	 * @param nomonsters true if so, false if not.
	 */
	public void setNoMonsters(boolean nomonsters)
	{
		this.nomonsters = nomonsters;
	}

	/**
	 * Gets if monsters remember their last target.
	 * @return true if so, false if not.
	 */
	public boolean getMonstersRememberTarget()
	{
		return monstersRememberTarget;
	}

	/**
	 * Sets if monsters remember their last target.
	 * @param monstersRememberTarget true if so, false if not.
	 */
	public void setMonstersRememberTarget(boolean monstersRememberTarget)
	{
		this.monstersRememberTarget = monstersRememberTarget;
	}

	/**
	 * Gets if the friction specials are enabled.
	 * @return true if so, false if not.
	 */
	public boolean getEnableFriction()
	{
		return enableFriction;
	}

	/**
	 * Sets if the friction specials are enabled.
	 * @param enableFriction true if so, false if not.
	 */
	public void setEnableFriction(boolean enableFriction)
	{
		this.enableFriction = enableFriction;
	}
	
	/**
	 * Gets if weapon recoil is enabled.
	 * @return true if so, false if not.
	 */
	public boolean getEnableWeaponRecoil()
	{
		return enableWeaponRecoil;
	}

	/**
	 * Sets if weapon recoil is enabled.
	 * @param enableWeaponRecoil true if so, false if not.
	 */
	public void setEnableWeaponRecoil(boolean enableWeaponRecoil)
	{
		this.enableWeaponRecoil = enableWeaponRecoil;
	}

	/**
	 * Gets if push/pull objects are enabled.
	 * @return true if so, false if not.
	 */
	public boolean getAllowPushers()
	{
		return allowPushers;
	}

	/**
	 * Sets if push/pull objects are enabled.
	 * @param allowPushers true if so, false if not.
	 */
	public void setAllowPushers(boolean allowPushers)
	{
		this.allowPushers = allowPushers;
	}

	/**
	 * Gets if player bobbing is enabled.
	 * @return true if so, false if not.
	 */
	public boolean getEnablePlayerBobbing()
	{
		return enablePlayerBobbing;
	}

	/**
	 * Sets if player bobbing is enabled.
	 * @param enablePlayerBobbing true if so, false if not.
	 */
	public void setEnablePlayerBobbing(boolean enablePlayerBobbing)
	{
		this.enablePlayerBobbing = enablePlayerBobbing;
	}

	/**
	 * Gets what kind of "demo insurance" this is set for.
	 * See the DEMO_INSURANCE constants.
	 * @return the demo insurance value.
	 */
	public int getDemoInsurance()
	{
		return demoInsurance;
	}

	/**
	 * Sets what kind of "demo insurance" this is set for.
	 * See the DEMO_INSURANCE constants.
	 * Clamps value from 0 to {@link Demo#DEMO_INSURANCE_SETTINGS} - 1.
	 * @param demoInsurance the new demo insurance value.
	 */
	public void setDemoInsurance(int demoInsurance)
	{
		this.demoInsurance = RMath.clampValue(demoInsurance, 0, DEMO_INSURANCE_SETTINGS - 1);
	}

	/**
	 * Gets the random seeding number that this demo uses.
	 * @return the seed number.
	 */
	public int getRandomSeed()
	{
		return randomSeed;
	}

	/**
	 * Sets the random seeding number that this demo uses.
	 * @param randomSeed the new seed value.
	 */
	public void setRandomSeed(int randomSeed)
	{
		this.randomSeed = randomSeed;
	}

	/**
	 * Gets the follow distance of allied CPU friends.
	 * @return the follow distance that was set.
	 */
	public int getFriendFollowDistance()
	{
		return friendFollowDistance;
	}

	/**
	 * Sets the follow distance of allied CPU friends.
	 * @param the new follow distance.
	 * @throws IllegalArgumentException if the distance is outside the range 0 to 999.
	 */
	public void setFriendFollowDistance(int friendFollowDistance)
	{
		RangeUtils.checkRange("Friend follow distance", 0, 999, friendFollowDistance);
		this.friendFollowDistance = friendFollowDistance;
	}

	/**
	 * Gets if monster infighting is enabled.
	 * @return true if so, false if not.
	 */
	public boolean getMonsterInfighting()
	{
		return monsterInfighting;
	}

	/**
	 * Sets if monster infighting is enabled.
	 * @param monsterInfighting true if so, false if not.
	 */
	public void setMonsterInfighting(boolean monsterInfighting)
	{
		this.monsterInfighting = monsterInfighting;
	}

	/**
	 * Gets if helper dogs are enabled.
	 * @return true if so, false if not.
	 */
	public boolean getEnableDogs()
	{
		return enableDogs;
	}

	/**
	 * Sets if helper dogs are enabled.
	 * @param enableDogs true if so, false if not.
	 */
	public void setEnableDogs(boolean enableDogs)
	{
		this.enableDogs = enableDogs;
	}

	/**
	 * Gets if helper monkeys are enabled.
	 * @return true if so, false if not.
	 */
	public boolean getEnableMonkeys()
	{
		return enableMonkeys;
	}

	/**
	 * Sets if helper monkeys are enabled.
	 * @param enableMonkeys true if so, false if not.
	 */
	public void setEnableMonkeys(boolean enableMonkeys)
	{
		this.enableMonkeys = enableMonkeys;
	}

	/**
	 * Gets if helper dogs jump.
	 * @return true if so, false if not.
	 */
	public boolean getDogsJump()
	{
		return dogsJump;
	}

	/**
	 * Sets if helper dogs jump.
	 * @param dogsJump true if so, false if not.
	 */
	public void setDogsJump(boolean dogsJump)
	{
		this.dogsJump = dogsJump;
	}

	/**
	 * Gets if monster backing is enabled.
	 * @return true if so, false if not.
	 */
	public boolean getMonsterBacking()
	{
		return monsterBacking;
	}

	/**
	 * Sets if monster backing is enabled.
	 * @param monsterBacking true if so, false if not.
	 */
	public void setMonsterBacking(boolean monsterBacking)
	{
		this.monsterBacking = monsterBacking;
	}

	/**
	 * Gets if monsters avoid hazardous areas.
	 * @return true if so, false if not.
	 */
	public boolean getMonstersAvoidHazards()
	{
		return monstersAvoidHazards;
	}

	/**
	 * Sets if monsters avoid hazardous areas.
	 * @param monstersAvoidHazards true if so, false if not.
	 */
	public void setMonstersAvoidHazards(boolean monstersAvoidHazards)
	{
		this.monstersAvoidHazards = monstersAvoidHazards;
	}

	/**
	 * Gets if monsters are susceptible to environmental friction.
	 * @return true if so, false if not.
	 */
	public boolean getMonsterFriction()
	{
		return monsterFriction;
}

	/**
	 * Sets if monsters are susceptible to environmental friction.
	 * @param monsterFriction true if so, false if not.
	 */
	public void setMonsterFriction(boolean monsterFriction)
	{
		this.monsterFriction = monsterFriction;
	}

	/**
	 * @return how many helpers are spawned.
	 */
	public int getHelperCount()
	{
		return helperCount;
	}

	/**
	 * Sets how many helpers are spawned.
	 * @param the amount of helpers.
	 */
	public void setHelperCount(int helperCount)
	{
		this.helperCount = helperCount;
	}

	/**
	 * Gets if old BSP methods are forced.
	 * @return true if so, false if not.
	 */
	public boolean getForceOldBSP()
	{
		return forceOldBSP;
	}

	/**
	 * Sets if old BSP methods are forced.
	 * @param forceOldBSP true if so, false if not.
	 */
	public void setForceOldBSP(boolean forceOldBSP)
	{
		this.forceOldBSP = forceOldBSP;
	}

	/**
	 * Gets the player viewpoint index that this demo is being played back from.
	 * Zero is player 1.
	 * @return the corresponding viewpoint index.
	 */
	public int getViewpoint()
	{
		return viewpoint;
	}

	/**
	 * Sets the player viewpoint index that this demo is being played back from.
	 * Zero is player 1.
	 * @param viewpoint the new viewpoint index.
	 * @throws IllegalArgumentException if episode is outside the range 0 to 255.
	 */
	public void setViewpoint(int viewpoint)
	{
		RangeUtils.checkByteUnsigned("Viewpoint", viewpoint);
		this.viewpoint = viewpoint;
	}

	/**
	 * @return the amount of players in this demo.
	 */
	public int getPlayers()
	{
		return players;
	}

	/**
	 * Sets the amount of players.
	 * Be very careful with this - the amount of player control info per tic added must be equal to this amount! 
	 * @param players the amount of players.
	 * @throws IllegalArgumentException if episode is outside the range 0 to 255.
	 */
	public void setPlayers(int players)
	{
		RangeUtils.checkByteUnsigned("Players", players);
		this.players = players;
	}
	
	/**
	 * Gets the demo's stored compatibility level.
	 * Only useful if this is a Boom-format Demo.
	 * See the COMPLEVEL constants for more info.
	 * @return the current compatibility level.
	 */
	public int getCompatibilityLevel()
	{
		return compatibilityLevel;
}

	/**
	 * Sets the demo's stored compatibility level.
	 * This affects how the demo is exported if written to
	 * an output stream of some kind.
	 * See the COMPLEVEL constants for more info.
	 * @param compatibilityLevel the new level.
	 */
	public void setCompatibilityLevel(int compatibilityLevel)
	{
		this.compatibilityLevel = compatibilityLevel;
	}

	/**
	 * Gets a compatibility flag setting.
	 * @param the flag index (see COMPATFLAG constants).
	 * @return true if set, false if not.
	 * @throws ArrayIndexOutOfBoundsException if <code>flag</code> is less than 0 or greater than or equal to {@value #COMPFLAG_LENGTH}.
	 */
	public boolean getCompatibilityFlag(int flag)
	{
		return compatibilityFlags[flag];
	}
	
	/**
	 * Sets a compatibility flag setting.
	 * This affects how the demo is exported if written to an output stream of some kind.
	 * @param flag the flag to set (see COMPATFLAG constants).
	 * @param value true to set, false to unset.
	 * @throws ArrayIndexOutOfBoundsException if <code>flag</code> is less than 0 or greater than or equal to {@value #COMPFLAG_LENGTH}.
	 */
	public void setCompatibilityFlag(int flag, boolean value)
	{
		compatibilityFlags[flag] = value;
	}
	
	/**
	 * @return how many game tics were recorded in this demo.
	 */
	public int getTicCount()
	{
		return gameTics.size();
	}
	
	/**
	 * @return how many seconds this demo lasts (approximate).
	 */
	public double getLength()
	{
		return getTicCount() * (1.0 / 35);
	}
	
	/**
	 * Returns a Tic at a particular gametic.
	 * @param tic the tic index.
	 * @param player the player index.
	 * @return the corresponding Tic or null if out of range.
	 * @throws ArrayIndexOutOfBoundsException if player is out of range. 
	 */
	public Tic getTic(int tic, int player)
	{
		Tic[] t = gameTics.getByIndex(tic);
		return t != null ? t[player] : null;
	}
	
	/**
	 * Gets a demo Tic at a particular gametic, first player only.
	 * @return the corresponding Tic or null if out of range.
	 * @see #getTic(int, int)
	 */
	public Tic getTic(int tic)
	{
		return getTic(tic, 0);
	}
	
	/**
	 * Adds a single tic for a set of players.
	 * If the amount of tics to add does not equal the number of players, 
	 * @param tics the set of tic data to add for one player.
	 * @throws IllegalArgumentException if the amount of 
	 * 		tics to add is not equal to the number of players.
	 */
	public void addTic(Tic ... tics)
	{
		if (tics.length != players)
			throw new IllegalArgumentException("Amount of tics to add is not equal to number of players.");
			
		Tic[] t = new Tic[tics.length];
		System.arraycopy(tics, 0, t, 0, tics.length);
		gameTics.add(t);
	}

	@Override
	public Iterator<Tic[]> iterator()
	{
		return gameTics.iterator();
	}

	@Override
	public byte[] toBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try { writeBytes(bos); } catch (IOException e) { /* Shouldn't happen. */ }
		return bos.toByteArray();
	}

	@Override
	public void fromBytes(byte[] data) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		readBytes(bin);
		Common.close(bin);
	}

	@Override
	public void readBytes(InputStream in) throws IOException
	{
		reset();
		SuperReader sr = new SuperReader(in, SuperReader.LITTLE_ENDIAN);
		
		version = sr.readUnsignedByte();
		
		final boolean VERSION0 = (version >= 0 && version <= 4); // Doom 1.2 has no version byte
		final boolean VERSION1 = (version >= VERSION_14 && version <= VERSION_FINALDOOM);
		final boolean VERSION2 = (version >= VERSION_BOOM && version <= VERSION_PRBOOM250);
		final boolean LONGTICS = version == VERSION_FINALDOOM || version == VERSION_PRBOOM250;
		
		if (VERSION0)
		{
			setCompatibilityLevel(COMPLEVEL_DOOM_12);
			setSkill(version); // read byte was skill.
			setEpisode(sr.readUnsignedByte());
			setMap(sr.readUnsignedByte());
			setVersion(0);
		}
		else if (VERSION1)
		{
			if (version <= VERSION_1666)
				setCompatibilityLevel(COMPLEVEL_DOOM_1666);
			else if (version == VERSION_19)
				setCompatibilityLevel(COMPLEVEL_DOOM_19);
			else
				setCompatibilityLevel(COMPLEVEL_UDOOM);
			
			setSkill(sr.readUnsignedByte());
			setEpisode(sr.readUnsignedByte());
			setMap(sr.readUnsignedByte());
			setGameMode(sr.readUnsignedByte());
			setMonsterRespawn(sr.readBoolean());
			setFastMonsters(sr.readBoolean());
			setNoMonsters(sr.readBoolean());
			setViewpoint(sr.readUnsignedByte());
		}
		else if (VERSION2)
		{
			byte[] head = sr.readBytes(6); // read header
			
			// set compatibility.
			switch (version)
			{
				case VERSION_BOOM:
				case VERSION_BOOM201:
					if (!sr.readBoolean())
						setCompatibilityLevel(COMPLEVEL_BOOM201);
					else
						setCompatibilityLevel(COMPLEVEL_BOOM);
					break;
				case VERSION_BOOM202:
					if (!sr.readBoolean())
						setCompatibilityLevel(COMPLEVEL_BOOM202);
					else
						setCompatibilityLevel(COMPLEVEL_BOOM);
					break;
				case VERSION_LXDOOM:
					if (head[1] == 'B')
						setCompatibilityLevel(COMPLEVEL_LXDOOM);
					else if (head[1] == 'M')
					{
						setCompatibilityLevel(COMPLEVEL_MBF);
						sr.readBoolean();
					}
					break;
				case VERSION_MBF:
					setCompatibilityLevel(COMPLEVEL_MBF);
					sr.readBoolean();
					break;
				case VERSION_PRBOOM210:
					setCompatibilityLevel(COMPLEVEL_PRBOOM_210);
					sr.readBoolean();
					break;
				case VERSION_PRBOOM220:
					setCompatibilityLevel(COMPLEVEL_PRBOOM_211_226);
					sr.readBoolean();
					break;
				case VERSION_PRBOOM230:
					setCompatibilityLevel(COMPLEVEL_PRBOOM_23X);
					sr.readBoolean();
					break;
				case VERSION_PRBOOM240:
					setCompatibilityLevel(COMPLEVEL_PRBOOM_240);
					sr.readBoolean();
					break;
				case VERSION_PRBOOM250:
					setCompatibilityLevel(COMPLEVEL_CURRENT_PRBOOM);
					sr.readBoolean();
					break;
			}
			
			setSkill(sr.readUnsignedByte());
			setEpisode(sr.readUnsignedByte());
			setMap(sr.readUnsignedByte());
			setGameMode(sr.readUnsignedByte());
			setViewpoint(sr.readUnsignedByte());
			
			byte[] options = sr.readBytes(OPTION_FLAGS_LENGTH);
			ByteArrayInputStream bis = new ByteArrayInputStream(options);
			SuperReader optr = new SuperReader(bis, SuperReader.LITTLE_ENDIAN);
			
			// read Boom options.
			setMonstersRememberTarget(optr.readBoolean());
			setEnableFriction(optr.readBoolean());
			setEnableWeaponRecoil(optr.readBoolean());
			setAllowPushers(optr.readBoolean());
			
			optr.readBoolean(); // skip byte
			
			setEnablePlayerBobbing(optr.readBoolean());
			setMonsterRespawn(optr.readBoolean());
			setFastMonsters(optr.readBoolean());
			setNoMonsters(optr.readBoolean());
			
			setDemoInsurance(optr.readByte());
			
			int r = 0;
			r |= optr.readByte() & 0x0ff; r <<= 8;
			r |= optr.readByte() & 0x0ff; r <<= 8;
			r |= optr.readByte() & 0x0ff; r <<= 8;
			r |= optr.readByte() & 0x0ff;
			setRandomSeed(r);
			
			if (compatibilityLevel >= COMPLEVEL_MBF)
			{
				setMonsterInfighting(optr.readBoolean());
				setEnableDogs(optr.readBoolean());
				
				optr.readShort(); // skip 2 bytes
				
				int f = 0;
				f |= optr.readByte() & 0x0ff; r <<= 8;
				f |= optr.readByte() & 0x0ff;
				setFriendFollowDistance(f);
				
				setMonsterBacking(optr.readBoolean());
				setMonstersAvoidHazards(optr.readBoolean());
				setMonsterFriction(optr.readBoolean());
				setHelperCount(optr.readByte());
				setDogsJump(optr.readBoolean());
				setEnableMonkeys(optr.readBoolean());
				
				for (int i = 0; i < COMPFLAG_LENGTH; i++)
					compatibilityFlags[i] = optr.readBoolean();
				
				setForceOldBSP(optr.readBoolean());
			}
			else if (version == VERSION_BOOM)
			{
				sr.readBytes(256 - OPTION_FLAGS_LENGTH);
			}
			
			optr.close();
		}
		else
			throw new IOException("Not a DEMO lump. Found version "+version);
		
		int p = 0;
		for (int i = 0; i < MAX_PLAYERS; i++)
			p += sr.readBoolean() ? 1 : 0;
		if (version >= VERSION_BOOM)
			for (int i = 0; i < BOOM_MIN_MAXPLAYERS - MAX_PLAYERS; i++)
				p += sr.readBoolean() ? 1 : 0;
		players = p;
		
		// Read demo tics.
		byte db = sr.readByte();
		Tic[] tics = new Tic[players];
		while (db != DEMO_END)
		{
			for (int i = 0; i < tics.length; i++)
			{
				if (i == 0)
					tics[i] = LONGTICS 
					? Tic.create(db, sr.readByte(), sr.readShort(), sr.readByte()) 
					: Tic.create(db, sr.readByte(), sr.readByte(), sr.readByte());
				else
					tics[i] = LONGTICS 
					? Tic.create(sr.readByte(), sr.readByte(), sr.readShort(), sr.readByte()) 
					: Tic.create(sr.readByte(), sr.readByte(), sr.readByte(), sr.readByte());
			}
			addTic(tics);
			db = sr.readByte();
		}
		
	}

	@Override
	public void writeBytes(OutputStream out) throws IOException
	{
		SuperWriter sw = new SuperWriter(out, SuperWriter.LITTLE_ENDIAN);
		
		final boolean VERSION0 = (version == VERSION_12); // Doom 1.2 has no version byte
		final boolean VERSION1 = (version >= VERSION_14 && version <= VERSION_FINALDOOM);
		final boolean VERSION2 = (version >= VERSION_BOOM && version <= VERSION_PRBOOM250);
		final boolean LONGTICS = version == VERSION_FINALDOOM || version == VERSION_PRBOOM250;
		
		if (!VERSION0)
		{
			sw.writeUnsignedByte((short)getVersion());
		}
		
		if (!VERSION2)
		{
			sw.writeUnsignedByte((short)getSkill());
			sw.writeUnsignedByte((short)getEpisode());
			sw.writeUnsignedByte((short)getMap());
		}
		
		if (VERSION1)
		{
			sw.writeUnsignedByte((short)getGameMode());
			sw.writeBoolean(getMonsterRespawn());
			sw.writeBoolean(getFastMonsters());
			sw.writeBoolean(getNoMonsters());
			sw.writeUnsignedByte((short)getViewpoint());
		}
		else if (VERSION2)
		{
			switch (version)
			{
				case VERSION_BOOM:
				case VERSION_BOOM201:
					sw.writeBytes(BOOM_SIGNATURE);
					sw.writeBoolean(getCompatibilityLevel() == COMPLEVEL_BOOM ? true : false);
					break;
				case VERSION_BOOM202:
					sw.writeBytes(BOOM_SIGNATURE);
					sw.writeBoolean(getCompatibilityLevel() == COMPLEVEL_BOOM ? true : false);
					break;
				case VERSION_LXDOOM:
					if (getCompatibilityLevel() == COMPLEVEL_LXDOOM)
						sw.writeBytes(BOOM_SIGNATURE);
					else
					{
						sw.writeBytes(MBF_SIGNATURE);
						sw.writeBoolean(false);
					}
					break;
				case VERSION_MBF:
					sw.writeBytes(MBF_SIGNATURE);
					sw.writeBoolean(false);
					break;
				case VERSION_PRBOOM210:
				case VERSION_PRBOOM220:
				case VERSION_PRBOOM230:
				case VERSION_PRBOOM240:
				case VERSION_PRBOOM250:
					sw.writeBytes(BOOM_SIGNATURE);
					sw.writeBoolean(false);
					break;
			}
			
			sw.writeUnsignedByte((short)getGameMode());
			sw.writeBoolean(getMonsterRespawn());
			sw.writeBoolean(getFastMonsters());
			sw.writeBoolean(getNoMonsters());
			sw.writeUnsignedByte((short)getViewpoint());
			
			// start option block
			ByteArrayOutputStream optout = new ByteArrayOutputStream();
			SuperWriter optwr = new SuperWriter(optout, SuperReader.LITTLE_ENDIAN);
			
			optwr.writeBoolean(getMonstersRememberTarget());
			optwr.writeBoolean(getEnableFriction());
			optwr.writeBoolean(getEnableWeaponRecoil());
			optwr.writeBoolean(getAllowPushers());
			
			optwr.writeBoolean(false); // pad

			optwr.writeBoolean(getEnablePlayerBobbing());
			optwr.writeBoolean(getMonsterRespawn());
			optwr.writeBoolean(getFastMonsters());
			optwr.writeBoolean(getNoMonsters());

			optwr.writeUnsignedByte((short)getDemoInsurance());
			
			int r = getRandomSeed();
			optwr.writeUnsignedByte((short)(r & 0x0ff)); r >>>= 8;
			optwr.writeUnsignedByte((short)(r & 0x0ff)); r >>>= 8;
			optwr.writeUnsignedByte((short)(r & 0x0ff)); r >>>= 8;
			optwr.writeUnsignedByte((short)(r & 0x0ff));
			
			if (compatibilityLevel >= COMPLEVEL_MBF)
			{
				optwr.writeBoolean(getMonsterInfighting());
				optwr.writeBoolean(getEnableDogs());
				
				optwr.writeShort((short)0); // skip 2 bytes
				
				int f = getFriendFollowDistance();
				optwr.writeUnsignedByte((short)(f & 0x0ff)); f >>>= 8;
				optwr.writeUnsignedByte((short)(f & 0x0ff));

				optwr.writeBoolean(getMonsterBacking());
				optwr.writeBoolean(getMonstersAvoidHazards());
				optwr.writeBoolean(getMonsterFriction());
				optwr.writeUnsignedByte((short)getHelperCount());
				optwr.writeBoolean(getDogsJump());
				optwr.writeBoolean(getEnableMonkeys());

				for (int i = 0; i < COMPFLAG_LENGTH; i++)
					optwr.writeBoolean(compatibilityFlags[i]);

				optwr.writeBoolean(getForceOldBSP());
			}
			else if (version == VERSION_BOOM)
			{
				sw.writeBytes(new byte[256 - OPTION_FLAGS_LENGTH]);
			}

			sw.writeBytes(optout.toByteArray());
			// end option block.
		}

		int p = getPlayers();
		for (int i = 0; i < MAX_PLAYERS; i++)
			sw.writeBoolean((p--) > 0);
		if (version >= VERSION_BOOM)
			for (int i = 0; i < BOOM_MIN_MAXPLAYERS - MAX_PLAYERS; i++)
				sw.writeBoolean((p--) > 0);

		for (Tic[] tic : gameTics)
		{
			for (Tic t : tic)
			{
				if (LONGTICS)
				{
					sw.writeByte(t.forwardMovement);
					sw.writeByte(t.rightStrafe);
					sw.writeShort(t.turnLeft);
					sw.writeByte(t.action);
				}
				else
				{
					sw.writeByte(t.forwardMovement);
					sw.writeByte(t.rightStrafe);
					sw.writeByte((byte)(RMath.clampValue(t.turnLeft, -127, 127)));
					sw.writeByte(t.action);
				}
			}
		}
		
		sw.writeByte(DEMO_END);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Demo");
		sb.append(' ').append("v").append(version);
		sb.append(' ').append("P").append(players);
		sb.append(' ').append("E").append(episode).append("M").append(map);
		sb.append(' ').append("S").append(skill);
		sb.append(' ').append(gameTics.size()).append(" Tics");
		sb.append(' ').append(String.format("%d:%02d.%03d", 
			(int)(getLength() / 60), 
			(int)(getLength()) % 60,
			(int)((getLength() % 1.0) * 1000))
		);
		return sb.toString();
	}
	
	/**
	 * Demo tic abstraction.
	 */
	public static class Tic
	{
		public static final int WEAPON_SHIFT =				3;
		public static final int SAVE_SHIFT =				2;
		
		public static final byte ACTION_FIRE =				(byte)(0x01 << 0);
		public static final byte ACTION_USE =				(byte)(0x01 << 1);
		public static final byte ACTION_CHANGE_WEAPON =		(byte)(0x01 << 2);
		public static final byte ACTION_SPECIAL =			(byte)(0x01 << 7);

		public static final byte WEAPON_1 =					0x00;
		public static final byte WEAPON_2 =					0x01;
		public static final byte WEAPON_3 =					0x02;
		public static final byte WEAPON_4 =					0x03;
		public static final byte WEAPON_5 =					0x04;
		public static final byte WEAPON_6 =					0x05;
		public static final byte WEAPON_7 =					0x06;
		public static final byte WEAPON_8 =					0x07;

		public static final byte ACTION_SPECIAL_LOAD =		(byte)(0x00);
		public static final byte ACTION_SPECIAL_PAUSE =		(byte)(0x01);
		public static final byte ACTION_SPECIAL_SAVE =		(byte)(0x02);
		public static final byte ACTION_SPECIAL_RESTART =	(byte)(0x03);
		
		/** Forward/backward movement. */
		private byte forwardMovement;
		/** Right strafe movement. */
		private byte rightStrafe;
		/** Player turning. */
		private short turnLeft;
		/** Miscellaneous action. */
		private byte action;

		/**
		 * Creates a new tic.
		 */
		private Tic()
		{
			forwardMovement = 0;
			rightStrafe = 0;
			turnLeft = 0;
			action = 0;
		}

		/**
		 * Creates a game tic.
		 * Entered values are clamped to the byte range.
		 * @param forward the forward movement amount (-127 to 127). Negative values = backwards movement.
		 * @param rightStrafe the right strafe movement amount (-127 to 127). Negative values = strafe left.
		 * @param turnLeft the left turn movement amount (-127 to 127, or -32767 to 32767 on long tics). Negative values = turn right.
		 * @param action the action byte.
		 * @return a new demo tic.
		 */
		public static Tic create(int forward, int rightStrafe, int turnLeft, byte action)
		{
			Tic out = new Tic();
			out.forwardMovement = (byte)RMath.clampValue(forward, -127, 127);
			out.rightStrafe = (byte)RMath.clampValue(rightStrafe, -127, 127);
			out.turnLeft = (short)RMath.clampValue(turnLeft, -32767, 32767);
			out.action = action;
			return out;
		}
		
		/**
		 * Returns the action byte for action buttons.
		 * @param fire true if set, false if not.
		 * @param use true if set, false if not.
		 * @return the action byte value.
		 */
		public static byte actionButton(boolean fire, boolean use)
		{
			return actionButton(fire, use, false, 0);
		}
		
		/**
		 * Returns the action byte for action buttons.
		 * @param fire true if set, false if not.
		 * @param use true if set, false if not.
		 * @param changeWeapon true if set, false if not.
		 * @param weapon the weapon number.
		 * @return the action byte value.
		 */
		public static byte actionButton(boolean fire, boolean use, boolean changeWeapon, int weapon)
		{
			return (byte)(
				(fire ? ACTION_FIRE : 0x00) 
				| (use ? ACTION_USE : 0x00) 
				| (changeWeapon ? ACTION_CHANGE_WEAPON : 0x00) 
				| (weapon << WEAPON_SHIFT)  
				);
		}

		/**
		 * Returns the action byte for saving a game.
		 * @param slot the save slot to save the game to.
		 * @return the action byte value.
		 */
		public static byte actionSave(int slot)
		{
			return (byte)( 
				ACTION_SPECIAL 
				| ACTION_SPECIAL_SAVE 
				| (slot << SAVE_SHIFT)
				);
		}
		
		/**
		 * Returns the action byte for loading a game.
		 * @param slot the load slot to load the game from.
		 * @return the action byte value.
		 */
		public static byte actionLoad(int slot)
		{
			return (byte)( 
				ACTION_SPECIAL 
				| ACTION_SPECIAL_LOAD 
				| (slot << SAVE_SHIFT)
				);
		}
		
		/**
		 * @return the action byte for pressing pause.
		 */
		public static byte actionPause()
		{
			return (byte)( 
				ACTION_SPECIAL 
				| ACTION_SPECIAL_PAUSE 
				);
		}
		
		/**
		 * @return the action byte for restarting the map.
		 */
		public static byte actionRestart()
		{
			return (byte)( 
				ACTION_SPECIAL 
				| ACTION_SPECIAL_RESTART 
				);
		}
		
		/**
		 * Returns forward movement units.
		 * Negative values are backwards.
		 * @return the unit value.
		 */
		public int getForwardMovement()
		{
			return forwardMovement;
		}

		/**
		 * Returns right strafe units.
		 * Negative values are left strafes.
		 * @return the unit value.
		 */
		public int getRightStrafe()
		{
			return rightStrafe;
		}

		/**
		 * Returns left turn units.
		 * Negative values are right turns.
		 * @return the unit value.
		 */
		public int getTurnLeft()
		{
			return turnLeft;
		}

		/**
		 * @return the action bits on this tic.
		 */
		public byte getAction()
		{
			return action;
		}

		@Override
		public String toString()
		{
			String actstr = "";
			
			if (action != 0)
			{
				if ((action & ACTION_SPECIAL) != 0)
				{
					int a = (action & 0x03);
					switch (a)
					{
						case ACTION_SPECIAL_LOAD:
							actstr = ", Load Game " + (action & 0x7c >> SAVE_SHIFT);
							break;
						case ACTION_SPECIAL_SAVE:
							actstr = ", Save Game " + (action & 0x7c >> SAVE_SHIFT);
							break;
						case ACTION_SPECIAL_PAUSE:
							actstr = ", Pause";
							break;
						case ACTION_SPECIAL_RESTART:
							actstr = ", Restart Level";
							break;
					}
				}
				else
				{
					StringBuilder sb = new StringBuilder();
					if ((action & ACTION_FIRE) != 0)
						sb.append(" Fire");
					else if ((action & ACTION_USE) != 0)
					{
						if (sb.length() > 0)
							sb.append(",");
						sb.append(" Use");
					}
					else if ((action & ACTION_CHANGE_WEAPON) != 0)
					{
						if (sb.length() > 0)
							sb.append(",");
						sb.append(" Weapon ").append((action & 0x7c) >> WEAPON_SHIFT);
					}
					actstr = sb.toString();
				}
			}
			
			return String.format("%s %d, %s %d, Turn %s %d%s",
				forwardMovement < 0 ? "Back" : "Forward",
				Math.abs(forwardMovement),
				rightStrafe < 0 ? "Left" : "Right",
				Math.abs(rightStrafe),
				turnLeft < 0 ? "Right" : "Left",
				Math.abs(turnLeft),
				actstr
			);
			
		}
		
	}
	
}
