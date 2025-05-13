package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Pawn extends Piece {

    boolean hasMoved = false;
    private boolean needsPromotion = false;

    public Pawn(boolean isWhite, Square position) {
        super(position, isWhite);
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();
        int currentCol = position.column();
        int currentRow = position.row();
        int direction = isWhite() ? -1 : 1;

        // Forward moves
        if (board.isWithinBounds(currentCol, currentRow + direction)) {
            if (board.getPieceAt(currentCol, currentRow + direction) == null) {
                moves.add(new Square(currentCol, currentRow + direction));

                // Two-square first move
                if (!hasMoved && board.isWithinBounds(currentCol, currentRow + 2 * direction)
                        && board.getPieceAt(currentCol, currentRow + 2 * direction) == null) {
                    moves.add(new Square(currentCol, currentRow + 2 * direction));
                }
            }
        }

        // Diagonal captures only if there's an enemy piece
        int[] captureColumns = {currentCol - 1, currentCol + 1};
        for (int col : captureColumns) {
            if (board.isWithinBounds(col, currentRow + direction)) {
                Piece target = board.getPieceAt(col, currentRow + direction);
                // Only add diagonal move if there's an enemy piece to capture
                if (target != null && target.isWhite() != isWhite()) {
                    moves.add(new Square(col, currentRow + direction));
                }
            }
        }

        // En passant (only when conditions are met)
        if ((isWhite() && currentRow == 4) || (!isWhite() && currentRow == 3)) {
            Square lastMoveTo = board.getLastMoveTo();
            Piece lastMoved = board.getLastMovedPiece();
            Square lastMoveFrom = board.getLastMoveFrom();

            if (lastMoveTo != null && lastMoved instanceof Pawn && lastMoveFrom != null) {
                int lastMoveCol = lastMoveTo.column();

                // Verificar se o ultimo movimento foi para a coluna adjacente
                if (Math.abs(lastMoveCol - currentCol) == 1
                        && lastMoveTo.row() == currentRow
                        && Math.abs(lastMoveFrom.row() - lastMoveTo.row()) == 2
                        && board.getPieceAt(lastMoveCol, currentRow) != null
                        && board.getPieceAt(lastMoveCol, currentRow).isWhite() != isWhite()) {

                    Square enPassantTarget = new Square(lastMoveCol, currentRow + direction);
                    if (board.isWithinBounds(enPassantTarget.column(), enPassantTarget.row())
                        && board.getPieceAt(enPassantTarget.column(), enPassantTarget.row()) == null) {
                        moves.add(enPassantTarget);
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Checks if the pawn needs promotion (has reached the opposite end)
     */
    public boolean needsPromotion() {
        return (isWhite() && position.row() == 7) || (!isWhite() && position.row() == 0);
    }

    public Piece promote(Board board, PieceType type) {
        return switch (type) {
            case QUEEN ->
                new Queen(isWhite(), position);
            case KNIGHT ->
                new Knight(isWhite(), position);
            default ->
                new Queen(isWhite(), position); // Fallback to Queen
        };
    }

    @Override
    public String toString() {
        if (isWhite()) {
            return "p";
        } else {
            return "P";
        }
    }

    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public void setHasMoved() {
        hasMoved = true;
    }
}
