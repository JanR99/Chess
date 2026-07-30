package game;

import model.*;
import pieces.*;

import java.awt.*;
import java.util.List;

public class ChessGame {

    protected final Board board;
    private final Player playerBlack;
    private final Player playerWhite;
    public Color currentColorsTurn = Color.WHITE;
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

        // TODO This should also disable the player to make a move, while he is in check without removing the check
        // You are not allowed to make that move
        if (!piece.isValidMove(board, move)) {
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
            if (moved && !endGame && pendingPromotion == null) {
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
        if (board.isCheckMate(currentColorsTurn)) {
            endGame = true;
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

        if (!endGame) {
            rotatePlayersTurn();
            return true;
        }
        return false;
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
