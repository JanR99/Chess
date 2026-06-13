package model;

import pieces.*;

import java.awt.*;

public class Board {

    private Piece[][] squares;

    public Board() {
        initializeBoard();
    }

    public Piece getPiece(Position position) {
        return squares[position.getRow()][position.getCol()];
    }

    /**
     * sets the piece to its new position,
     * @param position the new {@link Position}
     * @param piece the {@link Piece} to move
     * @return true if the current player lost his {@link King}
     */
    public boolean setPiece(Position position, Piece piece) {
        boolean won = false;
        Piece pieceOnSquare = getPiece(position);
        if (pieceOnSquare instanceof King) {
            won = true;
        }
        squares[position.getRow()][position.getCol()] = piece;
        return won;
    }

    public boolean isEmpty(Position position) {
        return getPiece(position) == null;
    }

    private void initializeBoard() {
        squares = new Piece[8][8];

        // pawns
        for (int col = 0; col < 8; col++) {
            squares[1][col] = new Pawn(new Position(1, col), Color.BLACK);
            squares[6][col] = new Pawn(new Position(6, col), Color.WHITE);
        }

        // black back rank
        squares[0][0] = new Rook(   new Position(0, 0), Color.BLACK);
        squares[0][1] = new Knight( new Position(0, 1), Color.BLACK);
        squares[0][2] = new Bishop( new Position(0, 2), Color.BLACK);
        squares[0][3] = new Queen(  new Position(0, 3), Color.BLACK);
        squares[0][4] = new King(   new Position(0, 4), Color.BLACK);
        squares[0][5] = new Bishop( new Position(0, 5), Color.BLACK);
        squares[0][6] = new Knight( new Position(0, 6), Color.BLACK);
        squares[0][7] = new Rook(   new Position(0, 7), Color.BLACK);

        // white back rank
        squares[7][0] = new Rook(   new Position(7, 0), Color.WHITE);
        squares[7][1] = new Knight( new Position(7, 1), Color.WHITE);
        squares[7][2] = new Bishop( new Position(7, 2), Color.WHITE);
        squares[7][3] = new Queen(  new Position(7, 3), Color.WHITE);
        squares[7][4] = new King(   new Position(7, 4), Color.WHITE);
        squares[7][5] = new Bishop( new Position(7, 5), Color.WHITE);
        squares[7][6] = new Knight( new Position(7, 6), Color.WHITE);
        squares[7][7] = new Rook(   new Position(7, 7), Color.WHITE);
    }
}