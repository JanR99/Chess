package game;

import model.*;
import pieces.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChessGame {

    protected final Board board;
    private final Player playerBlack;
    private final Player playerWhite;
    public Color currentColorsTurn = Color.WHITE;
    public Color winner = null;
    public Position selectedPosition = null;
    public Position pendingPromotion = null;
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

        // Don't allow a move that leaves your own king in check.
        Piece captured = board.getPiece(move.getTo());
        Position from = move.getFrom();
        Position to = move.getTo();

        board.setPiece(from, null);
        board.setPiece(to, piece);
        piece.setPosition(to);

        boolean leavesKingInCheck = board.isCheck(currentColorsTurn);

        // Undo
        board.setPiece(from, piece);
        board.setPiece(to, captured);
        piece.setPosition(from);

        if (leavesKingInCheck) {
            return false;
        }

        if (isCastlingMove(piece, move)) {
            moveCastlingRook(move);
        }

        board.movePiece(move, piece);

        if (isPromotion(piece)) {
            // Hold off on finishing the turn until the player picks a piece
            pendingPromotion = piece.getPosition();
            return true;
        }

        finalizeMove();
        return true;
    }

    /**
     * Returns every square the piece at {@code from} is currently allowed to move to,
     * used by the renderer to draw move-hint dots. Mirrors the same legality check
     * {@link #makeMove(Move)} uses, minus the turn/ownership guard (the caller already
     * only asks this for the currently selected piece).
     */
    public List<Position> getLegalMoves(Position from) {
        List<Position> moves = new ArrayList<>();
        if (from == null) return moves;

        Piece piece = board.getPiece(from);
        if (piece == null) return moves;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row == from.getRow() && col == from.getCol()) continue;

                Position to = new Position(row, col);
                Piece target = board.getPiece(to);
                if (target != null && target.getColor().equals(piece.getColor())) continue;

                if (piece.isValidMove(board, new Move(from, to))) {
                    moves.add(to);
                }
            }
        }
        return moves;
    }

    public boolean handleSquareClick(Position clicked) {
        if (endGame) return false;

        if (pendingPromotion != null) {
            return handlePromotionClick(clicked);
        }

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
            if (moved) {
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

    private void finalizeMove() {
        rotatePlayersTurn();

        if (board.isCheckMate(currentColorsTurn)) {
            endGame = true;
            winner = currentColorsTurn.equals(Color.WHITE)
                    ? Color.BLACK
                    : Color.WHITE;
        }
    }

    private boolean handlePromotionClick(Position clicked) {
        Piece choice = getPromotionChoiceAt(clicked);
        if (choice == null) {
            // Click missed the picker; keep waiting for a valid choice
            return false;
        }

        board.setPiece(pendingPromotion, choice);
        pendingPromotion = null;
        finalizeMove();

        return !endGame;
    }

    private Piece getPromotionChoiceAt(Position clicked) {
        List<? extends Piece> promotionOrder = Piece.getPromotableClasses(pendingPromotion, currentColorsTurn);

        int col = pendingPromotion.getCol();
        if (clicked.getCol() != col) {
            return null;
        }

        int pawnRow = pendingPromotion.getRow();
        boolean downward = pawnRow == Position.FIRST_ROW;
        int index = downward ? clicked.getRow() - pawnRow : pawnRow - clicked.getRow();

        if (index < 0 || index >= promotionOrder.size()) {
            return null;
        }
        return promotionOrder.get(index);
    }

    private boolean isPromotion(Piece piece) {
        if (!(piece instanceof Pawn)) {
            return false;
        }
        return piece.getColor().equals(Color.WHITE) && piece.getPosition().getRow() == Position.FIRST_ROW
                || piece.getColor().equals(Color.BLACK) && piece.getPosition().getRow() == Position.LAST_ROW;
    }
}
