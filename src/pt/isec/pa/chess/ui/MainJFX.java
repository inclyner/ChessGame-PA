package pt.isec.pa.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainJFX extends Application {

    private ModelData data;

    public MainJFX() {
        data = new ModelData();
    } // It can also be created in 'init'

    @Override
    public void start(Stage stage) throws Exception {
        RootPane root = new RootPane(data);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("ChessGame");
        stage.show();
    }

    @Override
    public void stop() {
        // Limpar listeners
        if (data != null && data.getBoardFx() != null) {
            data.getBoardFx().cleanup();
        }
    }
}
