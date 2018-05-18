package model;

public class Board {
    public final int LENGTH = 7;
    public final int HEIGHT = 6;
    private Slot[][] board;

    public Board() {
        this.board = new Slot[LENGTH][HEIGHT];
        for (int row=0; row<LENGTH; ++row) {
            for (int col=0; col<HEIGHT; ++col) {
                this.board[row][col] = new Slot(row, col, null);
            }
        }
    }

    @Override
    public String toString() {
        String output = "";
        for (int row=0; row<LENGTH; ++row) {
            for (int col=0; col<HEIGHT; ++col) {
                output += board[row][col];
            }
            output += "\n";
        }
        return output;
    }
}
