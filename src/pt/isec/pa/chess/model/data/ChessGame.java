package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.memento.IMemento;
import pt.isec.pa.chess.model.data.memento.IOriginator;
import pt.isec.pa.chess.model.data.memento.Memento;
import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.pieces.Piece;
import pt.isec.pa.chess.model.data.pieces.PieceType;

import java.io.*;

//Facade
public class ChessGame implements Serializable, IOriginator {

    private Board board;
    private Player currentPlayer;
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean isGameOver = false;
    int BOARD_SIZE;
    private boolean promotionPending = false;
    private Square promotionSquare = null;
    private PromotionHandler promotionHandler;

    public ChessGame() {
        board = new Board();
        whitePlayer = new Player(true);
        blackPlayer = new Player(false);
        currentPlayer = whitePlayer; // White starts
        BOARD_SIZE= board.getBoardSize();
    }

    public boolean startGame(String player1Name, String player2Name) {
        whitePlayer.setName(player1Name);
        blackPlayer.setName(player2Name);
        return true;
    }

    public boolean move(Square from, Square to) {
        if (isGameOver) {
            return false; // Cannot make moves after game is over
        }

        Piece piece = board.getPieceAt(from.column(), from.row());

        // Verify it's the correct player's turn
        if (piece == null || piece.isWhite() != currentPlayer.isWhite()) {
            return false;
        }

        // Check if player is in check and this move doesn't resolve it
        if (board.isPlayerInCheck(currentPlayer.isWhite())) {
            // Try the move - if it doesn't resolve check, it's invalid
            if (!board.movePiece(from, to)) {
                return false;
            }
        } else {
            // Normal move when not in check
            if (!board.movePiece(from, to)) {
                return false;
            }
        }

        Piece last = board.getLastMovedPiece();
        if (last instanceof Pawn p && (p.isWhite() && p.getPosition().row() == 0 ||
                !p.isWhite() && p.getPosition().row() == 7)) {
            PieceType choice = promotionHandler.getPromotionChoice();
            board.setPieceFromChar(
                    promotionSquare.column(),
                    promotionSquare.row(),
                    switch(choice) {
                        case QUEEN:  yield p.isWhite()? 'Q':'q';
                        case ROOK:   yield p.isWhite()? 'R':'r';
                        case BISHOP: yield p.isWhite()? 'B':'b';
                        case KNIGHT: yield p.isWhite()? 'N':'n';
                        default:     yield p.isWhite()? 'Q':'q';
                    }
            );
        }
        // Move was successful, switch turns first
        switchTurn();

        // Check if this move ended the game
        Board.GameResult result = board.getGameResult();
        if (result != Board.GameResult.IN_PROGRESS) {
            isGameOver = true;
            // Don't switch turns if game is over
            return true;
        }

        return true;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public void importGame(String gameState) {
        String[] lines = gameState.split("\n");
        if (lines.length < 10) { // Need at least current player + 8 rows + 1 player name
            throw new IllegalArgumentException("Invalid game state format");
        }

        // Clear the current board
        board.clearBoard();
        
        // Set the current player based on first line
        String playerTurn = lines[0].trim();
        if ("W".equals(playerTurn)) {
            currentPlayer = whitePlayer;
        } else if ("B".equals(playerTurn)) {
            currentPlayer = blackPlayer;
        } else {
            throw new IllegalArgumentException("Invalid player turn indicator: " + playerTurn);
        }

        // Import board state (mantendo a orientação do tabuleiro)
        for (int row = 0; row < 8; row++) {
            if (lines[row+1].length() < 8) {
                throw new IllegalArgumentException("Invalid board row: " + (row+1));
            }
            
            for (int col = 0; col < 8; col++) {
                char pieceChar = lines[row+1].charAt(col);
                if (pieceChar != '.') {
                    Piece piece = createPieceFromChar(pieceChar, col, row);
                    board.setPiece(col, row, piece);
                }
            }
        }

        // Set player names
        whitePlayer.setName(lines[9]);
        blackPlayer.setName(lines[10]);
    }

    public String exportGame() {
        StringBuilder export = new StringBuilder();

        // Export current turn (W for White, B for Black)
        export.append(currentPlayer.isWhite() ? "W" : "B").append("\n");

        // Export board state
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(col, row);
                if (piece == null) {
                    export.append(".");
                } else {
                    
                    String pieceChar = piece.toString();
                    if (piece.isWhite()) {
                        export.append(pieceChar.toLowerCase());
                    } else {
                        export.append(pieceChar.toUpperCase());
                    }
                }
            }
            export.append("\n");
        }

