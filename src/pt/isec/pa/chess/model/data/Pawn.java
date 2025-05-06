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

        int col = this.position.column();
        int row = this.position.row();

        int direction = this.isWhite() ? 1 : -1;
        int oneStepRow = row + direction;

        if (board.isWithinBounds(col, oneStepRow) && board.getPieceAt(col, oneStepRow) == null) {
            moves.add(new Square(col, oneStepRow));

            int twoStepRow = row + 2 * direction;
            if (!this.hasMoved && board.isWithinBounds(col, twoStepRow)
                    && board.getPieceAt(col, twoStepRow) == null) {
                moves.add(new Square(col, twoStepRow));
            }
        }

        int[] diagonalCols = {col - 1, col + 1};
        for (int diagCol : diagonalCols) {
            if (!board.isWithinBounds(diagCol, oneStepRow)) {
                continue;
            }

            // Regular diagonal capture
            Piece target = board.getPieceAt(diagCol, oneStepRow);
            if (target != null && target.isWhite() != this.isWhite()) {
                moves.add(new Square(diagCol, oneStepRow));
            }

            // En passant capture
            if (board.isWithinBounds(diagCol, row)) {
                if ((isWhite() && row == 4) || (!isWhite() && row == 3)) {
                    Piece adjacentPawn = board.getPieceAt(diagCol, row);
                    if (adjacentPawn instanceof Pawn && adjacentPawn.isWhite() != this.isWhite()) {
                        Square lastMoveFrom = board.getLastMoveFrom();
                        Square lastMoveTo = board.getLastMoveTo();

                        if (lastMoveFrom != null && lastMoveTo != null
                                && lastMoveFrom.equals(new Square(diagCol, row + (this.isWhite() ? -2 : 2)))
                                && lastMoveTo.equals(new Square(diagCol, row))) {
                            moves.add(new Square(diagCol, oneStepRow));
                        }
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
            return "P";
        } else {
            return "p";
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
