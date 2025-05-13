package pt.isec.pa.chess.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ModelLog;

import java.util.Arrays;

public class LogWindow extends Stage {
    private ListView<String> listView;

    public LogWindow() {
        setTitle("Logs");

        // Teste: adicionar uma entrada ao log
        ModelLog.getInstance().addEntry("Janela de logs aberta!");

        listView = new ListView<>();
        updateLog();

        Button btnRefresh = new Button("Atualizar");
        btnRefresh.setOnAction(e -> updateLog());

        BorderPane root = new BorderPane(listView);
        root.setBottom(btnRefresh);

        setScene(new Scene(root, 400, 300));
    }

    private void updateLog() {
        String log = ModelLog.getInstance().getLog();
        listView.getItems().setAll(Arrays.asList(log.split("\\R")));
    }
}

