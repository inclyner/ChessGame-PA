package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.GameResult;
import pt.isec.pa.chess.ui.Point;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.ui.PromotionHandler;
import pt.isec.pa.chess.model.data.pieces.PieceType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

public class BoardFx extends Canvas implements PropertyChangeListener, PromotionHandler {

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
    private boolean showMoves = false;
    private boolean soundEnabled = true; // O som está ligado por default
    private boolean isWhiteTurn = true; // Track the current turn

    public BoardFx(ChessGameManager gameManager) {
        this.gameManager = gameManager;
        setWidth(600);   // Match the window size
        setHeight(600);  // Match the window size
        BOARD_SIZE = gameManager.getBoardSize();

        // Registrar para eventos do ChessGameManager
        gameManager.addPropertyChangeListener(this);

        // incluir ModelLog para reações a mudanças importantes
        ModelLog.getInstance().addPropertyChangeListener(this);

        // Add mouse click event handler
        setOnMouseClicked(event -> {

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
                if (showMoves)
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
                // Get the piece before moving for sound playback
                String pieceStr = gameManager.getPieceAt(selectedSquare.x(), selectedSquare.y());

                String destPieceBeforeMove = gameManager.getPieceAt(clickedSquare.x(), clickedSquare.y());
                boolean moved = gameManager.move(selectedSquare, clickedSquare);

                if (moved) {
                    // Play sound sequence for the move
                    if (pieceStr != null && soundEnabled) {
                        playMoveSequence(pieceStr, selectedSquare, clickedSquare, destPieceBeforeMove);
                    }
                    
                    // Move successful
                    selectedSquare = null;
                    validMoves.clear();
                } else {
                    // Invalid move - check if clicking another own piece
                    pieceStr = gameManager.getPieceAt(col, row);
                    if (pieceStr == null) {
                        return;  // No piece at clicked square
                    }
                    char pieceChar = pieceStr.charAt(0);

                    // Only allow selecting pieces that belong to current player
                    if(gameManager.isWhitePlaying() && isLowerCase(pieceChar))
                    {
                        selectedSquare = clickedSquare;
                        if (showMoves)
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


    private void drawPiece(GraphicsContext gc, String pieceStr, int col, int row,
                           double cellSize, double padding) {
        double piecePadding = cellSize * 0.15;
        double size = cellSize - (2 * piecePadding);

        // Center the image in the square
        double x = padding + col * cellSize + (cellSize - size) / 2;
        double y = padding + row * cellSize + (cellSize - size) / 2;

        String pieceImgName = getPieceImgName(pieceStr);

        gc.drawImage(ImageManager.getImage(pieceImgName), x, y, size, size);
    }


    @Override
    public PieceType getPromotionChoice() {
        List<String> choices = List.of("Queen", "Rook", "Bishop", "Knight");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen", choices);
        dialog.setTitle("Pawn Promotion");
        dialog.setHeaderText("Choose piece for pawn promotion");
        dialog.setContentText("Select piece:");

        Optional<String> result = dialog.showAndWait();

        return result.map(choice -> switch (choice) {
            case "Queen" -> PieceType.QUEEN;
            case "Rook" -> PieceType.ROOK;
            case "Bishop" -> PieceType.BISHOP;
            case "Knight" -> PieceType.KNIGHT;
            default -> PieceType.QUEEN;
        }).orElse(PieceType.QUEEN);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Executar na thread da UI
        javafx.application.Platform.runLater(() -> {
            String propName = evt.getPropertyName();

            // Eventos do ChessGameManager
            if (ChessGameManager.PROP_BOARD_STATE.equals(propName) ||
                ChessGameManager.PROP_CURRENT_PLAYER.equals(propName)) {
                draw(); // Redesenhar o tabuleiro quando o estado mudar
            }
            else if (ChessGameManager.PROP_CHECK_STATE.equals(propName)) {
                draw(); // Atualiza o tabuleiro quando houver xeque
            }

            if (evt.getPropertyName().equals(ChessGameManager.PROP_CURRENT_PLAYER)) {
                isWhiteTurn = gameManager.isWhitePlaying();
                draw(); // Redraw with correct colors
            }
        });
    }


    public void cleanup() {
        gameManager.removePropertyChangeListener(this);
        ModelLog.getInstance().removePropertyChangeListener(this);
    }

    public void setShowMoves(boolean b) {
        showMoves = b;
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }
    
    private void playMoveSequence(String pieceType, Point from, Point to, String destPieceBeforeMove) {
    List<String> soundFiles = new ArrayList<>();

    if (!gameManager.isWhitePlaying()) {
        soundFiles.add("white.mp3");
    } else {
        soundFiles.add("black.mp3");
    }

    soundFiles.add(getPieceSoundFile(pieceType));

    String fromCell = convertToAlgebraicNotation(from);
    soundFiles.add(fromCell.substring(0, 1) + ".mp3");
    soundFiles.add(fromCell.substring(1) + ".mp3");

    // Only add "empty.mp3" if there was no piece before the move
    if (destPieceBeforeMove != null) {
        soundFiles.add("capture.mp3");
    }

    String toCell = convertToAlgebraicNotation(to);
    soundFiles.add(toCell.substring(0, 1) + ".mp3");
    soundFiles.add(toCell.substring(1) + ".mp3");

    if (destPieceBeforeMove != null) {
        // Add sound for the captured piece type
        soundFiles.add(getPieceSoundFile(destPieceBeforeMove));
    }

    // Add sound for check or checkmate
    GameResult result = gameManager.getGameResult();
    if (result == GameResult.WHITE_WINS || result == GameResult.BLACK_WINS) {
        //System.out.println("Detected CHECKMATE! Adding sound.");
        soundFiles.add("checkmate.mp3");
    } else {
        // Check if the OPPONENT is in check after this move
        boolean opponentInCheck = gameManager.isPlayerInCheck(gameManager.isWhitePlaying());
        if (opponentInCheck) {
            //System.out.println("Detected CHECK! Adding sound.");
            soundFiles.add("check.mp3");
        }
    }

    playSoundSequence(soundFiles, 0);
}

    private String getPieceSoundFile(String pieceStr) {
        char piece = Character.toLowerCase(pieceStr.charAt(0));
        return switch (piece) {
            case 'p' -> "pawn.mp3";
            case 'r' -> "rook.mp3";
            case 'n' -> "knight.mp3";
            case 'b' -> "bishop.mp3";
            case 'q' -> "queen.mp3";
            case 'k' -> "king.mp3";
            default -> "piece.mp3";
        };
    }

    private void playSoundSequence(List<String> files, int idx) {
        if (idx >= files.size()) return;
        String basePath = "src/pt/isec/pa/chess/ui/res/sounds/en/";
        File file = new File(basePath + files.get(idx));
        if (!file.exists()) {
            playSoundSequence(files, idx + 1); // Skip missing files
            return;
        }
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setOnEndOfMedia(() -> {
            // Add a short pause after the origin cell (after index 2)
            if (idx == 2) {
                new Thread(() -> {
                    try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                    javafx.application.Platform.runLater(() -> playSoundSequence(files, idx + 1));
                }).start();
            } else {
                playSoundSequence(files, idx + 1);
            }
        });
        player.play();
    }

    // Keep this method for algebraic notation
    private String convertToAlgebraicNotation(Point p) {
        char file = (char)('a' + p.x());
        int rank = 8 - p.y();
        return "" + file + rank;
    }
}
