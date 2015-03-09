/*******************************************************************************
 * Copyright (c) 2015 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.texture;

import java.util.Comparator;
import java.util.Iterator;

import net.mtrop.doom.texture.TextureSet.Texture.Patch;
import net.mtrop.doom.util.NameUtils;

import com.blackrook.commons.AbstractVector;
import com.blackrook.commons.Common;
import com.blackrook.commons.Sizable;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.commons.map.AbstractMappedVector;

/**
 * A helper class for the ridiculous TEXTUREx and PNAMES setup that Doom Texture definitions use.
 * @author Matthew Tropiano
 */
public class TextureSet implements Sizable
{
	/** The list of textures in this set, sorted. */
	private AbstractMappedVector<Texture, String> textureList;
	
	/**
	 * Creates a new TextureSet using an existing Patch Name lump and a series of Texture Lumps.
	 * @param pnames the patch name lump.
	 * @param textureLumps the list of texture lumps.
	 */
	@SafeVarargs
	public TextureSet(PatchNames pnames, final CommonTextureList<?> ... textureLumps)
	{
		this.textureList = new AbstractMappedVector<TextureSet.Texture, String>(textureLumps.length * 100)
		{
			@Override
			protected String getMappingKey(Texture object)
			{
				return object.toString();
			}
		};
		
		for (CommonTextureList<?> lump : textureLumps)
		{
			for (int i = 0; i < lump.size(); i++)
			{
				CommonTexture<?> t = lump.getByIndex(i);
				
				Texture newtex = createTexture(t.getName());
				newtex.setWidth(t.getWidth());
				newtex.setHeight(t.getHeight());
				
				textureList.add(newtex);

				for (int j = 0; j < t.getPatchCount(); j++)
				{
					CommonPatch p = t.getPatch(j);
					String patchName = pnames.getByIndex(p.getPatchIndex());
					Patch newpatch = newtex.createPatch(patchName);
					newpatch.setOriginX(p.getOriginX());
					newpatch.setOriginY(p.getOriginY());
				}
			}
		}
	}
	
	/**
	 * Checks an entry for a texture exists.
	 * @param textureName the texture name to search for.
	 * @return true if it exists, false otherwise.
	 */
	public boolean contains(String textureName)
	{
		return textureList.containsKey(textureName);
	}
	
	/**
	 * Returns an entry for a texture by name.
	 * @param textureName the texture name to search for.
	 * @return a texture with the composite information, or null if the texture could not be found.
	 */
	public Texture getTextureByName(String textureName)
	{
		return textureList.getUsingKey(textureName);
	}
	
	/**
	 * Adds a texture entry to this texture set.
	 * @param texture the texture to add.
	 */
	public void addTexture(Texture texture)
	{
		textureList.add(texture);
	}

	/**
	 * Creates a new entry for a texture, already added.
	 * @param textureName the name of the texture to add.
	 * @return a new, empty texture.
	 */
	public Texture createTexture(String textureName)
	{
		if (!NameUtils.isValidTextureName(textureName))
			throw new IllegalArgumentException("Not a valid texture name.");
		
		Texture out = new Texture();
		out.setName(textureName);
		return out;
	}

	/**
	 * Returns a sequence of texture names. Order and list of entries
	 * are dependent on the alphabetical order of all of the textures
	 * in this set.
	 * @param firstName the first texture name in the sequence. 
	 * @param lastName the last texture name in the sequence.
	 * @return an array of all of the textures in the sequence, including
	 * 		the provided textures, or null, if either texture does not exist.
	 */
	public String[] getSequence(String firstName, String lastName)
	{
		Queue<String> out = new Queue<String>();
		int index = textureList.getIndexUsingKey(firstName);
		if (index >= 0)
		{
			int index2 = textureList.getIndexUsingKey(lastName);
			if (index2 >= 0)
			{
				int min = Math.min(index, index2);
				int max = Math.max(index, index2);
				for (int i = min; i <= max; i++)
					out.add(textureList.getByIndex(i).getName());
			}
			else
				return null;
		}
		else
			return null;
		
		String[] outList = new String[out.size()];
		out.toArray(outList);
		return outList;
	}
	
