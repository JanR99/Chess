package model;

import java.awt.*;

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
            if (!board.isEmpty(row, col)) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }

        return true;
    }

    public abstract boolean isValidMove(Board board, Move move);
}
