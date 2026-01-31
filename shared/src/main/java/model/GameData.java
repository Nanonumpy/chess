package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    GameData addPlayer(ChessGame.TeamColor teamColor, String username){
        if(teamColor == ChessGame.TeamColor.WHITE){
            return new GameData(gameID, username, blackUsername, gameName, game);

        }
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }
}
