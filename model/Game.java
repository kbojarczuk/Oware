package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Random;

public class Game
{
	private static final int NUMBER_OF_HOUSES = 12, AI_ANALYSIS_MAXDEPTH = 8, SEEDS_REQUIRED_TO_WIN = 25;

	private House[] houses;
	private ArrayList<Integer> incrementedHouses;
	private int playerTurn;
	private int[] score;
	private boolean isPlayingAgainstAI; // player 2 will always be AI if this is true

	private Random random;

	//playerTurn=0 -> 0-5 in array
	//playerTurn=1 -> 6-11 in array

	// Board is presented like this
	// 0  1  2  3  4  5
	// 11 10 9  8  7  6

	/**
	 * Construct a game with default values
	 * @param isPlayingAgainstAI whether the game is PVP or AI
	 */
	public Game(boolean isPlayingAgainstAI)
	{
		this.isPlayingAgainstAI = isPlayingAgainstAI;
		reset();
	}

	/**
	 * Constructs a game by coping the details of another game
	 * @param game
	 */
	public Game(Game game)
	{
		this.isPlayingAgainstAI = game.isPlayingAgainstAI;
		score = new int[] { game.score[0], game.score[1] };
		houses = new House[NUMBER_OF_HOUSES];
		for (int i = 0; i < NUMBER_OF_HOUSES; i++)
		{
			houses[i] = new House(game.houses[i]);
		}
		playerTurn = game.playerTurn;
		incrementedHouses = new ArrayList<Integer>();
		for (Integer i : game.incrementedHouses)
		{
			incrementedHouses.add(new Integer(i));
		}
	}

	/**
	 * Resets the game to its default starting state.
	 */
	public void reset() {
		score = new int[] { 0, 0 };
		houses = new House[NUMBER_OF_HOUSES];
		for(int i = 0; i < NUMBER_OF_HOUSES; i++) {
			houses[i] = new House();
		}
		random = new Random();
		playerTurn = random.nextInt(2);
		incrementedHouses = new ArrayList<Integer>();
	}

	/**
	 * Tests whether the current turn is to be taken by the AI.
	 * @return	true if the current turn is to be taken by the AI.
	 */
	public boolean isAITurn()
	{
		return isPlayingAgainstAI && playerTurn == 1;
	}

	/**
	 * Advances the game to the next turn.
	 */
	public void nextTurn()
	{
		playerTurn = (playerTurn + 1) % 2;
		incrementedHouses.clear();
	}

	/**
	 * Deduces the next move that the AI wishes to perform.
	 *
	 * @return The house ID of the house to sow.
	 */
	public int nextAIMove()
	{
		return bestHouseToSow(this, AI_ANALYSIS_MAXDEPTH);
	}

	private static int bestHouseToSow(Game game, int maxDepth)
	{
		if (maxDepth == 0 || !game.canSowAny())
			return -1;
		int startingHouse;
		if (game.playerTurn == 0)
			startingHouse = 0;
		else
			startingHouse = NUMBER_OF_HOUSES / 2;
		int maxScore = -1, maxScoreHouse = -1;
		for (int i = startingHouse; i < startingHouse + (NUMBER_OF_HOUSES / 2); i++)
		{
			if (!game.canSow(i))
				continue;
			Game currentGame = new Game(game);
			currentGame.sow(i);
			currentGame.capture();
			int currentGamePlayer = currentGame.playerTurn;
			currentGame.nextTurn();
			bestHouseToSow(currentGame, maxDepth - 1);
			int score = currentGame.score[currentGamePlayer];
			if (score > maxScore)
			{
				maxScore = score;
				maxScoreHouse = i;
			}
		}
		return maxScoreHouse;
	}

	/**
	 * Tests whether the spcified house can sow seeds.
	 *
	 * @return true if seeds can be sown from the spcified house.
	 */
	public boolean canSow(int houseID)
	{
		return houseOwner(houseID) == playerTurn && houses[houseID].getSeedCount() > 0 && canHelpIfNoSeeds(houseID);
	}
	
	private boolean canHelpIfNoSeeds(int houseID){
		if(numberOfOpponentsSeeds()==0){
			int number = houses[houseID].getSeedCount();
			int current = houseID;
			int distanceFromEdge = current%6;

			if(number>distanceFromEdge)return true;

			return false;
		}
		return true;
	}
	
