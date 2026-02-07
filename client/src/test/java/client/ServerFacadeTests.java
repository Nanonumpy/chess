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
    @DisplayName("RegisterBad")
    public void registerBad(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("Login")
    public void login(){
        assertTrue(1==1);
    }

    @Test
    @DisplayName("LoginBad")
    public void loginBad(){
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
