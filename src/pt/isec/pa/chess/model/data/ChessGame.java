package pt.isec.pa.chess.model.data;

import java.io.Serializable;

//Facade
public class ChessGame implements Serializable {

    private final Board board;
    private Player currentPlayer;
    private final Player whitePlayer;
    private final Player blackPlayer;

    public ChessGame() {
        board = new Board();
        whitePlayer = new Player(true);
        blackPlayer = new Player(false);
        currentPlayer = whitePlayer; // White starts
    }

    public boolean startGame(String player1Name, String player2Name) {
        whitePlayer.setName(player1Name);
        blackPlayer.setName(player2Name);
        return true;
    }

    public boolean move(Square from, Square to) {
        Piece piece = board.getPieceAt(from.column(), from.row());

        // Verify it's the correct player's turn
        if (piece == null || piece.isWhite() != currentPlayer.isWhite()) {
            return false;
        }

        // Try to make the move
        if (board.movePiece(from, to)) {
            switchTurn();
            return true;
        }
        return false;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public void importGame(String gameState) {
        String[] lines = gameState.split("\n");
        if (lines.length < 10) // 1 turn line + 8 board lines + 2 player names
        {
            throw new IllegalArgumentException("Invalid game state format");
        }

        // Set current turn
        currentPlayer = lines[0].equals("W") ? whitePlayer : blackPlayer;

        // Import board state
        for (int row = 0; row < 8; row++) {
            String boardRow = lines[row + 1];
            for (int col = 0; col < 8; col++) {
                char pieceChar = boardRow.charAt(col);
                if (pieceChar != '.') {
                    board.setPieceFromChar(col, row, pieceChar);
                }
            }
        }

        // Set player names
        whitePlayer.setName(lines[9]);
        blackPlayer.setName(lines[10]);
    }

    public String exportGame() {
        StringBuilder export = new StringBuilder();

        // Export current turn (W for White, B for Black)
        export.append(currentPlayer.isWhite() ? "W" : "B").append("\n");

        // Export board state
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(col, row);
                if (piece == null) {
                    export.append(".");
                } else {
                    // Uppercase for white pieces, lowercase for black
                    export.append(piece.toString());
                }
            }
            export.append("\n");
        }

        // Export player names
        export.append(whitePlayer.getName()).append("\n");
        export.append(blackPlayer.getName());

        return export.toString();
    }
}
