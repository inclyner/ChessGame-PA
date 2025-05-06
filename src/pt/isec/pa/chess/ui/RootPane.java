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
    MenuItem miNew, miOpen, miSave, miImport, miExport,miShowMoves, miQuit,miUndo,miRedo;
    RadioMenuItem miNormal, miLearning;
    ChessGameManager gameManager;
    Canvas canvas;
    Pane center;
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
        center = new Pane();
        setCenter(center);
        canvas = new BoardFx(gameManager);
        center.getChildren().add(canvas);


    }

    private void registerHandlers() {

        center.widthProperty().addListener(
                (_,_,_) ->{
                    canvas.setWidth(center.getWidth());
                    canvas.setHeight(center.getHeight());
                    update();
                }
        );
        center.heightProperty().addListener(
                (_,_,_) ->{
                    canvas.setWidth(center.getWidth());
                    canvas.setHeight(center.getHeight());
                    update();
                }
        );



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

        miNormal.setOnAction(e -> {
            miUndo.setDisable(true);
            miRedo.setDisable(true);
            miShowMoves.setDisable(true);

        });

        miLearning.setOnAction(e -> {
            miUndo.setDisable(false);
            miRedo.setDisable(false);
            miShowMoves.setDisable(false);
        });

    }

    private void update() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#312e2b"));
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());




    }



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
        miNormal = new RadioMenuItem("Normal");
        miLearning = new RadioMenuItem("Learning");

        ToggleGroup toggleMode = new ToggleGroup();
        miNormal.setToggleGroup(toggleMode);
        miLearning.setToggleGroup(toggleMode);
        miNormal.setSelected(true); // default

        miShowMoves = new MenuItem("Show possible moves");
        miUndo = new MenuItem("Undo");
        miRedo = new MenuItem("Redo");
        miShowMoves.setDisable(true);
        miUndo.setDisable(true);
        miRedo.setDisable(true);


        menuMode.getItems().addAll(miNormal, miLearning, new SeparatorMenuItem(), miShowMoves, miUndo, miRedo);

        menuBar.getMenus().addAll(menuGame, menuMode);
        return menuBar;
    }
}