package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super.isWhite = isWhite;
    }
    @Override
    public ArrayList<MoveVector> getMoves() {
        return new ArrayList<>(List.of(
                new MoveVector(1, 1), new MoveVector(1, -1),
                new MoveVector(-1, 1), new MoveVector(-1, -1)
        ));
    }

    @Override
    public String toString() {
        if (isWhite) {
            return "B";
        } else {
            return "b";
        }
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }
}
