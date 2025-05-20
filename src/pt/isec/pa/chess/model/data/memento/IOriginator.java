package pt.isec.pa.chess.model.data.memento;

public interface IOriginator {
    IMemento save();
    void restore(IMemento memento);
}
