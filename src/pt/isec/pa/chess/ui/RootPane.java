package pt.isec.pa.chess.ui;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;

import java.io.File;
import java.io.PrintWriter;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RootPane extends BorderPane { //View-Controller
    ModelData data;
    MenuBar menuBar;
    MenuItem miNew, miOpen, miSave, miImport, miExport, miQuit,miUndo,miRedo;
    CheckMenuItem miShowMoves;
    RadioMenuItem miNormal, miLearning;
    ChessGameManager gameManager;
    Canvas canvas;
    Pane center;
    String whiteName, blackName;
    private MenuItem miLogs, miNotifications;
    private CheckMenuItem miSound;
    private boolean soundEnabled = true;


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
        
        // Just use bindings, don't set the size directly
        canvas.widthProperty().bind(center.widthProperty());
        canvas.heightProperty().bind(center.heightProperty());

        miSound.setSelected(true); // Sound on by default
        miSound.setOnAction(e -> {
            ((BoardFx)canvas).setSoundEnabled(miSound.isSelected());
        });
    }

    private void registerHandlers() {

        miNew.setOnAction(actionEvent -> {
            AskName askName = new AskName(data);
            askName.showAndWait();
            AskName askName2 = new AskName(data);
            askName2.showAndWait();
            whiteName=askName.tfName.getText();
            blackName=askName2.tfName.getText();
            ((BoardFx)canvas).setPlayerNames(whiteName, blackName);
            gameManager.startGame(whiteName, blackName);

            update(); // dá refresh na tela
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
            // Show file open dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Chess Game");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Chess Game (*.chess)", "*.chess"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (file != null) {
                try {
                    // Read the entire file content
                    StringBuilder content = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                    }
                    
                    String gameData = content.toString();
                    if (gameData.endsWith("\n")) {
                        gameData = gameData.substring(0, gameData.length() - 1);
                    }
                    
                    // Import the game data
                    gameManager.importGame(gameData);
                    
                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Imported");
                    alert.setHeaderText("Game loaded successfully");
                    alert.setContentText("Jogo importado de: " + file.getName());
                    alert.showAndWait();
                    
                    // Add log entry
                    ModelLog.getInstance().addEntry("Jogo importado de: " + file.getName());
                    
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Import Error");
                    alert.setHeaderText("Erro a importar jogo");
                    alert.setContentText("Could not read file: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        miExport.setOnAction(e -> {
            // Get the exported game string
            String exportedGame = gameManager.exportGame();
            
            // Show file save dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Game");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Chess Game (*.chess)", "*.chess"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            
            File file = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.print(exportedGame);
                    
                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Exported");
                    alert.setHeaderText("Game saved successfully");
                    alert.setContentText("Jogo exportado para: " + file.getAbsolutePath());
                    alert.showAndWait();
                    
                    // Add log entry
                    ModelLog.getInstance().addEntry("Jogo exportado para: " + file.getName());
                } catch (Exception ex) {
                    // Show error message if saving fails
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Export Error");
                    alert.setHeaderText("Error a exportar o jogo");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
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

        gameManager.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case ChessGameManager.PROP_BOARD_STATE, ChessGameManager.PROP_CURRENT_PLAYER -> update();
                case ChessGameManager.PROP_GAME_OVER -> {
                    update();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Fim de Jogo");
                    alert.setHeaderText("Jogo Terminado");
                    alert.setContentText(evt.getNewValue().toString());
                    alert.showAndWait();
                }
                case ChessGameManager.PROP_CHECK_STATE -> {
                    update();
                }
            }
        });


        miLogs.setOnAction(e -> {
            LogWindow logWindow = new LogWindow();
            logWindow.show();
        });
        
        miNotifications.setOnAction(e -> {
            NotificationWindow notificationWindow = new NotificationWindow();
            notificationWindow.show();
        });

        miShowMoves.setOnAction(e -> {
            ((BoardFx)canvas).setShowMoves(miShowMoves.isSelected());
        });

        miUndo.setOnAction(e -> gameManager.undo());
        miRedo.setOnAction(e -> gameManager.redo());

    }

    private void update() {
        if(miLearning.isSelected()) {
            miUndo.setDisable(!gameManager.hasUndo());
            miRedo.setDisable(!gameManager.hasRedo());
        }
        ((BoardFx)canvas).draw();
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
        miLogs = new MenuItem("Logs");
        miNotifications = new MenuItem("Notifications");
        miSound = new CheckMenuItem("Sound");

        menuGame.getItems().addAll(
            miNew, miOpen, miSave, new SeparatorMenuItem(),
            miImport, miExport, new SeparatorMenuItem(),
            miLogs, miNotifications,
            new SeparatorMenuItem(),
            miSound,
            miQuit
        );

        Menu menuMode = new Menu("Mode");
        miNormal = new RadioMenuItem("Normal");
        miLearning = new RadioMenuItem("Learning");

        ToggleGroup toggleMode = new ToggleGroup();
        miNormal.setToggleGroup(toggleMode);
        miLearning.setToggleGroup(toggleMode);
        miNormal.setSelected(true);

        miShowMoves = new CheckMenuItem("Show possible moves");
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