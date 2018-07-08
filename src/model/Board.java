package model;

import java.nio.ByteBuffer;
import java.util.Observable;

public class Board extends Observable {
    public static final int LENGTH = 7; //TODO change back to 7
    public static final int HEIGHT = 6; //TODO chnage back to 6
    private Slot[][] board;
    private boolean myTurn;
    private Piece.Color activeColor;
    private String gameDecision = null;
    private String movesString;

    public Board() {
        this.board = new Slot[LENGTH][HEIGHT];
        for (int row=0; row<LENGTH; ++row) {
            for (int col=0; col<HEIGHT; ++col) {
                this.board[row][col] = new Slot(row, col, null);
            }
        }
        this.myTurn = false;
        activeColor = Piece.Color.RED;
        this.movesString = "";
    }

    public Piece.Color getActiveColor() {
        return activeColor;
    }

    public String getGameDecision() {
        return gameDecision;
    }

    public void setGameDecision(String gameDecision) {
        this.gameDecision = gameDecision;
        super.setChanged();
        super.notifyObservers();
    }

    public void switchActiveColor() {
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
        movesString += (col+1);
        super.setChanged();
        super.notifyObservers();
    }

    public Slot getSlot(int row, int col) {
        return board[row][col];
    }

    public boolean checkTurn(int col) {
        return board[col][0].getPiece() == null;
    }

    private char checkWinHorizontal() {
        int yCounter = 0;
        int rCounter = 0;
        int rGreatest = 0;
        int yGreatest = 0;
        for (int col=0; col < HEIGHT; col++) {
            for (int row=0; row < LENGTH; row++) {
                Piece piece = board[row][col].getPiece();
                if(piece != null) {
                    if(piece.getColor().equals(Piece.Color.YELLOW)) {
                        yCounter++;
                        if(rCounter > rGreatest) { rGreatest = rCounter; }
                        rCounter = 0;
                    }
                    else {
                        rCounter++;
                        if(yCounter > yGreatest) { yGreatest = yCounter; }
                        yCounter = 0;
                    }
                }
                else {
                    if(rCounter > rGreatest) { rGreatest = rCounter; }
                    if(yCounter > yGreatest) { yGreatest = yCounter; }
                    rCounter = 0;
                    yCounter = 0;
                }
            }
            if(rCounter > rGreatest) { rGreatest = rCounter; }
            if(yCounter > yGreatest) { yGreatest = yCounter; }
            if(yGreatest >= 4) {
                return 'Y';
            }
            else if(rGreatest >= 4) {
                return 'R';
            }
            yGreatest = 0;
            rGreatest = 0;
            yCounter = 0;
            rCounter = 0;
        }
        return ' ';
    }

    private char checkWinVertical() {
        int yCounter = 0;
        int rCounter = 0;
        int rGreatest = 0;
        int yGreatest = 0;
        for (int row=0; row < LENGTH; row++) {
            for (int col=HEIGHT-1; col >=0; col--) {
                Piece piece = board[row][col].getPiece();
                if(piece != null) {
                    if(piece.getColor().equals(Piece.Color.YELLOW)) {
                        yCounter++;
                        if(rCounter > rGreatest) { rGreatest = rCounter; }
                        rCounter = 0;
                    }
                    else {
                        rCounter++;
                        if(yCounter > yGreatest) { yGreatest = yCounter; }
                        yCounter = 0;
                    }
                }
                else {
                    if(rCounter > rGreatest) { rGreatest = rCounter; }
                    if(yCounter > yGreatest) { yGreatest = yCounter; }
                    rCounter = 0;
                    yCounter = 0;
                }
            }
            if(rCounter > rGreatest) { rGreatest = rCounter; }
            if(yCounter > yGreatest) { yGreatest = yCounter; }
            if(yGreatest >= 4) {
                return 'Y';
            }
            else if(rGreatest >= 4) {
                return 'R';
            }
            yGreatest = 0;
            rGreatest = 0;
            yCounter = 0;
            rCounter = 0;
        }
        return ' ';
    }

    private char checkWinDiagonal() {
        for (int col=0; col < HEIGHT/2; col++) {
            for (int row = 0; row < LENGTH; row++) {
                char check = checkDiagonalCoords(row,col);
                if(check != ' ') {
                    return check;
                }
            }
        }
        return ' ';
    }

