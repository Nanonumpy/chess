package dataaccess;

import model.GameData;

public interface GameDAO {

    void clear();

    void createGame(GameData data);

    GameData getGame(int gameID);

    void updateGame(GameData data);

    GameData[] listGames();
}
