package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Board;
import model.Piece;
import server.NetworkHandler;
import java.util.*;

import static server.ServerProtocol.*;

public class PlayerGUI extends Application implements Observer {
    private Board model;
    private NetworkHandler serverCommunicator;
    private static ImageView[] topRow = new ImageView[7];
    private static ImageView[][] gameBoard = new ImageView[7][6];
    private static boolean isTurn;
    private static boolean error;
    private static Label message = new Label();

    private Image tile_empty_img = new Image(getClass().getResourceAsStream("resources/tile_empty.png"));
    private Image tile_yellow_img = new Image(getClass().getResourceAsStream("resources/tile_yellow.png"));
    private Image tile_red_img = new Image(getClass().getResourceAsStream("resources/tile_red.png"));
    private Image bg_img = new Image(getClass().getResourceAsStream("resources/bg.png"));
    private Image bg_yellow_img = new Image(getClass().getResourceAsStream("resources/bg_yellow.png"));
    private Image bg_red_img = new Image(getClass().getResourceAsStream("resources/bg_red.png"));

    private static final String IS_TURN_MSG = "It's your turn. Click to drop your piece.";
    private static final String NOT_TURN_MSG = "It's not your turn. Waiting for opponent.";
    private static final String ERROR_MSG = "That is not a valid move. Try again.";
    private static final String WON_MSG = "You won! Click this message to quit.";
    private static final String LOST_MSG = "You lost :( Click this message to quit.";
    private static final String TIED_MSG = "It was a tie game! Click this message to quit.";

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater( () ->{
            assert o == this.model : "Unexpected subject of observation";
            refresh();
        });
    }

    private void refresh() {
        isTurn = model.isMyTurn();
        if(error) {
            message.setText(ERROR_MSG);
            return;
        }
        else if(isTurn) {
            message.setText(IS_TURN_MSG);
        }
        else {
            message.setText(NOT_TURN_MSG);
        }

        for(ImageView slot: topRow) {
            slot.setImage(bg_img);
        }

        if(this.model.getGameDecision() != null) {
            isTurn = false;
            switch(this.model.getGameDecision()) {
                case GAME_WON:
                    message.setText(WON_MSG);
                    break;
                case GAME_LOST:
                    message.setText(LOST_MSG);
                    break;
                case GAME_TIED:
                    message.setText(TIED_MSG);
                    break;
            }
        }

        for(int i=0;i<model.getLength();i++) {
            for(int j=0;j<model.getHeight();j++) {
                Piece piece = this.model.getSlot(i,j).getPiece();
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
                        error = !this.serverCommunicator.makeMove(col);
                        refresh();
                    }
                });
                slot.setOnMouseClicked(event -> {
                    if(isTurn) {
                        error = !this.serverCommunicator.makeMove(col);
                        refresh();
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
        message.setText(NOT_TURN_MSG);
        message.setFont(Font.font(18));
        message.setOnMouseClicked(event -> {
            if(this.model.getGameDecision() != null) {
                System.exit(0);
            }
        });

        VBox vb = new VBox(message, gp);
        Scene myScene = new Scene(vb);
        vb.setBackground(new Background(new BackgroundFill(Color.web("#0DDEFF"), CornerRadii.EMPTY, Insets.EMPTY)));
        mainStage.setScene(myScene);
        mainStage.setOnCloseRequest(event -> {System.exit(1);});
        mainStage.show();
    }

    @Override
    public void init() {
        this.model = new Board();
        this.model.addObserver( this );

        List<String> params = super.getParameters().getRaw();
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
