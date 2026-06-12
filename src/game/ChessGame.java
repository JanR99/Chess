package game;

import model.Board;
import model.Move;
import model.Piece;

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

        if (piece.isValidMove(board, move.getFrom(), move.getTo())) {
            // TODO do moving logic here

            return true;
        }
        return false;
    }

    private static ChessGame initChessGame() {
        return new ChessGame();
    }
}
