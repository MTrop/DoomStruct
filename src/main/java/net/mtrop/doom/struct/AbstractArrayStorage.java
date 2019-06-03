package net.mtrop.doom.struct;

/**
 * This class contains the base for all data structures 
 * that make use of a contiguous memory structure.
 * @author Matthew Tropiano
 */
public abstract class AbstractArrayStorage<T extends Object>
{
	/** Default capacity for a new array. */
	public static final int DEFAULT_CAPACITY = 8;
	
	/** Underlying object array. */
	protected Object[] storageArray;

	/**
	 * Initializes the array with the default storage capacity.
	 */
	protected AbstractArrayStorage()
	{
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Initializes the array with a particular storage capacity.
	 * @param capacity the desired capacity.
	 */
	protected AbstractArrayStorage(int capacity)
	{
		storageArray = new Object[capacity];
	}
	
	/**
	 * Gets data at a particular index in the array.
	 * @param index the desired index.
	 * @return the data at a particular index in the array.
	 * @throws ArrayIndexOutOfBoundsException if the index falls outside of the array bounds.
	 */
	@SuppressWarnings("unchecked")
	protected T get(int index)
	{
		return (T)storageArray[index];
	}
	
	/**
	 * Sets the data at a particular index in the array.
	 * @param index the desired index.
	 * @param object the object to set.
	 * @throws ArrayIndexOutOfBoundsException if the index falls outside of the array bounds.
	 */
	protected void set(int index, T object)
	{
		storageArray[index] = object;
	}
	
}