package client;

import chess.ChessGame;

public record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID, boolean observe) {
}
