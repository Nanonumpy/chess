package client;

import chess.*;
import model.GameData;
import ui.EscapeSequences;

import java.util.*;

public class GameRepl {

    private final ServerFacade facade;
    private JoinGameRequest joinGameRequest;
    private String authToken;
    private final Scanner scanner;
    private GameData gameData;

    public GameRepl(ServerFacade facade, Scanner scanner){
        this.facade = facade;
        this.scanner = scanner;
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

    public void printLoop() {
        if(!getJoinGameRequest().observe()){
            String msg = "\n"+ (
                    (gameData.game().getTeamTurn() == getJoinGameRequest().playerColor())
                            ? "Your Turn"
                            : "Opponent's Turn");
            System.out.print(msg + " [" + getGameData().gameName() + ": "
                    + getJoinGameRequest().playerColor().toString().toLowerCase() + "] >>> ");
        }
        else{
            System.out.print("\n[" + getGameData().gameName() + ": observer] >>> ");
        }

    }

    public void loop(){
        printLoop();
        String input = scanner.nextLine().toLowerCase();

        switch (input) {
            case "help":
                help();
                break;

            case "redraw":
                redraw(null);
                break;

            case "leave":
                leave();
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
        if(getJoinGameRequest().observe()){
            System.out.println("  Redraw - redraw current chess board");
            System.out.println("  Leave - leave the current game");
            System.out.println("  Highlight - select a piece to highlight its legal moves");
            System.out.println("  Help - list available commands");
        }
        else {
            System.out.println("  Redraw - redraw current chess board");
            System.out.println("  Leave - leave the current game");
            System.out.println("  Move - make a chess move");
            System.out.println("  Resign - resign from the current game");
            System.out.println("  Highlight - select a piece to highlight its legal moves");
            System.out.println("  Help - list available commands");
        }
    }

    public void redraw(ChessPosition highlightPosition){
        displayBoard(getGameData().game().getBoard(), getJoinGameRequest().playerColor(), highlightPosition);
    }

    public void leave(){
        facade.leave(authToken, gameData.gameID());
        setGameData(null);
        setJoinGameRequest(null);
    }

    public void makeMove(){
        if(getJoinGameRequest().observe()){
            System.out.println("Invalid command!\n");
            return;
        }

        try {
            System.out.print("Input piece position (e.g. a4): ");
            String spos = scanner.nextLine().toLowerCase();
            ChessPosition startPosition = validatePos(spos);

            ChessPiece piece = getGameData().game().getBoard().getPiece(startPosition);
            if(piece == null || piece.getTeamColor() != getJoinGameRequest().playerColor()){
                throw new RuntimeException("Invalid piece selected");
            }

            System.out.print("Input move end position (e.g. e2): ");
            String epos = scanner.nextLine().toLowerCase();
            ChessPosition endPosition = validatePos(epos);

            ChessPiece.PieceType promotionPiece = null;
            if(piece.getPieceType() == ChessPiece.PieceType.PAWN
                    && (endPosition.getRow() == 1 || endPosition.getRow() == 8)){
                System.out.print("Enter pawn promotion piece (knight, bishop, rook, or queen): ");
                promotionPiece = ChessPiece.PieceType.valueOf(scanner.nextLine().toUpperCase());
            }

            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
            facade.makeMove(authToken, gameData.gameID(), move);
        }
        catch (RuntimeException e){
            System.out.println("Invalid Move: " + e.getMessage());
        }
    }

    public void resign(){
        if(getJoinGameRequest().observe()){
            System.out.println("Invalid command!\n");
            return;
        }
        try{
            System.out.print("Are you sure? (Y/n): ");
            String ans = scanner.nextLine();

            if(ans.equals("Y")){
                facade.resign(authToken, joinGameRequest.gameID());
            }

        }
        catch (RuntimeException e){
            System.out.println("Invalid Argument!");
        }

    }

    public void highlightMoves(){
        try{
            System.out.print("Input piece position (e.g. a4): ");
            String pos = scanner.nextLine().toLowerCase();
            ChessPosition highlightPosition = validatePos(pos);
            ChessPiece highlightPiece = getGameData().game().getBoard().getPiece(highlightPosition);
            if(highlightPiece == null){
                System.out.println("No valid piece at selected position!\n");
                return;
            }

            redraw(highlightPosition);
        }
        catch (RuntimeException e){
            System.out.println("Invalid Position: " + e.getMessage());
        }


    }

    public ChessPosition validatePos(String pos){
        if(pos.length() != 2){
            throw new RuntimeException("Invalid position format");
        }
        int r = pos.charAt(1)-'0';
        int c = (int)pos.charAt(0)-96;
        if(r < 1 || r > 8 || c < 0 || c > 8){
            throw new RuntimeException("Position out of bounds");
        }

        return new ChessPosition(r, c);


    }

    public void displayBoard(ChessBoard board, ChessGame.TeamColor color, ChessPosition highlightPosition){
        List<ChessPosition> highlightPositions = new ArrayList<>();
        if(highlightPosition != null){
            for(ChessMove move : getGameData().game().validMoves(highlightPosition)){
                highlightPositions.add(move.getEndPosition());
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("\r" + EscapeSequences.ERASE_LINE);
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
                String background;
                ChessPosition curPosition = new ChessPosition(r, c);

                if(curPosition.equals(highlightPosition)){
                    background = EscapeSequences.SET_BG_COLOR_YELLOW;
                }
                else if(highlightPositions.contains(curPosition)){
                    background = ((r+c)%2==1)
                            ? EscapeSequences.SET_BG_COLOR_GREEN : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                }
                else{
                    background = ((r+c)%2==1)
                            ? EscapeSequences.SET_BG_COLOR_LIGHT_BROWN : EscapeSequences.SET_BG_COLOR_DARK_BROWN;
                }

                if(piece == null){
                    out.append(background).append(EscapeSequences.EMPTY);
                }
                else{
                    String chr = background +
                            ((piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                            ? EscapeSequences.SET_TEXT_COLOR_WHITE
                            : EscapeSequences.SET_TEXT_COLOR_BLACK)
                            + switch (piece.getPieceType()) {
                        case ChessPiece.PieceType.BISHOP -> EscapeSequences.BLACK_BISHOP;
                        case ChessPiece.PieceType.KING -> EscapeSequences.BLACK_KING;
                        case ChessPiece.PieceType.KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                        case ChessPiece.PieceType.PAWN -> EscapeSequences.BLACK_PAWN;
                        case ChessPiece.PieceType.QUEEN -> EscapeSequences.BLACK_QUEEN;
                        case ChessPiece.PieceType.ROOK -> EscapeSequences.BLACK_ROOK;
                    } + EscapeSequences.RESET_TEXT_COLOR;

                    out.append(chr);
                }
            }
            out.append(EscapeSequences.SET_BG_COLOR_DARK_GREY).append(" ").append(r).append(" ");
            out.append(EscapeSequences.RESET_BG_COLOR + "\n");
        }
        out.append(letters);
        System.out.print(out);
    }
}
