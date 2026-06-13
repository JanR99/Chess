package game;

import model.*;
import pieces.Pawn;
import pieces.Queen;

import java.awt.*;

public class ChessGame {

    protected final Board board;
    private final Player playerBlack;
    private final Player playerWhite;
    public Color currentColorsTurn = Color.WHITE;
    public Position selectedPosition = null;
    public boolean endGame = false;

    private ChessGame() {
        this.playerBlack = new Player(Color.BLACK);
        this.playerWhite = new Player(Color.WHITE);
        this.board = new Board();
    }

    protected static ChessGame init() {
        return new ChessGame();
    }

    public boolean makeMove(Move move) {
        Piece piece = board.getPiece(move.getFrom());
        Piece target = board.getPiece(move.getTo());

        // move didn't make sense
        if (piece == null || target != null && target.getColor() == piece.getColor()) {
            return false;
        }

        // You can only move you pieces
        if (!piece.getColor().equals(currentColorsTurn)) {
            return false;
        }

        // You are not allowed to make that move
        if (!piece.isValidMove(board, move)) {
            return false;
        }
        boolean won = board.setPiece(move.getTo(), piece);
        piece.setPosition(move.getTo());
        board.setPiece(move.getFrom(), null);

        if (won) {
            endGame = true;
        }

        transformPawnToQueenIfLastRow(piece);
        return true;
    }

    public boolean handleSquareClick(Position clicked) {
        if (endGame) return false;
        if (selectedPosition == null) {
            // First click: select a piece belonging to the current player
            Piece piece = board.getPiece(clicked);
            if (piece != null && piece.getColor().equals(currentColorsTurn)) {
                selectedPosition = clicked;
            }
        } else {
            // Second click: attempt the move
            Move move = new Move(selectedPosition, clicked);
            selectedPosition = null;  // clear selection regardless of outcome
            boolean moved = makeMove(move);
            if (moved && !endGame) {
                rotatePlayersTurn();
                return true;
            }
            // Re-select if they clicked another own piece
            Piece piece = board.getPiece(clicked);
            if (piece != null && piece.getColor().equals(currentColorsTurn)) {
                selectedPosition = clicked;
            }
        }
        return false;
    }

    private void transformPawnToQueenIfLastRow(Piece piece) {
        if (!(piece instanceof Pawn)) {
            return;
        }
        if (piece.getColor().equals(Color.WHITE) && piece.getPosition().getRow() == Position.LAST_ROW
                || piece.getColor().equals(Color.BLACK) && piece.getPosition().getRow() == Position.FIRST_ROW) {
            // TODO let the User decide which Piece
            board.setPiece(piece.getPosition(), new Queen(piece.getPosition(), piece.getColor()));
        }
    }

    private void rotatePlayersTurn() {
        if (currentColorsTurn.equals(Color.WHITE)) {
            currentColorsTurn = Color.BLACK;
        } else {
            currentColorsTurn = Color.WHITE;
        }
    }
}
