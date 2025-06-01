import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.pieces.Piece;

import static org.junit.jupiter.api.Assertions.*;

public class PawnTest {
    ChessGame game;

    @BeforeEach
    void setUp() {
        game = new ChessGame();
        game.startGame("White", "Black");
    }


    @Test
    void testWhitePawnInitialMoves() {

        Piece pawn = game.getPieceAt(0, 6); // white pawn at a2 (6 == row index for white)
        assertNotNull(pawn);
        assertTrue(pawn instanceof Pawn);
        assertTrue(pawn.isWhite());
        var moves = pawn.getMoves(game.getBoard());
        assertEquals(2, moves.size()); // e.g. a3 and a4
    }

    @Test
    void testBlackPawnInitialMoves() {
        Piece pawn = game.getPieceAt(0, 1); // black pawn at a7
        assertNotNull(pawn);
        assertTrue(pawn instanceof Pawn);
        assertFalse(pawn.isWhite());
        var moves = pawn.getMoves(game.getBoard());
        assertEquals(2, moves.size()); // a6 and a5
    }


}