        // Export player names
        export.append(whitePlayer.getName()).append("\n");
        export.append(blackPlayer.getName());

        return export.toString();
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public String getGameStatus() {
        Board.GameResult result = board.getGameResult();
        if (isGameOver) {
            return switch (result) {
                case WHITE_WINS ->
                    "Game Over - Checkmate! White wins!";
                case BLACK_WINS ->
                    "Game Over - Checkmate! Black wins!";
                case STALEMATE ->
                    "Game Over - Draw by stalemate";
                default ->
                    "Game Over";
            };
        }
        return switch (result) {
            case WHITE_WINS ->
                "Checkmate! White wins!";
            case BLACK_WINS ->
                "Checkmate! Black wins!";
            case STALEMATE ->
                "Game drawn by stalemate";
            case IN_PROGRESS -> {
                if (board.isPlayerInCheck(currentPlayer.isWhite())) {
                    yield "Check! " + (currentPlayer.isWhite() ? "White" : "Black") + " to move";
                }
                yield (currentPlayer.isWhite() ? "White" : "Black") + " to move";
            }
        };
    }

    public boolean isWithinBounds(int col, int row) {
        return board.isWithinBounds(col, row);

    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public void setPromotionHandler(PromotionHandler handler) {
        this.promotionHandler = handler;
    }

    @Override
    public IMemento save() {
        return new Memento(this);
    }

    @Override
    public void restore(IMemento memento) {
        Object snapshot = memento.getSnapshot();
        if (snapshot instanceof ChessGame restored) {
            this.board = restored.board;
            this.currentPlayer = restored.currentPlayer;
            this.whitePlayer = restored.whitePlayer;
            this.blackPlayer = restored.blackPlayer;
            this.isGameOver = restored.isGameOver;
            this.BOARD_SIZE = restored.BOARD_SIZE;
            this.promotionPending = restored.promotionPending;
            this.promotionSquare = restored.promotionSquare;

        }
    }

    private Piece createPieceFromChar(char pieceChar, int col, int row) {
        boolean isWhite = Character.isLowerCase(pieceChar);
        char type = Character.toUpperCase(pieceChar);
        Square pos = new Square(col, row);
        switch (type) {
            case 'P': return new Pawn(isWhite, pos);
            case 'R': return new pt.isec.pa.chess.model.data.pieces.Rook(isWhite, pos);
            case 'N': return new pt.isec.pa.chess.model.data.pieces.Knight(isWhite, pos);
            case 'B': return new pt.isec.pa.chess.model.data.pieces.Bishop(isWhite, pos);
            case 'Q': return new pt.isec.pa.chess.model.data.pieces.Queen(isWhite, pos);
            case 'K': return new pt.isec.pa.chess.model.data.pieces.King(isWhite, pos);
            default: throw new IllegalArgumentException("Unknown piece type: " + pieceChar);
        }
    }


    public static ChessGame loadGameSerial(String absolutePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(absolutePath))) {
            ChessGame game = (ChessGame) ois.readObject();
            System.out.println("Jogo carregado com sucesso de: " + absolutePath);
            return game;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar o jogo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void saveGameSerial(String absolutePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(absolutePath))) {
            oos.writeObject(this);
            System.out.println("Jogo guardado com sucesso em: " + absolutePath);
        } catch (IOException e) {
            System.err.println("Erro ao guardar o jogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
