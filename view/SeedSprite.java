package view;

/**
 * Specialised Sprite class to wrap only a Seed Sprite, at set internal parameters.
 */
public class SeedSprite extends Sprite {

	private double verticalVelocity = 0;
	private double horizontalVelocity = 0;
	private double target[] = {0,0};
	private boolean isMoving = false;
	private static int movingSpriteCount = 0;
		
	/**
	 * Constructs new SeedSprite from sheet at 'images/seed.png', with dimensions of (30,30) and one frame.
	 */
	public SeedSprite() {
		super("seed.png", 30, 30, 1);
	}

	/**
	 * Sets the sprite on a straight line animation path, giving it pixel-based velocity based on target and time allowed.
	 * @param x The x coordinate of the target position.
	 * @param y The y coordinate of the target position.
	 * @param timeForMove The time in which is move animation should be completed in.
	 */
	public void moveTo(double x, double y, double timeForMove){
		if(!isMoving)++movingSpriteCount;
		isMoving = true;
		target[0] = x;
		target[1] = y;
		if(timeForMove!=0){
			horizontalVelocity = (x-pos[0])/timeForMove;
			verticalVelocity = (y-pos[1])/timeForMove;
		}else{
			horizontalVelocity = (x-pos[0]);
			verticalVelocity = (y-pos[1]);
		}
		
	}

	/**
	 * Specialised update algorithm for a seed.
	 * Changes position depending on whether sprite is on movement path set previously, and based on time passed.
	 * @param delta Time passed since last update in nanoseconds.
	 */
	@Override
	public void update(double delta){
		super.update(delta);
		if(isMoving){
			pos[0] += horizontalVelocity*(delta/1000000000);
			pos[1] += verticalVelocity*(delta/1000000000);
			
			if((horizontalVelocity > 0 && pos[0] >= target[0]) || (horizontalVelocity < 0 && pos[0] <= target[0])){
				--movingSpriteCount;
				isMoving = false;
				pos[0] = target[0];
				pos[1] = target[1];
			}else if((verticalVelocity > 0 && pos[1] >= target[1]) || (verticalVelocity < 0 && pos[1] <= target[1])){
				--movingSpriteCount;
				isMoving = false;
				pos[0] = target[0];
				pos[1] = target[1];
			}
		}
	}

	/**
	 * Check for whether sprite is currently in a moveTo path animation.
	 * @return Result of boolean flag for animation, true if in animation, false otherwise.
	 */	
	public boolean isMoving(){
		return isMoving;
	}
	
	/**
	 * Checks whether there are any referenceable instances of SeedSprite on a moveTo animation path.
	 * (Dependent on internal counter)
	 * @return Boolean flag. True if referenced sprites still animating, false if not.
	 */
	public static boolean seedsMoving(){
		
		return movingSpriteCount>0;
	}
	
	/**
	 * Resets the internal counter of currently animating seed sprite instances. 
	 * Only use when sure you've lost reference to all animating seeds.
	 */	
	public static void resetMoveCounter(){
		movingSpriteCount = 0;
	}
}


