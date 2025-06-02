package pt.isec.pa.chess.ui;

import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.ChessGame;

public class ModelData {

    private ChessGameManager gameManager;
    private BoardFx boardFx;

    public ModelData() {
        // Create game manager
        gameManager = new ChessGameManager();

        // Create board UI
        boardFx = new BoardFx(gameManager);

        gameManager.setPromotionHandler(boardFx);
    }

    public ChessGameManager getGameManager() {
        return gameManager;
    }

    public BoardFx getBoardFx() {
        return boardFx;
    }
}
