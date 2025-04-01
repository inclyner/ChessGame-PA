package pt.isec.pa.chess.model.data;

import java.util.ArrayList;

public abstract class Piece {
    boolean isWhite;

    // method gets a list of all possible moves, including attacks
    public abstract ArrayList<MoveVector> getMoves();
    //? como os Moves são final, isto será definido na factory?
    //? seria definido no construtor de cada peça



    public boolean isRepeatable() {
        return false;
    }

    public boolean hasMoved() {
        return false;
    }
    public boolean isKing() {
        //its not removable
        return false;
    }

    public boolean hasMovedMark(){
        return false;
    };

    public void setHasMoved(){};


}
