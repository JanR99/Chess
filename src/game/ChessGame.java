package game;

import model.*;
import pieces.King;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;

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

        if (isCastlingMove(piece, move)) {
            moveCastlingRook(move);
        }

        boolean won = board.setPiece(move.getTo(), piece);
        piece.setPosition(move.getTo());
        setAlreadyMoved(piece);
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

    private void setAlreadyMoved(Piece piece) {
        if (piece instanceof King king) {
            king.setAlreadyMoved(true);
        }

        if (piece instanceof Rook rook) {
            rook.setAlreadyMoved(true);
        }
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

    private void moveCastlingRook(Move kingMove) {
        int row = kingMove.getFrom().getRow();
        Position rookFrom;
        Position rookTo;
        if (kingMove.getTo().getCol() > kingMove.getFrom().getCol()) {
            rookFrom = new Position(row, Position.LAST_COLUMN);
            rookTo = new Position(row, kingMove.getTo().getCol() - 1);
        } else {
            rookFrom = new Position(row, Position.FIRST_COLUMN);
            rookTo = new Position(row, kingMove.getTo().getCol() + 1);
        }
        moveRook(new Move(rookFrom, rookTo));
    }

    private void moveRook(Move move) {
        Piece piece = board.getPiece(move.getFrom());

        if (!(piece instanceof Rook rook)) {
            return;
        }

        board.setPiece(move.getTo(), rook);
        board.setPiece(move.getFrom(), null);
        rook.setPosition(move.getTo());
        rook.setAlreadyMoved(true);
    }

    private boolean isCastlingMove(Piece piece, Move move) {
        if (!(piece instanceof King)) {
            return false;
        }
        return move.getFrom().getRow() == move.getTo().getRow()
                && Math.abs(move.getFrom().getCol() - move.getTo().getCol()) == 2;
    }
}
