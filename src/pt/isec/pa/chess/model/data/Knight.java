package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(boolean isWhite,Square position) {
        super.isWhite = isWhite;
        super.position = position;
    }


    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        // 8 moves in "L"
        int[][] moveset = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        int col = this.position.column();
        int row = this.position.row();

        for (int[] move : moveset) {
            int targetCol = col + move[0];
            int targetRow = row + move[1];

            if (!board.isWithinBounds(targetCol, targetRow)) continue;

            Piece targetPiece = board.getPieceAt(targetCol, targetRow);

            if (targetPiece == null || targetPiece.isWhite() != this.isWhite()) {
                moves.add(new Square(targetCol, targetRow));
            }
        }

        return moves;
    }


    @Override
    public String toString() {
        if (isWhite) {
            return "N";
        } else {
            return "n";
        }
    }

}
