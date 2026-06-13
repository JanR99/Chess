package game;

import model.Board;
import model.Move;
import model.Piece;
import model.Position;
import pieces.Pawn;
import pieces.Queen;

import java.awt.*;

public class ChessGame {

    private Board board;

    static void main(String[] args) {
        ChessGame game = initChessGame();
    }

    private ChessGame() {
        this.board = new Board();
    }

    public boolean makeMove(Move move) {
        Piece piece = board.getPiece(move.getFrom().getRow(), move.getFrom().getCol());
        Piece target = board.getPiece(move.getTo().getRow(), move.getTo().getCol());

        if (piece == null || target != null && target.getColor() == piece.getColor()) {
            return false;
        }

        if (!piece.isValidMove(board, move)) {
            return false;
        }

        board.setPiece(move.getTo(), piece);
        transformPawnToQueenIfLastRow(piece);
        return true;
    }

    private void transformPawnToQueenIfLastRow(Piece piece) {
        if (!(piece instanceof Pawn)) {
            return;
        }
        if (piece.getColor().equals(Color.WHITE) && piece.getPosition().getRow() == Position.LAST_ROW
                || piece.getColor().equals(Color.BLACK) && piece.getPosition().getRow() == Position.FIRST_ROW) {
            // TODO let the User decide which Piece
            piece = new Queen(piece);
        }
    }

    private static ChessGame initChessGame() {
        return new ChessGame();
    }
}
