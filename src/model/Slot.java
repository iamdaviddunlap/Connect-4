package model;

public class Slot {
    private int row;
    private int col;
    private Piece piece;

    public Slot(int row, int col, Piece piece) {
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public String toString() {
        if(piece != null) {
            return "[" + piece + "]";
        }
        else {
            return "[ ]";
        }
    }
}
