/**
 * Classe fachada observável entre a interface gráfica (UI) e a lógica do jogo (modelo).
 * Responsável por coordenar o fluxo de jogo, atualizar a UI através de PropertyChangeSupport,
 * gerir undo/redo com o padrão Memento, e encapsular interações externas com o modelo.
 *
 * Não contém lógica de jogo diretamente, mas delega-a para a classe {@link ChessGame}.
 * Também regista logs e gere serialização.
 */

package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.GameResult;
import pt.isec.pa.chess.model.data.Square;
import pt.isec.pa.chess.model.data.memento.ChessGameCaretaker;
import pt.isec.pa.chess.ui.Point;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class ChessGameManager {

    private ChessGame game;
    private pt.isec.pa.chess.ui.PromotionHandler promotionHandler;
    private transient final PropertyChangeSupport pcs;
    public static final String PROP_BOARD_STATE = "boardState";
    public static final String PROP_CURRENT_PLAYER = "currentPlayer";
    public static final String PROP_GAME_OVER = "gameOver";
    public static final String PROP_CHECK_STATE = "checkState";
    String player1, player2;
    private final ChessGameCaretaker caretaker;



    public ChessGameManager(ChessGame game, pt.isec.pa.chess.ui.PromotionHandler handler) {
        this.game = game;
        this.promotionHandler = handler;
        // REMOVE this line - Board doesn't need promotion handler
        // this.game.getBoard().setPromotionHandler(handler);
        
        // ADD this line - ChessGame needs it
        this.game.setPromotionHandler(new PromotionHandlerAdapter(handler));
        
        pcs = new PropertyChangeSupport(this);
        this.caretaker = new ChessGameCaretaker(game);
    }
    public ChessGameManager() {
        this.game = new ChessGame(); // Initialize game
        pcs = new PropertyChangeSupport(this);
        this.caretaker = new ChessGameCaretaker(game); // pass it to caretaker
    }
    /**
     * Inicia um novo jogo com os nomes dos jogadores.
     * @param player1 Nome do jogador branco
     * @param player2 Nome do jogador preto
     * @return true se o jogo foi iniciado
     */
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
    /**
     * Move uma peça da posição inicial para a posição final.
     * Atualiza o log, notifica observadores e verifica o estado do jogo.
     * @param from Coordenadas iniciais (como Point)
     * @param to Coordenadas finais (como Point)
     * @return true se o movimento foi realizado
     */
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
            GameResult result = game.getGameResult();
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
                    if (game.isPlayerInCheck(!isWhitePlaying())) {
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
        try {
            // Preserve promotion handler before import
            if (this.promotionHandler != null) {
                game.setPromotionHandler(new PromotionHandlerAdapter(this.promotionHandler));
            }
            
            game.importGame(gameState);
            
            // Atualizar a vista após importação bem-sucedida
            pcs.firePropertyChange(PROP_BOARD_STATE, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, null);
            
            // Log de sucesso
            ModelLog.getInstance().addEntry("Jogo importado com sucesso.");
        } catch (IllegalArgumentException e) {
            // Log do erro
            ModelLog.getInstance().addEntry("Erro ao importar jogo: " + e.getMessage());
            
            // Opcional: mostrar uma mensagem de erro para o usuário
            new Alert(Alert.AlertType.ERROR,
                     "Formato de jogo inválido.\nVerifique se o arquivo está no formato correto.",
                     ButtonType.OK).showAndWait();
        }
    }

    public String exportGame() {
        return game.exportGame();
    }

    /**
     * Obtém o nome da peça numa casa específica do tabuleiro.
     * @param col Coluna
     * @param row Linha
     * @return Nome da peça (ou null se vazio)
     */
    public String getPieceAt(int col, int row) {
        if(game.getPieceAt(col, row)!= null) {
            return game.getPieceAt(col, row).toString();
        }
        return null;
    }

    public boolean isWhitePlaying() {
        return game.getCurrentPlayer().isWhite();
    }
    /**
     * Verifica se uma posição está dentro dos limites do tabuleiro.
     * @param col Coluna
     * @param row Linha
     * @return true se estiver dentro dos limites
     */
    public boolean isWithinBounds(int col, int row) {
        return game.isWithinBounds(col, row);
    }
    /**
     * Retorna os movimentos válidos da peça na posição especificada.
     * @param col Coluna da peça
     * @param row Linha da peça
     * @return Lista de posições para onde a peça pode mover
     */
    public ArrayList<Point> getValidMovesAt(int col, int row) {
        return game.getValidMovesAt(col,row);
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

    //memento
    public void undo() {
        caretaker.undo();
        pcs.firePropertyChange(PROP_BOARD_STATE, null, null);
        pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, null);
    }
    public void redo() { 
        caretaker.redo();
        pcs.firePropertyChange(PROP_BOARD_STATE, null, null);
        pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, null);
    }
    public boolean hasUndo() { return caretaker.hasUndo(); }
    public boolean hasRedo() { return caretaker.hasRedo(); }
    

    public void loadGameSerial(String path) {
        ChessGame loaded = ChessGame.loadGameSerial(path);
        if (loaded != null) {
            // Preserve the promotion handler
            if (this.promotionHandler != null) {
                loaded.setPromotionHandler(new PromotionHandlerAdapter(this.promotionHandler));
            }
            
            this.game = loaded;
            pcs.firePropertyChange(PROP_BOARD_STATE, null, game.getBoard());
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, game.getCurrentPlayer());
            if (game.isGameOver())
                pcs.firePropertyChange(PROP_GAME_OVER, null, game.getGameStatus());
        }
    }

    public void saveGameSerial(String absolutePath) {
        game.saveGameSerial(absolutePath);

    }

    public GameResult getGameResult() {
        return game.getGameResult();
    }

    public boolean isPlayerInCheck(boolean whitePlaying) {
        return game.isPlayerInCheck(whitePlaying);

    }

    public ChessGame getGame() {
        return game;
    }

    /**
     * Sets the promotion handler for the game.
     * @param handler The promotion handler to use (from UI package)
     */
    public void setPromotionHandler(pt.isec.pa.chess.ui.PromotionHandler handler) {  
        this.promotionHandler = handler;
        if (game != null) {
            game.setPromotionHandler(new PromotionHandlerAdapter(handler));
        }
    }

    // Add this inner class at the bottom of ChessGameManager
    private static class PromotionHandlerAdapter implements pt.isec.pa.chess.model.data.PromotionHandler {
        private final pt.isec.pa.chess.ui.PromotionHandler uiHandler;
        
        public PromotionHandlerAdapter(pt.isec.pa.chess.ui.PromotionHandler uiHandler) {
            this.uiHandler = uiHandler;
        }
        
        @Override
        public pt.isec.pa.chess.model.data.pieces.PieceType getPromotionChoice() {
            return uiHandler.getPromotionChoice();
        }
    }
}
