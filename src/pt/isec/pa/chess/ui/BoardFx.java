package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceType;
import pt.isec.pa.chess.model.data.Player;
import pt.isec.pa.chess.model.data.Square;

import java.util.ArrayList;
import java.util.Optional;

public class BoardFx extends Canvas implements PromotionHandler {

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
        setWidth(600);   // Match the window size
        setHeight(600);  // Match the window size

        // Add mouse click event handler
        setOnMouseClicked(event -> {
            if (gameManager == null || gameManager.getBoard() == null) {
                return;
            }

            // Calculate board dimensions with padding
            final double padding = 30;
            final double effectiveCellSize = (Math.min(getWidth(), getHeight()) - 2 * padding) / 8;

            // Adjust for padding in click coordinates
            int col = (int) ((event.getX() - padding) / effectiveCellSize);
            int row = (int) ((event.getY() - padding) / effectiveCellSize);

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
        gc.clearRect(0, 0, getWidth(), getHeight());
        final double padding = 30;
        final double effectiveCellSize = (Math.min(getWidth(), getHeight()) - 2 * padding) / 8;

        // Draw board elements
        drawCoordinates(gc, padding, effectiveCellSize);
        drawBoardAndPieces(gc, padding, effectiveCellSize);
        drawTurnIndicator(gc);
    }

    private void drawCoordinates(GraphicsContext gc, double padding, double effectiveCellSize) {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));

        // Draw column letters (A-H)
        for (int col = 0; col < 8; col++) {
            String letter = String.valueOf((char) ('A' + col));
            gc.fillText(letter,
                    padding + col * effectiveCellSize + effectiveCellSize / 2 - 5,
                    getHeight() - padding / 3);
        }

        // Draw row numbers (1-8)
        for (int row = 0; row < 8; row++) {
            String number = String.valueOf(8 - row);
            gc.fillText(number,
                    padding / 3,
                    padding + row * effectiveCellSize + effectiveCellSize / 2 + 5);
        }
    }

    private void drawBoardAndPieces(GraphicsContext gc, double padding, double effectiveCellSize) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                drawSquare(gc, col, row, padding, effectiveCellSize);
                drawHighlights(gc, col, row, padding, effectiveCellSize);
                drawPieceAt(gc, col, row, padding, effectiveCellSize);
            }
        }
    }

    private void drawSquare(GraphicsContext gc, int col, int row, double padding, double effectiveCellSize) {
        // Draw squares
        gc.setFill((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
        gc.fillRect(
                padding + col * effectiveCellSize,
                padding + row * effectiveCellSize,
                effectiveCellSize,
                effectiveCellSize
        );
    }

    private void drawHighlights(GraphicsContext gc, int col, int row, double padding, double effectiveCellSize) {
        // Highlight selected square
        if (selectedSquare != null && selectedSquare.column() == col && selectedSquare.row() == row) {
            gc.setFill(HIGHLIGHT);
            gc.fillRect(
                    padding + col * effectiveCellSize,
                    padding + row * effectiveCellSize,
                    effectiveCellSize,
                    effectiveCellSize
            );
        }

        // Highlight valid moves
        if (validMoves.stream().anyMatch(s -> s.column() == col && s.row() == row)) {
            gc.setFill(MOVE_INDICATOR);
            gc.fillOval(
                    padding + col * effectiveCellSize + effectiveCellSize * 0.3,
                    padding + row * effectiveCellSize + effectiveCellSize * 0.3,
                    effectiveCellSize * 0.4,
                    effectiveCellSize * 0.4
            );
        }
    }

    private void drawPieceAt(GraphicsContext gc, int col, int row, double padding, double effectiveCellSize) {
        // Draw pieces
        if (gameManager != null && gameManager.getBoard() != null) {
            String pieceStr = gameManager.getPieceAt(col, row);
            if (pieceStr != null) {
                drawPiece(gc, pieceStr, col, row, effectiveCellSize, padding);
            }
        }
    }

    private void drawTurnIndicator(GraphicsContext gc) {
        boolean isWhiteTurn = gameManager.getGame().getCurrentPlayer().isWhite();
        gc.setFill(isWhiteTurn ? PIECE_WHITE : PIECE_BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeText(isWhiteTurn ? "White's turn" : "Black's turn", 10, getHeight() - 10);
        gc.fillText(isWhiteTurn ? "White's turn" : "Black's turn", 10, getHeight() - 10);
    }

    private void drawBoard() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Add padding for coordinates
        double padding = 30;
        double effectiveCellSize = (Math.min(getWidth(), getHeight()) - 2 * padding) / 8;

        // Draw coordinates
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));

        // Draw column letters (A-H)
        for (int col = 0; col < 8; col++) {
            String letter = String.valueOf((char) ('A' + col));
            gc.fillText(letter,
                    padding + col * effectiveCellSize + effectiveCellSize / 2 - 5,
                    getHeight() - padding / 3);
        }

        // Draw row numbers (1-8)
        for (int row = 0; row < 8; row++) {
            String number = String.valueOf(8 - row);
            gc.fillText(number,
                    padding / 3,
                    padding + row * effectiveCellSize + effectiveCellSize / 2 + 5);
        }

        // Draw board squares with offset for coordinates
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                gc.setFill((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                gc.fillRect(
                        padding + col * effectiveCellSize,
                        padding + row * effectiveCellSize,
                        effectiveCellSize,
                        effectiveCellSize
                );

                // Draw pieces with the new offset
                if (gameManager != null && gameManager.getBoard() != null) {
                    String pieceStr = gameManager.getPieceAt(col, row);
                    if (pieceStr != null) {
                        drawPiece(gc, pieceStr, col, row, effectiveCellSize, padding);
                    }
                }
            }
        }
    }

    private void drawPiece(GraphicsContext gc, String pieceStr, int col, int row,
                           double cellSize, double padding) {
        double x = padding + col * cellSize;
        double y = padding + row * cellSize;

        // Get piece type from the piece string

        double piecePadding = cellSize * 0.15;
        double size = cellSize - (2 * piecePadding);


        String pieceImgName = getPieceImgName(pieceStr);

        gc.drawImage(ImageManager.getImage(pieceImgName), x, y, size, size);

    }


    @Override
    public PieceType getPromotionChoice() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen", "Queen", "Knight");
        dialog.setTitle("Pawn Promotion");
        dialog.setHeaderText("Choose piece for pawn promotion");
        dialog.setContentText("Select piece:");

        Optional<String> result = dialog.showAndWait();
        return result.map(choice
                -> choice.equals("Queen") ? PieceType.QUEEN : PieceType.KNIGHT
        ).orElse(PieceType.QUEEN);
    }

    private String getPieceImgName(String piece) {
        String symbol = piece.substring(0, 1);
        return switch (symbol) {
            case "k" -> "kingW.png";
            case "K" -> "kingB.png";
            case "q" -> "queenW.png";
            case "Q" -> "queenB.png";
            case "r" -> "rookW.png";
            case "R" -> "rookB.png";
            case "b" -> "bishopW.png";
            case "B" -> "bishopB.png";
            case "n" -> "knightW.png";
            case "N" -> "knightB.png";
            case "p" -> "pawnW.png";
            case "P" -> "pawnB.png";
            default -> "";
        };
    }
}
