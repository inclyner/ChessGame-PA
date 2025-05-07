package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.Square;
import pt.isec.pa.chess.ui.PromotionHandler;

public class ChessGameManager {

    private ChessGame game;
    private PromotionHandler promotionHandler;

    public ChessGameManager(ChessGame game, PromotionHandler handler) {
        this.game = game;
        this.promotionHandler = handler;
        this.game.getBoard().setPromotionHandler(handler);
    }

    public boolean startGame(String player1, String player2) {
        return game.startGame(player1, player2);
    }

    public boolean move(Square from, Square to) {
        return game.move(from, to);
    }

    public void importGame(String gameState) {
        game.importGame(gameState);
    }

    public String exportGame() {
        return game.exportGame();
    }

    public ChessGame getGame() {
        return game;
    }

    public Board getBoard() {
        return game.getBoard();
    }

    public String getPieceAt(int col, int row) {
        if(game.getBoard().getPieceAt(col, row)!= null) {
            return game.getBoard().getPieceAt(col, row).toString();
        }
        return "0";
    }
}
