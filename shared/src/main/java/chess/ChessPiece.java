package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public String toString() {
        String out = switch (getPieceType()) {
            case PieceType.BISHOP -> "B";
            case PieceType.KING -> "K";
            case PieceType.KNIGHT -> "N";
            case PieceType.PAWN -> "P";
            case PieceType.QUEEN -> "Q";
            case PieceType.ROOK -> "R";
        };

        if (getTeamColor() == ChessGame.TeamColor.BLACK){
            out = out.toLowerCase();
        }

        return out;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return getTeamColor() == that.getTeamColor() && getPieceType() == that.getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamColor(), getPieceType());
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if(piece.getPieceType() == PieceType.BISHOP){
            return bishopMoves(board, myPosition);
        }

        if(piece.getPieceType() == PieceType.ROOK){
            return rookMoves(board, myPosition);
        }

        if(piece.getPieceType() == PieceType.KNIGHT){
            return knightMoves(board, myPosition);
        }

        if(piece.getPieceType() == PieceType.QUEEN){
            List<ChessMove> validMoves = bishopMoves(board, myPosition);
            validMoves.addAll(rookMoves(board, myPosition));
            return validMoves;
        }

        if(piece.getPieceType() == PieceType.KING){
            return kingMoves(board, myPosition);
        }

        if(piece.getPieceType() == PieceType.PAWN){
            return pawnMoves(board, myPosition);
        }

        return null;
    }

    public boolean checkMove(ChessBoard board, ChessMove move){
        ChessPosition endPosition = move.getEndPosition();
        int r = endPosition.getRow();
        int c = endPosition.getColumn();
        if(r <= 0 || r > 8 || c <= 0 || c > 8) return false;

        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece otherPiece = board.getPiece(endPosition);
        return otherPiece == null || otherPiece.getTeamColor() != piece.getTeamColor();

    }

    public List<ChessMove> moveTillEnd(ChessBoard board, ChessPosition myPosition, int deltaR, int deltaC){
        List<ChessMove> validMoves = new ArrayList<>();
        int r = myPosition.getRow() + deltaR;
        int c = myPosition.getColumn() + deltaC;
        ChessMove move = new ChessMove(myPosition, new ChessPosition(r, c), null);
        while(checkMove(board, move)){
            validMoves.add(move);
            if(board.getPiece(new ChessPosition(r, c)) != null) break;
            r += deltaR;
            c += deltaC;
            move = new ChessMove(myPosition, new ChessPosition(r, c), null);
        }
        return validMoves;
    }

    public List<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        for(int deltaR = -1; deltaR <= 1; deltaR += 2){
            for(int deltaC = -1; deltaC <= 1; deltaC += 2){
                validMoves.addAll(moveTillEnd(board, myPosition, deltaR, deltaC));
            }
        }

        return validMoves;
    }

    public List<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        for(int deltaR = -1; deltaR <= 1; deltaR++){
            for(int deltaC = -1; deltaC <= 1; deltaC ++){
                if(Math.abs(deltaR) + Math.abs(deltaC) != 1) continue;
                validMoves.addAll(moveTillEnd(board, myPosition, deltaR, deltaC));
            }
        }

        return validMoves;
    }

    public List<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        for(int deltaR = -2; deltaR <= 2; deltaR++){
            for(int deltaC = -2; deltaC <= 2; deltaC++){
                if(Math.abs(deltaR) + Math.abs(deltaC) != 3) continue;
                ChessMove move = new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+deltaR,
                        myPosition.getColumn() + deltaC), null);
                if(checkMove(board, move)){
                    validMoves.add(move);
                }
            }
        }

        return validMoves;
    }

    public List<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        for(int deltaR = -1; deltaR <= 1; deltaR++){
            for(int deltaC = -1; deltaC <= 1; deltaC++){
                ChessMove move = new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+deltaR,
                        myPosition.getColumn() + deltaC), null);
                if(checkMove(board, move)){
                    validMoves.add(move);
                }
            }
        }

        return validMoves;
    }

    public List<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        int dir;
        boolean canPromote;
        boolean firstMove;
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            dir = 1;
            canPromote = curRow == 7;
            firstMove = curRow == 2;
        }
        else{
            dir = -1;
            canPromote = curRow == 2;
            firstMove = curRow == 7;
        }

        ChessPosition forwardPosition = new ChessPosition(curRow + dir, curCol);
        ChessPosition doubleForwardPosition = new ChessPosition(curRow + 2*dir, curCol);
        ChessPosition diagLeftPosition = new ChessPosition(curRow + dir, curCol-1);
        ChessPosition diagRightPosition = new ChessPosition(curRow + dir, curCol+1);


        if(board.getPiece(forwardPosition) == null){
            if(!canPromote) {
                validMoves.add(new ChessMove(myPosition, forwardPosition, null));

                if (firstMove && board.getPiece(doubleForwardPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, doubleForwardPosition, null));
                }
            }
            else{
                validMoves.addAll(promoteMoves(myPosition, forwardPosition));
            }

        }
        if(curCol != 1 && board.getPiece(diagLeftPosition) != null && board.getPiece(diagLeftPosition).getTeamColor() != piece.getTeamColor()){
            if(!canPromote) {
                validMoves.add(new ChessMove(myPosition, diagLeftPosition, null));
            }
            else{
                validMoves.addAll(promoteMoves(myPosition, diagLeftPosition));
            }
        }
        if(curCol != 8 && board.getPiece(diagRightPosition) != null && board.getPiece(diagRightPosition).getTeamColor() != piece.getTeamColor()){
            if(!canPromote) {
                validMoves.add(new ChessMove(myPosition, diagRightPosition, null));
            }
            else{
                validMoves.addAll(promoteMoves(myPosition, diagRightPosition));
            }

        }



        return validMoves;
    }

    public List<ChessMove> promoteMoves(ChessPosition startPosition, ChessPosition endPosition) {
        List<ChessMove> validMoves = new ArrayList<>();
        for(PieceType promotion : PieceType.values()){
            if(promotion == PieceType.KING || promotion == PieceType.PAWN) continue;
            validMoves.add(new ChessMove(startPosition, endPosition, promotion));
        }
        return validMoves;
    }
}
