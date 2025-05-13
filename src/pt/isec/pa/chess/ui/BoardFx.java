package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pt.isec.pa.chess.model.ChessGameManager;


import java.util.ArrayList;
import java.util.Optional;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

public class BoardFx extends Canvas {

    private ChessGameManager gameManager;
    private final Color LIGHT_SQUARE = Color.web("#f0d9b5");
    private final Color DARK_SQUARE = Color.web("#b58863");
    private final Color PIECE_WHITE = Color.WHITE;
    private final Color PIECE_BLACK = Color.BLACK;
    private final Color HIGHLIGHT = Color.web("#ff494980"); // Semi-transparent red
    private final Color MOVE_INDICATOR = Color.web("#00ff007f"); // Semi-transparent green

    private Point selectedSquare = null;
    private ArrayList<Point> validMoves = new ArrayList<>(); // Store valid moves for highlighting
    private int BOARD_SIZE;
    private String whiteName, blackName;
    public BoardFx(ChessGameManager gameManager) {
        this.gameManager = gameManager;
        setWidth(600);   // Match the window size
        setHeight(600);  // Match the window size
        BOARD_SIZE = gameManager.getBoardSize();
        // Add mouse click event handler
        setOnMouseClicked(event -> {
            if (gameManager == null) {
                return;
            }

            // Calculate board dimensions with padding
            final double padding = 30;
            final double effectiveCellSize = (Math.min(getWidth(), getHeight()) - 2 * padding) / BOARD_SIZE;

            // Adjust for padding in click coordinates
            int col = (int) ((event.getX() - padding) / effectiveCellSize);
            int row = (int) ((event.getY() - padding) / effectiveCellSize);

            // Make sure the coordinates are within the board
            if (col >= 0 && col < BOARD_SIZE && row >= 0 && row < BOARD_SIZE) {
                handleBoardClick(col, row);
            }
        });
    }

    public void setPlayerNames(String white, String black) {
        this.whiteName = white;
        this.blackName = black;
    }

    private void handleBoardClick(int col, int row) {
        // First verify bounds
        if (!gameManager.isWithinBounds(col, row)) {
            return;
        }


        Point clickedSquare = new Point(col, row);

        // First click - select a piece
        if (selectedSquare == null) {
            String pieceStr = gameManager.getPieceAt(col, row);

            if (pieceStr == null) {
                return;  // No piece at clicked square
            }

            char pieceChar = pieceStr.charAt(0);
            // Only allow selecting pieces that belong to current player
            if(gameManager.isWhitePlaying() && isLowerCase(pieceChar) || !gameManager.isWhitePlaying() && isUpperCase(pieceChar))
            {
                selectedSquare = clickedSquare;
                highlightPossibleMoves(col,row);
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
                    String pieceStr = gameManager.getPieceAt(col, row);
                    if (pieceStr == null) {
                        return;  // No piece at clicked square
                    }
                    char pieceChar = pieceStr.charAt(0);

                    // Only allow selecting pieces that belong to current player
                    if(gameManager.isWhitePlaying() && isLowerCase(pieceChar))
                    {
                        selectedSquare = clickedSquare;
                        highlightPossibleMoves(col,row);
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
    private void highlightPossibleMoves(int col, int row) {
        // Get valid moves from piece and highlight them
        validMoves = gameManager.getValidMovesAt(col, row);
        draw(); // Redraw to show move indicators
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        final double padding = 30;
        final double effectiveCellSize = (Math.min(getWidth(), getHeight()) - 2 * padding) / BOARD_SIZE;

        // Draw board elements
        drawCoordinates(gc, padding, effectiveCellSize);
        drawBoardAndPieces(gc, padding, effectiveCellSize);
        drawTurnIndicator(gc);
    }

    private void drawCoordinates(GraphicsContext gc, double padding, double effectiveCellSize) {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));

        // Draw column letters (A-H)
        for (int col = 0; col < BOARD_SIZE; col++) {
            String letter = String.valueOf((char) ('A' + col));
            gc.fillText(letter,
                    padding + col * effectiveCellSize + effectiveCellSize / 2 - 5,
                    getHeight() - padding / 3);
        }

        // Draw row numbers (1-8)
        for (int row = 0; row < BOARD_SIZE; row++) {
            String number = String.valueOf(BOARD_SIZE - row);
            gc.fillText(number,
                    padding / 3,
                    padding + row * effectiveCellSize + effectiveCellSize / 2 + 5);
        }
    }

    private void drawBoardAndPieces(GraphicsContext gc, double padding, double effectiveCellSize) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
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
        if (selectedSquare != null && selectedSquare.x() == col && selectedSquare.y() == row) {
            gc.setFill(HIGHLIGHT);
            gc.fillRect(
                    padding + col * effectiveCellSize,
                    padding + row * effectiveCellSize,
                    effectiveCellSize,
                    effectiveCellSize
            );
        }

        // Highlight valid moves
        if (validMoves.stream().anyMatch(s -> s.x() == col && s.y() == row)) {
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
        if (gameManager != null) {
            String pieceStr = gameManager.getPieceAt(col, row);
            if (pieceStr != null) {
                drawPiece(gc, pieceStr, col, row, effectiveCellSize, padding);
            }
        }
    }

    private void drawTurnIndicator(GraphicsContext gc) {
        boolean isWhiteTurn = gameManager.isWhitePlaying();


        gc.setFill(Color.BLACK);

        // Fonte para o jogador atual: bold
        Font boldFont = Font.font("System", FontWeight.BOLD, 18);
        Font normalFont = Font.font("System", FontWeight.NORMAL, 18);

        // Coordenadas do topo
        double centerY = 22;

        // Nome do jogador branco à esquerda
        gc.setFont(isWhiteTurn ? boldFont : normalFont);
        gc.fillText(whiteName, getWidth() * 0.15, centerY);

        // Nome do jogador preto à direita
        gc.setFont(!isWhiteTurn ? boldFont : normalFont);
        gc.fillText(blackName, getWidth() * 0.65, centerY);



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
        double effectiveCellSize = (Math.min(getWidth(), getHeight()) - 2 * padding) / BOARD_SIZE;

        // Draw coordinates
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));

        // Draw column letters (A-H)
        for (int col = 0; col < BOARD_SIZE; col++) {
            String letter = String.valueOf((char) ('A' + col));
            gc.fillText(letter,
                    padding + col * effectiveCellSize + effectiveCellSize / 2 - 5,
                    getHeight() - padding / 3);
        }

        // Draw row numbers (1-8)
        for (int row = 0; row < BOARD_SIZE; row++) {
            String number = String.valueOf(BOARD_SIZE - row);
            gc.fillText(number,
                    padding / 3,
                    padding + row * effectiveCellSize + effectiveCellSize / 2 + 5);
        }

        // Draw board squares with offset for coordinates
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                gc.setFill((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                gc.fillRect(
                        padding + col * effectiveCellSize,
                        padding + row * effectiveCellSize,
                        effectiveCellSize,
                        effectiveCellSize
                );

                // Draw pieces with the new offset
                if (gameManager != null) {
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



    public String getPromotionChoice(boolean isWhite) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen", "Queen", "Knight");
        dialog.setTitle("Pawn Promotion");
        dialog.setHeaderText("Choose piece for pawn promotion");
        dialog.setContentText("Select piece:");

        Optional<String> result = dialog.showAndWait();
        if (isWhite) {
            return result.map(choice
                    -> choice.equals("Queen") ? "Q" : "K"
            ).orElse("Q");
        }
        return result.map(choice
                -> choice.equals("Queen") ? "q" : "k"
        ).orElse("q");

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
