package pt.isec.pa.chess.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

//TODO as peças têm acesso a board



public abstract class Piece implements Serializable {
    @Serial
    static final long serialVersionUID = 100L;
    boolean isWhite;
    Square position;

    // method gets a list of all possible moves, including attacks
    public abstract ArrayList<Square> getMoves(Board board);
    //? os moves vao passar a ser calculados em cada peça




    public boolean hasMoved() {
        return false;
    }
    public boolean isKing() {
        return false;
    }

    public boolean hasMovedMark(){
        return false;
    }

    public void setHasMoved(){}

    public boolean isWhite(){return isWhite;}

}
