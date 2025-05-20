package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Square;

public class PieceFactoryType {

    public static Piece createPiece(PieceType type, boolean isWhite, Square square) {
        return switch (type) {
            case KING -> new King(isWhite,square);
            case QUEEN -> new Queen(isWhite,square);
            case BISHOP -> new Bishop(isWhite,square);
            case KNIGHT -> new Knight(isWhite,square);
            case ROOK -> new Rook(isWhite,square);
            case PAWN -> new Pawn(isWhite,square);
        };
    }


}
