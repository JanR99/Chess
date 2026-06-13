package model;

import pieces.*;

import java.awt.*;

public class Board {

    private Piece[][] squares;
    private int missingWhites = 0;
    private int missingBlack = 0;

    public Board() {
        initializeBoard();
    }

    public Piece getPiece(int row, int col) {
        return squares[row][col];
    }

    public void setPiece(Position position, Piece piece) {
        incrementMissingPieces(position);
        squares[position.getRow()][position.getCol()] = piece;
    }

    public boolean isEmpty(int row, int col) {
        return getPiece(row, col) == null;
    }

    public int getMissingWhites() {
        return missingWhites;
    }

    public void setMissingWhites(int missingWhites) {
        this.missingWhites = missingWhites;
    }

    public int getMissingBlack() {
        return missingBlack;
    }

    public void setMissingBlack(int missingBlack) {
        this.missingBlack = missingBlack;
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

    private void incrementMissingPieces(Position position) {
        if (isEmpty(position.getRow(), position.getCol())) {
            return;
        }
        Piece currentPiece = getPiece(position.getRow(), position.getCol());
        if (currentPiece.getColor().equals(Color.WHITE)) {
            missingWhites++;
        } else {
            missingBlack++;
        }
    }
}