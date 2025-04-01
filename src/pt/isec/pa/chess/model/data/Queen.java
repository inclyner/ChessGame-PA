package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(boolean isWhite) {
        super.isWhite = isWhite;
    }


    @Override
    public ArrayList<MoveVector> getMoves() {
        return new ArrayList<>(List.of(
                new MoveVector(1, 0), new MoveVector(-1, 0),
                new MoveVector(0, 1), new MoveVector(0, -1),
                new MoveVector(1, 1), new MoveVector(1, -1),
                new MoveVector(-1, 1), new MoveVector(-1, -1)
        ));
    }

    @Override
    public String toString() {
        if (isWhite) {
            return "Q";
        } else {
            return "q";
        }
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }


}
