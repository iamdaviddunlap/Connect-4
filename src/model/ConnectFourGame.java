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

    }
}
