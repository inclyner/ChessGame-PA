import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.Square;
import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.pieces.PieceType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChessGameTest {
    ChessGame game;

    @BeforeEach
    void setUp() {
        game = new ChessGame();
        game.startGame("White", "Black");
    }

    @Test
    void testWhitePawnInitialDoubleMove() {
        Square from = new Square(0, 6); // White pawn at a2
        Square to = new Square(0, 4);   // Move to a4

        assertTrue(game.move(from, to));
    }

    @Test
    void testWhitePawnBlockedMove() {
        // Block pawn
        game.getBoard().setPiece(0, 5, new Pawn(false, new Square(0,5))); // Black piece in front

        Square from = new Square(0, 6);
        Square to = new Square(0, 5);

        assertFalse(game.move(from, to));
    }

    @Test
    void testBlackPawnInitialDoubleMove() {
        game.move(new Square(0, 6), new Square(0, 4)); // White move
        Square from = new Square(0, 1); // Black pawn
        Square to = new Square(0, 3);

        assertTrue(game.move(from, to));
    }

    @Test
    void testWhiteKnightValidMove() {
        Square from = new Square(1, 7); // White knight at b1
        Square to = new Square(2, 5);   // Jump to c3

        assertTrue(game.move(from, to));
    }

    @Test
    void testWhiteKnightBlockedByOwnPiece() {
        Square from = new Square(1, 7); // White knight
        Square to = new Square(2, 6);   // Square occupied by white pawn

        assertFalse(game.move(from, to));
    }

    @Test
    void testKnightJumpOverPieces() {
        Square from = new Square(1, 7); // b1
        Square to = new Square(0, 5);   // a3

        assertTrue(game.move(from, to));
    }
}
