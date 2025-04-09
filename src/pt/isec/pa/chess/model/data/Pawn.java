package pt.isec.pa.chess.model.data;


import java.util.ArrayList;

public class Pawn extends Piece {
    boolean hasMoved = false;
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
            if (!board.isWithinBounds(diagCol, oneStepRow)) continue;

            Piece target = board.getPieceAt(diagCol, oneStepRow);
            if (target != null && target.isWhite() != this.isWhite()) {
                moves.add(new Square(diagCol, oneStepRow));
            }
        }

        return moves;
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
    public void setHasMoved(){
        hasMoved=true;
    }
}
