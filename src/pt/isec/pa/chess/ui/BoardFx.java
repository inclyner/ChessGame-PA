package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.Square;

public class BoardFx extends Canvas {
    private ChessGameManager gameManager;
    private final Color LIGHT_SQUARE = Color.web("#f0d9b5");
    private final Color DARK_SQUARE = Color.web("#b58863");
    private final Color PIECE_WHITE = Color.WHITE;
    private final Color PIECE_BLACK = Color.BLACK;
    private final Color HIGHLIGHT = Color.web("#ff494980"); // Semi-transparent red for highlighting

    private Square selectedSquare = null;

    public BoardFx(ChessGameManager gameManager) {
        this.gameManager = gameManager;
        setWidth(600);
        setHeight(600);
        
        // Add mouse click event handler
        setOnMouseClicked(event -> {
            if (gameManager == null || gameManager.getBoard() == null)
                return;
                
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
        // First click - select a piece
        if (selectedSquare == null) {
            Piece piece = gameManager.getBoard().getPieceAt(col, row);
            if (piece != null) {
                selectedSquare = new Square(col, row);
                draw(); // Redraw the board with selection highlighted
            }
        } 
        // Second click - move the piece
        else {
            Square targetSquare = new Square(col, row);
            
            // Attempt to move the piece
            boolean moved = gameManager.move(selectedSquare, targetSquare);
            
            // Reset selection
            selectedSquare = null;
            
            // Redraw the board with the new position
            draw();
        }
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        double cellSize = Math.min(getWidth(), getHeight()) / 8;

        // Clear previous drawing
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Draw the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Draw squares
                Color squareColor = (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
                gc.setFill(squareColor);
                gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                
                // Highlight selected square
                if (selectedSquare != null && selectedSquare.column() == col && selectedSquare.row() == row) {
                    gc.setFill(HIGHLIGHT);
                    gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }

                // Draw pieces if they exist
                if (gameManager != null) {
                    Board board = gameManager.getBoard();
                    if (board != null) {
                        Piece piece = board.getPieceAt(col, row);
                        if (piece != null) {
                            drawPiece(gc, piece, col, row, cellSize);
                        }
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

        gc.strokeText(pieceSymbol, x + size/4, y + size * 0.75);
        gc.fillText(pieceSymbol, x + size/4, y + size * 0.75);
    }

    private String getPieceSymbol(Piece piece) {
        String symbol = piece.toString().toLowerCase();
        return switch (symbol) {
            case "k" -> "♔";
            case "q" -> "♕";
            case "r" -> "♖";
            case "b" -> "♗";
            case "n" -> "♘";
            case "p" -> "♙";
            default -> "?";
        };
    }
}