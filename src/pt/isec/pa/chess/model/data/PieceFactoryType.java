package pt.isec.pa.chess.model.data;

enum PieceType {
    KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
}

public class PieceFactoryType {

    static Piece createPiece( PieceType type, boolean isWhite) {
        return switch (type) {
            case KING -> new King(isWhite);
            case QUEEN -> new Queen(isWhite);
            case BISHOP -> new Bishop(isWhite);
            case KNIGHT -> new Knight(isWhite);
            case ROOK -> new Rook(isWhite);
            case PAWN -> new Pawn(isWhite);
            default -> null;
        };
    }


}
