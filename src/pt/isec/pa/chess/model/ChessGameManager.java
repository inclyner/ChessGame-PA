package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.Square;
import pt.isec.pa.chess.model.data.Board;

public class ChessGameManager {

    ChessGame game;
    public ChessGameManager(ChessGame game) {
        this.game = game;
    }

    public boolean startGame(String player1, String player2) {
        return game.startGame(player1, player2);

    }
    public boolean move(Square from, Square to) {
        return game.move(from, to);
    }
    public boolean checkEndGame() {
        return game.checkEndGame();}

    public void importGame(String gameState){
        game.importGame(gameState);
    }
    public String exportGame() {
        return game.exportGame();
    }


    public String getPieceAt(int row, int col) {
        return null;
    }

    public Board getBoard() {
        return game.getBoard();
    }

}
