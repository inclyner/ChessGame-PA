package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class King extends Piece {
    boolean hasMoved = false;

    public King(boolean isWhite, Square position) {
        super(position, isWhite);
    }

    @Override
    public ArrayList<Square> getMoves(Board board) {
        ArrayList<Square> moves = new ArrayList<>();

        int[][] directions = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 }
        };

        int col = this.position.column();
        int row = this.position.row();

        for (int[] dir : directions) {
            int targetCol = col + dir[0];
            int targetRow = row + dir[1];

            if (!board.isWithinBounds(targetCol, targetRow))
                continue;

            Piece targetPiece = board.getPieceAt(targetCol, targetRow);

            if (targetPiece == null || targetPiece.isWhite() != this.isWhite()) {
                moves.add(new Square(targetCol, targetRow));
            }
        }

        // castle
        if (!this.hasMoved) {
            // Short Castle
            Piece rookKingside = board.getPieceAt(7, row);
            if (rookKingside instanceof Rook rook && !rook.hasMoved()) {
                boolean empty1 = board.getPieceAt(5, row) == null;
                boolean empty2 = board.getPieceAt(6, row) == null;
                boolean safe1 = !board.isSquareUnderAttack(new Square(4, row), this.isWhite());
                boolean safe2 = !board.isSquareUnderAttack(new Square(5, row), this.isWhite());
                boolean safe3 = !board.isSquareUnderAttack(new Square(6, row), this.isWhite());
                if (empty1 && empty2 && safe1 && safe2 && safe3) {
                    moves.add(new Square(6, row));
                }
            }

            // Long Castle
            Piece rookQueenside = board.getPieceAt(0, row);
            if (rookQueenside instanceof Rook rook && !rook.hasMoved()) {
                boolean empty1 = board.getPieceAt(1, row) == null;
                boolean empty2 = board.getPieceAt(2, row) == null;
                boolean empty3 = board.getPieceAt(3, row) == null;
                boolean safe1 = !board.isSquareUnderAttack(new Square(4, row), this.isWhite());
                boolean safe2 = !board.isSquareUnderAttack(new Square(3, row), this.isWhite());
                boolean safe3 = !board.isSquareUnderAttack(new Square(2, row), this.isWhite());
                if (empty1 && empty2 && empty3 && safe1 && safe2 && safe3) {
                    moves.add(new Square(2, row));
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        if (isWhite()) {
            return "K";
        } else {
            return "k";
        }
    }

    @Override
    public boolean isKing() {
        return true;
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
