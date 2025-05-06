package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import pt.isec.pa.chess.model.ChessGameManager;

public class BoardFx extends Canvas {
    ChessGameManager gameManager;
    Point selectedSquare = null;

    public BoardFx(ChessGameManager gameManager) {
        this.gameManager = gameManager;

        widthProperty().addListener((obs, o, n) -> draw());
        heightProperty().addListener((obs, o, n) -> draw());
        draw();
        setOnMouseClicked(event -> {
            int[] pos = mouseToBoard(event.getX(), event.getY());
            if (pos == null) return;

            int col = pos[0], row = pos[1];

            if (selectedSquare == null) {
                if (gameManager.getPieceAt(row, col) != null)
                    selectedSquare = new Point(col, row);
            } else {
                int fromCol = (int) selectedSquare.x();
                int fromRow = (int) selectedSquare.y();
            }

            draw();
        });



    }

    private void draw() {
        var gc = getGraphicsContext2D();
        double width = getWidth(), height = getHeight();
        double boardSize = Math.min(width, height);
        double cellSize = boardSize / 8.0;
        double offsetX = (width - boardSize) / 2;
        double offsetY = (height - boardSize) / 2;

        Color light = Color.web("#f0d9b5");
        Color dark = Color.web("#b58863");
        Color background = Color.web("#302e2b");

        gc.setFill(background);
        gc.fillRect(0, 0, width, height);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean lightSquare = (row + col) % 2 == 0;
                gc.setFill(lightSquare ? light : dark);
                gc.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
            }
        }

        // Desenha etiquetas
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(cellSize * 0.2));
        for (int i = 0; i < 8; i++) {
            String colLabel = String.valueOf((char) ('a' + i));
            String rowLabel = String.valueOf(8 - i);

            gc.fillText(colLabel,
                    offsetX + i * cellSize + cellSize * 0.85,
                    offsetY + boardSize + cellSize * 0.15);

            gc.fillText(rowLabel,
                    offsetX - cellSize * 0.3,
                    offsetY + i * cellSize + cellSize * 0.65);
        }

        // Highlight peça selecionada
        if (selectedSquare != null) {
            int selCol = (int) selectedSquare.x();
            int selRow = (int) selectedSquare.y();
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.strokeRect(offsetX + selCol * cellSize, offsetY + selRow * cellSize, cellSize, cellSize);
        }


    }

    private int[] mouseToBoard(double x, double y) {
        double size = Math.min(getWidth(), getHeight());
        double cell = size / 8.0;
        double offsetX = (getWidth() - size) / 2;
        double offsetY = (getHeight() - size) / 2;

        int col = (int) ((x - offsetX) / cell);
        int row = (int) ((y - offsetY) / cell);

        if (col < 0 || col >= 8 || row < 0 || row >= 8)
            return null;

        return new int[]{col, row};
    }
}
