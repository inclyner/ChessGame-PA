package pt.isec.pa.chess.ui;

import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.ChessGame;

public class ModelData {
    private ChessGameManager gameManager;

    public ModelData() {
        gameManager = new ChessGameManager(new ChessGame());
    }

    public ChessGameManager getGameManager() {
        return gameManager;
    }


}
