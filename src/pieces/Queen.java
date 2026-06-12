package pieces;

import model.Board;
import model.Piece;
import model.Position;

import java.awt.*;

public class Queen extends Piece {

    public Queen(Position position, Color color) {
        super(position, color);
    }

    @Override
    public boolean isValidMove(Board board, Position from, Position to) {
        int dx = Math.abs(to.getCol() - from.getCol());
        int dy = Math.abs(to.getRow() - from.getRow());

        boolean rookMove = from.getRow() == to.getRow() || from.getCol() == to.getCol();
        boolean bishopMove = dx == dy;
        if (!rookMove && !bishopMove) {
            return false;
        }
        return super.checkRookFields(board, from, to);
    }
}
