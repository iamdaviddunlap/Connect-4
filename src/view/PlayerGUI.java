package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Board;
import model.Piece;
import server.NetworkHandler;

import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class PlayerGUI extends Application implements Observer {
    private static Board model;
    private static List<String> params = null;
    private static ImageView[] topRow = new ImageView[7];
    private static ImageView[][] gameBoard = new ImageView[7][6];
    private static boolean isTurn;
    private static String turnMsg;
    private static Label turn = new Label();
    private static NetworkHandler serverCommunicator;

    private Image tile_empty_img = new Image(getClass().getResourceAsStream("resources/tile_empty.png"));
    private Image tile_yellow_img = new Image(getClass().getResourceAsStream("resources/tile_yellow.png"));
    private Image tile_red_img = new Image(getClass().getResourceAsStream("resources/tile_red.png"));
    private Image bg_img = new Image(getClass().getResourceAsStream("resources/bg.png"));
    private Image bg_yellow_img = new Image(getClass().getResourceAsStream("resources/bg_yellow.png"));
    private Image bg_red_img = new Image(getClass().getResourceAsStream("resources/bg_red.png"));

    private static final String IS_TURN_MSG = "It's your turn. Click to drop your piece.";
    private static final String NOT_TURN_MSG = "It's not your turn. Waiting for opponent.";
    private static final String NO_CONNECTION_MSG = "Waiting for your opponent to connect.";

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater( () ->{
            assert o == this.model : "Unexpected subject of observation";
            refresh();
        });
    }

    public void refresh() {
        System.out.println("refresh");
        isTurn = model.isMyTurn();
        if(isTurn) {
            turn.setText(IS_TURN_MSG);
        }
        else {
            turn.setText(NOT_TURN_MSG);
        }

        for(int i=0;i<topRow.length;i++) {
            topRow[i].setImage(bg_img);
        }

        System.out.println(this.model);
        for(int i=0;i<model.getLength();i++) {
            for(int j=0;j<model.getHeight();j++) {
                Piece piece = this.model.getSlot(i,j).getPiece();
                System.out.println(piece);
                if(piece == null) {
                    gameBoard[i][j].setImage(tile_empty_img);
                }
                else if(piece.getColor() == Piece.Color.YELLOW) {
                    gameBoard[i][j].setImage(tile_yellow_img);
                }
                else {
                    gameBoard[i][j].setImage(tile_red_img);
                }
            }
        }
    }

    public void start( Stage mainStage ) {
        GridPane gp = new GridPane();
        for(int i=0;i<model.getLength();i++) {
            for (int j = 0; j < model.getHeight()+1; j++) {
                ImageView slot = new ImageView(tile_empty_img);
                ImageView back = new ImageView(bg_img);

                back.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                    if(isTurn) {
                        if(model.getActiveColor() == Piece.Color.YELLOW) {
                            back.setImage(bg_yellow_img);
                        }
                        else {
                            back.setImage(bg_red_img);
                        }
                    }
                });
                back.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                    if(isTurn) {
                        back.setImage(bg_img);
                    }
                });

                int col = i;
                slot.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                    if(isTurn) {
                        if(model.getActiveColor() == Piece.Color.YELLOW) {
                            topRow[col].setImage(bg_yellow_img);
                        }
                        else {
                            topRow[col].setImage(bg_red_img);
                        }
                    }
                });
                slot.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                    if(isTurn) {
                        topRow[col].setImage(bg_img);
                    }
                });

                back.setOnMouseClicked(event -> {
                    if(isTurn) {
                        System.out.println("clicked");
                        this.serverCommunicator.sendMove(col);
                    }
                });
                slot.setOnMouseClicked(event -> {
                    if(isTurn) {
                        this.serverCommunicator.sendMove(col);
                    }
                });

                if(j==0) {
                    gp.add(back, i, j);
                    topRow[i] = back;
                }
                else {
                    gp.add(slot,i,j);
                    gameBoard[i][j-1] = slot;
                }
            }
        }
        turn.setText(NOT_TURN_MSG);
        VBox vb = new VBox(turn, gp);
        Scene myScene = new Scene(vb);
        mainStage.setScene(myScene);
        mainStage.show();
    }

    @Override
    public void init() throws Exception {
        this.model = new Board();
        this.model.addObserver( this );

        params = super.getParameters().getRaw();
        if (params.size() != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        this.serverCommunicator = new NetworkHandler(params.get(0),Integer.valueOf(params.get(1)),model);
    }

    public static void main( String[] args ) {
        Application.launch( args );
    }
}
