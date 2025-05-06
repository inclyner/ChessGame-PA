package pt.isec.pa.chess.model.data;

import java.io.Serial;
import java.io.Serializable;

//Facade
public class ChessGame implements Serializable {
    @Serial
    static final long serialVersionUID = 100L;

    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;
    private int moveCount;
    private Square lastMoveFrom;
    private Square lastMoveTo;

    public ChessGame() {
        this.board = new Board();
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        this.currentPlayer = whitePlayer;
        this.moveCount = 0;
    }

    public boolean move(Square from, Square to) {
        Piece piece = board.getPieceAt(from.column(), from.row()); // Posição inicial

        // Checa se a peça existe e se é do jogador atual
        if (piece == null || piece.isWhite() != currentPlayer.isWhite()) {
            return false;
        }

        if (piece instanceof King king) {
            if (!king.hasMoved()) {
                int row = from.row();
                if (to.equals(new Square(6, row))) { // Short castle
                    board.movePiece(board.getPieceAt(7, row), 5, row, king.isWhite());
                } else if (to.equals(new Square(2, row))) { // Long castle
                    board.movePiece(board.getPieceAt(0, row), 3, row, king.isWhite());
                }
            }
        }

        if (piece instanceof Pawn pawn) {
            // En passant
            if (from.column() != to.column() && board.getPieceAt(to.column(), to.row()) == null) {
                int capturedPawnRow = to.row() + (pawn.isWhite() ? -1 : 1);
                board.removePiece(to.column(), capturedPawnRow);
            }
        }

        // Tenta mover
        if (board.movePiece(piece, to.column(), to.row(), currentPlayer.isWhite())) {
            // Remove a peça da posiçao anterior
            board.removePiece(from.column(), from.row());

            // Verifica o ultimo moivemento
            lastMoveFrom = from;
            lastMoveTo = to;

            moveCount++;
            switchPlayer();

            // Lógica de promoção de peão
            if (piece instanceof Pawn pawn && pawn.needsPromotion()) {
                // O jogador decide
                PieceType promotionType = PieceType.QUEEN; // Default to queen

                Piece promotedPiece = pawn.promote(board, promotionType);
                board[to.column()][to.row()] = promotedPiece;
            }

            return true;
        }

        return false;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    public boolean checkEndGame() {
        return board.checkEndGame();
    }

    public void importGame(String gameState) {
        // Dividir os dados exportados
        String[] split = gameState.split(",", 3);

        // Definir o jogador atual
        this.currentPlayer = split[0].equalsIgnoreCase("WHITE") ? whitePlayer : blackPlayer;

        // Definir o número de jogadas
        this.moveCount = Integer.parseInt(split[1]);

        // Recriar o tabuleiro vazio e importar o estado
        this.board = new Board();
        this.board.importGame(split[2]);
    }

    public String exportGame() {
        // Formato: "WHITE/BLACK,moveCount,peçaposiçao,peçaposiçao,..."
        // Exemplo: "WHITE,15,Ra1,Nb1,..."
        StringBuilder gameState = new StringBuilder();

        // Adicionar o jogador atual
        gameState.append(currentPlayer.getColor());
        gameState.append(",");

        // Adicionar o número de jogadas
        gameState.append(moveCount);
        gameState.append(",");

        // Adicionar o estado do tabuleiro
        gameState.append(board.toString());

        return gameState.toString();
    }

    public boolean startGame(String player1, String player2) {
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        return true;
    }
}
