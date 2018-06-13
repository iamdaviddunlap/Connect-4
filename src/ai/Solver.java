package ai;

import model.Board;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static model.Board.*;

public class Solver {

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
    private static boolean isTerminal(Board board) {
        boolean terminal = false;
        for(int i=0;i<LENGTH;i++) {
            if(board.checkTurn(i)) {
                Board boardCp = new Board();
                if(!populateBoard(boardCp,board.getMovesString())) {
                    return true;
                }
                boardCp.makeMove(i);
                if(terminal == false && boardCp.checkWin() != ' ') {
                    terminal = true;
                }
            }
        }
        return terminal;
    }

    private static int negamax(Board board,int move) { //TODO complete
        board.makeMove(move);
        if(isTerminal(board)) {
            return 22-(board.totalPieces(board.getActiveColor())+1);
        }
        else {
            int localMax = Integer.MIN_VALUE;
            for(int i=0;i<LENGTH;i++) {
                if(board.checkTurn(i)) {
                    int current = -negamax(board,i);
                    if(current > localMax) {
                        localMax = current;
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
            String[] temp = line.split(" ");
            moves.add(temp[0]);
            outputs.add(temp[1]);
        }
        return new ArrayList[]{moves,outputs};
    }

    public static int bestMove(Board board) {
        int max = Integer.MIN_VALUE;
        for(int i=0;i<LENGTH;i++) {
            if(board.checkTurn(i)) {
                int current = negamax(board, i);
                if (current > max) {
                    max = current;
                }
            }
        }
        return max;
    }

    public static void main(String args[]) throws IOException {
        Board board = new Board();
        populateBoard(board,"225257625346224411156336534367135144");
        System.out.println(board);
        System.out.println("active color: "+board.getActiveColor());
        System.out.println("board string: "+board.getMovesString());
        System.out.println("score: "+negamax(board,0));

        ArrayList<String>[] values = readFile("src/ai/test1.txt");
        for(int i=0;i<values[0].size();i++) {
            Board tempBoard = new Board();
            int move = Integer.parseInt(""+values[0].get(i).charAt(values[0].get(i).length()-1))-1;
            String str = values[0].get(i).substring(0, values[0].get(i).length() - 1);
            populateBoard(tempBoard,str);
            if(negamax(tempBoard,move) == Integer.parseInt(values[1].get(i))) {
                System.out.println("Match!");
            }
            else {
                System.out.println("Oops. Looking for: "+Integer.parseInt(values[1].get(i))+" but found: "+move+
                " movesString: "+values[0].get(i));
            }
        }
    }

}
