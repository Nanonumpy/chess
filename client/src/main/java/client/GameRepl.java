package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.ServerFacade;
import service.JoinGameRequest;
import ui.EscapeSequences;

import java.util.HashMap;
import java.util.Map;

public class GameRepl {

    private final ServerFacade facade;
    private JoinGameRequest joinGameRequest;
    private String authToken;
    private final Map<Integer, GameData> games = new HashMap<>();


    public GameRepl(ServerFacade facade){
        this.facade = facade;
    }

    public JoinGameRequest getJoinGameRequest() {
        return joinGameRequest;
    }

    public void setJoinGameRequest(JoinGameRequest joinGameRequest) {
        this.joinGameRequest = joinGameRequest;
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public void loop(){
        if(games.isEmpty()) {
            GameData[] gamesList = facade.listGames(authToken).games();
            for (GameData game : gamesList) {
                games.put(game.gameID(), game);
            }
        }
        GameData curGame = games.get(getJoinGameRequest().gameID());
        System.out.println("Game: " + curGame.gameName());
        displayBoard(curGame.game().getBoard(), getJoinGameRequest().playerColor());

        setJoinGameRequest(null);
    }


    public void displayBoard(ChessBoard board, ChessGame.TeamColor color){
        StringBuilder out = new StringBuilder();
        if(color == ChessGame.TeamColor.WHITE){
            StringBuilder letters = new StringBuilder();
            letters.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(EscapeSequences.EMPTY);
            for (int i = 0; i < 8; i++){
                letters.append(" ").append((char)(97+i)).append(" ");
            }
            letters.append(EscapeSequences.EMPTY);
            letters.append(EscapeSequences.RESET_BG_COLOR + "\n");
            out.append(letters);

            for (int r = 8; r > 0; r--){
                out.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(" ").append(r).append(" ");
                for (int c = 1; c <= 8; c++){
                    ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                    String background = ((r+c)%2==1)
                            ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_BLACK;
                    if(piece == null){
                        out.append(background).append(EscapeSequences.EMPTY);
                    }
                    else{
                        String chr = background + switch (piece.getPieceType()) {
                            case ChessPiece.PieceType.BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                            case ChessPiece.PieceType.KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                            case ChessPiece.PieceType.KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                            case ChessPiece.PieceType.PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                            case ChessPiece.PieceType.QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                            case ChessPiece.PieceType.ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                        };

                        out.append(chr);
                    }
                }
                out.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(" ").append(r).append(" ");
                out.append(EscapeSequences.RESET_BG_COLOR + "\n");
            }
            out.append(letters);
        }

        else if(color == ChessGame.TeamColor.BLACK){
            StringBuilder letters = new StringBuilder();
            letters.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(EscapeSequences.EMPTY);
            for (int i = 7; i >= 0; i--){
                letters.append(" ").append((char)(97+i)).append(" ");
            }
            letters.append(EscapeSequences.EMPTY);
            letters.append(EscapeSequences.RESET_BG_COLOR + "\n");
            out.append(letters);

            for (int r = 1; r <= 8; r++){
                out.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(" ").append(r).append(" ");
                for (int c = 8; c > 0; c--){
                    ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                    String background = ((r+c)%2==1)
                            ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_BLACK;
                    if(piece == null){
                        out.append(background).append(EscapeSequences.EMPTY);
                    }
                    else{
                        String chr = background + switch (piece.getPieceType()) {
                            case ChessPiece.PieceType.BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                            case ChessPiece.PieceType.KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                            case ChessPiece.PieceType.KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                            case ChessPiece.PieceType.PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                            case ChessPiece.PieceType.QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                            case ChessPiece.PieceType.ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                    ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                        };

                        out.append(chr);
                    }
                }
                out.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(" ").append(r).append(" ");
                out.append(EscapeSequences.RESET_BG_COLOR + "\n");
            }
            out.append(letters);
        }


        else{
            throw new RuntimeException("Invalid color!");
        }

        System.out.print(out);
    }
}
