package pieces;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.*;

public class Pawn extends Piece {

    public Pawn(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        int direction = getColor() == Color.WHITE ? -1 : 1;
        int startRow = getColor() == Color.WHITE ? 6 : 1;
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();

        // 1 square
        if (colDiff == 0
                && rowDiff == direction
                && board.isEmpty(
                to.getRow(),
                to.getCol())) {
            return true;
        }

        // 2 squares
        if (colDiff == 0
                && from.getRow() == startRow
                && rowDiff == 2 * direction
                && board.isEmpty(from.getRow() + direction, from.getCol())
                && board.isEmpty(to.getRow(), to.getCol())) {

            return true;
        }

        // diagonal
        if (Math.abs(colDiff) == 1 && rowDiff == direction) {
            Piece target = board.getPiece(to.getRow(), to.getCol());
            return target != null && target.getColor() != getColor();
        }
        return false;
    }
}