	/**
	 * Tests whether any seeds can be sown for the current player.
	 *
	 * ~return true if there exists a house where seeds can be sown from.
	*/
	public boolean canSowAny(){
		for(int i = 0; i < 12; i++){
			if(canSow(i)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Captures all seeds from houses on the players side.
	 *
	 * @return The houses where seeds were captured from.
	 */
	public ArrayList<Integer> captureAll(){
		ArrayList<Integer> captured = new ArrayList<Integer>();
		int startingHouse;
		if (playerTurn == 0)
			startingHouse = 0;
		else
			startingHouse = NUMBER_OF_HOUSES / 2;
		for (int i = startingHouse; i < startingHouse + (NUMBER_OF_HOUSES / 2); i++)
		{
			if (houses[i].getSeedCount() == 0)
				continue;
			captured.add(i);
			score[playerTurn] += houses[i].getSeedCount();
			houses[i].clear();
		}
		return captured;
	}
	
	/**
	 * Sowes the seeds for the specified house
	 */
	public void sow(int houseID){
		int number = houses[houseID].getSeedCount();
		houses[houseID].clear();
			
		int current = houseID;
		for(int i = 0; i < number; i++){
			do
			{
				current--;
				if (current < 0) current = 11;
			} while (current == houseID);

			houses[current].incrementSeedCount();
			incrementedHouses.add(current);
		}
	}

	/**
	 * Captures seeds from opponents houses where appropriate.
	 *
	 * @return The house IDs of the houses captured from.
	*/
	public ArrayList<Integer> capture()
	{
		ArrayList<Integer> housesToCapture = new ArrayList<Integer>();
		int seedsToCapture = 0;

		for (int i = incrementedHouses.size() - 1; i >= 0; i--)
		{
			int houseID = incrementedHouses.get(i),
				seedCount = houses[houseID].getSeedCount();
			if (houseOwner(houseID)!=playerTurn && (seedCount == 2 || seedCount == 3))
			{
				housesToCapture.add(houseID);
				seedsToCapture += houses[houseID].getSeedCount();
			}
			else
				break;
		}

		if(seedsToCapture!=numberOfOpponentsSeeds()){
			for(int n: housesToCapture){
				score[playerTurn] += houses[n].getSeedCount();
				houses[n].clear();
			}
			return housesToCapture;
		}
		housesToCapture.clear();
		return housesToCapture;
	}

	private int houseOwner(int houseID)
	{
		if (houseID < 6)
			return 0;
		else
			return 1;
	}

	/**
	 * Returns the winner of the game, if there is one.
	 *
	 * @return the number of the player that has one, or -1 if no winner exists.
	 */
	public int winner() {
		if(score[0] >= SEEDS_REQUIRED_TO_WIN) {
			return 0;
		} else if (score[1] >= SEEDS_REQUIRED_TO_WIN) {
			return 1;
		}
		return -1;
	}

	/**
	 * Determines if the game has ended.
	 *
	 * @return true if the game has ended.
	 */
	public boolean hasEnded() {
		return winner() != -1 || hasDrawn();
	}
	
	/**
	 * Determines if the current player has won.
	 *
	 * @return true if the current player has won.
	 */
	public boolean hasWon(){
		return score[playerTurn] >= SEEDS_REQUIRED_TO_WIN;
	}

	/**
	 * Determines if the game has been drawn.
	 *
	 * @return true if the game has been drawn
	 */
	public boolean hasDrawn(){
		return score[0] == 24 && score[1] == 24;
	}

	private int numberOfPlayersSeeds()
	{
		return playerSeedCount(playerTurn);
	}

	private int numberOfOpponentsSeeds()
	{
		int opponent;
		if (playerTurn == 0)
			opponent = 1;
		else
			opponent = 0;
		return playerSeedCount(opponent);
	}

	private int playerSeedCount(int player)
	{
		int seeds = 0, startingHouse;
		if (player == 0)
			startingHouse = 0;
		else
			startingHouse = NUMBER_OF_HOUSES / 2;
		for (int i = startingHouse; i < startingHouse + (NUMBER_OF_HOUSES / 2); i++)
		{
			seeds += houses[i].getSeedCount();
		}
		return seeds;
	}

	/**
	 * Returns a string representation of the game
	 *
	 * @return A string representation of the game.
	 */
	public String toString()
	{
		String r = "";
		for (int i = (NUMBER_OF_HOUSES / 2) - 1; i >= 0; i--)
		{
			int seedCount = houses[i].getSeedCount();
			if (seedCount < 10)
				r += " ";
			r += seedCount;
			if (i > 0)
				r += "|";
		}
		r += "\n";
		for (int i = NUMBER_OF_HOUSES / 2; i < NUMBER_OF_HOUSES; i++)
		{
			int seedCount = houses[i].getSeedCount();
			if (seedCount < 10)
				r += " ";
			r += seedCount;
			if (i < NUMBER_OF_HOUSES - 1)
				r += "|";
		}
		r += "\nPlayer 0 Score: " + score[0] + ", Player 1 Score: " + score[1] + ", current turn: " + playerTurn;
		return r;
	}

	/**
	 * Get the current player turn.
	 *
	 * @return the number of the player whose turn it currently is.
	 */
	public int getPlayerTurn() {
		return playerTurn;
	}
}
