package game;

import model.*;
import pieces.Pawn;
import pieces.Queen;

import java.awt.*;
import java.util.Scanner;

public class ChessGame {

    private Board board;
    private final Player playerBlack;
    private final Player playerWhite;
    private Color currentColorsTurn = Color.WHITE;
    private final Scanner scanner = new Scanner(System.in);

    static void main(String[] args) {
        ChessGame game = initChessGame();
        game.start();
    }

    private ChessGame() {
        this.board = new Board();
        this.playerBlack = new Player(Color.BLACK);
        this.playerWhite = new Player(Color.WHITE);
    }

    private void start() {
        while (true) {
            Move move = getInputMove();
            if (makeMove(move)) {
                System.out.println("Move accepted");
                rotatePlayersTurn();
            } else {
                System.out.println("Illegal move");
            }
        }
    }

    public boolean makeMove(Move move) {
        Piece piece = board.getPiece(move.getFrom());
        Piece target = board.getPiece(move.getTo());

        // move didn't make sense
        if (piece == null || target != null && target.getColor() == piece.getColor()) {
            return false;
        }

        // You can only move you pieces
        if (!piece.getColor().equals(currentColorsTurn)) {
            return false;
        }

        // You are not allowed to make that move
        if (!piece.isValidMove(board, move)) {
            return false;
        }
        board.setPiece(move.getTo(), piece);
        board.setPiece(move.getFrom(), null);
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
            board.setPiece(piece.getPosition(), new Queen(piece.getPosition(), piece.getColor()));
        }
    }

    private Move getInputMove() {
        System.out.print(currentColorsTurn.equals(Color.WHITE) ? "White move: " : "Black move: ");
        String input = scanner.nextLine();

        String[] parts = input.split(" ");

        Position from = parsePosition(parts[0]);
        Position to = parsePosition(parts[1]);

        return new Move(from, to);
    }

    private Position parsePosition(String field) {
        int col = field.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(field.charAt(1));
        return new Position(row, col);
    }

    private void rotatePlayersTurn() {
        if (currentColorsTurn.equals(Color.WHITE)) {
            currentColorsTurn = Color.BLACK;
        } else {
            currentColorsTurn = Color.WHITE;
        }
    }

    private static ChessGame initChessGame() {
        return new ChessGame();
    }
}
