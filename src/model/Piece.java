package model;

public class Piece {
    public enum Color {RED, YELLOW}
    private Color pieceColor;

    Piece(Color color) {
        this.pieceColor = color;
    }

    public Color getColor() {
        return pieceColor;
    }

    public static char colorToChar(Color color) {
        if(color.equals(Color.YELLOW)) {
            return 'Y';
        }
        else {
            return 'R';
        }
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
