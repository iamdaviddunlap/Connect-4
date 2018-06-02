package server;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Closeable, ServerProtocol {
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


    public int makeMove() throws Exception {
        this.printer.println(MAKE_MOVE);
        String response = scanner.nextLine();
        int col;
        if(response.startsWith(MOVE_MADE)) {
            try {
                col = Integer.parseInt(response.split(" ")[1]);
            } catch(Exception e) {throw new Exception("Invalid player response.");}
        }
        else {
            throw new Exception("Invalid player response.");
        }
        return col;
    }

    public void moveMade(int col) {
        this.printer.println(MOVE_MADE+" "+col);
    }

    public void gameWon() {
        this.printer.println(GAME_WON);
    }

    public void gameLost() {
        this.printer.println(GAME_LOST);
    }

    public void gameTied() {
        this.printer.println(GAME_TIED);
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
