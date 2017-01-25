package model;
public class House
{
	private final int STARTING_NUMBER_OF_SEEDS = 4;

	private int seedCount;

	/**
	 * Construct a house with the default number of seeds
	 */
	public House()
	{
		seedCount = STARTING_NUMBER_OF_SEEDS;
	}

	/**
	 * Construct a house by coping the seed count from the otherhouse
	 * @param house
	 */
	public House(House house)
	{
		seedCount = house.seedCount;
	}

	/**
	 * Increment the seed count by one
	 */
	public void incrementSeedCount()
	{
		seedCount++;
	}

	/**
	 * Clear all seeds from house
	 */
	public void clear()
	{
		seedCount = 0;
	}

	/**
	 * Get the number of seeds currently in the house.
	 */
	public int getSeedCount()
	{
		return seedCount;
	}
}