package pt.isec.pa.chess.model.data.memento;

public interface IMemento {
    default Object getSnapshot() {
        return null;
    }
}
