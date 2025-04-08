package pt.isec.pa.chess.model.data;

import java.io.Serializable;

public class Player implements Serializable {
    private final boolean isWhite;
    private int score;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
        this.score = 0; // Score é o valor total de peças capturadas
    }

    public boolean isWhite() {
        return isWhite;
    }

    public String getColor() { // Nome do jogador, pode ser "WHITE" ou "BLACK"
        return isWhite ? "WHITE" : "BLACK";
    }

    public void incrementScore() { // Peças mais valiosas valem mais pontos
        score++;
    }

    public int getScore() {
        return score;
    }
}
