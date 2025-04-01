package pt.isec.pa.chess.model.data;


import java.util.ArrayList;

public class Pawn extends Piece {
    boolean hasMoved = false;
    public Pawn(boolean isWhite) {
        super.isWhite = isWhite;
    }

    @Override
    public ArrayList<MoveVector> getMoves() {
        ArrayList<MoveVector> moves = new ArrayList<>();
        if(isWhite){
            moves.add(new MoveVector(0, -1));
            if (!hasMoved())
                moves.add(new MoveVector(0, -2));
            moves.add(new MoveVector(-1, -1));
            moves.add(new MoveVector(1, -1));
            return moves;
        }
        //black
        moves.add(new MoveVector(0, 1));
        if (!hasMoved())
            moves.add(new MoveVector(0, 2));
        moves.add(new MoveVector(1, 1));
        moves.add(new MoveVector(-1, 1));
        return moves;
    }

    @Override
    public String toString() {
        if (isWhite) {
            return "P";
        } else {
            return "p";
        }
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
