package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDAOTests {
    private UserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new DatabaseUserDAO();
        userDAO.clear();
        userDAO.createUser(new UserData("user1", "pass", "email"));
    }

    @Test
    @DisplayName("Create User")
    public void createUser() throws DataAccessException {
        userDAO.createUser(new UserData("user2", "pass", "email"));
        assertEquals("user2", userDAO.getUser("user2").username());
    }

    @Test
    @DisplayName("Create Taken User")
    public void createTaken() {
        assertThrows(DataAccessException.class, () ->
                userDAO.createUser(new UserData("user1", "pass", "email"))
        );
    }

    @Test
    @DisplayName("Get user")
    public void getUser() throws DataAccessException {
        UserData userData = userDAO.getUser("user1");
        assertEquals("user1", userData.username());
        assertTrue(BCrypt.checkpw("pass", userData.password()));
        assertEquals("email", userData.email());
    }

    @Test
    @DisplayName("Get nonexistent user")
    public void getNoUser() throws DataAccessException {
        assertNull(userDAO.getUser("user2"));
    }

    @Test
    @DisplayName("Clear users")
    public void clear() throws DataAccessException {
        assertDoesNotThrow(() ->
                userDAO.clear()
        );
        assertNull(userDAO.getUser("user1"));
    }
}
