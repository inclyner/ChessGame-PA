package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(boolean isWhite) {
        super.isWhite = isWhite;
    }

    @Override
    public ArrayList<MoveVector> getMoves() {
        return new ArrayList<>(List.of(
                new MoveVector(2, 1), new MoveVector(1, 2),
                new MoveVector(-1, 2), new MoveVector(-2, 1),
                new MoveVector(-2, -1), new MoveVector(-1, -2),
                new MoveVector(1, -2), new MoveVector(2, -1)
        ));
    }
    @Override
    public String toString() {
        if (isWhite) {
            return "N";
        } else {
            return "n";
        }
    }

}
