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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP){
            return bishopMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.KING){
            return kingMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.KNIGHT){
            return knightMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.PAWN){
            return pawnMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.QUEEN){
            List<ChessMove> validMoves = new ArrayList<>();
            validMoves.addAll(bishopMoves(board, myPosition));
            validMoves.addAll(rookMoves(board, myPosition));
            return validMoves;
        }

        if (piece.getPieceType() == PieceType.ROOK){
            return rookMoves(board, myPosition);
        }

        return null;
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
     * Finds all valid moves for a bishop
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid bishop moves
     */
    private List<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> validMoves = new ArrayList<>();
        int r;
        int c;
        for (int deltaR = -1; deltaR <= 1; deltaR+=2) {
            for (int deltaC = -1; deltaC <= 1; deltaC+=2) {
                r = myPosition.getRow()+deltaR;
                c = myPosition.getColumn()+deltaC;
                while (r > 0 && r <= 8 && c > 0 && c <= 8) {
                    ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                    if(otherPiece == null) {
                        validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(r, c), null));
                        r += deltaR;
                        c += deltaC;
                    } else if (piece.getTeamColor() != otherPiece.getTeamColor()) {
                        validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(r, c), null));
                        break;
                    } else {
                        break;
                    }

                }
            }
        }
        return validMoves;
    }

    /**
     * Finds all valid moves for a rook
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid rook moves
     */
    private List<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> validMoves = new ArrayList<>();
        int r;
        int c;
        for (int delta = -1; delta <= 1; delta+=2) {
            r = myPosition.getRow()+delta;
            c = myPosition.getColumn();
            while (r > 0 && r <= 8 && c > 0 && c <= 8) {
                ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                if(otherPiece == null) {
                    validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(r, c), null));
                    r += delta;
                } else if (piece.getTeamColor() != otherPiece.getTeamColor()) {
                    validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(r, c), null));
                    break;
                } else {
                    break;
                }
            }

            r = myPosition.getRow();
            c = myPosition.getColumn()+delta;
            while (r > 0 && r <= 8 && c > 0 && c <= 8) {
                ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                if(otherPiece == null) {
                    validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(r, c), null));
                    c += delta;
                } else if (piece.getTeamColor() != otherPiece.getTeamColor()) {
                    validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(r, c), null));
                    break;
                } else {
                    break;
                }
            }
        }
        return validMoves;
    }

    /**
     * Finds all valid moves for a king
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid king moves
     */
    private List<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> validMoves = new ArrayList<>();
        int r;
        int c;
        for (int deltaR = -1; deltaR <= 1; deltaR++) {
            for (int deltaC = -1; deltaC <= 1; deltaC++) {
                r = myPosition.getRow()+deltaR;
                c = myPosition.getColumn()+deltaC;
                if(r <= 0 || r > 8 || c <= 0 || c > 8){continue;}
                ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                if(otherPiece == null || piece.getTeamColor() != otherPiece.getTeamColor()) {
                    validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(r, c), null));
                }
            }
        }
        return validMoves;
    }

    /**
     * Finds all valid moves for a knight
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid knight moves
     */
    private List<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> validMoves = new ArrayList<>();
        int r;
        int c;
        for (int deltaR = -2; deltaR <= 2; deltaR++) {
            if(deltaR == 0){continue;}
            for (int negate=1; negate>=-1; negate-=2) {
                int deltaC = negate*(3 - Math.abs(deltaR));
                r = myPosition.getRow() + deltaR;
                c = myPosition.getColumn() + deltaC;
                if (r <= 0 || r > 8 || c <= 0 || c > 8) {
                    continue;
                }
                ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                if (otherPiece == null || piece.getTeamColor() != otherPiece.getTeamColor()) {
                    validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(r, c), null));
                }
            }
        }
        return validMoves;
    }

    /**
     * Finds all valid moves for a pawn
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid pawn moves
     */
    private List<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> validMoves = new ArrayList<>();
        int dir;
        boolean firstMove;
        boolean canPromote;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            dir = 1;
            firstMove = myPosition.getRow() == 2;
            canPromote = myPosition.getRow() == 7;
        }
        else{
            dir = -1;
            firstMove = myPosition.getRow() == 7;
            canPromote = myPosition.getRow() == 2;
        }
        int r;
        int c;
        for (int deltaC = -1; deltaC <= 1; deltaC++) {
            r = myPosition.getRow() + dir;
            c = myPosition.getColumn() + deltaC;
            if (r <= 0 || r > 8 || c <= 0 || c > 8) {
                continue;
            }
            ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
            if ((deltaC != 0 && otherPiece != null && piece.getTeamColor() != otherPiece.getTeamColor())
                    || (deltaC == 0 && otherPiece == null)) {
                if (canPromote){
                    validMoves.addAll(promotionMoves(myPosition, new ChessPosition(r, c)));
                }
                else {
                    validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(r, c), null));
                }
            }
            if (deltaC == 0 && otherPiece == null && firstMove
                    && board.getPiece(new ChessPosition(r+dir, c)) == null){
                validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                        new ChessPosition(r+dir, c), null));
            }

        }
        return validMoves;
    }

    /**
     * Returns all promotion options for a given promotion move
     *
     * @return Collection of promotion moves
     */
    List<ChessMove> promotionMoves(ChessPosition startPosition, ChessPosition endPosition){
        List<ChessMove> moves = new ArrayList<>();
        for(PieceType promotionType : PieceType.values()){
            if(promotionType == PieceType.KING || promotionType == PieceType.PAWN) {continue;}
            moves.add(new ChessMove(startPosition, endPosition, promotionType));
        }
        return moves;
    }
}
