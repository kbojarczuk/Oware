package tests;

import model.Game;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameTest {
    @Test
    public void testConstructor() {
        Game game = new Game(false);
        String[] expectedBoard = {" 4| 4| 4| 4| 4| 4", " 4| 4| 4| 4| 4| 4"};
        String[] gameBoard = boardForGame(game);
        assertEquals("Initializing a game returns a board with 4 seeds on the first row", gameBoard[0], expectedBoard[0]);
        assertEquals("Initializing a game returns a board with 4 seeds on the second row", gameBoard[1], expectedBoard[1]);

        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests

        game.sow(0);
        Game game2 = new Game(game);
        String[] expectedBoard2 = {" 4| 4| 4| 4| 4| 0", " 4| 4| 5| 5| 5| 5"};
        String[] gameBoard2 = boardForGame(game2);

        assertEquals("Copying a game copies the correct seeds on the first row", gameBoard2[0], expectedBoard2[0]);
        assertEquals("Initializing a copies the correct seeds on the last row", gameBoard2[1], expectedBoard2[1]);
    }

    @Test
    public void testSow() {
        Game game = new Game(false);
        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests
        game.sow(0);
        String[] gameBoard = boardForGame(game);
        String[] expectedBoard = {" 4| 4| 4| 4| 4| 0", " 4| 4| 5| 5| 5| 5"};


        assertEquals("We have the correct number of seeds on the first row after sowing house 0", gameBoard[0], expectedBoard[0]);
        assertEquals("We have the correct number of seeds on the second row after sowing house 0", gameBoard[1], expectedBoard[1]);

        game.nextTurn();
        game.sow(7);

        gameBoard = boardForGame(game);
        expectedBoard = new String[]{" 5| 5| 5| 4| 4| 0", " 5| 0| 5| 5| 5| 5"};
        assertEquals("After cloning a game, we have the correct number of seeds on the first row after sowing house 7", gameBoard[0], expectedBoard[0]);
        assertEquals("After cloning a game, we have the correct number of seeds on the second row after sowing house 7", gameBoard[1], expectedBoard[1]);
        assertEquals("After cloning a game, we have the correct player turn", game.getPlayerTurn(), game.getPlayerTurn());
    }

    @Test
    public void testReset() {
        Game game = new Game(false);
        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests
        game.sow(0);

        game.reset();

        String[] expectedBoard = {" 4| 4| 4| 4| 4| 4", " 4| 4| 4| 4| 4| 4"};
        String[] gameBoard = boardForGame(game);
        assertEquals("After resetting a game that had changes we have a board with 4 seeds on the first row", gameBoard[0], expectedBoard[0]);
        assertEquals("After resetting a game that had changes we have a board with 4 seeds on the second row", gameBoard[1], expectedBoard[1]);
    }

    @Test
    public void testCanSow() {
        Game game = new Game(false);
        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests

        assertFalse("I can't sow an opponent's house", game.canSow(8));
        assertTrue("I can sow my house", game.canSow(1));
    }

    @Test
    public void testIsAITurn() {
        Game game = new Game(true);
        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests

        assertFalse("AI is always player 2", game.isAITurn());
        game.nextTurn();
        assertTrue("AI is always player 2", game.isAITurn());

        game = new Game(false);
        assertFalse("In a PVP game there's never an AI", game.isAITurn());
        game.nextTurn();
        assertFalse("In a PVP game there's never an AI", game.isAITurn());
    }

    @Test
    public void nextTurn() {
        Game game = new Game(true);
        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests

        game.nextTurn();
        assertEquals("The player after player 1 is player 2", game.getPlayerTurn(), 1);

        game.nextTurn();
        assertEquals("The player after player 2 is player 1", game.getPlayerTurn(), 0);
    }

    @Test
    public void testCapture() {
        Game game = new Game(false);
        if(game.getPlayerTurn() == 0) game.nextTurn(); // Default player turn is random; This prevents random results for the tests
        // Perform some actions to get to a point where capture is !=0
        game.sow(11); game.nextTurn();
        game.sow(0); game.nextTurn();
        game.sow(10); game.nextTurn();
        game.sow(1); game.nextTurn();
        game.sow(9); game.nextTurn();
        game.sow(2); game.nextTurn();
        game.sow(8);

        ArrayList<Integer> capturedHouse = game.capture();
        ArrayList<Integer> expectedCapturedHouses = new ArrayList<>();
        expectedCapturedHouses.add(0);expectedCapturedHouses.add(1);

        assertEquals("After starting with player 2 with houses sowed 11, 0, 10, 1, 9, 2, 8, Player 2 can capture 2 houses", capturedHouse.size(), 2);
        assertEquals("After starting with player 2 with houses sowed 11, 0, 10, 1, 9, 2, 8, Player 2 can capture houses 0 and 1", capturedHouse, expectedCapturedHouses);
    }

    @Test
    public void testCaptureAll() {
        Game game = new Game(false);
        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests
        game.sow(0);
        ArrayList<Integer> capturedHouse = game.captureAll();
        ArrayList<Integer> expectedCapturedHouses = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            expectedCapturedHouses.add(i);
        }

        assertEquals("Capturing all houses captures all houses that the player owns where seed count is not 0", capturedHouse, expectedCapturedHouses);
    }

    @Test
    public void testNextAIMove() {
        Game game = new Game(true);
        if(game.getPlayerTurn() == 1) game.nextTurn(); // Default player turn is random; This prevents random results for the tests
        game.sow(0);
        game.nextTurn();

        assertEquals("After player 1 sowing house 0, AI will sow the best house(6)", game.nextAIMove(), 6);
        game.sow(6);
        game.nextTurn();

        game.sow(1);
        game.nextTurn();

        assertEquals("After player 1 sowing house 1, AI will sow the best house(7)", game.nextAIMove(), 7);
    }

    @Test
    public void testWinner() {
        Game game = new Game(false);
        int[] moves = {6,0,7,1,6,2,8,3,9,4,10,5,11,0,6,2,8};
        if(game.getPlayerTurn() == 0) game.nextTurn(); // Default player turn is random; This prevents random results for the tests

        for(int i = 0; i < moves.length; i++) {
            game.sow(moves[i]);
            game.capture();
            game.nextTurn();
        }

        assertEquals("After playing a set of moves that leads to player 2, winner() returns the correct winner", game.winner(), 1);
        assertTrue("After playing a set of moves that leads to player 2, hasEnded() returns true", game.hasEnded());
        assertFalse("After playing a set of moves that leads to player 2, hasDrawn() returns false", game.hasDrawn());
        assertFalse("After playing a set of moves that leads to player 2, hasWon() returns false for player 1", game.hasWon());
        game.nextTurn();
        assertTrue("After playing a set of moves that leads to player 2, hasWon() returns false for player 2", game.hasWon());
    }

    private String[] boardForGame(Game game) {
        return new String[]{game.toString().split("\n")[0], game.toString().split("\n")[1]};
    }
}
