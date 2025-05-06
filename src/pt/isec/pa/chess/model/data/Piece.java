package pt.isec.pa.chess.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

//TODO as peças têm acesso a board

public class Piece implements Serializable {
    @Serial
    static final long serialVersionUID = 100L;
    protected Square position; // Change from private to protected
    private boolean isWhite; // Whether the piece is white or black
    private boolean hasMoved; // Whether the piece has moved

    public Piece(Square position, boolean isWhite) {
        this.position = position;
        this.isWhite = isWhite;
        this.hasMoved = false;
    }

    public Square getPosition() {
        return position;
    }

    public void setPosition(Square position) {
        this.position = position;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    public boolean isKing() {
        return false; // Override na class King
    }

    public ArrayList<Square> getMoves(Board board) {
        // Override in specific piece classes (e.g., Pawn, Rook, etc.)
        return new ArrayList<>();
    }
}
