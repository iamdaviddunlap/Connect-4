package server;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Closeable {
    private Socket sock; //to communicate with client
    private Scanner scanner; //to read responses from the client
    private PrintStream printer; //to send requests to the client

    public Player(Socket sock) {
        this.sock = sock;
        try {
            this.scanner = new Scanner(sock.getInputStream());
            this.printer = new PrintStream(sock.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called to close the client connection after the game is over.
     */
    @Override
    public void close() {
        try {
            this.sock.close();
        }
        catch(IOException ioe) {
            // squash
        }
    }
}
