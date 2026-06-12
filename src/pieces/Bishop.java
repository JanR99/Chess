package pieces;

import model.Board;
import model.Piece;
import model.Position;

import java.awt.*;

public class Bishop extends Piece {

    public Bishop(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Position from, Position to) {
        int dx = Math.abs(to.getCol() - from.getCol());
        int dy = Math.abs(to.getRow() - from.getRow());

        if (dx != dy) {
            return false;
        }

        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());

        int row = from.getRow() + rowStep;
        int col = from.getCol() + colStep;

        while (row != to.getRow()) {
            if (!board.isEmpty(row, col)) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }
}
