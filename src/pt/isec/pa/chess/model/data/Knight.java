package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(boolean isWhite, Square position) {
        super(position, isWhite);
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        int[][] knightMoves = {
            {2, 1}, // Right 2, Up 1
            {2, -1}, // Right 2, Down 1
            {-2, 1}, // Left 2, Up 1
            {-2, -1}, // Left 2, Down 1
            {1, 2}, // Right 1, Up 2
            {1, -2}, // Right 1, Down 2
            {-1, 2}, // Left 1, Up 2
            {-1, -2} // Left 1, Down 2
        };

        int currentCol = this.position.column();
        int currentRow = this.position.row();

        for (int[] move : knightMoves) {
            int targetCol = currentCol + move[0];
            int targetRow = currentRow + move[1];

            // Check if the move is within board bounds
            if (!board.isWithinBounds(targetCol, targetRow)) {
                continue;
            }

            try {
                Piece pieceAtTarget = board.getPieceAt(targetCol, targetRow);

                // Add move if square is empty or occupied by opponent's piece
                if (pieceAtTarget == null || pieceAtTarget.isWhite() != this.isWhite()) {
                    moves.add(new Square(targetCol, targetRow));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue; // Skip if we hit array bounds
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        return isWhite() ? "N" : "n";
    }
}
