package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;

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

    @Test
    @DisplayName("Login user")
    public void login() throws UnauthorizedException {
        LoginResult loginResult = userService.login(new LoginRequest("user1", "pass"));
        assertEquals("user1", loginResult.username());
    }

    @Test
    @DisplayName("Login nonexistent user")
    public void loginNoUser() {
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("user2", "pass"))
        );
    }

    @Test
    @DisplayName("Login with wrong password")
    public void loginBadPass() {
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("user1", "idk"))
        );
    }
}