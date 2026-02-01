package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public void createGame(GameData data) {
        games.put(data.gameID(), data);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData data) {
        games.put(data.gameID(), data);
    }

    @Override
    public GameData[] listGames() {
        return games.values().toArray(new GameData[0]);
    }
}
