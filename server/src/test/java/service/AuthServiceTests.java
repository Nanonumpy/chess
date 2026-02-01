package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests {

    private AuthDAO authDAO;
    private AuthService authService;

    @BeforeEach
    public void setup() {
        authDAO = new MemoryAuthDAO();
        authDAO.createAuth(new AuthData("token1", "test1"));
        authDAO.createAuth(new AuthData("token2", "test2"));
        authService = new AuthService(authDAO);
    }

    @Test
    @DisplayName("Generate Auth Successfully")
    public void generateAuth() {
        AuthData authData = authService.generateAuth("test3");


        Assertions.assertNotNull(authDAO.getAuth(authData.authToken()), "Auth Data not stored");
    }

    @Test
    @DisplayName("Generate Existing Auth")
    public void generateExistingAuth() {
        AuthData existingAuth = authDAO.getAuth("token2");
        AuthData authData = authService.generateAuth("test2");

        Assertions.assertNotEquals(existingAuth, authDAO.getAuth(authData.authToken()), "Auth Data not stored");
    }

    @Test
    @DisplayName("Validate Existing Auth")
    public void validateAuth() throws UnauthorizedException {
        assertDoesNotThrow(() ->
            authService.validateAuth("token2")
        );
        AuthData authData = authService.validateAuth("token2");

        Assertions.assertEquals("token2", authData.authToken(), "Auth Data not stored");
    }

    @Test
    @DisplayName("Validate Auth that doesn't exist")
    public void validateMissingAuth() {
        assertThrows(UnauthorizedException.class, () ->
            authService.validateAuth("token3")
        );
    }

    @Test
    @DisplayName("Delete Existing Auth")
    public void deleteAuth() {
        assertDoesNotThrow(() ->
            authService.deleteAuth(authService.validateAuth("token2"))
        );
    }

    @Test
    @DisplayName("Delete Auth that doesn't exist")
    public void deleteMissingAuth() {
        assertDoesNotThrow(() ->
            authService.deleteAuth(new AuthData("bad", "bad"))
        );
    }

    @Test
    @DisplayName("Clear Auths")
    public void clear() {
        assertDoesNotThrow(() ->
            authService.clear()
        );
        assertNull(authDAO.getAuth("token2"));
    }

}