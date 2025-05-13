package pt.isec.pa.chess.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
}