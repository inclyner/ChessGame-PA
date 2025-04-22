package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Rook extends Piece {
    boolean hasMoved = false;

    public Rook(boolean isWhite, Square position) {
        super(position, isWhite);
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        int[][] directions = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }
        };

        int col = this.position.column();
        int row = this.position.row();

        for (int[] dir : directions) {
            int currentCol = col;
            int currentRow = row;

            while (true) {
                currentCol += dir[0];
                currentRow += dir[1];

                if (!board.isWithinBounds(currentCol, currentRow))
                    break;

                Piece targetPiece = board.getPieceAt(currentCol, currentRow);

                if (targetPiece == null) {
                    moves.add(new Square(currentCol, currentRow));
                } else {
                    if (targetPiece.isWhite() != this.isWhite()) {
                        moves.add(new Square(currentCol, currentRow));
                    }
                    break;
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        if (isWhite()) {
            return "R";
        } else {
            return "r";
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
