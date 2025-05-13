package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.Board;
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
    public static final String PROP_GAME_OVER = "gameOver";
    public static final String PROP_CHECK_STATE = "checkState";
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
        if(game.startGame(player1, player2)){
            // Adicionar log
            ModelLog.getInstance().addEntry("Jogo iniciado: " + player1 + " (Brancas) vs " + player2 + " (Pretas)");
            
            pcs.firePropertyChange(PROP_BOARD_STATE, null,null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, player1, player2);
            return true;
        }
        return false;
    }

    public boolean move(Point from, Point to) {
        Square fromSquare = new Square(from.x(), from.y());
        Square toSquare = new Square(to.x(), to.y());

        // Verificar se há peça na posição de destino (para registrar captura)
        String pieceAtTarget = getPieceAt(to.x(), to.y());
        
        if (game.move(fromSquare, toSquare)) {
            // Converter coordenadas para notação de xadrez (ex: e2-e4)
            String fromNotation = columnToLetter(from.x()) + (8 - from.y());
            String toNotation = columnToLetter(to.x()) + (8 - to.y());
            
            // Registrar movimento no log
            if (pieceAtTarget != null) {
                ModelLog.getInstance().addEntry(
                    (isWhitePlaying() ? "Brancas" : "Pretas") + 
                    " moveram de " + fromNotation + " para " + toNotation + 
                    " e capturaram " + pieceAtTarget);
            } else {
                ModelLog.getInstance().addEntry(
                    (isWhitePlaying() ? "Brancas" : "Pretas") + 
                    " moveram de " + fromNotation + " para " + toNotation);
            }
            
            // Verificar estado do jogo após movimento
            Board.GameResult result = game.getBoard().getGameResult();
            switch (result) {
                case WHITE_WINS:
                    ModelLog.getInstance().addEntry("XEQUE-MATE! Brancas (" + player1 + ") vencem.");
                    pcs.firePropertyChange(PROP_GAME_OVER, null, "Brancas (" + player1 + ") vencem por xeque-mate.");
                    break;
                case BLACK_WINS:
                    ModelLog.getInstance().addEntry("XEQUE-MATE! Pretas (" + player2 + ") vencem.");
                    pcs.firePropertyChange(PROP_GAME_OVER, null, "Pretas (" + player2 + ") vencem por xeque-mate.");
                    break;
                case STALEMATE:
                    ModelLog.getInstance().addEntry("EMPATE por afogamento (stalemate)!");
                    pcs.firePropertyChange(PROP_GAME_OVER, null, "Empate por afogamento (stalemate).");
                    break;
                case IN_PROGRESS:
                    // Check for check
                    if (game.getBoard().isPlayerInCheck(!isWhitePlaying())) {
                        ModelLog.getInstance().addEntry("XEQUE! " + 
                            (isWhitePlaying() ? "Pretas" : "Brancas") + " em xeque.");
                        pcs.firePropertyChange(PROP_CHECK_STATE, null, 
                            (isWhitePlaying() ? "Pretas" : "Brancas") + " em xeque.");
                    }
                    break;
            }
            
            pcs.firePropertyChange(PROP_BOARD_STATE, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, null);
            return true;
        }
        return false;
    }
    
    // Método auxiliar para converter índice da coluna para letra
    private String columnToLetter(int column) {
        return String.valueOf((char)('a' + column));
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

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(propertyName, listener);
        }

        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(propertyName, listener);
        }
}
