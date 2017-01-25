package view;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * Animated interface class to represent an Oware game's state.
 */
public class GameBoard extends StackPane {
	private Canvas gameView;
	private GraphicsContext graphicsContext;
	private BoardInterface boardInterface;
	private ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
	private ArrayList<HouseGroup> houseList = new ArrayList<HouseGroup>();
	private int playerOneSeeds = 0;
	private int playerTwoSeeds = 0;
	private Label winnerLabel;	
	private Label turnLabel;
	private HBox labelPane;
		
	/**
	 * Constructs new GameBoard object with initial option to choose player count,
	 * and game representation being at default (4 seeds in each house).
	 */
	public GameBoard(){
		boardInterface = new BoardInterface();
		gameView = new Canvas(640,400);
		graphicsContext = gameView.getGraphicsContext2D();
		winnerLabel = new Label();
		turnLabel = new Label();
		labelPane = new HBox();

		setupView();
		startLoop();
	}

	/**
	 * Registers an EventHandler for all house buttons in interface.
	 * @param handler The handler to register.
	 */
	public void setHouseHandler(EventHandler<ActionEvent> handler){
		boardInterface.setHouseHandler(handler);
	}	

	/**
	 * Registers an EventHandler for all player number option buttons in interface.
	 * @param handler The handler to register.
	 */
	public void setPlayerOptionHandler(EventHandler<ActionEvent> handler){
		boardInterface.setPlayerOptionHandler(handler);
	}

	/**
	 * Registers an EventHandler for all game option buttons in interface.
	 * @param handler The handler to register.
	 */
	public void setGameOptionHandler(EventHandler<ActionEvent> handler){
		boardInterface.setGameOptionHandler(handler);
	}	
	
	/**
	 * Check for whether representation is being updated on screen to match game state changes. 
	 * @return boolean flag for whether game changes are being drawn, true if yes, false otherwise.
	 */	
	public synchronized boolean isAnimating(){
		return SeedSprite.seedsMoving();
	}

	public void write() {
		System.out.println("Board:");
		for(int i = 0; i < 6; i++) {
			System.out.print(houseList.get(i).getSeedCount()+ " ");
		}
		System.out.print("\n");
		for(int i = 11; i >= 6; i--) {
			System.out.print(houseList.get(i).getSeedCount()+ " ");
		}
		System.out.print("\n");
	}

	/**
	 * Starts updating view to represent a distribution of seeds from a house.
	 * @param houseNo The index of the house that was distributed.
	 */
	public synchronized void sow(int houseNo){
		ArrayList<SeedSprite> seedsToDistribute = houseList.get(houseNo).removeSeeds();
		int skipOffset = -1;
		for(int i = 0; i < seedsToDistribute.size(); ++i){
			if(i % 11 == 0) ++skipOffset; 
			int receiverIndx = houseNo-i-1-skipOffset;
			receiverIndx = (receiverIndx%12);
			if(receiverIndx  < 0) receiverIndx+=12;
			HouseGroup receiver = houseList.get(receiverIndx);
			SeedSprite movingSeed = seedsToDistribute.get(i);
			movingSeed.moveTo(receiver.getX(),receiver.getY(), 1);
			receiver.give(movingSeed);
		}

	}
	
	/**
	 * Starts updating view to represent a capturing of seeds from a house.
	 * @param houseNo The index of the house from which seeds were captured.
	 */
	public synchronized void take(int houseNo, boolean ownHouses){
		ArrayList<SeedSprite> seedsToTake = houseList.get(houseNo).removeSeeds();
		for(SeedSprite s : seedsToTake){
			if(houseNo<6 ^ ownHouses){
				double y = Math.floorDiv(playerTwoSeeds, 16);
				s.moveTo(330+((playerTwoSeeds++%16)*15), 180+(20*y), 1);
			}else{
				double y = Math.floorDiv(playerOneSeeds, 16);
				s.moveTo(295-((playerOneSeeds++%16)*15), 180+(20*y), 1);
			}
			
		}
	}
	

	/**
	 * Resets the GameBoard to represent beginning of new game, with player number choice interface.
	 */	
	public void reset(){
		playerOneSeeds = 0;
		playerTwoSeeds = 0;
		houseList.clear();
		for(int i = 0; i < 12; ++i){
			houseList.add(new HouseGroup(i));
			
		}

		spriteList.clear();
		spriteList.add(new Sprite("board.png",640,400,1));

		for(int i = 0; i < 12; ++i){
			for(int j = 0; j < 4; ++j){
				SeedSprite newSeed = new SeedSprite();
				houseList.get(i).give(newSeed);
				spriteList.add(newSeed);
			}
		}

		SeedSprite.resetMoveCounter();
		turnLabel.setText("");
		labelPane.getChildren().clear();
		labelPane.getChildren().add(turnLabel);
		boardInterface.setPlayerChoice();
	}
	
	/**
	 * Switched interface to main game buttons so the game interaction can begin.
	 */	
	public void switchToGame(){
		boardInterface.setGameInterface();
	}	
	
	/**
	 * Changes displayed turn status
	 * @param playerN Number of player whose turn should now be displayed (1 or 2)
	 */	
	public void setTurnLabel(int playerN){
		switch(playerN){
		case 1: 
			turnLabel.setText("Player 1's Turn");
			break;
		case 2:
			turnLabel.setText("Player 2's Turn");
			break;
		default:
			break;
		}
	}	

	/**
	 * Sets the screen to show an end game winner result.
	 * @param winner Value communicating who the winner was. 
	 * Can be 0,1,2 for a draw, player one and player two respectively.
	 */
	public void setWinner(int winner){
		switch(winner){
		case 0:
			winnerLabel.setText("Draw!");
			break;
		case 1:
			winnerLabel.setText("Player One Wins!");
			break;
			
		case 2:
			winnerLabel.setText("Player Two Wins!");
			break;

		default:
			break;
		}
		
		labelPane.getChildren().clear();
		labelPane.getChildren().add(winnerLabel);
	}
	

	
	private void startLoop(){
		new Thread(){{this.setDaemon(true);}
			double updateInterval = 1000000000/60;
			double lastTime = System.nanoTime();
			public void run(){
				while(true){
					try{
					        Thread.sleep(15);
					}
					catch (Exception e){
					        e.printStackTrace();
					}
					double curTime = System.nanoTime();
					double delta = curTime - lastTime;
					if(delta > updateInterval){
						
						update(delta);
						
						Platform.runLater(new Runnable() {
						      @Override public void run() {
						       draw();     
						      }
					    });
					
						lastTime = curTime;
					}
				}
				
			}
			
		}.start();
		
	}
	
	private void draw(){
		for(Sprite s : spriteList){
			s.draw(graphicsContext);
			
		}
	}	
	
	private void update(double delta){
		for(Sprite s : spriteList){
			s.update(delta);
		}

		for(HouseGroup h : houseList){
			
			h.update(delta);
		}
	}	

	private void setupView(){
		this.getChildren().add(gameView);
		this.getChildren().add(labelPane);
		this.getChildren().add(boardInterface); // add interface here
		
		labelPane.setAlignment(Pos.TOP_CENTER);
		turnLabel.setFont(Font.font("Cambria", 50));
		winnerLabel.setFont(Font.font("Cambria", 70));
		winnerLabel.setTextFill(Paint.valueOf("Red"));
		winnerLabel.setEffect(new DropShadow());

		reset();
	}
	
	
}
