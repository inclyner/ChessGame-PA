package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(boolean isWhite, Square position) {
        super(position, isWhite);

    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        int[][] directions = {
            {1, 1}, // down-right
            {-1, 1}, // down-left
            {1, -1}, // up-right
            {-1, -1} // up-left
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
        if (isWhite()) {
            return "B";
        } else {
            return "b";
        }
    }

}
