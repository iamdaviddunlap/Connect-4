package server;

import ai.AiClient;
import model.ConnectFourGame;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Closeable {
    private ServerSocket server;

    /**
     * creates a new server
     * @param port the port to run it on
     * @throws Exception if there is an error creating the server
     */
    public Server(int port) throws Exception {
        server = new ServerSocket(port);
    }

    /**
     * closes the server
     */
    public void close() {
        try {
            server.close();
        } catch (Exception ex) {
            System.err.println("there was an error closing the server");
        }
    }

    /**
     * main entry point of the program
     * @param args the args for the server
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: <port number> <game mode>");
            System.exit(1);
        }
        try {
            int portNumber = Integer.parseInt(args[0]);
            ServerSocket server = new ServerSocket(portNumber);
            System.out.print("hosting on: ");
            String total = InetAddress.getLocalHost().toString();
            String remove = InetAddress.getLocalHost().getHostName()+"/";
            System.out.println(total.replace(remove,"")+":"+server.getLocalPort());

            System.out.println("Waiting for player one...");
            Socket playerOneSocket = server.accept();
            Player playerOne = new Player(playerOneSocket);
            System.out.println("Player one connected!");

            System.out.println("Waiting for player two...");

            if(Integer.parseInt(args[1]) == 1) { //creates AI player
                AiClient ai = new AiClient(portNumber);
                ai.start();
            }

            Socket playerTwoSocket = server.accept();
            Player playerTwo = new Player(playerTwoSocket);
            System.out.println("Player two connected!");
            System.out.println("Starting game!");

            ConnectFourGame game = new ConnectFourGame(playerOne,playerTwo);
            game.run();
        } catch(IOException e) {e.printStackTrace();}
    }
}
