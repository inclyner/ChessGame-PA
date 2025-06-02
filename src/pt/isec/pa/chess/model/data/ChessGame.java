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
    private transient PromotionHandler promotionHandler;


    public ChessGame() {
        board = new Board();
        whitePlayer = new Player(true);
        blackPlayer = new Player(false);
        currentPlayer = whitePlayer; // White starts
        BOARD_SIZE= board.getBoardSize();
    }

    /**
     * Inicia um novo jogo com os nomes dos jogadores.
     * Reseta completamente o estado do jogo atual.
     * @param player1Name Nome do jogador das peças brancas
     * @param player2Name Nome do jogador das peças pretas
     * @return true se o jogo foi iniciado com sucesso
     */
    public boolean startGame(String player1Name, String player2Name) {
        // Reset the entire game state
        resetGame();
        
        // Set player names
        whitePlayer.setName(player1Name);
        blackPlayer.setName(player2Name);
        
        return true;
    }

    /**
     * Reseta o jogo para o estado inicial.
     * Limpa o tabuleiro, reinicializa as peças e reseta todos os estados.
     */
    public void resetGame() {
        // Store the current promotion handler before reset
        PromotionHandler currentHandler = this.promotionHandler;
        
        // Reset board to initial position
        board = new Board();
        
        // Reset players but keep existing instances
        whitePlayer = new Player(true);
        blackPlayer = new Player(false);
        
        // Reset game state
        currentPlayer = whitePlayer; // White always starts
        isGameOver = false;
        promotionPending = false;
        promotionSquare = null;
        
        // Reset board size reference
        BOARD_SIZE = board.getBoardSize();
        
        // Restore the promotion handler
        this.promotionHandler = currentHandler;
    }

    /**
     * Inicia um novo jogo mantendo os nomes dos jogadores atuais.
     * Útil para reiniciar sem precisar reintroduzir nomes.
     */
    public void newGame() {
        String whiteName = whitePlayer.getName();
        String blackName = blackPlayer.getName();
        
        resetGame();
        
        whitePlayer.setName(whiteName);
        blackPlayer.setName(blackName);
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

            PieceType choice = PieceType.QUEEN; // Default fallback
            
            if (promotionHandler != null) {
                System.out.println("Promotion handler found: " + promotionHandler.getClass().getSimpleName());
                choice = promotionHandler.getPromotionChoice();
            } else {
                System.out.println("NO PROMOTION HANDLER SET - defaulting to Queen");
            }
            
            char code = switch (choice) {
                case QUEEN  -> p.isWhite() ? 'Q' : 'q';
                case ROOK   -> p.isWhite() ? 'R' : 'r';
                case BISHOP -> p.isWhite() ? 'B' : 'b';
                case KNIGHT -> p.isWhite() ? 'N' : 'n';
                default     -> {
                    System.out.println("Unknown piece type: " + choice + ", defaulting to Queen");
                    yield p.isWhite() ? 'Q' : 'q';
                }
            };
            
            System.out.println("Creating piece with code: " + code); // Debug line
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
        // Store the current promotion handler before clearing
        PromotionHandler currentHandler = this.promotionHandler;
        
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
        
        // Restore the promotion handler
        this.promotionHandler = currentHandler;
        
        // Reset game state
        isGameOver = false;
    }
    /**
     * Exporta o estado atual do jogo para uma string em formato CSV compatível com importGame.
     * @return Representação textual do jogo em formato CSV
     */
    public String exportGame() {
        StringBuilder export = new StringBuilder();

        // Export current turn (WHITE or BLACK)
        export.append(currentPlayer.isWhite() ? "WHITE" : "BLACK");

        // Export all pieces on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(col, row);
                if (piece != null) {
                    export.append(",");
                    
                    // Get piece character based on type and color
                    char pieceChar = getPieceChar(piece);
                    
                    // Convert position to chess notation (a1-h8)
                    char colChar = (char) ('a' + col);
                    char rowChar = (char) ('1' + (7 - row)); // Convert array row to chess row
                    
                    // Add piece notation
                    export.append(pieceChar).append(colChar).append(rowChar);
                    
                    // Add '*' if piece has moved
                    if (piece.hasMoved()) {
                        export.append("*");
                    }
                }
            }
        }

        return export.toString();
    }

    /**
     * Converte uma peça para o caractere correspondente (maiúsculo = branco, minúsculo = preto).
     * @param piece A peça a converter
     * @return Caractere representativo da peça
     */
    private char getPieceChar(Piece piece) {
        char baseChar = switch (piece) {
            case Pawn p -> 'P';
            case Rook r -> 'R';
            case Knight n -> 'N';
            case Bishop b -> 'B';
            case Queen q -> 'Q';
            case King k -> 'K';
            default -> '?';
        };
        
        // White pieces = uppercase, Black pieces = lowercase
        return piece.isWhite() ? baseChar : Character.toLowerCase(baseChar);
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

    /**
     * Sets the promotion handler for this game.
     * @param handler The promotion handler to use
     */
    public void setPromotionHandler(PromotionHandler handler) {
        this.promotionHandler = handler;
    }

    /**
     * Handles pawn promotion at the specified position.
     * This method should be called by the UI when a pawn reaches the promotion row.
     * @param col Column of the pawn to promote
     * @param row Row of the pawn to promote
     * @param pieceType Type of piece to promote to
     * @return true if promotion was successful
     */
    public boolean promotePawn(int col, int row, PieceType pieceType) {
        Piece piece = board.getPieceAt(col, row);
        
        if (!(piece instanceof Pawn)) {
            return false;
        }
        
        Pawn pawn = (Pawn) piece;
        
        // Verify this pawn is eligible for promotion
        if (!((pawn.isWhite() && row == 0) || (!pawn.isWhite() && row == 7))) {
            return false;
        }
        
        char code = switch (pieceType) {
            case QUEEN  -> pawn.isWhite() ? 'Q' : 'q';
            case ROOK   -> pawn.isWhite() ? 'R' : 'r';
            case BISHOP -> pawn.isWhite() ? 'B' : 'b';
            case KNIGHT -> pawn.isWhite() ? 'N' : 'n';
            default     -> pawn.isWhite() ? 'Q' : 'q';
        };
        
        board.setPieceFromChar(col, row, code);
        return true;
    }

    /**
     * Checks if there's a pawn at the given position that needs promotion.
     * @param col Column to check
     * @param row Row to check
     * @return true if there's a pawn that needs promotion
     */
    public boolean needsPromotion(int col, int row) {
        Piece piece = board.getPieceAt(col, row);
        if (!(piece instanceof Pawn pawn)) {
            return false;
        }
        
        return (pawn.isWhite() && row == 0) || (!pawn.isWhite() && row == 7);
    }

    /**
     * Gets the promotion handler for UI interaction.
     * @return The current promotion handler
     */
    public PromotionHandler getPromotionHandler() {
        return promotionHandler;
    }

    @Override
    public IMemento save() {
        return new Memento(this);
    }

    @Override
    public void restore(IMemento memento) {
        // Store the current promotion handler before restore
        PromotionHandler currentHandler = this.promotionHandler;
    
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
    
        // Restore the promotion handler (since it's set by the UI layer)
        this.promotionHandler = currentHandler;
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

    /**
     * Checks if a move is valid without actually executing it.
     * @param from Source square
     * @param to Destination square
     * @return true if the move is valid
     */
    public boolean isValidMove(Square from, Square to) {
        if (!isWithinBounds(from.column(), from.row()) || !isWithinBounds(to.column(), to.row())) {
            return false;
        }
        
        Piece piece = board.getPieceAt(from.column(), from.row());
        if (piece == null || piece.isWhite() != currentPlayer.isWhite()) {
            return false;
        }
        
        // Check if the destination is in the piece's valid moves
        ArrayList<Point> validMoves = getValidMovesAt(from.column(), from.row());
        Point targetPoint = new Point(to.column(), to.row());
        
        return validMoves.contains(targetPoint);
    }
}
