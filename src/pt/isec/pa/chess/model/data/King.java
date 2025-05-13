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

            if (!board.isWithinBounds(targetCol, targetRow)) {
                continue;
            }

            Piece pieceAtTarget = board.getPieceAt(targetCol, targetRow);
            if (pieceAtTarget == null || pieceAtTarget.isWhite() != this.isWhite()) {
                moves.add(new Square(targetCol, targetRow));
            }
        }

        // Castling moves
        if (!hasMoved) {
            // Kingside castling
            if (canCastleKingside(board)) {
                moves.add(new Square(currentCol + 2, currentRow));
            }

            // Queenside castling
            if (canCastleQueenside(board)) {
                moves.add(new Square(currentCol - 2, currentRow));
            }
        }

        return moves;
    }

    private boolean canCastleKingside(Board board) {
        int row = this.position.row();
        int col = this.position.column();

        // Check if rook is present and hasn't moved
        Piece rook = board.getPieceAt(7, row);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        // Check if squares between king and rook are empty
        for (int i = col + 1; i < 7; i++) {
            if (board.getPieceAt(i, row) != null) {
                return false;
            }
        }

        // Check if king is not in check and doesn't pass through check
        return !board.isSquareUnderAttack(position, isWhite())
                && !board.isSquareUnderAttack(new Square(col + 1, row), isWhite())
                && !board.isSquareUnderAttack(new Square(col + 2, row), isWhite());
    }

    private boolean canCastleQueenside(Board board) {
        int row = this.position.row();
        int col = this.position.column();

        // Check if rook is present and hasn't moved
        Piece rook = board.getPieceAt(0, row);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        // Check if squares between king and rook are empty
        for (int i = col - 1; i > 0; i--) {
            if (board.getPieceAt(i, row) != null) {
                return false;
            }
        }

        // Check if king is not in check and doesn't pass through check
        return !board.isSquareUnderAttack(position, isWhite())
                && !board.isSquareUnderAttack(new Square(col - 1, row), isWhite())
                && !board.isSquareUnderAttack(new Square(col - 2, row), isWhite());
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    @Override
    public String toString() {
        return isWhite() ? "k" : "K";
    }
}
