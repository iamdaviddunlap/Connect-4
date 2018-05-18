package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Board;

import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class PlayerGUI extends Application implements Observer {
    private static Board model;
    private List<String> params = null;
    private ImageView[] topRow = new ImageView[7];
    private boolean isTurn;

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater( () ->{
            assert o == this.model : "Unexpected subject of observation";
            refresh();
        });
    }

    public void refresh() {
        System.out.println("refresh");
        //TODO: main JavaFX method
    }

    public void start( Stage mainStage ) {
        GridPane gp = new GridPane();
        for(int i=0;i<model.getLength();i++) {
            for (int j = 0; j < model.getHeight()+1; j++) {
                Image slot_img = new Image(getClass().getResourceAsStream("resources/tile_empty.png"));
                Image bg_img = new Image(getClass().getResourceAsStream("resources/bg.png"));
                Image bg_yellow_img = new Image(getClass().getResourceAsStream("resources/bg_yellow.png"));

                ImageView slot = new ImageView(slot_img);
                ImageView back = new ImageView(bg_img);

                back.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                    if(isTurn) {
                        back.setImage(bg_yellow_img);
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
                        topRow[col].setImage(bg_yellow_img);
                    }
                });
                slot.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                    if(isTurn) {
                        topRow[col].setImage(bg_img);
                    }
                });

                if(j==0) {
                    gp.add(back, i, j);
                    topRow[i] = back;
                }
                else {
                    gp.add(slot,i,j);
                }
            }
        }
        Scene myScene = new Scene(gp);
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

        Socket sock = new Socket(params.get(0),Integer.valueOf(params.get(1)));
        Scanner networkIn = new Scanner( sock.getInputStream() );
        PrintStream networkOut = new PrintStream( sock.getOutputStream() );
    }

    public static void main( String[] args ) {
        Application.launch( args );
    }
}
