package ai;

import model.Board;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static server.ServerProtocol.*;
import static server.ServerProtocol.GAME_LOST;
import static server.ServerProtocol.GAME_TIED;

public class AiClient extends Thread{

    private int portNumber;
    private String hostName = "localhost";
    private Scanner networkIn;
    private PrintStream networkOut;
    private Board model;
    private Solver solver;

    public AiClient(int portNumber) {
        this.portNumber = portNumber;
    }


    @Override
    public void run() {
        try {
            Socket sock = new Socket( hostName, portNumber ); //communicate with server
            this.networkIn = new Scanner( sock.getInputStream() );
            this.networkOut = new PrintStream( sock.getOutputStream() );
            this.model = new Board();
            this.solver = new Solver();

            while(true) {
                String request = this.networkIn.next();
                String arguments = this.networkIn.nextLine().trim();
                switch (request) {
                    case MAKE_MOVE:
                        this.model.canMakeMove();
                        break;
                    case MOVE_MADE:
                        this.model.makeMove(Integer.parseInt(arguments));
                        break;
                    case GAME_WON:
                        this.model.setGameDecision(GAME_WON);
                        break;
                    case GAME_LOST:
                        this.model.setGameDecision(GAME_LOST);
                        break;
                    case GAME_TIED:
                        this.model.setGameDecision(GAME_TIED);
                        break;
                }
            }

        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }

}
