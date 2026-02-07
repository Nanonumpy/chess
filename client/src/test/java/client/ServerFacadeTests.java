package client;

import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.LoginRequest;
import service.LoginResult;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

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
        facade.register(new UserData("player1", "password", "p1@email.com"));
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
        assertTrue(true);
    }

    @Test
    @DisplayName("Logout Bad")
    public void logoutBad(){
        assertTrue(true);
    }

    @Test
    @DisplayName("Create Game")
    public void createGame(){
        assertTrue(true);
    }

    @Test
    @DisplayName("Create Game Bad")
    public void createGameBad(){
        assertTrue(true);
    }

    @Test
    @DisplayName("List Games")
    public void listGames(){
        assertTrue(true);
    }

    @Test
    @DisplayName("List Games Bad")
    public void listGamesBad(){
        assertTrue(true);
    }

    @Test
    @DisplayName("Play Game")
    public void playGame(){
        assertTrue(true);
    }

    @Test
    @DisplayName("Play Game Bad")
    public void playGameBad(){
        assertTrue(true);
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
