package ai;

import model.Board;
import model.Piece;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static model.Board.*;

public class Solver {

    private static int negamaxCount = 0;
    private static long startTime;
    private static final int[] searchOrder = new int[]{3,2,4,1,5,0,6}; //traverse the board from the middle outwards

    /**
     * Uses the passed in string to simulate moves on a game board
     * @param board the Board object to populate with moves
     * @param boardS the string of moves (1 is the leftmost, 7 is the rightmost slot)
     * @return true if it completed populating the board, false if a winner was found
     */
    private static boolean populateBoard(Board board, String boardS) {
        for(int i=0;i<boardS.length();i++) {
            board.makeMove(Character.getNumericValue(boardS.charAt(i))-1);
            if(board.checkWin() != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this board position will result in a win for the current player next turn
     * @param board the board who's position is being checked
     * @return char ' ' if not terminal, 'Y' if yellow wins, and 'R' if red wins
     */
    private static char isTerminal(Board board) {
        char initial = board.checkWin();
        if(initial != ' ') {
            board.switchActiveColor();
            return initial;
        }
        for(int i=0;i<LENGTH;i++) {
            if(board.checkTurn(i)) {
                Board boardCp = new Board();
                if(!populateBoard(boardCp,board.getMovesString())) {
                    return boardCp.checkWin();
                }
                boardCp.makeMove(i);
                if(boardCp.checkWin() != ' ') {
                    return boardCp.checkWin();
                }
            }
        }
        return initial;
    }

    private static int negamax(Board board,int move,int alpha, int beta) { //TODO complete
        negamaxCount++; //keeps track of how many nodes are explored
        board.makeMove(move);
        char term = isTerminal(board);
        if(term != ' ') {
            if(term == 'T') {
                return 0;
            }
            else {
                if(board.checkWin() == Piece.colorToChar(board.getActiveColor())) {
                    return (board.totalPieces(board.getActiveColor()))-22;
                }
                else {
                    return 22-(board.totalPieces(board.getActiveColor()) + 1);
                }
            }
        }
        else {
            int localMax = Integer.MIN_VALUE;
            for(int i:searchOrder) {
                if(board.checkTurn(i)) {
                    Board boardCp = new Board();
                    populateBoard(boardCp,board.getMovesString());
                    int current = -negamax(boardCp,i,-beta,-alpha);
                    if(current > localMax) {
                        localMax = current;
                    }
                    if(current >= beta) {
                        return current;
                    }
                    if(current > alpha) {
                        alpha = current;
                    }
                    if(alpha >= beta) {
                        return beta;
                    }
                }
            }
            return localMax;
        }
    }

    private static ArrayList<String>[] readFile(String fileName) throws IOException {
        BufferedReader buff = new BufferedReader(new FileReader(fileName));
        String line;
        ArrayList<String> moves = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        while((line = buff.readLine()) != null) {
            if(line.charAt(0) != '/' && line.charAt(1) != '/') {
                String[] temp = line.split(" ");
                moves.add(temp[0]);
                outputs.add(temp[1]);
            }
        }
        return new ArrayList[]{moves,outputs};
    }

    public int bestMove(Board board) {
        int max = Integer.MIN_VALUE;
        int move = -1;
        for(int i=0;i<LENGTH;i++) {
            if(board.checkTurn(i)) {
                Board boardCp = new Board();
                populateBoard(boardCp,board.getMovesString());
                int current = negamax(boardCp, i,-Integer.MAX_VALUE,Integer.MAX_VALUE);
                if (current > max) {
                    max = current;
                    move = i;
                }
            }
        }
        return move;
    }


    public static void main(String args[]) {
        Board board = new Board();
        populateBoard(board,"435654434334");
        System.out.println(board.toString());
        System.out.println(board.getActiveColor());
        byte[] by = board.encode();
        for(byte byt:by) {
            String s1 = String.format("%8s", Integer.toBinaryString(byt & 0xFF)).replace(' ', '0').substring(1);
            System.out.println(s1);
        }

    }

//    public static void main(String args[]) throws IOException {
//        ArrayList<String>[] values = readFile("src/ai/"+args[0]);
//        for(int i=0;i<values[0].size();i++) {
//            Board tempBoard = new Board();
//            int move = Integer.parseInt(""+values[0].get(i).charAt(values[0].get(i).length()-1))-1;
//            String str = values[0].get(i).substring(0, values[0].get(i).length() - 1);
//            populateBoard(tempBoard,str);
//            startTime = System.nanoTime();
//            int score = negamax(tempBoard,move,-Integer.MAX_VALUE,Integer.MAX_VALUE);
//            if(score == Integer.parseInt(values[1].get(i))) {
//                System.out.println("Match!");
//            }
//            else {
//                System.out.println("Oops. Looking for: "+Integer.parseInt(values[1].get(i))+" but found: "+score+
//                " movesString: "+values[0].get(i));
//            }
//            double time = (System.nanoTime()-startTime)/1000000000.0;
//            System.out.println("final count: "+negamaxCount+" time elapsed: "+time+"s mean time(microseconds): "+((time/negamaxCount)*1000000)+"\n");
//            negamaxCount = 0;
//        }
//    }
}
