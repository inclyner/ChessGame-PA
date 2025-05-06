package pt.isec.pa.chess.ui;

import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.ChessGame;

public class ModelData {

    private ChessGameManager gameManager;
    private BoardFx boardFx;

    public ModelData() {
        ChessGame game = new ChessGame();
        gameManager = new ChessGameManager(game, null); // Temporarily pass null
        boardFx = new BoardFx(gameManager);
        gameManager.getBoard().setPromotionHandler(boardFx); // Set the handler after creation
    }

    public ChessGameManager getGameManager() {
        return gameManager;
    }

    public BoardFx getBoardFx() {
        return boardFx;
    }
}