	/**
	 * Removes a patch at a particular index.
	 */
	public Texture removeTexture(int index)
	{
		return textureList.removeIndex(index);
	}

	/**
	 * Returns a patch at a particular index.
	 */
	public Texture getTexture(int index)
	{
		return textureList.getByIndex(index);
	}

	/**
	 * Shifts the ordering of a patch.
	 * @see AbstractMappedVector#shift(int, int)
	 */
	public void shiftTexture(int index, int newIndex)
	{
		textureList.shift(index, newIndex);
	}

	/**
	 * Sorts the texture lumps in this set.
	 */
	public void sort()
	{
		textureList.sort();
	}

	/**
	 * Sorts the texture lumps in this set using a comparator.
	 */
	public void sort(Comparator<Texture> comparator)
	{
		textureList.sort(comparator);
	}

	@Override
	public int size()
	{
		return textureList.size();
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	/**
	 * A class that represents a single composite Texture entry.
	 */
	public static class Texture implements Iterable<Patch>, Sizable
	{
		/**
		 * Texture patch.
		 */
		public static class Patch
		{
			/** Patch name. */
			private String name;
			/** Offset X. */
			private int originX;
			/** Offset Y. */
			private int originY;
			
			private Patch()
			{
				name = "";
				originX = 0;
				originY = 0;
			}
			
			/** Returns the patch name. */
			public String getName()
			{
				return name;
			}

			/** Sets the patch name. */
			public void setName(String name)
			{
				this.name = name;
			}
			
			/** Returns the patch offset X. */
			public int getOriginX()
			{
				return originX;
			}
			
			/** Sets the patch offset X. */
			public void setOriginX(int originX)
			{
				this.originX = originX;
			}
			
			/** Returns the patch offset Y. */
			public int getOriginY()
			{
				return originY;
			}
			
			/** Sets the patch offset Y. */
			public void setOriginY(int originY)
			{
				this.originY = originY;
			}
			
		}
		
		/** Texture name. */
		private String name;
		/** Texture width. */
		private int width;
		/** Texture height. */
		private int height;
		
		/** Patch entry. */
		private List<Texture.Patch> patches;
		
		private Texture()
		{
			name = null;
			width = 0;
			height = 0;
			patches = new List<Texture.Patch>();
		}
		
		/** 
		 * Returns the texture entry name. 
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 * Sets the texture entry name.
		 */
		public void setName(String name)
		{
			this.name = name;
		}
		
		/**
		 * Returns the width of the texture in pixels.
		 */
		public int getWidth()
		{
			return width;
		}
		
		/**
		 * Sets the width of the texture in pixels.
		 */
		public void setWidth(int width)
		{
			this.width = width;
		}
		
		/**
		 * Returns the height of the texture in pixels.
		 */
		public int getHeight()
		{
			return height;
		}
		
		/**
		 * Sets the height of the texture in pixels.
		 */
		public void setHeight(int height)
		{
			this.height = height;
		}
		
		/**
		 * Adds a patch to this entry.
		 */
		public Patch createPatch(String name)
		{
			if (Common.isEmpty(name))
				throw new IllegalArgumentException("patch name cannot be empty.");

			Patch p = new Patch();
			p.setName(name);
			patches.add(p);
			return p;
		}
		
		/**
		 * Removes a patch at a particular index.
		 */
		public Patch removePatch(int index)
		{
			return patches.removeIndex(index);
		}

		/**
		 * Returns a patch at a particular index.
		 */
		public Patch getPatch(int index)
		{
			return patches.getByIndex(index);
		}
		
		/**
		 * Shifts the ordering of a patch.
		 * @see AbstractVector#shift(int, int)
		 */
		public void shiftPatch(int index, int newIndex)
		{
			patches.shift(index, newIndex);
		}
		
		/**
		 * Returns how many patches are on this texture entry.
		 */
		public int getPatchCount()
		{
			return patches.size();
		}

		@Override
		public Iterator<Patch> iterator()
		{
			return patches.iterator();
		}

		@Override
		public int size()
		{
			return patches.size();
		}

		@Override
		public boolean isEmpty()
		{
			return size() == 0;
		}

	}
}
