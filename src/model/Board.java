package model;

import java.util.Observable;

public class Board extends Observable {
    public final int LENGTH = 7;
    public final int HEIGHT = 6;
    private Slot[][] board;
    private boolean myTurn;
    private Piece.Color activeColor;

    public Board() {
        this.board = new Slot[LENGTH][HEIGHT];
        for (int row=0; row<LENGTH; ++row) {
            for (int col=0; col<HEIGHT; ++col) {
                this.board[row][col] = new Slot(row, col, null);
            }
        }
        this.myTurn = false;
        activeColor = Piece.Color.YELLOW;
    }

    public Piece.Color getActiveColor() {
        return activeColor;
    }

    private void switchActiveColor() {
        if(activeColor == Piece.Color.YELLOW) {
            activeColor = Piece.Color.RED;
        }
        else {
            activeColor = Piece.Color.YELLOW;
        }
    }

    public int getLength() {
        return LENGTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void canMakeMove() {
        myTurn = true;
        System.out.println("my turn, notify observers");
        super.setChanged();
        super.notifyObservers();
    }

    private int getLowest(int col) {
        int row = -1;
        for(int i=0;i<HEIGHT;i++) {
            Piece piece = board[col][i].getPiece();
            if(piece == null) {
                row = i;
            }
        }
        return row;
    }

    public void makeMove(int col) {
        board[col][getLowest(col)].setPiece(new Piece(activeColor));
        switchActiveColor();
        myTurn = false;
        super.setChanged();
        super.notifyObservers();
    }

    public Slot getSlot(int row, int col) {
        return board[row][col];
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
