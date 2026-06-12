package pieces;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.*;

public class Rook extends Piece {

    public Rook(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        if (from.getRow() != to.getRow() && from.getCol() != to.getCol()) {
            return false;
        }
        return super.checkRookFields(board, from, to);
    }
}
