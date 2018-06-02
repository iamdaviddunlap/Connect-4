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
                    System.out.println("The game has ended.");
                    keepGoing = false;
                } else if (makeMove(this.playerTwo, this.playerOne)) {
                    System.out.println("The game has ended.");
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
        this.gameBoard.makeMove(col);

        active.moveMade(col);
        other.moveMade(col);

        boolean isOver = false;
        switch(this.gameBoard.checkWin()) {
            case 'Y': //Yellow won
                isOver = true;
                playerOne.gameWon();
                playerTwo.gameLost();
                break;
            case 'R': //Red won
                isOver = true;
                playerOne.gameLost();
                playerTwo.gameWon();
                break;
            case 'T': //There was a tie
                isOver = true;
                playerOne.gameTied();
                playerTwo.gameTied();
                break;
            case ' ': //Nobody won this turn
                break;
        }

        return isOver; //returns whether game has ended
        //NOTE: this currently causes the clients' games to close as soon as the game
        //ends. To stop this, change this return value to false and they will wait for
        //the clients to end the game on their own.
    }
}
