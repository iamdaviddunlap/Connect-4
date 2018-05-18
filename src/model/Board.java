package model;

import java.util.Observable;

public class Board extends Observable {
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

    public int getLength() {
        return LENGTH;
    }

    public int getHeight() {
        return HEIGHT;
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
