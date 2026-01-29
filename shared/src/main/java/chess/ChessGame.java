package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        setBoard(board);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard board = getBoard();
        ChessPiece curPiece = board.getPiece(startPosition);
        if(curPiece == null) return null;

        List<ChessMove> validMoves = new ArrayList<>();
        for(ChessMove move : curPiece.pieceMoves(board, startPosition)){
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            ChessPiece replacementPiece;
            if(move.getPromotionPiece() == null) replacementPiece = curPiece;
            else replacementPiece = new ChessPiece(curPiece.getTeamColor(), move.getPromotionPiece());

            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), replacementPiece);
            if(!isInCheck(curPiece.getTeamColor())) validMoves.add(move);
            board.addPiece(move.getStartPosition(), curPiece);
            board.addPiece(move.getEndPosition(), capturedPiece);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessBoard board = getBoard();
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if(piece == null){
            throw new InvalidMoveException("No piece at selected square!");
        }
        if (piece.getTeamColor()!= getTeamTurn()){
            throw new InvalidMoveException("Not your turn!");
        }
        if(!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Illegal move!");
        }

        if(move.getPromotionPiece() != null){
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);
        setBoard(board);
        piece.setHasMoved(true);
        if(getTeamTurn() == TeamColor.BLACK) setTeamTurn(TeamColor.WHITE);
        else setTeamTurn(TeamColor.BLACK);
    }

    private ChessPosition getKingPosition(TeamColor teamColor){
        ChessBoard board = getBoard();
        for(int r=1; r<=8; r++){
            for(int c=1 ; c<=8; c++){
                ChessPosition curPos = new ChessPosition(r, c);
                ChessPiece curPiece = board.getPiece(curPos);
                if(curPiece != null && curPiece.getTeamColor() == teamColor
                        && curPiece.getPieceType() == ChessPiece.PieceType.KING){
                    return curPos;
                }
            }
        }
        return null; // should not happen
    }

    private boolean positionInCheck(ChessPosition pos){
        ChessBoard board = getBoard();
        for(int r=1; r<=8; r++){
            for(int c=1 ; c<=8; c++){
                ChessPosition curPos = new ChessPosition(r, c);
                ChessPiece curPiece = board.getPiece(curPos);
                if(curPiece == null) continue;
                for(ChessMove curMove : curPiece.pieceMoves(board, curPos)) {
                    if (curMove.getEndPosition().equals(pos)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return positionInCheck(getKingPosition(teamColor));
    }

    private boolean kingCanMove(TeamColor teamColor){
        ChessPosition kingPos = getKingPosition(teamColor);

        return !validMoves(kingPos).isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor) || kingCanMove(teamColor)) return false;

        for(int r=1; r<=8; r++){
            for(int c=1 ; c<=8; c++){
                ChessPosition curPos = new ChessPosition(r, c);
                ChessPiece curPiece = getBoard().getPiece(curPos);
                if(curPiece == null || curPiece.getTeamColor() != teamColor) continue;
                if(!validMoves(curPos).isEmpty()) return false;
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor) || kingCanMove(teamColor)) return false;

        for(int r=1; r<=8; r++){
            for(int c=1 ; c<=8; c++){
                ChessPosition curPos = new ChessPosition(r, c);
                ChessPiece curPiece = getBoard().getPiece(curPos);
                if(curPiece == null || curPiece.getTeamColor() != teamColor) continue;
                if(!validMoves(curPos).isEmpty()) return false;
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getBoard());
    }
}
