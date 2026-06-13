package graphics;

import de.gurkenlabs.litiengine.graphics.IRenderable;
import model.Board;
import model.Piece;
import model.Position;
import pieces.*;

import java.awt.*;

public class ChessBoardRenderer implements IRenderable {

    private final Board board;
    private static final int TILE_SIZE = 80;

    public ChessBoardRenderer(Board board) {
        this.board = board;
    }

    @Override
    public void render(Graphics2D g) {
        drawBoard(g);
        drawPieces(g);
    }

    private void drawBoard(Graphics2D g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? Color.WHITE : Color.DARK_GRAY);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawPieces(Graphics2D g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece == null) continue;
                g.setColor(piece.getColor());
                g.drawString(getSymbol(piece), col * TILE_SIZE + 30, row * TILE_SIZE + 45);
            }
        }
    }

    private String getSymbol(Piece piece) {
        if (piece instanceof Pawn) return "P";
        if (piece instanceof Rook) return "R";
        if (piece instanceof Knight) return "N";
        if (piece instanceof Bishop) return "B";
        if (piece instanceof Queen) return "Q";
        if (piece instanceof King) return "K";
        return "?";
    }
}
