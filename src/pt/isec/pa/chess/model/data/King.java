package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class King extends Piece {

    private boolean hasMoved = false;

    private static final int[][] directions = {
        {0, 1}, // Up
        {0, -1}, // Down
        {1, 0}, // Right
        {-1, 0}, // Left
        {1, 1}, // Up-Right
        {-1, 1}, // Up-Left
        {1, -1}, // Down-Right
        {-1, -1} // Down-Left
    };

    public King(boolean isWhite, Square position) {
        super(position, isWhite);
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();
        int currentCol = this.position.column();
        int currentRow = this.position.row();

        // Regular moves
        for (int[] dir : directions) {
            int targetCol = currentCol + dir[0];
            int targetRow = currentRow + dir[1];

            // Skip if out of bounds
            if (!board.isWithinBounds(targetCol, targetRow)) {
                continue;
            }

            Piece pieceAtTarget = board.getPieceAt(targetCol, targetRow);
            if (pieceAtTarget == null || pieceAtTarget.isWhite() != this.isWhite()) {
                moves.add(new Square(targetCol, targetRow));
            }
        }

        // Castling logic...
        return moves;
    }

    private boolean canCastleKingside(Board board, int row) {
        Piece rook = board.getPieceAt(7, row);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        // Check if squares between king and rook are empty
        return board.getPieceAt(5, row) == null
                && board.getPieceAt(6, row) == null
                && !board.isSquareUnderAttack(new Square(5, row), this.isWhite())
                && !board.isSquareUnderAttack(new Square(6, row), this.isWhite());
    }

    private boolean canCastleQueenside(Board board, int row) {
        Piece rook = board.getPieceAt(0, row);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        // Check if squares between king and rook are empty
        return board.getPieceAt(1, row) == null
                && board.getPieceAt(2, row) == null
                && board.getPieceAt(3, row) == null
                && !board.isSquareUnderAttack(new Square(2, row), this.isWhite())
                && !board.isSquareUnderAttack(new Square(3, row), this.isWhite());
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    @Override
    public String toString() {
        return isWhite() ? "K" : "k";
    }
}
