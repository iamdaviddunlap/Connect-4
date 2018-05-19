package model;

public class Piece {
    public enum Color {RED, YELLOW};
    private Color pieceColor;

    Piece(Color color) {
        this.pieceColor = color;
    }

    public Color getColor() {
        return pieceColor;
    }

    @Override
    public String toString() {
        if(pieceColor == Color.RED) {
            return "R";
        }
        else {
            return "Y";
        }
    }
}
