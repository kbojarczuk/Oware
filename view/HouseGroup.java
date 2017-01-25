package view;

import java.util.ArrayList;

/**
 * Sprite container class for grouping positions of SeedSprite objects,
 * to form organised animation paths.
 */
public class HouseGroup {
	private double curTheta = 0;
	private static double thetaIncrementPerSecond = (2*Math.PI)/4;
	private double startX, startY;
	private ArrayList<SeedSprite> seedsInGroup = new ArrayList<SeedSprite>();
	
	/**
	 * Constructs new HouseGroup, it's seed formation position based on given grouping number.
	 * Wraps back and under at grouping index 6
	 * @param num The grouping number
	 */
	public HouseGroup(int num){
		
		if(num < 6){
			startX = 80+(93*(num%6));
			startY = 90;
		}
		else{
			startY = 290;
			startX = 545-(93*(num%6));
		}
	}

	/**
	 * Retrieves the starting X coordinate of the HouseGroup's formation position.
	 * @return The starting X coordinate.
	 */
	public double getX(){
		return startX;
	}

	/**
	 * Retrieves the starting Y coordinate of the HouseGroup's formation position.
	 * @return The starting Y coordinate.
	 */
	public double getY(){
		return startY;
	}

	public int getSeedCount() {
		return seedsInGroup.size();
	}

	/**
	 * Removes all tracked SeedSprite objects in this grouping. 
	 * @return ArrayList of SeedSprite objects removed.
	 */
	public ArrayList<SeedSprite> removeSeeds(){
		ArrayList<SeedSprite> toReturn = new ArrayList<SeedSprite>(seedsInGroup);
		seedsInGroup.clear();
		return toReturn;
	}

	/**
	 * Adds a new SeedSprite to be tracked by the grouping.
	 * @param newSeed The new SpriteSeed to be added to the formation.
	 */
	public void give(SeedSprite newSeed) {
		seedsInGroup.add(newSeed);
	}
	
	/**
	 * Updates the positions of tracked SeedSprites based on a circular animation path of the grouping.
	 * @param delta Time since last update in nanoseconds.
	 */
	public void update(double delta){

		curTheta = (curTheta + thetaIncrementPerSecond*(delta/1000000000))%(2*Math.PI);
		for(int i = 0; i < seedsInGroup.size(); ++i){
			SeedSprite nextSeed = seedsInGroup.get(i);
			double seedTheta = curTheta + ((2*Math.PI)/seedsInGroup.size())*i;
			double x = 15*Math.cos(seedTheta);
			double y = 15*Math.sin(seedTheta);
			if(nextSeed.isMoving()){
				double[] pos = nextSeed.getPos();
				nextSeed.moveTo(x+startX, y+startY, Math.sqrt(Math.pow((x+startX)-pos[0], 2)+Math.pow((y+startY)-pos[1], 2))/200);
				continue;
			}
			nextSeed.setPos(x+startX, y+startY);
		}
	}
	
}

