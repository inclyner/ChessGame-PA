package pt.isec.pa.chess.ui;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.ChessGameManager;

import java.io.File;

public class RootPane extends BorderPane { //View-Controller
    ModelData data;
    MenuBar menuBar;
    MenuItem miNew, miOpen, miSave, miImport, miExport, miQuit;
    ChessGameManager gameManager;


    // variables, including reference to views
    public RootPane(ModelData data) {
        this.data = data;
        this.gameManager = data.getGameManager();
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        setTop(createMenu());


    }

    private void registerHandlers() {
        miNew.setOnAction(actionEvent -> {
            AskName askName = new AskName(data);
            askName.showAndWait();
            AskName askName2 = new AskName(data);
            askName.showAndWait();
            gameManager.startGame(askName.tfName.getText(), askName2.tfName.getText());

        });

        miOpen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("File open...");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PA Files (*.paf)", "*.paf"),
                    new FileChooser.ExtensionFilter("All", "*.*")
            );
            File hFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (hFile != null) {
                System.out.println(hFile.getAbsolutePath());


            }
        });
        miSave.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("File save...");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PA Files (*.paf)", "*.paf"),
                    new FileChooser.ExtensionFilter("All", "*.*")
            );
            File hFile = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (hFile != null) {
                System.out.println(hFile.getAbsolutePath());
            }
        });

        miImport.setOnAction(e -> {
            AskName askName = new AskName(data);
            askName.showAndWait();
            gameManager.importGame(askName.tfName.getText());
        });
        miExport.setOnAction(e -> {
            gameManager.exportGame();
        });
        miQuit.setOnAction(actionEvent -> {
            Platform.exit();
        });

    }

    private void update() { /* update views */ }



    private MenuBar createMenu() {
        menuBar = new MenuBar();

        Menu menuGame = new Menu("Game");
        miNew = new MenuItem("New");
        miOpen = new MenuItem("Open");
        miSave = new MenuItem("Save");
        miImport = new MenuItem("Import");
        miExport = new MenuItem("Export");
        miQuit = new MenuItem("Quit");

        menuGame.getItems().addAll(miNew, miOpen, miSave, new SeparatorMenuItem(), miImport, miExport, new SeparatorMenuItem(), miQuit);

        Menu menuMode = new Menu("Mode");
        RadioMenuItem miNormal = new RadioMenuItem("Normal");
        RadioMenuItem miLearning = new RadioMenuItem("Learning");

        ToggleGroup toggleMode = new ToggleGroup();
        miNormal.setToggleGroup(toggleMode);
        miLearning.setToggleGroup(toggleMode);
        miNormal.setSelected(true); // default

        CheckMenuItem miShowMoves = new CheckMenuItem("Show possible moves");
        MenuItem miUndo = new MenuItem("Undo");
        MenuItem miRedo = new MenuItem("Redo");
        miUndo.setDisable(true);
        miRedo.setDisable(true);

        menuMode.getItems().addAll(miNormal, miLearning, new SeparatorMenuItem(), miShowMoves, miUndo, miRedo);

        menuBar.getMenus().addAll(menuGame, menuMode);
        return menuBar;
    }
}