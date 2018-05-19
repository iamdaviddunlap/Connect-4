package model;

import server.Player;

public class ConnectFourGame {
    private Board gameBoard;
    private Player playerOne;
    private Player playerTwo;

    public ConnectFourGame(Player playerOne, Player playerTwo) {
        gameBoard = new Board();
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public void run() {
        boolean keepGoing = true;
        while(keepGoing) {
            try {
                if (makeMove(this.playerOne, this.playerTwo)) {
                    keepGoing = false;
                } else if (makeMove(this.playerTwo, this.playerOne)) {
                    keepGoing = false;
                }
            } catch(Exception e) {
                System.out.println("A player has disconnected. Aborting.");
                keepGoing = false;
            }
        }
    }

    private boolean makeMove(Player active, Player other) throws Exception {
        int col = active.makeMove();
        System.out.println(col);
        this.gameBoard.makeMove(col);

        active.moveMade(col);
        other.moveMade(col);

        switch(this.gameBoard.checkWin()) {
            case 'Y': //Yellow won
                playerOne.gameWon();
                playerTwo.gameLost();
                break;
            case 'R': //Red won
                playerOne.gameLost();
                playerTwo.gameWon();
                break;
            case 'T': //There was a tie
                playerOne.gameTied();
                playerTwo.gameTied();
                break;
            case ' ': //Nobody won this turn
                break;
        }

        return false; //returns whether game has ended, TODO finish this
    }
}
