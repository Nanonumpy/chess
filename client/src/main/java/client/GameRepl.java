package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GameRepl {

    private final ServerFacade facade;
    private JoinGameRequest joinGameRequest;
    private String authToken;
    private final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, GameData> games = new HashMap<>();
    private GameData gameData;


    public GameRepl(ServerFacade facade){
        this.facade = facade;
    }

    public JoinGameRequest getJoinGameRequest() {
        return joinGameRequest;
    }

    public GameData getGameData(){
        return gameData;
    }

    public void setJoinGameRequest(JoinGameRequest joinGameRequest) {
        this.joinGameRequest = joinGameRequest;
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    public void loop(){

        if(getGameData() == null){redraw();}

        System.out.print("[" + getGameData().gameName() + "] >>> ");
        String input = scanner.nextLine().toLowerCase();

        switch (input) {
            case "help":
                help();
                break;

            case "redraw":
                redraw();
                break;

            case "leave":
                setGameData(null);
                setJoinGameRequest(null);
                break;

            case "move":
                makeMove();
                break;

            case "resign":
                resign();
                break;

            case "highlight":
                highlightMoves();
                break;

            default:
                System.out.println("Invalid command!\n");

        }
    }

    public void help(){
        System.out.println("  Redraw - redraw current chess board");
        System.out.println("  Leave - leave the current game");
        System.out.println("  Move - make a chess move");
        System.out.println("  Resign - resign from the current game");
        System.out.println("  Highlight - select a piece to highlight its legal moves");
        System.out.println("  Help - list available commands");
    }

    public void redraw(){
        if (games.isEmpty()) {
            GameData[] gamesList = facade.listGames(authToken).games();
            for (GameData game : gamesList) {
                games.put(game.gameID(), game);
            }
        }
        setGameData(games.get(getJoinGameRequest().gameID()));
        System.out.println("Game: " + getGameData().gameName());
        displayBoard(getGameData().game().getBoard(), getJoinGameRequest().playerColor());
    }

    public void makeMove(){
        System.out.println("Make move");
    }

    public void resign(){
        System.out.println("Resign");
    }

    public void highlightMoves(){
        System.out.println("Highlight");
    }

    public void displayBoard(ChessBoard board, ChessGame.TeamColor color){
        StringBuilder out = new StringBuilder();
        StringBuilder letters;
        int startR;
        int endR;
        int incR;
        int startC;
        int endC;
        int incC;

        if(color == ChessGame.TeamColor.WHITE){
            startR = 8;
            endR = 0;
            incR = -1;
            startC = 1;
            endC = 9;
            incC = 1;
        }

        else if(color == ChessGame.TeamColor.BLACK){
            startR = 1;
            endR = 9;
            incR = 1;
            startC = 8;
            endC = 0;
            incC = -1;
        }

        else{
            throw new RuntimeException("Invalid color!");
        }

        letters = new StringBuilder();
        letters.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(EscapeSequences.EMPTY);
        for (int c = startC; c != endC; c+=incC){
            letters.append(" ").append((char)(96+c)).append(" ");
        }
        letters.append(EscapeSequences.EMPTY);
        letters.append(EscapeSequences.RESET_BG_COLOR + "\n");

        out.append(letters);
        for (int r = startR; r != endR; r+=incR){
            out.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(" ").append(r).append(" ");
            for (int c = startC; c != endC; c+=incC){
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
        System.out.println(out);
    }
}
