package view;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

/**
 * Specialised pane containing all button interfaces for the GameBoard view.
 */
public class BoardInterface extends VBox{
	
	private FlowPane housePane;
	private VBox playerChoice;
	private HBox gameOptions;
	
	/**
	 * Constructs new BoardInterface object, with it's state set to display player number interface.
	 */
	public BoardInterface(){
		setupLayouts();
		setPlayerChoice();
	}

	/**
	 * Registers an EventHandler for all house buttons.
	 * @param handler The handler to register.
	 */
	public void setHouseHandler(EventHandler<ActionEvent> handler){
		for(Node hB : housePane.getChildren()){
			((Button) hB).setOnAction(handler);
		}
	}

	/**
	 * Registers an EventHandler for all player selection buttons.
	 * @param handler The handler to register.
	 */
	public void setPlayerOptionHandler(EventHandler<ActionEvent> handler){
		for(Node pO : playerChoice.getChildren()){
			((Button) pO).setOnAction(handler);
		}
	}

	/**
	 * Registers an EventHandler for all game option buttons.
	 * @param handler The handler to register.
	 */
	public void setGameOptionHandler(EventHandler<ActionEvent> handler){
		for(Node gO : gameOptions.getChildren()){
			((Button) gO).setOnAction(handler);
		}
	}
		
	/**
	 * Switched interface to display player count buttons.
	 */
	public void setPlayerChoice(){
		this.setAlignment(Pos.CENTER);
		this.getChildren().clear();
		this.getChildren().add(playerChoice);
	}

	/**
	 * Switched interface to display main game buttons.
	 */
	 public void setGameInterface() {
		this.setAlignment(Pos.TOP_LEFT);
		this.getChildren().clear();
		this.getChildren().addAll(housePane, gameOptions);
	}

	private void setupLayouts(){
		this.setSpacing(25);
		
		housePane = new FlowPane();
		housePane.setAlignment(Pos.CENTER);
		housePane.setVgap(125);
		housePane.setHgap(19);
		housePane.setPrefWrapLength(700);
		housePane.setPadding(new Insets(65,0,10,16));
		for(int i = 0; i < 6; ++i){
			Button newHB = new Button();
			
			newHB.setOpacity(0);
			newHB.setStyle(
			        "-fx-background-radius: 5em; " +
			                "-fx-min-width: 75px; " +
			                "-fx-min-height: 75px; " +
			                "-fx-max-width: 75px; " +
			                "-fx-max-height: 75px; " +
			                "-fx-background-color: -fx-body-color;" +
			                "-fx-background-insets: 0px; " +
			                "-fx-padding: 0px;"
			        );
			newHB.setUserData(i);
	
			housePane.getChildren().add(newHB);
			
		}
		
		for(int i = 11; i > 5; --i){
			Button newHB = new Button();
			
			newHB.setOpacity(0);
			newHB.setUserData(i);
			newHB.setStyle(
			        "-fx-background-radius: 5em; " +
			                "-fx-min-width: 75px; " +
			                "-fx-min-height: 75px; " +
			                "-fx-max-width: 75px; " +
			                "-fx-max-height: 75px; " +
			                "-fx-background-color: -fx-body-color;" +
			                "-fx-background-insets: 0px; " +
			                "-fx-padding: 0px;"
			        );
			housePane.getChildren().add(newHB);
			
		}
		
		playerChoice = new VBox();
		gameOptions = new HBox();
		
		
		Button pvpGame = new Button("Two Player");
		pvpGame.setUserData("PVP");
		Button pvcpuGame = new Button("Single Player");
		pvcpuGame.setUserData("CPU");
		
		playerChoice.getChildren().addAll(pvpGame, pvcpuGame);
		playerChoice.setSpacing(20);
		playerChoice.setAlignment(Pos.CENTER);
		
		Button resetGame = new Button("New Game");
        resetGame.setUserData("Reset");
		gameOptions.getChildren().add(resetGame);

	}

}

