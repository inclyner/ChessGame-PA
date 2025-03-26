package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public class King extends Piece {
    boolean hasMoved = false;

    public King(boolean isWhite) {
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
            return "K";
        } else {
            return "k";
        }
    }
}

