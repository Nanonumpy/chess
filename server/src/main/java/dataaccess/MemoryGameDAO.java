package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int createGame(String gameName) {
        games.put(nextID, new GameData(nextID, null, null, gameName, new ChessGame()));
        return nextID++;
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
