package pt.isec.pa.chess.model.data.memento;

import java.io.*;

public class Memento implements IMemento{
    private final byte[] snapshot;

    public Memento(Object obj) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(obj);
            snapshot = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create memento", e);
        }
    }

    @Override
    public Object getSnapshot() {
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(snapshot);
                ObjectInputStream ois = new ObjectInputStream(bais)
        ) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to restore memento", e);
        }
    }
}
