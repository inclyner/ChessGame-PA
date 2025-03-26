package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super.isWhite = isWhite;
    }
    public ArrayList<int [][]> getMoves(int column, int row) {
        //returns all possible moves for the king
        return null;
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
