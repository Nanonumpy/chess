package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserDAO userDAO;
    private UserService userService;
    private String auth;

    @BeforeEach
    public void setup() throws AlreadyTakenException {
        userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        auth = userService.register(new UserData("user1", "pass", "email")).authToken();
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

    @Test
    @DisplayName("Logout")
    public void logout() {
        assertDoesNotThrow(() ->
                userService.logout(auth)
        );
    }

    @Test
    @DisplayName("Logout Bad Token")
    public void logoutBad() {
        assertThrows(UnauthorizedException.class, () ->
                userService.logout("badToken")
        );
    }

    @Test
    @DisplayName("Clear users")
    public void clear() {
        assertDoesNotThrow(() ->
                userService.clear()
        );
        assertNull(userDAO.getUser("user1"));
    }
}