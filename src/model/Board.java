package model;

import ai.Solver;

import java.nio.ByteBuffer;
import java.util.Observable;

public class Board extends Observable {
    public static final int LENGTH = 7; //TODO change back to 7
    public static final int HEIGHT = 6; //TODO chnage back to 6
    private Slot[][] board; //col, row (0,0) is top left, (0,5) is bottom left
    private boolean myTurn;
    private Piece.Color activeColor;
    private String gameDecision = null;
    private String movesString;
    public byte[] bitboard;
    public byte[] bitboardMask;

    public Board() {
        this.board = new Slot[LENGTH][HEIGHT];
        for (int row=0; row<LENGTH; ++row) {
            for (int col=0; col<HEIGHT; ++col) {
                this.board[row][col] = new Slot(row, col, null);
            }
        }
        this.myTurn = false;
        this.activeColor = Piece.Color.RED;
        this.movesString = "";
        this.bitboard = new byte[LENGTH];
        this.bitboardMask = new byte[LENGTH];
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

    /**
     * Gets the lowest position of the given row
     * @param col the column to check
     * @return the lowest empty row, -1 if column is full
     */
    private int getLowest(int col) {
        int row = -1;
        for(int i=HEIGHT-1;i>=0;i--) {
            Piece piece = board[col][i].getPiece();
            if(piece == null) {
                return i;
            }
        }
        return row;
    }

    public void makeMove(int col) {
        board[col][getLowest(col)].setPiece(new Piece(activeColor));
        bitboard[col] = encodeRow(col);
        bitboardMask[col] = encodeRowMask(col);
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
        char colorChar = 'Y';
        for (int col=0; col < HEIGHT; col++) {
            Piece piece = board[1][col].getPiece();
            if(piece != null) {
                Piece.Color color = piece.getColor();
                if(color.equals(Piece.Color.RED)) {
                    colorChar = 'R';
                }
                if(board[2][col].getPiece() != null
                        && board[3][col].getPiece() != null
                        && board[2][col].getPiece().getColor().equals(color)
                        && board[3][col].getPiece().getColor().equals(color)) {
                    if(((board[0][col].getPiece() != null) && (board[0][col].getPiece().getColor().equals(color)))
                           || ((board[4][col].getPiece() != null) && (board[4][col].getPiece().getColor().equals(color)))) {
                        return colorChar;
                    }
                }
            }
            piece = board[5][col].getPiece();
            if(piece != null) {
                Piece.Color color = piece.getColor();
                if(color.equals(Piece.Color.RED)) {
                    colorChar = 'R';
                }
                if(board[4][col].getPiece() != null
                        && board[3][col].getPiece() != null
                        && board[4][col].getPiece().getColor().equals(color)
                        && board[3][col].getPiece().getColor().equals(color)) {
                    if(((board[6][col].getPiece() != null) && (board[6][col].getPiece().getColor().equals(color)))
                            || ((board[2][col].getPiece() != null) && (board[2][col].getPiece().getColor().equals(color)))) {
                        return colorChar;
                    }
                }
            }
        }
        return ' ';
    }

    private char checkWinVertical() {
        char colorChar = 'Y';
        for (int row=0; row < LENGTH; row++) {
            Piece piece0 = board[row][0].getPiece();
            Piece piece1 = board[row][1].getPiece();
            Piece piece2 = board[row][2].getPiece();
            Piece piece3 = board[row][3].getPiece();
            Piece piece4 = board[row][4].getPiece();
            Piece piece5 = board[row][5].getPiece();
            if(piece2 != null && piece3 != null
                    && piece3.getColor().equals(piece2.getColor())) {
                Piece.Color color = piece2.getColor();
                if(color.equals(Piece.Color.RED)) {
                    colorChar = 'R';
                }
                if(((piece0 != null && piece0.getColor().equals(color)) && (piece1 != null && piece1.getColor().equals(color)))
                        || ((piece4 != null && piece4.getColor().equals(color)) && (piece5 != null && piece5.getColor().equals(color)))
                        || ((piece1 != null && piece1.getColor().equals(color)) && (piece4 != null && piece4.getColor().equals(color)))) {
                    return colorChar;
                }
            }
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

    /**
     * Encodes the mask of the board.
     * 1 is a piece, 0 is an empty space.
     * When XOR is applied with this and the bitboard, it gives the bitboard of the opposing player.
     * @param col
     * @return the byte representation of the mask of this column
     */
    private byte encodeRowMask(int col) {
        int lowest = getLowest(col);
        if(lowest == 5) {
            return  0; //000000
        } else if(lowest == 4) {
            return 1; //000001
        } else if(lowest == 3) {
            return 3; //000011
        } else if(lowest == 2) {
            return 7; //000111
        } else if(lowest == 1) {
            return 15; //001111
        } else if(lowest == 0) {
            return 31; //011111
        } else {
            return 63; //111111
        }
    }

    /**
     * Encodes the given column as a byte.
     * The following example would be encoded as (always encodes from red's perspective):
     *     0        1
     * [ ] 0    [Y] 0
     * [ ] 1    [Y] 0
     * [Y] 0    [Y] 0
     * [R] 1    [R] 1
     * [Y] 0    [R] 1
     * [R] 1    [Y] 0
     * Red is encoded as 1, yellow as 0, and the top empty cell as 1
     * @param col
     * @return the byte for the bitboard representation of this column
     */
    private byte encodeRow(int col) {
        byte binary = 1;
        StringBuilder binaryS = new StringBuilder(HEIGHT+1);
        int lowest = getLowest(col);
        if(lowest == 5) {
            return binary;
        }
        else {
            binaryS.append("1");
            for(int row=lowest+1;row<HEIGHT;row++) {
                if(board[col][row].getPiece().getColor().equals(Piece.Color.RED)) {
                    binaryS.append("1");
                }
                else {
                    binaryS.append("0");
                }
            }
            binary = Byte.parseByte(binaryS.toString(), 2);
            return binary;
        }
    }

    /**
     * Combines the bitboard array into a long.
     * For the yellow player, applies XOR with mask to switch the board.
     * @return a long representing the board state
     */
    public long encode() {
        long result = 0;
        for (int i = 0; i < LENGTH; i++) {
            result <<= 8;
            if(activeColor.equals(Piece.Color.RED)) {
                result |= ((bitboard[i]) & 0xFF);
            } else {
                result |= (((byte)(bitboard[i]) ^ bitboardMask[i]) & 0xFF);
            }
        }
        return result;
    }
}