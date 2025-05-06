package pt.isec.pa.chess.model.data;

import java.io.Serializable;

public class Player implements Serializable {

    private String name;
    private final boolean isWhite;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isWhite() {
        return isWhite;
    }
}
