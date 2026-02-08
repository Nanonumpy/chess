package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameDAOTests {
    private GameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO = new DatabaseGameDAO();
        gameDAO.clear();
        gameDAO.createGame("TestGame");
    }

    @Test
    @DisplayName("Create Game")
    public void createGame() throws DataAccessException {
        int gameID = gameDAO.createGame("NewGame");


        assertEquals(2, gameID);
    }

    @Test
    @DisplayName("Get Game")
    public void getGame() throws DataAccessException {
        assertDoesNotThrow(() ->
                gameDAO.getGame(1)
        );
        GameData gameData = gameDAO.getGame(1);


        assertEquals(1, gameData.gameID());
    }

    @Test
    @DisplayName("Get Game That does not exist")
    public void getBadGame() throws DataAccessException {
        assertDoesNotThrow(() ->
                gameDAO.getGame(2)
        );
        assertNull(gameDAO.getGame(2));

    }

    @Test
    @DisplayName("Join Game")
    public void updateGame(){
        assertDoesNotThrow(() ->
                gameDAO.updateGame(new GameData(1, "Player1", "Player2", "TestGame", new ChessGame()))
        );
    }

    @Test
    @DisplayName("Make Move")
    public void updateMove() throws DataAccessException, InvalidMoveException {
        ChessGame game = gameDAO.getGame(1).game();
        game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1), null));
        assertNotEquals(game, gameDAO.getGame(1).game());
        gameDAO.updateGame(new GameData(1, "Player1", "Player2", "TestGame", game));
        assertEquals(game, gameDAO.getGame(1).game());
    }

    @Test
    @DisplayName("Update game that doesn't exist")
    public void updateBadGame() throws DataAccessException {
        gameDAO.updateGame(new GameData(3, "Player1", "Player2", "BadGame", new ChessGame()));
        assertNull(gameDAO.getGame(3));
    }

    @Test
    @DisplayName("List Games")
    public void listGames() throws DataAccessException {
        GameData[] games = gameDAO.listGames();

        assertEquals(1, games.length);
    }

    @Test
    @DisplayName("Clear games")
    public void clear() throws DataAccessException {
        assertDoesNotThrow(() ->
                gameDAO.clear()
        );
        assertEquals(0, gameDAO.listGames().length);
    }
}
