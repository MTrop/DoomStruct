/*******************************************************************************
 * Copyright (c) 2015-2019 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.struct.vector;

/**
 * Maintains an expandable list of objects, but is always sorted.
 * @author Matthew Tropiano
 * @param <T> any comparable Object.
 */
public class SortedList<T extends Comparable<T>> extends AbstractVector<T>
{
	/**
	 * Constructs a new SortedVector with capacity DEFAULT_CAPACITY.
	 */
	public SortedList()
	{
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * Constructs a new SortedVector with defined capacity that doubles each expand.
	 * @param capacity	the initial capacity of the vector.
	 */
	public SortedList(int capacity)
	{
		this(DEFAULT_CAPACITY, 0);
	}
	
	/**
	 * Constructs a new SortedVector with defined capacity and capacity increment.
	 * @param capacity the initial capacity of the vector.
	 * @param capacityIncrement the capacity incrementor.
	 */
	public SortedList(int capacity, int capacityIncrement)
	{
		super(capacity, capacityIncrement);
	}
	
	/**
	 * Adds an object to the list and sorts it.
	 */
	public void add(T object)
	{
		super.add(object);
		resort(size-1);
	}
	
	/**
	 * Checks if an object exists in this vector via comparison binary-search style.
	 */
	@Override
	public int getIndexOf(T obj)
	{
		int u = size, l = 0;
		int i = (u+l)/2;
		int prev = u;
		
		while (i != prev)
		{
			if (get(i).equals((T)obj))
				return i;

			int c = get(i).compareTo((T)obj); 
			
			if (c < 0)
				l = i;
			else if (c == 0)
				return i;
			else
				u = i;
			
			prev = i;
			i = (u+l)/2;
		}
		
		return -1;
	}

	/**
	 * Resorts this vector (insertion sort) from an index.
	 * @param index	the index.
	 */
	private void resort(int index)
	{
		while (index > 0 && get(index).compareTo(get(index-1)) < 0)
			swap(index--);
	}
	
	/** 
	 * Swaps the object at this index with the one before it (used in sort).
	 */
	private void swap(int index)
	{
		T tmp = get(index);
		set(index, get(index-1));
		set(index-1, tmp);
	}
	
}