package pt.isec.pa.chess.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.scene.control.Alert;

public class ModelLog {
    // Constantes para identificar eventos
    public static final String PROP_LOG_ENTRY_ADDED = "logEntryAdded";
    public static final String PROP_LOG_CLEARED = "logCleared";

    private static ModelLog instance;
    private StringBuilder log;
    private final PropertyChangeSupport pcs;

    private ModelLog() {
        log = new StringBuilder();
        pcs = new PropertyChangeSupport(this);
    }

    public static synchronized ModelLog getInstance() {
        if (instance == null) {
            instance = new ModelLog();
        }
        return instance;
    }

    public void addEntry(String entry) {
        String oldLog = log.toString();
        log.append(entry).append(System.lineSeparator());
        String newLog = log.toString();
        pcs.firePropertyChange(PROP_LOG_ENTRY_ADDED, oldLog, newLog);
    }

    public String getLog() {
        return log.toString();
    }

    public void clear() {
        String oldLog = log.toString();
        log.setLength(0);
        pcs.firePropertyChange(PROP_LOG_CLEARED, oldLog, "");
    }

    // Métodos os listeners
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void importGame(File file) {
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

            addEntry(gameData);

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText("Error importing game");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
}