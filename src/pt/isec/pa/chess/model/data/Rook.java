package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Rook extends Piece {

    private boolean hasMoved = false;

    public Rook(boolean isWhite, Square position) {
        super(position, isWhite);
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        int[][] directions = {
            {0, 1}, // Up
            {0, -1}, // Down
            {1, 0}, // Right
            {-1, 0} // Left
        };

        for (int[] dir : directions) {
            int currentCol = this.position.column();
            int currentRow = this.position.row();

            while (true) {
                currentCol += dir[0];
                currentRow += dir[1];

                // Check bounds before accessing board
                if (!board.isWithinBounds(currentCol, currentRow)) {
                    break;
                }

                try {
                    Piece pieceAtTarget = board.getPieceAt(currentCol, currentRow);

                    if (pieceAtTarget == null) {
                        moves.add(new Square(currentCol, currentRow));
                    } else {
                        if (pieceAtTarget.isWhite() != this.isWhite()) {
                            moves.add(new Square(currentCol, currentRow)); // Can capture
                        }
                        break; // Stop this direction if we hit any piece
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    break; // Stop if we hit array bounds
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        return isWhite() ? "r" : "R";
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }
}
