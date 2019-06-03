/*******************************************************************************
 * Copyright (c) 2019 Black Rook Software
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.util.trie;

/**
 * An implementation of a Trie that stores strings mapped to values.
 * @author Matthew Tropiano
 */
public class StringTrieMap<V extends Object> extends AbstractTrieMap<String, V, Character>
{
	public StringTrieMap()
	{
		super();
	}
	
	@Override
	protected Character[] getSegmentsForKey(String value)
	{
		Character[] out = new Character[value.length()];
		for (int i = 0; i < value.length(); i++)
			out[i] = value.charAt(i);
		return out;
	}

}
