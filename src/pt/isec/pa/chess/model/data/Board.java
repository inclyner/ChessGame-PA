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
    private Square lastMoveFrom;
    private Square lastMoveTo;
    private Piece lastMovedPiece;

    private Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];


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



    public void setPromotionHandler(PromotionHandler handler) {
        this.promotionHandler = handler;
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }




    public void clearBoard() {
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                setPiece(col, row, null);
            }
        }
    }

    public Square getLastMoveTo() {
        return lastMoveTo;
    }

    public Piece getLastMovedPiece() {
        return lastMovedPiece;
    }

    public Square getLastMoveFrom() {
        return lastMoveFrom;
    }
    public void setLastMoveTo(Square lastMoveTo) {
        this.lastMoveTo = lastMoveTo;
    }

    public void setLastMovedPiece(Piece piece) {
        this.lastMovedPiece = piece;
    }

    public void setLastMoveFrom(Square lastMoveFrom) {
        this.lastMoveFrom = lastMoveFrom;
    }
}
