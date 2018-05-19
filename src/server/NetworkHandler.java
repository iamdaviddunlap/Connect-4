package server;

import model.Board;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import static server.ServerProtocol.*;

public class NetworkHandler {

    private Socket sock; //communicate with server

    private Scanner networkIn; //read requests from server

    private PrintStream networkOut; //write responses to server

    private Board board; //the game board

    private boolean canGo;

    public NetworkHandler( String hostname, int port, Board model ) {
        try {
            this.sock = new Socket( hostname, port );
            this.networkIn = new Scanner( sock.getInputStream() );
            this.networkOut = new PrintStream( sock.getOutputStream() );
            this.board = model;
            this.canGo = true;

            Thread netThread = new Thread( () -> this.run() );
            netThread.start();
        } catch(IOException e) {e.printStackTrace();}
    }

    private synchronized boolean canGo() {
        return canGo;
    }

    private void run() {
        while(this.canGo()) {
            String request = this.networkIn.next();
            String arguments = this.networkIn.nextLine().trim();
            System.out.println( "Net message in = \"" + request + '"' );
            switch(request) {
                case MAKE_MOVE:
                    System.out.println("network recognized MAKE_MOVE");
                    this.board.canMakeMove();
                    break;
                case MOVE_MADE:
                    System.out.println("network recognized "+request+" "+arguments);
                    this.board.makeMove(Integer.parseInt(arguments));
                    break;
            }
        }
    }

    public void sendMove(int col) {
        System.out.println("Column: "+col);
        this.networkOut.println(MOVE_MADE+" "+col);
    }
}