    private char checkDiagonalCoords(int row, int col) {
        int count = 0;
        char returnVal;
        if(board[row][col].getPiece() != null) {
            Piece.Color color = board[row][col].getPiece().getColor();
            if (color.equals(Piece.Color.YELLOW)) {
                returnVal = 'Y';
            } else {
                returnVal = 'R';
            }

            //Down, right
            try {
                for (int i = 1; i <= 3; i++) {
                    if (board[row + i][col + i].getPiece().getColor().equals(color)) {
                        count++;
                    }
                }
            }
            //Either breaks, or the piece is not found. Either way this means there is
            //not a 4 in a row in this direction.
            catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                count = 0;
            }
            if (count == 3) {
                return returnVal;
            } else {
                count = 0;
                //Down, left
                try {
                    for (int i = 1; i <= 3; i++) {
                        if (board[row - i][col + i].getPiece().getColor().equals(color)) {
                            count++;
                        }
                    }
                }
                //Either breaks, or the piece is not found. Either way this means there is
                //not a 4 in a row in this direction.
                catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                    count = 0;
                }
                if (count == 3) {
                    return returnVal;
                } else {
                    count = 0;
                    //Up, right
                    try {
                        for (int i = 1; i <= 3; i++) {
                            if (board[row + i][col - i].getPiece().getColor().equals(color)) {
                                count++;
                            }
                        }
                    }
                    //Either breaks, or the piece is not found. Either way this means there is
                    //not a 4 in a row in this direction.
                    catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                        count = 0;
                    }
                    if (count == 3) {
                        return returnVal;
                    } else {
                        count = 0;
                        //Up, left
                        try {
                            for (int i = 1; i <= 3; i++) {
                                if (board[row - i][col - i].getPiece().getColor().equals(color)) {
                                    count++;
                                }
                            }
                        }
                        //Either breaks, or the piece is not found. Either way this means there is
                        //not a 4 in a row in this direction.
                        catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                            count = 0;
                        }
                        if (count == 3) {
                            return returnVal;
                        } else {
                            return ' ';
                        }
                    }
                }
            }
        }
        return ' ';
    }

    private boolean boardFull() {
        for (int col=0; col < HEIGHT; col++) {
            for (int row = 0; row < LENGTH; row++) {
                if(board[row][col].getPiece() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public char checkWin() {
        char value = checkWinHorizontal();
        if(value != ' ') {
            return value;
        }
        else {
            value = checkWinVertical();
            if(value != ' ') {
                return value;
            }
            else {
                value = checkWinDiagonal();
                if(value != ' ') {
                    return value;
                }
                else {
                    if(boardFull()) {
                        return 'T';
                    }
                    else {
                        return ' ';
                    }
                }
            }
        }
    }

    public int totalPieces(Piece.Color color) {
        int count = 0;
        for (int col=0; col<HEIGHT; col++) {
            for (int row=0; row<LENGTH; row++) {
                if(board[row][col].getPiece() != null &&
                   board[row][col].getPiece().getColor().equals(color)) {
                    count++;
                }
            }
        }
        return count;
    }

    public String getMovesString() {
        return movesString;
    }

    @Override
    public String toString() {
        String output = "";
        for (int col=0; col<HEIGHT; col++) {
            for (int row=0; row<LENGTH; row++) {
                output += board[row][col];
            }
            output += "\n";
        }
        return output;
    }

    public byte[] encode() {
        String val = "";
        byte[] by = new byte[HEIGHT+1];
        for (int col = 0; col <= HEIGHT; col++) {
            for (int row = 0; row < LENGTH; row++) {
                boolean terminal = false;
                boolean thisPiece = false;
                try {
                    try {
                        thisPiece = (board[row][col - 1].getPiece() != null);
                    } catch (ArrayIndexOutOfBoundsException e) {}
                    if(!thisPiece && col==HEIGHT) {
                        terminal = true;
                    }
                    else if (!thisPiece && board[row][col].getPiece() != null) {
                        terminal = true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                boolean match = false;
                if(thisPiece && board[row][col-1].getPiece().getColor().equals(activeColor)) {
                    match = true;
                }
                if (match || terminal) {
                    val += "1";
                }
                else {
                    val += "0";
                }
            }
            by[col] = Byte.parseByte(val, 2);
            val = "";
        }
        //by[HEIGHT] = (byte)~by[HEIGHT]; //inverts the bottom row
        return by;
    }
}