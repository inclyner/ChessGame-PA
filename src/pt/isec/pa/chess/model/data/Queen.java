package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(boolean isWhite, Square position) {
        super(position, isWhite);
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        int[][] directions = {
            {0, 1}, // Up
            {0, -1}, // Down
            {1, 0}, // Right
            {-1, 0}, // Left
            {1, 1}, // Up-Right
            {-1, 1}, // Up-Left
            {1, -1}, // Down-Right
            {-1, -1} // Down-Left
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
        return isWhite() ? "Q" : "q";
    }
}
