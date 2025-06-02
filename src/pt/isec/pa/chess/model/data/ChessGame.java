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

    public boolean startGame(String player1Name, String player2Name) {
        whitePlayer.setName(player1Name);
        blackPlayer.setName(player2Name);
        return true;
    }

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
