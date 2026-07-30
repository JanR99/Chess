package model;

import pieces.Bishop;
import pieces.Knight;
import pieces.Queen;
import pieces.Rook;

import java.awt.*;
import java.util.List;

public abstract class Piece {

    private Position position;
    private final Color color;

    public Piece(Position position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Piece(Piece piece) {
        this.position = piece.getPosition();
        this.color = piece.getColor();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public boolean checkRookFields(Board board, Position from, Position to) {
        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());

        int row = from.getRow() + rowStep;
        int col = from.getCol() + colStep;

        while (row != to.getRow() || col != to.getCol()) {
            if (!board.isEmpty(new Position(row, col))) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }

        return true;
    }

    public static List<? extends Piece> getPromotableClasses(Position position, Color color) {
        return List.of(
            new Queen(position, color),
            new Rook(position, color),
            new Bishop(position, color),
            new Knight(position, color)
        );
    }

    public abstract boolean isValidMove(Board board, Move move);
}
