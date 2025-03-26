package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Rook extends Piece {
    boolean hasMoved = false;
    public Rook(boolean isWhite) {
        super.isWhite = isWhite;
    }
    public ArrayList<int [][]> getMoves(int column, int row) {
        //returns all possible moves for the king
        return null;
    }

    @Override
    public String toString() {
        //? o * mete-se aqui?
        if (isWhite) {
            return "R";
        } else {
            return "r";
        }
    }
}
