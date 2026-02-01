package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import passoff.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;

    @BeforeEach
    public void setup() {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        authDAO.createAuth(new AuthData("token1", "user1"));
        authDAO.createAuth(new AuthData("token2", "user2"));
        authDAO.createAuth(new AuthData("token3", "user3"));
        gameService = new GameService(gameDAO, authDAO);
        assertDoesNotThrow(() ->
                gameService.createGame("token1", "testGame")
        );
    }

    @Test
    @DisplayName("Create Game")
    public void createGame() throws UnauthorizedException {
        int gameID = gameService.createGame("token1", "newGame").gameID();

        assertEquals(2, gameID);
    }

    @Test
    @DisplayName("Create Game With Bad Token")
    public void createBadGame() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.createGame("token4", "badGame")
        );
    }

    @Test
    @DisplayName("Join Game")
    public void joinGame() throws UnauthorizedException, DataAccessException, AlreadyTakenException {
        gameService.joinGame("token1", new JoinGameRequest(ChessGame.TeamColor.WHITE, 1));
    }

    @Test
    @DisplayName("Join game that doesn't exist")
    public void joinBadGame() {
        assertThrows(DataAccessException.class, () ->
                gameService.joinGame("token1", new JoinGameRequest(ChessGame.TeamColor.WHITE, 2))
        );
    }

    @Test
    @DisplayName("Attempt to join taken slot in game")
    public void joinTakenGame() throws UnauthorizedException, DataAccessException, AlreadyTakenException {
        gameService.joinGame("token1", new JoinGameRequest(ChessGame.TeamColor.WHITE, 1));
        assertThrows(AlreadyTakenException.class, () ->
                gameService.joinGame("token2", new JoinGameRequest(ChessGame.TeamColor.WHITE, 1))
        );
    }

    @Test
    @DisplayName("List Games")
    public void listGames() throws UnauthorizedException {
        GameData[] games = gameService.listGames("token1").games();

        assertEquals(1, games.length);
    }

    @Test
    @DisplayName("List Games Unauthorized")
    public void listBadGames() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.listGames("token4")
        );
    }

    @Test
    @DisplayName("Clear games")
    public void clear() throws UnauthorizedException {
        assertDoesNotThrow(() ->
                gameService.clear()
        );
        assertEquals(0, gameService.listGames("token2").games().length);
    }
}