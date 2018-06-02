package server;

import model.Board;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static server.ServerProtocol.*;

public class NetworkHandler {

    private Scanner networkIn; //read requests from server

    private PrintStream networkOut; //write responses to server

    private Board board; //the game board

    private boolean canGo;

    public NetworkHandler( String hostname, int port, Board model ) {
        try {
            Socket sock = new Socket( hostname, port ); //communicate with server
            System.out.println("Connected to server on port "+port);
            this.networkIn = new Scanner( sock.getInputStream() );
            this.networkOut = new PrintStream( sock.getOutputStream() );
            this.board = model;
            this.canGo = true;

            Thread netThread = new Thread( () -> this.run() );
            netThread.start();
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private synchronized boolean canGo() {
        return canGo;
    }

    private void run() {
        while(this.canGo()) {
            try {
                String request = this.networkIn.next();
                String arguments = this.networkIn.nextLine().trim();
                switch (request) {
                    case MAKE_MOVE:
                        this.board.canMakeMove();
                        break;
                    case MOVE_MADE:
                        this.board.makeMove(Integer.parseInt(arguments));
                        break;
                    case GAME_WON:
                        this.board.setGameDecision(GAME_WON);
                        break;
                    case GAME_LOST:
                        this.board.setGameDecision(GAME_LOST);
                        break;
                    case GAME_TIED:
                        this.board.setGameDecision(GAME_TIED);
                        break;
                }
            } catch(NoSuchElementException e) {
                System.out.println("There was a problem with the server. Aborting.");
                System.exit(1);
            }
        }
    }

    public boolean makeMove(int col) {
        if(this.board.checkTurn(col)) {
            sendMove(col);
            return true;
        }
        return false;
    }

    private void sendMove(int col) {
        this.networkOut.println(MOVE_MADE+" "+col);
    }
}
