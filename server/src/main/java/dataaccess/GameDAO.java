package dataaccess;

import model.GameData;

public interface GameDAO {

    void clear() throws DataAccessException;

    void createGame(GameData data) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData data) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;
}
