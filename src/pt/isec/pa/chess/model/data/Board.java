package pt.isec.pa.chess.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import pt.isec.pa.chess.model.data.pieces.*;
import pt.isec.pa.chess.ui.PromotionHandler;

public class Board implements Serializable {

    private PromotionHandler promotionHandler;

    @Serial
    static final long serialVersionUID = 100L;
    // * column a to h
    // * row 1 to 8
    private static final int BOARD_SIZE = 8;

    private Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];
    private Square lastMoveFrom;
    private Square lastMoveTo;
    private Piece lastMovedPiece;  // Add this field

    public Board() {
        this.setupBoard();
    }

    private void setupBoard() {
        // Primeira linha (pretas)
        addPiece(PieceType.ROOK, false, 0, 0);
        addPiece(PieceType.KNIGHT, false, 1, 0);
        addPiece(PieceType.BISHOP, false, 2, 0);
        addPiece(PieceType.QUEEN, false, 3, 0);
        addPiece(PieceType.KING, false, 4, 0);
        addPiece(PieceType.BISHOP, false, 5, 0);
        addPiece(PieceType.KNIGHT, false, 6, 0);
        addPiece(PieceType.ROOK, false, 7, 0);

        // Peões pretos
        for (int col = 0; col < BOARD_SIZE; col++) {
            addPiece(PieceType.PAWN, false, col, 1);
        }

        // Peões brancos
        for (int col = 0; col < BOARD_SIZE; col++) {
            addPiece(PieceType.PAWN, true, col, 6);
        }

        // Primeira linha (brancas)
        addPiece(PieceType.ROOK, true, 0, 7);
        addPiece(PieceType.KNIGHT, true, 1, 7);
        addPiece(PieceType.BISHOP, true, 2, 7);
        addPiece(PieceType.QUEEN, true, 3, 7);
        addPiece(PieceType.KING, true, 4, 7);
        addPiece(PieceType.BISHOP, true, 5, 7);
        addPiece(PieceType.KNIGHT, true, 6, 7);
        addPiece(PieceType.ROOK, true, 7, 7);
    }

    public boolean addPiece(PieceType type, boolean isWhite, int column, int row) {
        // será chamado com addPiece (Knight,1,1); com o uso de uma factory de peças
        Square sq = new Square(column, row);
        Piece piece = PieceFactoryType.createPiece(type, isWhite, sq);
        if (board[column][row] != null) {
            return false;
        }

        board[column][row] = piece;
        return true;
    }

    public boolean movePiece(Square from, Square to) {
        Piece piece = getPieceAt(from.column(), from.row());
        if (piece == null) {
            return false;
        }

        // Get valid moves and verify if the target square is among them
        ArrayList<Square> validMoves = piece.getMoves(this);
        if (!validMoves.contains(to)) {
            return false;  // Invalid move
        }

        // Save current state
        Square originalPosition = piece.getPosition();
        Piece targetPiece = getPieceAt(to.column(), to.row());

        // Check for En Passant capture
        boolean isEnPassantCapture = false;
        if (piece instanceof Pawn &&
            from.column() != to.column() && 
            targetPiece == null) {
            isEnPassantCapture = true;
            System.out.println("En Passant capture detected");
            int capturedPawnRow = from.row();
            int capturedPawnCol = to.column();
            targetPiece = getPieceAt(capturedPawnCol, capturedPawnRow);
            board[capturedPawnCol][capturedPawnRow] = null; // Remove captured pawn
        }

        // Make the move temporarily
        board[to.column()][to.row()] = piece;
        board[from.column()][from.row()] = null;
        piece.setPosition(to);

        
        // Check if move leaves or keeps own king in check
        boolean causesCheck = isPlayerInCheck(piece.isWhite());

        // If move causes/leaves check, undo it and return false
        if (causesCheck) {
            // Undo move
            board[from.column()][from.row()] = piece;
            board[to.column()][to.row()] = targetPiece;
            piece.setPosition(originalPosition);
            return false;
        }

        // Handle castling
        if (piece instanceof King && Math.abs(to.column() - from.column()) == 2) {
            boolean isKingsideCastle = to.column() > from.column();
            int rookFromCol = isKingsideCastle ? 7 : 0;
            int rookToCol = isKingsideCastle ? 5 : 3;

            Piece rook = getPieceAt(rookFromCol, from.row());
            if (rook instanceof Rook) {
                board[rookToCol][from.row()] = rook;
                board[rookFromCol][from.row()] = null;
                rook.setPosition(new Square(rookToCol, from.row()));
                rook.setHasMoved();
            }
        }

        // Store last move info
        lastMoveFrom = from;
        lastMoveTo = to;
        lastMovedPiece = piece;
        piece.setHasMoved();

        return true;
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(); // thread safe for multiple games
        char[][] positions = {{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}, {'8', '7', '6', '5', '4', '3', '2', '1'}};
        for (int column = 0; column <= BOARD_SIZE - 1; column++) {
            for (int row = 0; row <= BOARD_SIZE - 1; row++) {
                if (board[column][row] != null) {
                    buffer.append(board[column][row].toString()).append(positions[column][row]);
                    if (!board[column][row].hasMoved()) {
                        buffer.append("*");
                    }
                }
            }
        }
        return buffer.toString();
    }

    public void setPieceFromChar(int col, int row, char pieceChar) {
        boolean isWhite = Character.isUpperCase(pieceChar);
        char type = Character.toUpperCase(pieceChar);

        Piece piece = switch (type) {
            case 'P' ->
                new Pawn(isWhite, new Square(col, row));
            case 'R' ->
                new Rook(isWhite, new Square(col, row));
            case 'N' ->
                new Knight(isWhite, new Square(col, row));
            case 'B' ->
                new Bishop(isWhite, new Square(col, row));
            case 'Q' ->
                new Queen(isWhite, new Square(col, row));
            case 'K' ->
                new King(isWhite, new Square(col, row));
            default ->
                throw new IllegalArgumentException("Invalid piece character: " + pieceChar);
        };

        board[col][row] = piece;
    }

    public void setPiece(int col, int row, Piece piece) {
        if (isWithinBounds(col, row)) {
            board[col][row] = piece;
        }
    }

    public boolean isWithinBounds(int col, int row) {
        return col >= 0 && col < BOARD_SIZE && row >= 0 && row < BOARD_SIZE;
    }

    public Piece getPieceAt(int col, int row) {
        if (!isWithinBounds(col, row)) {
            return null;
        }
        return board[col][row];
    }

    public boolean isPlayerInCheck(boolean isWhite) {
        Square kingPosition = findKingPosition(isWhite);
        if (kingPosition == null) {
            return false;
        }

        // Check if any opponent piece can attack the king
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                Piece opponentPiece = getPieceAt(col, row);
                if (opponentPiece != null && opponentPiece.isWhite() != isWhite) {
                    ArrayList<Square> moves = opponentPiece.getMoves(this);
                    if (moves.contains(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean wouldCauseSelfCheck(Piece piece, int targetColumn, int targetRow) {
        // Save the current state
        Square originalPosition = piece.getPosition();
        Piece targetPiece = board[targetColumn][targetRow];

        // Simulate the move
        board[originalPosition.column()][originalPosition.row()] = null;
        board[targetColumn][targetRow] = piece;
        piece.setPosition(new Square(targetColumn, targetRow));

        // Check if the current player's king is in check
        boolean isInCheck = isPlayerInCheck(piece.isWhite());

        // Undo the move
        board[targetColumn][targetRow] = targetPiece;
        board[originalPosition.column()][originalPosition.row()] = piece;
        piece.setPosition(originalPosition);

        return isInCheck;
    }

    private Square findKingPosition(boolean isWhite) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                Piece piece = getPieceAt(col, row);
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return new Square(col, row);
                }
            }
        }
        return null;
    }


    public boolean isSquareUnderAttack(Square square, boolean isWhite) {
        // Check if any opponent's piece can move to this square
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                Piece piece = getPieceAt(col, row);
                if (piece != null && piece.isWhite() != isWhite) {
                    ArrayList<Square> moves = piece.getMoves(this);
                    if (moves.contains(square)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public Square getLastMoveFrom() {
        return lastMoveFrom;
    }

    public Square getLastMoveTo() {
        return lastMoveTo;
    }

    public Piece getLastMovedPiece() {
        return lastMovedPiece;
    }

    public void setPromotionHandler(PromotionHandler handler) {
        this.promotionHandler = handler;
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public enum GameResult {
        IN_PROGRESS,
        WHITE_WINS,
        BLACK_WINS,
        STALEMATE
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
                Piece piece = getPieceAt(col, row);
                if (piece != null && piece.isWhite() == isWhite) {
                    // Get all possible moves for this piece
                    ArrayList<Square> moves = piece.getMoves(this);

                    // Try each move to see if it's legal
                    for (Square move : moves) {
                        // Save current state
                        Square originalPosition = piece.getPosition();
                        Piece targetPiece = getPieceAt(move.column(), move.row());

                        // Try move
                        board[move.column()][move.row()] = piece;
                        board[originalPosition.column()][originalPosition.row()] = null;
                        piece.setPosition(move);

                        // Check if move is legal (doesn't leave king in check)
                        boolean causesCheck = isPlayerInCheck(isWhite);

                        // Restore position
                        board[originalPosition.column()][originalPosition.row()] = piece;
                        board[move.column()][move.row()] = targetPiece;
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

    public void clearBoard() {
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                setPiece(col, row, null);
            }
        }
    }
}
