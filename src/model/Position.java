package model;

public class Position {

    public static final int FIRST_ROW = 0;
    public static final int LAST_ROW = 7;
    public static final int FIRST_COLUMN = 0;
    public static final int LAST_COLUMN = 7;

    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
