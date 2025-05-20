package pt.isec.pa.chess.model.data.memento;

import java.util.ArrayDeque;
import java.util.Deque;

public class ChessGameCaretaker {
    private final IOriginator originator;
    private final Deque<IMemento> history = new ArrayDeque<>();
    private final Deque<IMemento> redoHist = new ArrayDeque<>();

    public ChessGameCaretaker(IOriginator originator) {
        this.originator = originator;
    }

    public void save() {
        redoHist.clear();
        history.push(originator.save());
    }

    public void undo() {
        if (history.isEmpty()) return;
        redoHist.push(originator.save());
        originator.restore(history.pop());
        redoHist.push(originator.save());
        originator.restore(history.pop());
    }

    public void redo() {
        if (redoHist.isEmpty()) return;
        history.push(originator.save());
        originator.restore(redoHist.pop());
        history.push(originator.save());
        originator.restore(redoHist.pop());
    }

    public void reset() {
        history.clear();
        redoHist.clear();
    }

    public boolean hasUndo() { return !history.isEmpty(); }
    public boolean hasRedo() { return !redoHist.isEmpty(); }
}