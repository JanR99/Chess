package graphics;

import de.gurkenlabs.litiengine.graphics.IRenderable;
import game.ChessGame;
import model.Board;
import model.Piece;
import model.Position;
import pieces.*;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ChessBoardRenderer implements IRenderable {

    private final Board board;
    private final ChessGame game;
    public static final int TILE_SIZE = 80;
    private static final int BOARD_PX = 8 * TILE_SIZE;

    private static final Color LIGHT_SQUARE   = new Color(238, 216, 192);
    private static final Color DARK_SQUARE    = new Color(150, 111,  87);
    private static final Color SELECTED_TINT  = new Color(255, 214,  10, 130);
    private static final Color PANEL_BG       = new Color(24, 22, 20, 210);
    private static final Color PANEL_BORDER   = new Color(255, 255, 255, 30);
    private static final Color GOLD           = new Color(240, 190,  80);
    private static final Color CHECK_RED      = new Color(220,  70,  70);
    private static final Color MOVE_HINT      = new Color(255, 205, 0, 180);
    private static final Color CAPTURE_HINT   = new Color(255, 205, 0, 200);

    public ChessBoardRenderer(Board board, ChessGame game) {
        this.board = board;
        this.game  = game;
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,    RenderingHints.VALUE_STROKE_PURE);

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

    private void drawBoard(Graphics2D g) {
        Position selected = game.selectedPosition;
        List<Position> legalMoves = game.getLegalMoves(selected);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? LIGHT_SQUARE : DARK_SQUARE);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Highlight selected square
                if (selected != null && selected.getRow() == row && selected.getCol() == col) {
                    // Soft glow fill + crisp border so the selection reads clearly on both square colors
                    g.setColor(SELECTED_TINT);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(new Color(255, 200, 0));
                    g.setStroke(new BasicStroke(3f));
                    g.drawRect(col * TILE_SIZE + 1, row * TILE_SIZE + 1, TILE_SIZE - 3, TILE_SIZE - 3);
                    g.setStroke(new BasicStroke(1f));
                }
            }
        }

        drawMoveHints(g, legalMoves);

        // Thin outer frame around the whole board for a "mounted" look
        g.setColor(new Color(40, 30, 22));
        g.setStroke(new BasicStroke(3f));
        g.drawRect(1, 1, BOARD_PX - 2, BOARD_PX - 2);
        g.setStroke(new BasicStroke(1f));

        // Rank/file labels, tucked into square corners
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        for (int i = 0; i < 8; i++) {
            // files along the bottom row
            boolean bottomLight = (7 + i) % 2 == 0;
            g.setColor(bottomLight ? DARK_SQUARE : LIGHT_SQUARE);
            g.drawString(String.valueOf((char) ('a' + i)),
                    i * TILE_SIZE + TILE_SIZE - 12, BOARD_PX - 6);

            // ranks along the left column
            boolean leftLight = (i) % 2 == 0;
            g.setColor(leftLight ? DARK_SQUARE : LIGHT_SQUARE);
            g.drawString(String.valueOf(8 - i), 6, i * TILE_SIZE + 15);
        }
    }

    private void drawMoveHints(Graphics2D g, List<Position> legalMoves) {
        for (Position pos : legalMoves) {
            int x = pos.getCol() * TILE_SIZE;
            int y = pos.getRow() * TILE_SIZE;
            boolean isCapture = board.getPiece(pos) != null;

            if (isCapture) {
                // Ring around the edge of the square, chess.com-style, so the target piece stays visible
                int inset = 6;
                g.setColor(CAPTURE_HINT);
                g.setStroke(new BasicStroke(4f));
                g.drawOval(x + inset, y + inset, TILE_SIZE - inset * 2, TILE_SIZE - inset * 2);
                g.setStroke(new BasicStroke(1f));
            } else {
                // Small filled dot centered on empty target squares
                int dotSize = 22;
                int dotX = x + (TILE_SIZE - dotSize) / 2;
                int dotY = y + (TILE_SIZE - dotSize) / 2;
                g.setColor(MOVE_HINT);
                g.fillOval(dotX, dotY, dotSize, dotSize);
            }
        }
    }

    private void drawPieces(Graphics2D g) {
        Font pieceFont = new Font("Serif", Font.PLAIN, 58);
        g.setFont(pieceFont);
        FontMetrics fm = g.getFontMetrics();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece == null) continue;

                String symbol = getUnicodeSymbol(piece);
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;
                int textX = x + (TILE_SIZE - fm.stringWidth(symbol)) / 2;
                int textY = y + (TILE_SIZE + fm.getAscent() - fm.getDescent()) / 2;

                boolean isWhite = piece.getColor().equals(Color.WHITE);

                // Soft drop shadow for depth
                g.setColor(new Color(0, 0, 0, 70));
                g.drawString(symbol, textX + 2, textY + 3);

                // Piece body
                g.setColor(isWhite ? new Color(250, 250, 250) : new Color(25, 25, 25));
                g.drawString(symbol, textX, textY);

                // Thin contrasting outline pass for white pieces so they don't wash out on light squares
                if (isWhite) {
                    g.setColor(new Color(40, 40, 40, 90));
                    g.drawString(symbol, textX + 1, textY);
                }
            }
        }
    }

    private void drawPromotionChooser(Graphics2D g, Position pawnPos, Color color) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, BOARD_PX, BOARD_PX);

        List<? extends Piece> choices = Piece.getPromotableClasses(pawnPos, color);
        int col = pawnPos.getCol();
        int pawnRow = pawnPos.getRow();
        boolean downward = pawnRow == Position.FIRST_ROW;

        // Backing panel behind the choice tiles
        int panelX = col * TILE_SIZE - 6;
        int panelTop = downward ? -6 : (pawnRow - choices.size() + 1) * TILE_SIZE - 6;
        int panelH = choices.size() * TILE_SIZE + 12;
        g.setColor(PANEL_BG);
        g.fill(new RoundRectangle2D.Float(panelX, panelTop, TILE_SIZE + 12, panelH, 14, 14));
        g.setColor(PANEL_BORDER);
        g.draw(new RoundRectangle2D.Float(panelX, panelTop, TILE_SIZE + 12, panelH, 14, 14));

        for (int i = 0; i < choices.size(); i++) {
            int row = downward ? pawnRow + i : pawnRow - i;
            int x = col * TILE_SIZE;
            int y = row * TILE_SIZE;

            g.setColor(new Color(252, 250, 246));
            g.fill(new RoundRectangle2D.Float(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8, 10, 10));
            g.setColor(GOLD);
            g.setStroke(new BasicStroke(2f));
            g.draw(new RoundRectangle2D.Float(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8, 10, 10));
            g.setStroke(new BasicStroke(1f));

            String symbol = getUnicodeSymbol(choices.get(i));
            g.setFont(new Font("Serif", Font.PLAIN, 44));
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (TILE_SIZE - fm.stringWidth(symbol)) / 2;
            int textY = y + (TILE_SIZE + fm.getAscent() - fm.getDescent()) / 2;

            g.setColor(new Color(35, 30, 25));
            g.drawString(symbol, textX, textY);
        }

        // Caption above/below the picker
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(Color.WHITE);
        String caption = "Choose promotion";
        FontMetrics cfm = g.getFontMetrics();
        int capY = downward ? panelTop - 6 : panelTop + panelH + 16;
        g.drawString(caption, panelX + (TILE_SIZE + 12 - cfm.stringWidth(caption)) / 2, capY);
    }

    private void drawWinScreen(Graphics2D g) {
        String winner = game.currentColorsTurn.equals(Color.WHITE) ? "White" : "Black";

        // Dim + subtle vertical gradient for polish
        GradientPaint fade = new GradientPaint(
                0, 0, new Color(0, 0, 0, 140),
                0, BOARD_PX, new Color(0, 0, 0, 190));
        Paint oldPaint = g.getPaint();
        g.setPaint(fade);
        g.fillRect(0, 0, BOARD_PX, BOARD_PX);
        g.setPaint(oldPaint);

        // Card behind the text
        int cardW = 320, cardH = 130;
        int cardX = (BOARD_PX - cardW) / 2;
        int cardY = (BOARD_PX - cardH) / 2;
        g.setColor(PANEL_BG);
        g.fill(new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 18, 18));
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 18, 18));
        g.setStroke(new BasicStroke(1f));

        g.setColor(GOLD);
        g.setFont(new Font("SansSerif", Font.BOLD, 34));
        FontMetrics fm = g.getFontMetrics();
        String line1 = winner + " wins!";
        g.drawString(line1, (BOARD_PX - fm.stringWidth(line1)) / 2, cardY + 58);

        g.setColor(new Color(225, 225, 225));
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        fm = g.getFontMetrics();
        String line2 = "Checkmate — game over";
        g.drawString(line2, (BOARD_PX - fm.stringWidth(line2)) / 2, cardY + 86);

        g.setColor(new Color(170, 170, 170));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        String line3 = "Close the window to exit";
        g.drawString(line3, (BOARD_PX - fm.stringWidth(line3)) / 2, cardY + 108);
    }

    private void drawTurnIndicator(Graphics2D g) {
        boolean whiteTurn = game.currentColorsTurn.equals(Color.WHITE);
        boolean inCheck = board.isCheck(game.currentColorsTurn);

        String label = (whiteTurn ? "White" : "Black") + " to move" + (inCheck ? "  •  CHECK" : "");

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(label);
        int pillW = textW + 46;
        int pillH = 28;
        int pillX = 6;
        int pillY = BOARD_PX + 6;

        g.setColor(inCheck ? new Color(60, 20, 20, 220) : PANEL_BG);
        g.fill(new RoundRectangle2D.Float(pillX, pillY, pillW, pillH, 8, 8));
        if (inCheck) {
            g.setColor(CHECK_RED);
            g.setStroke(new BasicStroke(1.5f));
            g.draw(new RoundRectangle2D.Float(pillX, pillY, pillW, pillH, 8, 8));
            g.setStroke(new BasicStroke(1f));
        }

        // Color swatch showing whose turn it is
        int dotSize = 14;
        int dotX = pillX + 10;
        int dotY = pillY + (pillH - dotSize) / 2;
        g.setColor(whiteTurn ? Color.WHITE : new Color(30, 30, 30));
        g.fillOval(dotX, dotY, dotSize, dotSize);
        g.setColor(new Color(255, 255, 255, 120));
        g.drawOval(dotX, dotY, dotSize, dotSize);

        g.setColor(inCheck ? new Color(255, 210, 210) : Color.WHITE);
        g.drawString(label, dotX + dotSize + 8, pillY + pillH - 9);
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