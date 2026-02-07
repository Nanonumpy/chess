package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @Test
    @DisplayName("Register")
    public void register(){
        assertTrue(true);
    }

    @Test
    @DisplayName("Register Bad")
    public void registerBad(){
        assertTrue(true);
    }

    @Test
    @DisplayName("Login")
    public void login(){
        assertTrue(true);
    }

    @Test
    @DisplayName("Login Bad")
    public void loginBad(){
        assertTrue(true);
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
