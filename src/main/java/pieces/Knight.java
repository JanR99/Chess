package pieces;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.*;

public class Knight extends Piece {

    public Knight(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        int dx = Math.abs(from.getCol() - to.getCol());
        int dy = Math.abs(from.getRow() - to.getRow());

        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }
}
