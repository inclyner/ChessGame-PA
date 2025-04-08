package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(boolean isWhite,Square position) {
        super.isWhite = isWhite;
        super.position = position;
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        int[][] directions = {
                {1, 1},   // ↘️
                {-1, 1},  // ↙️
                {1, -1},  // ↗️
                {-1, -1}  // ↖️
        };

        for (int[] dir : directions) {
            int col = this.position.column();
            int row = this.position.row();

            while (true) {
                col += dir[0];
                row += dir[1];

                if (!board.isWithinBounds(col, row)) break;

                Piece pieceAtTarget = board.getPieceAt(col, row);

                if (pieceAtTarget == null) {
                    moves.add(new Square(col, row));
                } else {
                    if (pieceAtTarget.isWhite() != this.isWhite()) {
                        moves.add(new Square(col, row)); // pode capturar
                    }
                    break; // não pode passar por cima
                }
            }
        }

        return new ArrayList<>(moves);
    }


    @Override
    public String toString() {
        if (isWhite) {
            return "B";
        } else {
            return "b";
        }
    }


}
