package dataaccess;

import model.GameData;

public class MemoryGameDAO implements GameDAO{
    @Override
    public void createGame(GameData data) {

    }

    @Override
    public GameData getGame(String gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData data) {

    }

    @Override
    public GameData[] listGames() {
        return new GameData[0];
    }
}
