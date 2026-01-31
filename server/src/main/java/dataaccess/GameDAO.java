package dataaccess;

import model.GameData;

public interface GameDAO {
    void createGame(GameData data);

    GameData getGame(String gameID);

    void updateGame(GameData data);

    GameData[] listGames();
}
