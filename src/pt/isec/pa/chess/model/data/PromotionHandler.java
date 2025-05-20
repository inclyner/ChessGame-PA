package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.pieces.PieceType;

public interface PromotionHandler {

    PieceType getPromotionChoice();
}
