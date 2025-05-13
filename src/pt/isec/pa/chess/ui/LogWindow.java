package pt.isec.pa.chess.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ModelLog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

public class LogWindow extends Stage implements PropertyChangeListener {
    private ListView<String> listView;
    private final ModelLog modelLog;

    public LogWindow() {
        setTitle("Logs");
        modelLog = ModelLog.getInstance();
        
        // Registrar esta janela como listener
        modelLog.addPropertyChangeListener(this);
        

        listView = new ListView<>();
        updateLog();

        Button btnRefresh = new Button("Atualizar");
        btnRefresh.setOnAction(e -> updateLog());
        
        Button btnClear = new Button("Limpar");
        btnClear.setOnAction(e -> modelLog.clear());
        
        // Criar layout com os botões lado a lado
        BorderPane buttonPane = new BorderPane();
        BorderPane root = new BorderPane(listView);
        
        BorderPane.setAlignment(btnRefresh, javafx.geometry.Pos.CENTER_LEFT);
        BorderPane.setAlignment(btnClear, javafx.geometry.Pos.CENTER_RIGHT);
        buttonPane.setLeft(btnRefresh);
        buttonPane.setRight(btnClear);
        root.setBottom(buttonPane);

        setScene(new Scene(root, 400, 300));
        setOnShown(e -> updateLog());
        setOnCloseRequest(e -> modelLog.removePropertyChangeListener(this));
    }

    private void updateLog() {
        String log = modelLog.getLog();
        listView.getItems().setAll(Arrays.asList(log.split("\\R")));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // executar a atualização na thread da UI
        javafx.application.Platform.runLater(() -> {
            switch (evt.getPropertyName()) {
                case ModelLog.PROP_LOG_ENTRY_ADDED, ModelLog.PROP_LOG_CLEARED -> updateLog();
            }
        });
    }
}

