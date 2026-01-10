package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        return validMoves;
    }
}
