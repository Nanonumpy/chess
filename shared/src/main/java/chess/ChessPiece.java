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
        List<ChessMove> validMoves = new ArrayList<>();
        if (piece.getPieceType() == PieceType.BISHOP){
            int r;
            int c;
            for (int delta_r = -1; delta_r <= 1; delta_r+=2) {
                for (int delta_c = -1; delta_c <= 1; delta_c+=2) {
                    r = myPosition.getRow()+delta_r;
                    c = myPosition.getColumn()+delta_c;
                    while (r > 0 && r <= 8 && c > 0 && c <= 8) {
                        ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                        if(otherPiece == null) {
                            validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                    new ChessPosition(r, c), null));
                            r += delta_r;
                            c += delta_c;
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
        }

        if (piece.getPieceType() == PieceType.KING){
            int r;
            int c;
            for (int delta_r = -1; delta_r <= 1; delta_r++) {
                for (int delta_c = -1; delta_c <= 1; delta_c++) {
                    r = myPosition.getRow()+delta_r;
                    c = myPosition.getColumn()+delta_c;
                    if(r <= 0 || r > 8 || c <= 0 || c > 8){continue;}
                    ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                    if(otherPiece == null || piece.getTeamColor() != otherPiece.getTeamColor()) {
                        validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(r, c), null));
                    }
                }
            }
        }

        if (piece.getPieceType() == PieceType.KNIGHT){
            int r;
            int c;
            for (int delta_r = -2; delta_r <= 2; delta_r++) {
                if(delta_r == 0){continue;}
                for (int negate=1; negate>=-1; negate-=2) {
                    int delta_c = negate*(3 - Math.abs(delta_r));
                    r = myPosition.getRow() + delta_r;
                    c = myPosition.getColumn() + delta_c;
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
        }

        if (piece.getPieceType() == PieceType.PAWN){
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
            for (int delta_c = -1; delta_c <= 1; delta_c++) {
                r = myPosition.getRow() + dir;
                c = myPosition.getColumn() + delta_c;
                if (r <= 0 || r > 8 || c <= 0 || c > 8) {
                    continue;
                }
                ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                if (delta_c != 0 && otherPiece != null && piece.getTeamColor() != otherPiece.getTeamColor()) {
                    if (canPromote){
                        for(PieceType promotionType : PieceType.values()){
                            if(promotionType == PieceType.KING || promotionType == PieceType.PAWN) {continue;}
                            validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                    new ChessPosition(r, c), promotionType));
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(r, c), null));
                    }
                }
                else if (delta_c == 0 && otherPiece == null){
                    if (canPromote){
                        for(PieceType promotionType : PieceType.values()){
                            if(promotionType == PieceType.KING || promotionType == PieceType.PAWN) {continue;}
                            validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                    new ChessPosition(r, c), promotionType));
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(r, c), null));
                    }

                    // First move extra space forward check (impossible to promote here)
                    if (firstMove && board.getPiece(new ChessPosition(r+dir, c)) == null){
                        validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(r+dir, c), null));
                    }
                }

            }
        }

        if (piece.getPieceType() == PieceType.QUEEN){
            int r;
            int c;
            for (int delta_r = -1; delta_r <= 1; delta_r++) {
                for (int delta_c = -1; delta_c <= 1; delta_c++) {
                    r = myPosition.getRow()+delta_r;
                    c = myPosition.getColumn()+delta_c;
                    while (r > 0 && r <= 8 && c > 0 && c <= 8) {
                        ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                        if(otherPiece == null) {
                            validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                    new ChessPosition(r, c), null));
                            r += delta_r;
                            c += delta_c;
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
        }

        if (piece.getPieceType() == PieceType.ROOK){
            int r;
            int c;
            for (int delta_r = -1; delta_r <= 1; delta_r++) {
                for (int delta_c = -1; delta_c <= 1; delta_c++) {
                    if (Math.abs(delta_c) + Math.abs(delta_r) != 1){continue;}
                    r = myPosition.getRow()+delta_r;
                    c = myPosition.getColumn()+delta_c;
                    while (r > 0 && r <= 8 && c > 0 && c <= 8) {
                        ChessPiece otherPiece = board.getPiece(new ChessPosition(r, c));
                        if(otherPiece == null) {
                            validMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                    new ChessPosition(r, c), null));
                            r += delta_r;
                            c += delta_c;
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
        }
        return validMoves;
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
}
