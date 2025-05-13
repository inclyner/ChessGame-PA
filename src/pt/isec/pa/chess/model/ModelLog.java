package pt.isec.pa.chess.model;

public class ModelLog {
    private static ModelLog instance;
    private StringBuilder log;

    private ModelLog() {
        log = new StringBuilder();
    }

    public static synchronized ModelLog getInstance() {
        if (instance == null) {
            instance = new ModelLog();
        }
        return instance;
    }

    public void addEntry(String entry) {
        log.append(entry).append(System.lineSeparator());
    }

    public String getLog() {
        return log.toString();
    }

    public void clear() {
        log.setLength(0);
    }
}