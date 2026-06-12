package pieces;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.*;

public class King extends Piece {

    public King(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        int dx = Math.abs(to.getCol() - from.getCol());
        int dy = Math.abs(to.getRow() - from.getRow());

        return dx <= 1 && dy <= 1 && (dx != 0 || dy != 0);
    }
}
