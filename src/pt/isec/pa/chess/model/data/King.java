package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    ArrayList<MoveVector> movelist = new ArrayList<>();
    boolean hasMoved = false;
    public King(boolean isWhite) {
        super.isWhite = isWhite;
    }



    public ArrayList<MoveVector> getMoves() {
        //returns all possible moves for the king
        movelist = new ArrayList<>(List.of(
                new MoveVector(1, 1), new MoveVector(1, 0), new MoveVector(1, -1),
                new MoveVector(0, 1), new MoveVector(0, -1),
                new MoveVector(-1, 1), new MoveVector(-1, 0), new MoveVector(-1, -1)
        ));
        return movelist;
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

    @Override
    public boolean isKing() {
    return false;}

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

