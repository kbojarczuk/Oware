package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import model.Game;
import view.GameBoard;

import java.util.ArrayList;


/**
 * Controller for the game
 */
public class GameController{
    /**
     * The view of the game
     */
    private GameBoard gameBoard;
    /**
     * The model of the game
     */
    private Game game;

    /**
     * Builds a new controller
     * @param gameBoard the view that the controller will control
     */
    public GameController(GameBoard gameBoard) {
        this.gameBoard = gameBoard;

        setActionListeners();
    }

    /**
     * Set the three action listeners received from the view
     */
    private void setActionListeners() {
        /**
         * This action listener receives the type of game - CPU / PVP
         */
        gameBoard.setPlayerOptionHandler(event -> {
            Button button = (Button) event.getSource();
            this.game = new Game(button.getUserData().equals("CPU"));
            gameBoard.switchToGame();
            gameBoard.setTurnLabel(game.getPlayerTurn() + 1);
            if (game.isAITurn())
            {
                new Thread()
                {
                    public void run()
                    {
                        sowAndCapture(game.nextAIMove());
                    }
                }.start();
            }
        });

        /**
         * This action listener receives in the userData the houseId of the house clicked. If the game is finished
         * or if animations are performing the action is ignored.
         */
        gameBoard.setHouseHandler(event -> {
            if(gameBoard.isAnimating() || game.hasEnded()) return;

            Object houseUserData = ((Node)event.getSource()).getUserData();
            int houseNumber = (Integer) houseUserData;

            if(game.canSow(houseNumber)) {
                sowAndCapture(houseNumber);
            }
        });

        gameBoard.setGameOptionHandler(event -> {
            Button button = (Button) event.getSource();
            if(button.getUserData().equals("Reset")) {
                game.reset();
                gameBoard.reset();
            }
        });
    }

    /**
     * Sow a house and then capture possible houses after the sow is finished.
     * In addition, if the model returns an action from the AI rerun the method for that action.
     * This method starts a new thread as we need to perform Thread.sleep() in order to wait for the animations to finish
     * before starting a new animation.
     * At the end of capturing and sowing, the method also checks for a winner/ draw.
     * @param houseNumber the house that will be sowed
     */
    private void sowAndCapture(int houseNumber) {
        Thread animThread = new Thread() {
            public void run() {
                sow(houseNumber);

                /**
                 * We need for sow animations before starting the capture animations
                 */
                waitForAnimations();

                capture();

		game.nextTurn();
                Platform.runLater(() -> gameBoard.setTurnLabel(game.getPlayerTurn() + 1));

                if(checkForEndOfGame() != -1) {
                    waitForAnimations();
                    Platform.runLater(() -> gameBoard.setWinner(checkForEndOfGame()));
                }
                else if(!game.canSowAny()) {
                    captureAll();
                    waitForAnimations();
                    if (checkForEndOfGame() == -1)
                    {
                        Platform.runLater(() -> gameBoard.setTurnLabel(game.getPlayerTurn() + 1));
                        game.nextTurn();
                    }
                    else
                        Platform.runLater(() -> gameBoard.setWinner(checkForEndOfGame()));
                } else if (game.isAITurn()) {
                    waitForAnimations();
                    sowAndCapture(game.nextAIMove());
                }

		
            }
        };

        animThread.start();
    }

    /**
     * Helper method to avoid repetitions. Should not be called on Main Thread.
     */
    private void waitForAnimations() {
        while(gameBoard.isAnimating()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper method that returns whether the game has ended
     * @return -1 if the game has not ended, 0 if the game has ended in a draw, 1 or 2 if player 1/2 won
     */
    private int checkForEndOfGame() {
        if(game.hasDrawn()) return 0;
        else if(game.winner() != -1) return (game.winner()+1);
        else return -1;
    }

    /**
     * Sow from a house number
     * @param houseNumber
     */
    private void sow(int houseNumber) {
        gameBoard.sow(houseNumber);
        game.sow(houseNumber);
    }

    /**
     * Capture the houses that the model returns when calling capture()
     */
    private void capture() {
        ArrayList<Integer> capturedHouses = game.capture();
        for(Integer houseId : capturedHouses) {
            gameBoard.take(houseId, false);
        }
    }

    /**
     * Capture the houses the the model returns when calling captureAll()
     */
    private void captureAll() {
        ArrayList<Integer> capturedHouses = game.captureAll();
        for(Integer houseId : capturedHouses) {
            gameBoard.take(houseId, true);
        }
    }

}
