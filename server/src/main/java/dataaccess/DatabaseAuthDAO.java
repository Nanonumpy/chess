package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO{

    public DatabaseAuthDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(256) NOT NULL,
              `token` varchar(512) NOT NULL,
              PRIMARY KEY (`token`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {
        var statement = "INSERT INTO auth (username, token) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, data.username(), data.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, token FROM auth WHERE token=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("token"),
                                rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData data) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE token=?";
        DatabaseManager.executeUpdate(statement, data.authToken());
    }



}
