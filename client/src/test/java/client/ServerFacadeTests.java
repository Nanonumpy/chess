package client;

import chess.ChessGame;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost", port);
    }

    @BeforeEach
    void clear() {
        facade.clear();
        authToken = facade.register(new UserData("player1", "password", "p1@email.com")).authToken();
        facade.createGame(authToken, new CreateGameRequest("Game 1"));
    }

    @Test
    @DisplayName("Register")
    public void register(){
        LoginResult res = facade.register(new UserData("player2", "password", "p1@email.com"));
        assertTrue(res.authToken().length() > 10);
    }

    @Test
    @DisplayName("Register Bad")
    public void registerBad(){
        assertThrows(RuntimeException.class, () ->
                facade.register(new UserData("player1", "password", null))
        );

        assertThrows(RuntimeException.class, () ->
                facade.register(new UserData("player1", "password2", "p2@email.com"))
        );
    }

    @Test
    @DisplayName("Login")
    public void login(){
        LoginResult res = facade.login(new LoginRequest("player1", "password"));
        assertTrue(res.authToken().length() > 10);
    }

    @Test
    @DisplayName("Login Bad")
    public void loginBad(){
        assertThrows(RuntimeException.class, () ->
                facade.login(new LoginRequest("player2", "password"))
        );

        assertThrows(RuntimeException.class, () ->
                facade.login(new LoginRequest("player1", "wrong"))
        );
    }

    @Test
    @DisplayName("Logout")
    public void logout(){
        assertDoesNotThrow(() ->
                facade.logout(authToken)
        );
    }

    @Test
    @DisplayName("Logout Bad")
    public void logoutBad(){
        assertThrows(RuntimeException.class, () ->
                facade.logout("bad token")
        );
    }

    @Test
    @DisplayName("Create Game")
    public void createGame(){
        CreateGameResult res = facade.createGame(authToken, new CreateGameRequest("Game 2"));
        assertEquals(2,res.gameID()); // Revisit id generation
    }

    @Test
    @DisplayName("Create Game Bad")
    public void createGameBad(){
        assertThrows(RuntimeException.class, () ->
                facade.createGame("bad token", new CreateGameRequest("Game 2"))
        );
    }

    @Test
    @DisplayName("List Games")
    public void listGames(){
        ListGamesResult res = facade.listGames(authToken);
        assertEquals(1, res.games().length);
    }

    @Test
    @DisplayName("List Games Bad")
    public void listGamesBad(){
        assertThrows(RuntimeException.class, () ->
                facade.listGames("bad token")
        );
    }

    @Test
    @DisplayName("Play Game")
    public void playGame(){
        assertDoesNotThrow(() ->
            facade.playGame(authToken, new JoinGameRequest(ChessGame.TeamColor.WHITE, 1))
        );
    }

    @Test
    @DisplayName("Play Game Bad")
    public void playGameBad(){

        assertThrows(RuntimeException.class, () ->
                facade.playGame("Bad token", new JoinGameRequest(ChessGame.TeamColor.WHITE, 1))
        );
        facade.playGame(authToken, new JoinGameRequest(ChessGame.TeamColor.WHITE, 1));
        assertThrows(RuntimeException.class, () ->
                facade.playGame(authToken, new JoinGameRequest(ChessGame.TeamColor.WHITE, 1))
        );


    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

}
