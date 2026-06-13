package pieces;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.*;

public class Queen extends Piece {

    public Queen(Position position, Color color) {
        super(position, color);
    }

    public Queen(Piece piece) {
        super(piece);
    }

    @Override
    public boolean isValidMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
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
