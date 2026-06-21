package pieces;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.*;

public class King extends Piece {

    private boolean alreadyMoved;

    public King(Position position, Color color) {
        super(position, color);
        this.alreadyMoved = false;
    }

    public boolean getAlreadyMoved() {
        return this.alreadyMoved;
    }

    public void setAlreadyMoved(boolean alreadyMoved) {
        this.alreadyMoved = alreadyMoved;
    }

    @Override
    public boolean isValidMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        int dx = Math.abs(to.getCol() - from.getCol());
        int dy = Math.abs(to.getRow() - from.getRow());

        if (!alreadyMoved) {
            boolean moveIsCastling = dy == 0 && dx == 2;
            if (moveIsCastling) {
                Rook rook = getCorrespondingRook(board, move);
                return rook != null && !rook.getAlreadyMoved() && pathClear(board, move);
            }
        }

        return dx <= 1 && dy <= 1 && (dx != 0 || dy != 0);
    }

    private Rook getCorrespondingRook(Board board, Move move) {
        Piece piece;
        if (this.getColor().equals(Color.WHITE)) {
            if (move.getFrom().getCol() < move.getTo().getCol()) {
                piece = board.getPiece(new Position(Position.LAST_ROW, Position.LAST_COLUMN));
            } else {
                piece = board.getPiece(new Position(Position.LAST_ROW, Position.FIRST_COLUMN));
            }
        } else {
            if (move.getFrom().getCol() <  move.getTo().getCol()) {
                piece = board.getPiece(new Position(Position.FIRST_ROW, Position.LAST_COLUMN));
            } else {
                piece = board.getPiece(new Position(Position.FIRST_ROW, Position.FIRST_COLUMN));
            }
        }
        return piece instanceof Rook ? (Rook) piece : null;
    }

    private boolean pathClear(Board board, Move move) {
        int row = move.getFrom().getRow();
        int start = Math.min(move.getFrom().getCol(), move.getTo().getCol());
        int end = Math.max(move.getFrom().getCol(), move.getTo().getCol());

        if (move.getTo().getCol() > move.getFrom().getCol()) {
            end++;
        } else {
            start--;
        }

        for (int col = start + 1; col < end; col++) {
            if (board.getPiece(new Position(row, col)) != null) {
                return false;
            }
        }
        return true;
    }
}
