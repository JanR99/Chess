package model;

import java.awt.*;

public abstract class Piece {

    private Position position;
    private final Color color;

    public Piece(Position position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    protected Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract boolean isValidMove(Board board, Position from, Position to);
}
