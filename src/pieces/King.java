package pieces;

import model.Board;
import model.Piece;
import model.Position;

import java.awt.*;

public class King extends Piece {

    public King(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Position from, Position to) {
        int dx = Math.abs(to.getCol() - from.getCol());
        int dy = Math.abs(to.getRow() - from.getRow());

        return dx <= 1 && dy <= 1 && (dx != 0 || dy != 0);
    }
}
