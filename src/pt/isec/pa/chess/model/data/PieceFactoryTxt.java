package pt.isec.pa.chess.model.data;


public class PieceFactoryTxt {

    static Piece createPiece(char piece, Square position) {
        return switch (piece) {
            case 'K' -> new King( true,position);
            case 'Q' -> new Queen( true,position);
            case 'B' -> new Bishop( true,position);
            case 'N' -> new Knight( true,position);
            case 'R' -> new Rook( true,position);
            case 'P' -> new Pawn( true,position);
            case 'k' -> new King( false,position);
            case 'q' -> new Queen( false,position);
            case 'b' -> new Bishop( false,position);
            case 'n' -> new Knight( false,position);
            case 'r' -> new Rook( false,position);
            case 'p' -> new Pawn( false,position);
            default -> null;
        };
    }
}
