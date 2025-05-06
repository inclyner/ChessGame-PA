package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.Player;
import pt.isec.pa.chess.model.data.Square;

import java.util.ArrayList;

public class BoardFx extends Canvas {

    private ChessGameManager gameManager;
    private final Color LIGHT_SQUARE = Color.web("#f0d9b5");
    private final Color DARK_SQUARE = Color.web("#b58863");
    private final Color PIECE_WHITE = Color.WHITE;
    private final Color PIECE_BLACK = Color.BLACK;
    private final Color HIGHLIGHT = Color.web("#ff494980"); // Semi-transparent red
    private final Color MOVE_INDICATOR = Color.web("#00ff007f"); // Semi-transparent green

    private Square selectedSquare = null;
    private ArrayList<Square> validMoves = new ArrayList<>(); // Store valid moves for highlighting

    public BoardFx(ChessGameManager gameManager) {
        this.gameManager = gameManager;
        setWidth(600);
        setHeight(600);

        // Add mouse click event handler
        setOnMouseClicked(event -> {
            if (gameManager == null || gameManager.getBoard() == null) {
                return;
            }

            int cellSize = (int) (Math.min(getWidth(), getHeight()) / 8);
            int col = (int) (event.getX() / cellSize);
            int row = (int) (event.getY() / cellSize);

            // Make sure the coordinates are within the board
            if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                handleBoardClick(col, row);
            }
        });
    }

    private void handleBoardClick(int col, int row) {
        // First verify bounds
        if (!gameManager.getBoard().isWithinBounds(col, row)) {
            return;
        }

        Square clickedSquare = new Square(col, row);

        // First click - select a piece
        if (selectedSquare == null) {
            Piece piece = gameManager.getBoard().getPieceAt(col, row);
            // Only allow selecting pieces that belong to current player
            if (piece != null && piece.isWhite() == gameManager.getGame().getCurrentPlayer().isWhite()) {
                selectedSquare = clickedSquare;
                highlightPossibleMoves(piece);
                draw();
            }
        } // Second click
        else {
            // Clicking the same square - deselect
            if (selectedSquare.equals(clickedSquare)) {
                selectedSquare = null;
                validMoves.clear(); // Clear highlighted moves
                draw();
                return;
            }

            try {
                // Attempt to move the piece
                boolean moved = gameManager.move(selectedSquare, clickedSquare);

                if (moved) {
                    // Move successful
                    selectedSquare = null;
                    validMoves.clear();
                } else {
                    // Invalid move - check if clicking another own piece
                    Piece newPiece = gameManager.getBoard().getPieceAt(col, row);
                    if (newPiece != null
                            && newPiece.isWhite() == gameManager.getGame().getCurrentPlayer().isWhite()) {
                        selectedSquare = clickedSquare;
                        highlightPossibleMoves(newPiece);
                    } else {
                        selectedSquare = null;
                        validMoves.clear();
                    }
                }
            } catch (Exception e) {
                // Reset selection on error
                selectedSquare = null;
                validMoves.clear();
                System.err.println("Error making move: " + e.getMessage());
            }
            draw();
        }
    }

    // Add method to highlight possible moves
    private void highlightPossibleMoves(Piece piece) {
        // Get valid moves from piece and highlight them
        validMoves = piece.getMoves(gameManager.getBoard());
        draw(); // Redraw to show move indicators
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        final double cellSize = Math.min(getWidth(), getHeight()) / 8;

        // Clear previous drawing
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Draw the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int currentCol = col;
                final int currentRow = row;

                // Draw squares
                Color squareColor = (currentRow + currentCol) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
                gc.setFill(squareColor);
                gc.fillRect(currentCol * cellSize, currentRow * cellSize, cellSize, cellSize);

                // Highlight selected square
                if (selectedSquare != null
                        && selectedSquare.column() == currentCol
                        && selectedSquare.row() == currentRow) {
                    gc.setFill(HIGHLIGHT);
                    gc.fillRect(currentCol * cellSize, currentRow * cellSize, cellSize, cellSize);
                }

                // Highlight valid moves
                if (validMoves.stream()
                        .anyMatch(s -> s.column() == currentCol && s.row() == currentRow)) {
                    gc.setFill(MOVE_INDICATOR);
                    gc.fillOval(
                            currentCol * cellSize + cellSize * 0.3,
                            currentRow * cellSize + cellSize * 0.3,
                            cellSize * 0.4,
                            cellSize * 0.4
                    );
                }

                // Draw pieces
                if (gameManager != null && gameManager.getBoard() != null) {
                    Piece piece = gameManager.getBoard().getPieceAt(currentCol, currentRow);
                    if (piece != null) {
                        drawPiece(gc, piece, currentCol, currentRow, cellSize);
                    }
                }
            }
        }
    }

    private void drawPiece(GraphicsContext gc, Piece piece, int col, int row, double cellSize) {
        gc.setFill(piece.isWhite() ? PIECE_WHITE : PIECE_BLACK);
        gc.setStroke(piece.isWhite() ? PIECE_BLACK : PIECE_WHITE);
        gc.setLineWidth(1.5);

        double padding = cellSize * 0.15;
        double x = col * cellSize + padding;
        double y = row * cellSize + padding;
        double size = cellSize - (2 * padding);

        String pieceSymbol = getPieceSymbol(piece);
        gc.setFont(javafx.scene.text.Font.font("Arial", size * 0.8));

        gc.strokeText(pieceSymbol, x + size / 4, y + size * 0.75);
        gc.fillText(pieceSymbol, x + size / 4, y + size * 0.75);
    }

    private String getPieceSymbol(Piece piece) {
        String symbol = piece.toString().toLowerCase();
        return switch (symbol) {
            case "k" ->
                "♔";
            case "q" ->
                "♕";
            case "r" ->
                "♖";
            case "b" ->
                "♗";
            case "n" ->
                "♘";
            case "p" ->
                "♙";
            default ->
                "?";
        };
    }
}
