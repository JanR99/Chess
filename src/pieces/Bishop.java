package pieces;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.*;

public class Bishop extends Piece {

    public Bishop(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
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
            if (!board.isEmpty(new Position(row, col))) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }
}
