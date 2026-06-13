package input;

import game.ChessGame;
import graphics.ChessBoardRenderer;
import model.Position;

import java.awt.event.MouseEvent;

public class MouseInputHandler {

    private final ChessGame game;

    public MouseInputHandler(ChessGame game) {
        this.game = game;
    }

    public void onMouseClicked(MouseEvent e) {
        int col = e.getX() / ChessBoardRenderer.TILE_SIZE;
        int row = e.getY() / ChessBoardRenderer.TILE_SIZE;

        // Guard: ignore clicks outside the 8×8 board
        if (col < 0 || col > 7 || row < 0 || row > 7) return;

        game.handleSquareClick(new Position(row, col));
    }
}