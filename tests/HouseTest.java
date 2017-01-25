package tests;

import model.House;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class HouseTest {
    @Test
    public void testConstructor() {
        House house1 = new House();

        assertEquals("Default seed for a house is 4", house1.getSeedCount(), 4);

        house1.incrementSeedCount();
        House house2 = new House(house1);

        assertEquals("When copying a house it copies the amount of seeds", house2.getSeedCount(), 5);
    }

    @Test
    public void testIncrementSeedCount() {
        House house1 = new House();

        house1.incrementSeedCount();
        assertEquals("Incrementing a house with 4 seeds result in a house with 5 seeds", house1.getSeedCount(), 5);

        house1.incrementSeedCount();
        assertEquals("Incrementing a house with 5 seeds result in a house with 6 seeds", house1.getSeedCount(), 6);
    }

    @Test
    public void testClear() {
        House house1 = new House();

        house1.clear();
        assertEquals("Clearing a house with 4 seeds result in a house with no seeds", house1.getSeedCount(), 0);

        house1 = new House();
        house1.incrementSeedCount();
        house1.incrementSeedCount();

        house1.clear();
        assertEquals("Clearing a house with 6 seeds result in a house with no seeds", house1.getSeedCount(), 0);
    }
}
