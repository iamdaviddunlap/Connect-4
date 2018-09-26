package ai;

import model.Board;
import model.Piece;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import static model.Board.*;

public class Solver {

    private static int negamaxCount = 0;
    private static long startTime;
    private static final int[] searchOrder = new int[]{3,2,4,1,5,0,6}; //traverse the board from the middle outwards
    private static TranspositionTable cache = new TranspositionTable();

    /**
     * Uses the passed in string to simulate moves on a game board
     * @param board the Board object to populate with moves
     * @param boardS the string of moves (1 is the leftmost, 7 is the rightmost slot)
     * @return true if it completed populating the board, false if a winner was found
     */
    public static boolean populateBoard(Board board, String boardS) {
        for(int i=0;i<boardS.length();i++) {
            int move = Character.getNumericValue(boardS.charAt(i)) - 1;
            if(board.checkTurn(move)) {
                board.makeMove(move);
                if (board.checkWin() != ' ') {
                    return false;
                }
            } else {
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

        if(negamaxCount == 15365) {
            System.out.println("just before?");
        }

        board.makeMove(move);

        if(board.getMovesString().equals("62444612461574235571247762637533565")) { //65
            //System.out.println("breakpoint");
        }

        long encode = board.encode();
        if(cache.containsKey(board.toString())) {
            if(board.getActiveColor().equals(Piece.Color.RED)) {
                return cache.get(board.toString());
            } else {
                return -cache.get(board.toString());
            }
        }
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

                    Board boardCp2 = new Board();
                    populateBoard(boardCp2,board.getMovesString());
                    boardCp2.makeMove(i);
                    int current;
                    if(cache.containsKey(boardCp2.toString())) {
                        if(board.getActiveColor().equals(Piece.Color.RED)) {
                            current = cache.get(boardCp2.toString());
                        } else {
                            current = -cache.get(boardCp2.toString());
                        }
                    } else {
                        current = -negamax(boardCp, i, -beta, -alpha);
                    }
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
            if(board.getActiveColor().equals(Piece.Color.YELLOW)) {
                cache.put(board.toString(),-localMax);
            } else {
                cache.put(board.toString(),localMax);
            }

            if(localMax == 2) {
                //System.out.println(board.getMovesString()+" 2");
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


//    public static void main(String args[]) {
//        Board board = new Board();
//        populateBoard(board,"1213124123151543355572244443326666667577");
//        System.out.println(board.toString());
//        System.out.println(board.getActiveColor());
//        System.out.println(board.encode());
//        //board.switchActiveColor();
////        byte[] playerPos = board.bitboard;
////        for(byte byt:playerPos) {
////            String s1 = String.format("%8s", Integer.toBinaryString(byt & 0xFF)).replace(' ', '0');
////            System.out.println(s1);
////        }
//    }

    public static void main(String args[]) throws IOException {
        ArrayList<String>[] values = readFile("src/ai/"+args[0]);
        long meanTimes = 0;
        for(int i=0;i<values[0].size();i++) {
            Board tempBoard = new Board();
            int move = Integer.parseInt(""+values[0].get(i).charAt(values[0].get(i).length()-1))-1;
            String str = values[0].get(i).substring(0, values[0].get(i).length() - 1);
            populateBoard(tempBoard,str);
            startTime = System.nanoTime();
            int score = negamax(tempBoard,move,-Integer.MAX_VALUE,Integer.MAX_VALUE);

            Iterator iterator = cache.entrySet().iterator();
            Object lastElement = 0;
            while (iterator.hasNext()) { lastElement = iterator.next(); }
            if(lastElement.toString().equals("3179806099929367=2")) {
                System.out.println("CAUGHT BEFORE");
            }
            //System.out.println("Last element: \n"+lastElement);

            if(score == Integer.parseInt(values[1].get(i))) {
                System.out.println("Match!");
            }
            else {
                System.out.println("Oops. Looking for: "+Integer.parseInt(values[1].get(i))+" but found: "+score+
                " movesString: "+values[0].get(i));
            }
            double time = (System.nanoTime()-startTime)/1000000000.0;
            System.out.println("final count: "+negamaxCount+" time elapsed: "+time+"s mean time(microseconds): "+((time/negamaxCount)*1000000)+"\n");
            meanTimes += ((time/negamaxCount)*1000000);
            negamaxCount = 0;
        }
        double avg = meanTimes/(double)values[0].size();
        System.out.println("Average mean time: "+avg);
    }
}
