package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;

import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {
    private GameDAO gameDAO;
    private GameService gameService;

    @BeforeEach
    public void setup() throws UnauthorizedException {
        gameDAO = new MemoryGameDAO();
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
    @DisplayName("Create Game With Bad Token")
    public void generateAuth() {
        assertThrows(UnauthorizedException.class, () ->
                gameService.createGame("token4", "badGame")
        );
    }
}