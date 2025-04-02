package pt.isec.pa.chess.model.data;


public class PieceFactoryTxt {

    static Piece createPiece(char piece) {
        return switch (piece) {
            case 'K' -> PieceFactoryType.createPiece(PieceType.KING, true);
            case 'Q' -> PieceFactoryType.createPiece(PieceType.QUEEN, true);
            case 'B' -> PieceFactoryType.createPiece(PieceType.BISHOP, true);
            case 'N' -> PieceFactoryType.createPiece(PieceType.KNIGHT, true);
            case 'R' -> PieceFactoryType.createPiece(PieceType.ROOK, true);
            case 'P' -> PieceFactoryType.createPiece(PieceType.PAWN, true);
            case 'k' -> PieceFactoryType.createPiece(PieceType.KING, false);
            case 'q' -> PieceFactoryType.createPiece(PieceType.QUEEN, false);
            case 'b' -> PieceFactoryType.createPiece(PieceType.BISHOP, false);
            case 'n' -> PieceFactoryType.createPiece(PieceType.KNIGHT, false);
            case 'r' -> PieceFactoryType.createPiece(PieceType.ROOK, false);
            case 'p' -> PieceFactoryType.createPiece(PieceType.PAWN, false);
            default -> null;
        };
    }
}
