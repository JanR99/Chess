package pieces;

import model.Board;
import model.Piece;
import model.Position;

import java.awt.*;

public class Rook extends Piece {

    public Rook(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Position from, Position to) {
        if (from.getRow() != to.getRow() && from.getCol() != to.getCol()) {
            return false;
        }
        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());
        int row = from.getRow() + rowStep;
        int col = from.getCol() + colStep;
        while (row != to.getRow() || col != to.getCol()) {
            if (!board.isEmpty(row, col)) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }
}
