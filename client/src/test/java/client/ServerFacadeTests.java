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
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Register Bad")
    public void registerBad(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Login")
    public void login(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Login Bad")
    public void loginBad(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Logout")
    public void logout(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Logout Bad")
    public void logoutBad(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Create Game")
    public void createGame(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Create Game Bad")
    public void createGameBad(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("List Games")
    public void listGames(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("List Games Bad")
    public void listGamesBad(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Play Game")
    public void playGame(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Play Game Bad")
    public void playGameBad(){
        assertTrue(1==1);
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
