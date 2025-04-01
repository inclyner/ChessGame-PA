package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    boolean hasMoved = false;
    public Rook(boolean isWhite) {
        super.isWhite = isWhite;
    }
    @Override
    public ArrayList<MoveVector> getMoves() {
        return new ArrayList<>(List.of(
                new MoveVector(1, 0), new MoveVector(-1, 0),
                new MoveVector(0, 1), new MoveVector(0, -1)
        ));
    }

    @Override
    public String toString() {
        if (isWhite) {
            return "R";
        } else {
            return "r";
        }
    }
    @Override
    public boolean isRepeatable() {
        return true;
    }
    @Override
    public boolean hasMovedMark() {
        return hasMoved;
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
