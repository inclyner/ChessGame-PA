package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.Square;
import pt.isec.pa.chess.ui.Point;
import pt.isec.pa.chess.ui.PromotionHandler;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class ChessGameManager {

    private ChessGame game;
    private PromotionHandler promotionHandler;
    private final PropertyChangeSupport pcs;
    public static final String PROP_BOARD_STATE = "boardState";
    public static final String PROP_CURRENT_PLAYER = "currentPlayer";
    String player1, player2;



    public ChessGameManager(ChessGame game, PromotionHandler handler) {
        this.game = game;
        this.promotionHandler = handler;
        this.game.getBoard().setPromotionHandler(handler);
        pcs = new PropertyChangeSupport(this);
    }

    public boolean startGame(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        if( game.startGame(player1, player2)){
            pcs.firePropertyChange(PROP_BOARD_STATE, null,null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, player1, player2);
            return true;
        }
        return false;
    }

    public boolean move(Point from, Point to) {
        Square fromSquare = new Square(from.x(), from.y());
        Square toSquare = new Square(to.x(), to.y());

        if (game.move(fromSquare, toSquare)){
            pcs.firePropertyChange(PROP_BOARD_STATE,null,null); //? aqui supostamente teriamos a board antiga e a nova para poder fazer o undo e o redo, mas não podemos ter uma board na facade certo
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, null);
            return true;
        }
        return false;
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

    public int getBoardSize() {
        return game.getBoardSize();
    }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }


}
