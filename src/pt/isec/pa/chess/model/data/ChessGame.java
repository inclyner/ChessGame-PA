/**
 * Classe principal de lógica do jogo de xadrez.
 * Atua como fachada sobre o tabuleiro e as peças, mantendo o estado do jogo,
 * controlando turnos, regras especiais (promoção, roque, en passant),
 * verificação de xeque/mate e lógica de vitória/empate.
 *
 * Esta classe é independente da interface gráfica e contém apenas lógica de domínio.
 */

package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.memento.IMemento;
import pt.isec.pa.chess.model.data.memento.IOriginator;
import pt.isec.pa.chess.model.data.memento.Memento;
import pt.isec.pa.chess.model.data.pieces.*;
import pt.isec.pa.chess.ui.Point;

import java.io.*;
import java.util.ArrayList;

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

    /**
     * Inicia o jogo com os nomes dos jogadores.
     * @param player1Name do jogador das peças brancas
     * @param player2Name Nome do jogador das peças pretas
     * @return true se o jogo foi iniciado com sucesso
     */
    public boolean startGame(String player1Name, String player2Name) {
        whitePlayer.setName(player1Name);
        blackPlayer.setName(player2Name);
        return true;
    }
    /**
     * Tenta mover uma peça de uma posição para outra.
     * Valida o turno, a jogada, aplica roque, en passant, promoção e atualiza o estado do jogo.
     * @param from Casa de origem
     * @param to Casa de destino
     * @return true se o movimento foi realizado com sucesso
     */
    public boolean move(Square from, Square to) {
        if (isGameOver) return false;

        Piece piece = board.getPieceAt(from.column(), from.row());
        if (piece == null || piece.isWhite() != currentPlayer.isWhite()) return false;

        ArrayList<Square> validMoves = piece.getMoves(board);
        if (!validMoves.contains(to)) return false;

        // Guarda estado atual
        Square originalPosition = piece.getPosition();
        Piece targetPiece = board.getPieceAt(to.column(), to.row());

        // En Passant
        boolean isEnPassantCapture = false;
        if (piece instanceof Pawn &&
                from.column() != to.column() &&
                targetPiece == null) {

            Piece lastMoved = board.getLastMovedPiece();
            Square lastFrom = board.getLastMoveFrom();
            Square lastTo = board.getLastMoveTo();

            if (lastMoved instanceof Pawn &&
                    lastFrom != null && lastTo != null &&
                    Math.abs(lastTo.row() - lastFrom.row()) == 2 &&
                    lastTo.column() == to.column() &&
                    lastTo.row() == from.row()) {

                isEnPassantCapture = true;
                board.setPiece(lastTo.column(), lastTo.row(), null); // Remove o peão capturado
            }
        }


        // Aplica movimento temporariamente
        board.setPiece(to.column(), to.row(), piece);
        board.setPiece(from.column(), from.row(), null);
        piece.setPosition(to);

        // Verifica se move deixa rei em xeque
        if (isPlayerInCheck(piece.isWhite())) {
            // Undo
            board.setPiece(from.column(), from.row(), piece);
            board.setPiece(to.column(), to.row(), targetPiece);
            piece.setPosition(originalPosition);
            return false;
        }

        // Roque
        if (piece instanceof King && Math.abs(to.column() - from.column()) == 2) {
            boolean kingside = to.column() > from.column();
            int rookFromCol = kingside ? 7 : 0;
            int rookToCol = kingside ? 5 : 3;

            Piece rook = board.getPieceAt(rookFromCol, from.row());
            if (rook instanceof Rook) {
                board.setPiece(rookToCol, from.row(), rook);
                board.setPiece(rookFromCol, from.row(), null);
                rook.setPosition(new Square(rookToCol, from.row()));
                rook.setHasMoved();
            }
        }

        piece.setHasMoved();
        board.setLastMoveFrom(from);
        board.setLastMoveTo(to);
        board.setLastMovedPiece(piece);
        // Promoção
        if (piece instanceof Pawn p &&
                (p.isWhite() && p.getPosition().row() == 0 || !p.isWhite() && p.getPosition().row() == 7)) {

            PieceType choice = promotionHandler.getPromotionChoice();
            char code = switch (choice) {
                case QUEEN  -> p.isWhite() ? 'Q' : 'q';
                case ROOK   -> p.isWhite() ? 'R' : 'r';
                case BISHOP -> p.isWhite() ? 'B' : 'b';
                case KNIGHT -> p.isWhite() ? 'N' : 'n';
                default     -> p.isWhite() ? 'Q' : 'q';
            };
            board.setPieceFromChar(p.getPosition().column(), p.getPosition().row(), code);
        }

        // Verifica fim de jogo
        GameResult result = getGameResult();
        if (result != GameResult.IN_PROGRESS) {
            isGameOver = true;
            return true;
        }

        // Troca de jogador
        switchTurn();
        return true;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    //! acho que não podemos ter isto aqui
    public Board getBoard() {
        return board;
    }
    /**
     * Importa um jogo a partir de uma string no formato CSV.
     * Substitui o estado atual pelo jogo importado.
     * @param gameState String com o estado do jogo em formato CSV
     */
    public void importGame(String gameState) {
        // Clear the current board
        board.clearBoard();
        
        // Collect all CSV data into one string, removing line breaks within the CSV
        String csvData = gameState.replaceAll("\\n", ",").replaceAll("\\r", "");
        
        // Split by comma and clean up
        String[] parts = csvData.split(",");
        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid CSV format");
        }
        
        // Find and set current player from first non-empty part
        String playerTurn = null;
        int startIndex = 0;
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (!part.isEmpty()) {
                if ("WHITE".equalsIgnoreCase(part) || "white".equalsIgnoreCase(part)) {
                    currentPlayer = whitePlayer;
                    playerTurn = part;
                    startIndex = i + 1;
                    break;
                } else if ("BLACK".equalsIgnoreCase(part) || "black".equalsIgnoreCase(part)) {
                    currentPlayer = blackPlayer;
                    playerTurn = part;
                    startIndex = i + 1;
                    break;
                }
            }
        }
        
        if (playerTurn == null) {
            throw new IllegalArgumentException("Invalid or missing player turn indicator");
        }
        
        // Process each piece starting from after the player indicator
        for (int i = startIndex; i < parts.length; i++) {
            String pieceData = parts[i].trim();
            if (pieceData.isEmpty()) continue;
            
            // Parse piece notation (e.g., "Ra1*", "Pa2", "ke8*")
            if (pieceData.length() < 3) continue;
            
            char pieceType = pieceData.charAt(0);
            char colChar = pieceData.charAt(1);
            char rowChar = pieceData.charAt(2);
            
            // Convert chess notation to array indices
            int col = colChar - 'a';
            int row = 8 - (rowChar - '0'); // Convert from chess row to array row
            
            if (!isWithinBounds(col, row)) continue;
            
            // Determine if piece is white (uppercase) or black (lowercase)
            boolean isWhite = Character.isUpperCase(pieceType);
            
            // Create and place piece
            Piece piece = createPieceFromChar(pieceType, col, row);
            if (piece != null) {
                // Check if piece has moved (indicated by '*')
                if (pieceData.endsWith("*")) {
                    piece.setHasMoved();
                }
                board.setPiece(col, row, piece);
            }
        }
        
        // Set default player names if not provided
        whitePlayer.setName("White Player");
        blackPlayer.setName("Black Player");
        
        // Reset game state
        isGameOver = false;
    }
    /**
     * Exporta o estado atual do jogo para uma string em formato específico.
     * @return Representação textual do jogo
     */
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
        GameResult result = getGameResult();
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
                if (isPlayerInCheck(currentPlayer.isWhite())) {
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
        boolean isWhite = Character.isUpperCase(pieceChar);
        char type = Character.toUpperCase(pieceChar);
        Square pos = new Square(col, row);
        
        return switch (type) {
            case 'P' -> new Pawn(isWhite, pos);
            case 'R' -> new Rook(isWhite, pos);
            case 'N' -> new Knight(isWhite, pos);
            case 'B' -> new Bishop(isWhite, pos);
            case 'Q' -> new Queen(isWhite, pos);
            case 'K' -> new King(isWhite, pos);
            default -> null;
        };
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
    /**
     * Indica se o jogador da cor especificada está em xeque.
     * @param isWhite true para jogador branco, false para preto
     * @return true se o jogador está em xeque
     */
    public boolean isPlayerInCheck(boolean isWhite) {
        Square kingPosition = findKingPosition(isWhite);
        if (kingPosition == null) {
            return false;
        }

        // Check if any opponent piece can attack the king
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                Piece opponentPiece = board.getPieceAt(col, row);
                if (opponentPiece != null && opponentPiece.isWhite() != isWhite) {
                    ArrayList<Square> moves = opponentPiece.getMoves(board);
                    if (moves.contains(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Square findKingPosition(boolean isWhite) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                Piece piece = board.getPieceAt(col, row);
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return new Square(col, row);
                }
            }
        }
        return null;
    }

    public Piece getPieceAt(int col, int row) {
        return board.getPieceAt(col,row);
    }

    public ArrayList<Point> getValidMovesAt(int col, int row) {
        ArrayList<Point> validMoves = new ArrayList<>();
        for (Square square : board.getPieceAt(col, row).getMoves(board)) {
            validMoves.add(new Point(square.column(), square.row()));
        }
        return validMoves;
    }



    /**
     * Indica o estado atual do jogo (em progresso, xeque-mate, empate).
     * @return Estado do jogo
     */
    public GameResult getGameResult() {
        boolean whiteInCheck = isPlayerInCheck(true);
        boolean blackInCheck = isPlayerInCheck(false);

        // Check if any player has legal moves
        boolean whiteHasMoves = hasLegalMoves(true);
        boolean blackHasMoves = hasLegalMoves(false);

        if (whiteInCheck && !whiteHasMoves) {
            return GameResult.BLACK_WINS;
        }
        if (blackInCheck && !blackHasMoves) {
            return GameResult.WHITE_WINS;
        }
        if (!whiteHasMoves || !blackHasMoves) {
            return GameResult.STALEMATE;
        }

        return GameResult.IN_PROGRESS;
    }


    private boolean hasLegalMoves(boolean isWhite) {
        // Get all pieces of the current player
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                Piece piece = board.getPieceAt(col, row);
                if (piece != null && piece.isWhite() == isWhite) {
                    // Get all possible moves for this piece
                    ArrayList<Square> moves = piece.getMoves(board);

                    // Try each move to see if it's legal
                    for (Square move : moves) {
                        // Save current state
                        Square originalPosition = piece.getPosition();
                        Piece targetPiece = board.getPieceAt(move.column(), move.row());

                        // Try move
                        board.setPiece(move.column(), move.row(), piece);
                        board.setPiece(originalPosition.column(), originalPosition.row(), null);
                        piece.setPosition(move);

                        // Check if move is legal (doesn't leave king in check)
                        boolean causesCheck = isPlayerInCheck(isWhite);

                        // Restore position
                        board.setPiece(originalPosition.column(), originalPosition.row(), piece);
                        board.setPiece(move.column(), move.row(), targetPiece);
                        piece.setPosition(originalPosition);

                        // If we found a legal move, return true
                        if (!causesCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



}
