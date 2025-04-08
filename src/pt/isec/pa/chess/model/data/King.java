package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class King extends Piece {
    boolean hasMoved = false;

    public King(boolean isWhite, Square position) {
        super.isWhite = isWhite;
        super.position = position;
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
            // TODO: verificar se o rei passa por uma casa atacada ou termina nela

            // small castle
            Piece rookKingside = board.getPieceAt(7, row);
            if (rookKingside instanceof Rook rook && !rook.hasMoved()) {
                boolean empty1 = board.getPieceAt(5, row) == null;
                boolean empty2 = board.getPieceAt(6, row) == null;
                if (empty1 && empty2) {
                    moves.add(new Square(6, row));
                }
            }

            // big castle
            Piece rookQueenside = board.getPieceAt(0, row);
            if (rookQueenside instanceof Rook rook && !rook.hasMoved()) {
                boolean empty1 = board.getPieceAt(1, row) == null;
                boolean empty2 = board.getPieceAt(2, row) == null;
                boolean empty3 = board.getPieceAt(3, row) == null;
                if (empty1 && empty2 && empty3) {
                    moves.add(new Square(2, row));
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        // ? o * mete-se aqui?
        if (isWhite) {
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
