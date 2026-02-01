package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;

import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;

    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        assertDoesNotThrow(() -> userService.register(new UserData("user1", "pass", "email")));
    }

    @Test
    @DisplayName("Register User")
    public void register() throws AlreadyTakenException {
        LoginResult loginResult = userService.register(new UserData("user2", "pass", "email"));
        assertEquals("user2", loginResult.username());
    }

    @Test
    @DisplayName("Register Taken User")
    public void registerTaken() {
        assertThrows(AlreadyTakenException.class, () ->
                userService.register(new UserData("user1", "pass", "email"))
        );
    }
}