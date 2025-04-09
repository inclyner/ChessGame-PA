package pt.isec.pa.chess.ui;

import javafx.scene.layout.VBox;

public class RootPane extends VBox { //View-Controller
    ModelData data;

    // variables, including reference to views
    public RootPane(ModelData data) {
        this.data = data;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() { /* create and configure views */ }

    private void registerHandlers() { /* handlers/listeners */ }

    private void update() { /* update views */ }
}