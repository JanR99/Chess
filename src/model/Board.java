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
     *
     * @param position the new {@link Position}
     * @param piece    the {@link Piece} to move
     */
    public void setPiece(Position position, Piece piece) {
        squares[position.getRow()][position.getCol()] = piece;
    }

    public void movePiece(Move move, Piece piece) {
        Position from = move.getFrom();
        Position to = move.getTo();

        squares[to.getRow()][to.getCol()] = piece;
        piece.setPosition(to);
        setAlreadyMoved(piece);
        squares[from.getRow()][from.getCol()] = null;
    }

    public boolean isEmpty(Position position) {
        return getPiece(position) == null;
    }

    public boolean isCheckMate(Color color) {
        King king = null;

        for (int row = 0; row < 8 && king == null; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece instanceof King && piece.getColor().equals(color)) {
                    king = (King) piece;
                    break;
                }
            }
        }

        // No king
        if (king == null) {
            return true;
        }

        Position kingPos = king.getPosition();
        // Can any enemy piece legally move to the king?
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];

                if (piece != null && !piece.getColor().equals(color)) {
                    Move move = new Move(piece.getPosition(), kingPos);

                    if (piece.isValidMove(this, move)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void setAlreadyMoved(Piece piece) {
        if (piece instanceof King king) {
            king.setAlreadyMoved(true);
        }

        if (piece instanceof Rook rook) {
            rook.setAlreadyMoved(true);
        }
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