package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.Square;
import pt.isec.pa.chess.ui.Point;
import pt.isec.pa.chess.ui.PromotionHandler;

import java.util.ArrayList;

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

    public boolean move(Point from, Point to) {
        Square fromSquare = new Square(from.x(), from.y());
        Square toSquare = new Square(to.x(), to.y());
        return game.move(fromSquare, toSquare);
    }

    public void importGame(String gameState) {
        game.importGame(gameState);
    }

    public String exportGame() {
        return game.exportGame();
    }




    public String getPieceAt(int col, int row) {
        if(game.getBoard().getPieceAt(col, row)!= null) {
            return game.getBoard().getPieceAt(col, row).toString();
        }
        return null;
    }

    public boolean isWhitePlaying() {
        return game.getCurrentPlayer().isWhite();
    }

    public boolean isWithinBounds(int col, int row) {
        return game.isWithinBounds(col, row);
    }

    public ArrayList<Point> getValidMovesAt(int col, int row) {
        //convert ArrayList<Square> to ArrayList<Point>
        ArrayList<Point> validMoves = new ArrayList<>();
        for (Square square : game.getBoard().getPieceAt(col, row).getMoves(game.getBoard())) {
            validMoves.add(new Point(square.column(), square.row()));
        }
        return validMoves;
    }

}
