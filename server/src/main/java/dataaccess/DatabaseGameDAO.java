package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseGameDAO implements GameDAO{

    public DatabaseGameDAO() throws DataAccessException{
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  game (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(whiteUsername),
              INDEX(blackUsername)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.executeUpdate("TRUNCATE game");
    }

    @Override
    public void createGame(GameData data) throws DataAccessException {
        var statement = "INSERT INTO game (name, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(data.game());
        DatabaseManager.executeUpdate(statement, data.gameName(), data.whiteUsername(), data.blackUsername(), json);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException {
        var statement = "UPDATE game SET name = ?, whiteUsername=?, blackUsername=?, game=? WHERE id = ?";
        String json = new Gson().toJson(data.game());
        DatabaseManager.executeUpdate(statement, data.gameName(), data.whiteUsername(), data.blackUsername(), json, data.gameID());
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result.toArray(new GameData[0]);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String gameName = rs.getString("name");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String json = rs.getString("game");

        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }

}
