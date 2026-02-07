package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {
    private AuthDAO authDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
        authDAO.clear();
        authDAO.createAuth(new AuthData("token1", "test1"));
        authDAO.createAuth(new AuthData("token2", "test2"));
    }

    @Test
    @DisplayName("Create Auth Successfully")
    public void createAuth() throws DataAccessException {
        AuthData authData = new AuthData("token3", "test3");
        authDAO.createAuth(authData);


        Assertions.assertNotNull(authDAO.getAuth(authData.authToken()), "Auth Data not stored");
    }

    @Test
    @DisplayName("Create Existing Auth")
    public void createExistingAuth() throws DataAccessException {
        AuthData existingAuth = authDAO.getAuth("token2");
        assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(existingAuth)
        );
    }

    @Test
    @DisplayName("Get Existing Auth")
    public void getAuth() throws DataAccessException {
        assertDoesNotThrow(() ->
                authDAO.getAuth("token2")
        );
        AuthData authData = authDAO.getAuth("token2");

        Assertions.assertEquals("token2", authData.authToken(), "Auth Data not stored");
    }

    @Test
    @DisplayName("Get Auth that doesn't exist")
    public void getMissingAuth() throws DataAccessException {
        assertNull(authDAO.getAuth("token3"));
    }

    @Test
    @DisplayName("Delete Existing Auth")
    public void deleteAuth() throws DataAccessException {
        assertDoesNotThrow(() ->
                authDAO.deleteAuth(authDAO.getAuth("token2"))
        );
        assertNull(authDAO.getAuth("token2"));

    }

    @Test
    @DisplayName("Delete Auth that doesn't exist")
    public void deleteMissingAuth() {
        assertDoesNotThrow(() ->
                authDAO.deleteAuth(new AuthData("bad", "bad"))
        );
    }

    @Test
    @DisplayName("Clear Auths")
    public void clear() throws DataAccessException {
        assertDoesNotThrow(() ->
                authDAO.clear()
        );
        assertNull(authDAO.getAuth("token2"));
    }
}
