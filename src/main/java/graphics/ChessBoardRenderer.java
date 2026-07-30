package graphics;

import de.gurkenlabs.litiengine.graphics.IRenderable;
import game.ChessGame;
import model.Board;
import model.Piece;
import model.Position;
import pieces.*;

import java.awt.*;
import java.util.List;

public class ChessBoardRenderer implements IRenderable {

    private final Board board;
    private final ChessGame game;
    public static final int TILE_SIZE = 80;

    private static final Color LIGHT_SQUARE  = new Color(240, 217, 181);
    private static final Color DARK_SQUARE   = new Color(181, 136,  99);
    private static final Color HIGHLIGHT     = new Color(255, 255,  0, 120);

    public ChessBoardRenderer(Board board, ChessGame game) {
        this.board = board;
        this.game  = game;
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBoard(g);
        drawPieces(g);

        if (game.endGame) {
            drawWinScreen(g);
        } else if (game.pendingPromotion != null) {
            drawPromotionChooser(g, game.pendingPromotion, game.currentColorsTurn);
        } else {
            drawTurnIndicator(g);
        }
    }

    private void drawPromotionChooser(Graphics2D g, Position pawnPos, Color color) {
        // Dim the board so the picker reads as modal
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, 8 * TILE_SIZE, 8 * TILE_SIZE);

        List<? extends Piece> choices = Piece.getPromotableClasses(pawnPos, color);
        int col = pawnPos.getCol();
        int pawnRow = pawnPos.getRow();
        boolean downward = pawnRow == Position.FIRST_ROW;

        for (int i = 0; i < choices.size(); i++) {
            int row = downward ? pawnRow + i : pawnRow - i;
            int x = col * TILE_SIZE;
            int y = row * TILE_SIZE;

            g.setColor(new Color(250, 250, 250));
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            g.setColor(new Color(90, 90, 90));
            g.drawRect(x, y, TILE_SIZE - 1, TILE_SIZE - 1);

            String symbol = getUnicodeSymbol(choices.get(i));
            g.setFont(new Font("Serif", Font.PLAIN, 48));
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (TILE_SIZE - fm.stringWidth(symbol)) / 2;
            int textY = y + (TILE_SIZE + fm.getAscent()) / 2 - 6;

            g.setColor(new Color(30, 30, 30));
            g.drawString(symbol, textX, textY);
        }
    }

    private void drawWinScreen(Graphics2D g) {
        String winner = game.currentColorsTurn.equals(Color.WHITE) ? "White" : "Black";

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, 8 * TILE_SIZE, 8 * TILE_SIZE);

        // Winner banner
        g.setColor(new Color(255, 215, 0));  // gold
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String line1 = winner + " wins!";
        g.drawString(line1,
                (8 * TILE_SIZE - fm.stringWidth(line1)) / 2,
                (8 * TILE_SIZE) / 2 - 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        String line2 = "Close the window to exit.";
        g.drawString(line2,
                (8 * TILE_SIZE - fm.stringWidth(line2)) / 2,
                (8 * TILE_SIZE) / 2 + 30);
    }

    private void drawBoard(Graphics2D g) {
        Position selected = game.selectedPosition;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? LIGHT_SQUARE : DARK_SQUARE);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Highlight selected square
                if (selected != null && selected.getRow() == row && selected.getCol() == col) {
                    g.setColor(HIGHLIGHT);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Draw rank/file labels
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < 8; i++) {
            g.setColor((i % 2 == 0) ? DARK_SQUARE : LIGHT_SQUARE);
            g.drawString(String.valueOf((char)('a' + i)), i * TILE_SIZE + 4, 8 * TILE_SIZE - 4);
            g.drawString(String.valueOf(8 - i), 4, i * TILE_SIZE + 14);
        }
    }

    private void drawPieces(Graphics2D g) {
        Font pieceFont = new Font("Serif", Font.PLAIN, 56);
        g.setFont(pieceFont);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece == null) continue;

                String symbol = getUnicodeSymbol(piece);
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;

                // Draw a subtle shadow first for contrast on both square colors
                g.setColor(new Color(0, 0, 0, 60));
                g.drawString(symbol, x + 13, y + 60);

                // Draw the piece
                g.setColor(piece.getColor().equals(Color.WHITE)
                        ? new Color(255, 255, 255)
                        : new Color(20, 20, 20));
                g.drawString(symbol, x + 12, y + 59);
            }
        }
    }

    private void drawTurnIndicator(Graphics2D g) {
        String turn = game.currentColorsTurn.equals(Color.WHITE) ? "White to move" : "Black to move";
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(6, 8 * TILE_SIZE + 4, 140, 24, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString(turn, 12, 8 * TILE_SIZE + 21);
    }

    private String getUnicodeSymbol(Piece piece) {
        boolean white = piece.getColor().equals(Color.WHITE);
        return switch (piece) {
            case King _     -> white ? "♔" : "♚";
            case Queen _    -> white ? "♕" : "♛";
            case Rook _     -> white ? "♖" : "♜";
            case Bishop _   -> white ? "♗" : "♝";
            case Knight _   -> white ? "♘" : "♞";
            case Pawn _     -> white ? "♙" : "♟";
            default -> "?";
        };
    }
}