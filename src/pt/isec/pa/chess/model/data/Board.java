package pt.isec.pa.chess.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Board implements Serializable {
    @Serial
    static final long serialVersionUID = 100L;
    //* column a to h
    //* row 1 to 8
    private static final int BOARD_SIZE = 8;


    private Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];
    private boolean isWhitePlaying;



    public Board() {
        this.setupBoard();
    }


    private void setupBoard() {
        // Primeira linha (brancas)
        addPiece(PieceType.ROOK, true, 0, 0);
        addPiece(PieceType.KNIGHT, true, 1, 0);
        addPiece(PieceType.BISHOP, true, 2, 0);
        addPiece(PieceType.QUEEN, true, 3, 0);
        addPiece(PieceType.KING, true, 4, 0);
        addPiece(PieceType.BISHOP, true, 5, 0);
        addPiece(PieceType.KNIGHT, true, 6, 0);
        addPiece(PieceType.ROOK, true, 7, 0);

        // Peões brancos
        for (int col = 0; col < BOARD_SIZE; col++) {
            addPiece(PieceType.PAWN, true, col, 1);
        }

        // Peões pretos
        for (int col = 0; col < BOARD_SIZE; col++) {
            addPiece(PieceType.PAWN, false, col, 6);
        }

        // Primeira linha (pretas)
        addPiece(PieceType.ROOK, false, 0, 7);
        addPiece(PieceType.KNIGHT, false, 1, 7);
        addPiece(PieceType.BISHOP, false, 2, 7);
        addPiece(PieceType.QUEEN, false, 3, 7);
        addPiece(PieceType.KING, false, 4, 7);
        addPiece(PieceType.BISHOP, false, 5, 7);
        addPiece(PieceType.KNIGHT, false, 6, 7);
        addPiece(PieceType.ROOK, false, 7, 7);
    }


    public boolean addPiece(PieceType type,boolean isWhite, int column, int row) {
        // será chamado com addPiece (Knight,1,1); com o uso de uma factory de peças
        Square sq = new Square(column,row);
        Piece piece=PieceFactoryType.createPiece(type,isWhite,sq);
        if(board[column][row] != null) return false;

        board[column][row] = piece;
        return true;
    }

    public boolean removePiece(int column, int row) {
        //checks if piece can be removed
        if(board[column][row] != null && board[column][row].isKing())
            return false;
        board[column][row] = null;
        return true;
    }


    public boolean movePiece(Piece piece, int column, int row, boolean isWhitePlaying) {
        if (checkMove(piece, column, row, isWhitePlaying)) {
            piece.position= new Square(column, row);
            piece.setHasMoved();
            board[column][row] = piece;
            return true;
        }
        return false;

    }



    public boolean checkMove(Piece piece, int column, int row, boolean isWhitePlaying) {
        //checks if player is in check (self check is when a player is moving a piece)
        if (piece == null || piece.isWhite() != isWhitePlaying)
            return false;

        ArrayList<Square> possibleMoves = piece.getMoves(this);
        for (Square s : possibleMoves) {
            if (s.column() == column && s.row() == row)
                return true;
        }
        return false;

    }


    public boolean isWhitePlaying() {
        return isWhitePlaying;
    }

    public boolean checkEndGame() {
        //TODO implementar
        return false;
    }

    public void clear() {
        for (int column = 0; column <= BOARD_SIZE-1; column++) {
            for (int row = 0; row <= BOARD_SIZE-1; row++) {
                board[column][row]=null;
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(); //thread safe for multiple games
        char [][] positions = {{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}, {'8', '7', '6', '5', '4', '3', '2', '1'}};
        for (int column = 0; column <= BOARD_SIZE-1; column++) {
            for (int row = 0; row <= BOARD_SIZE-1; row++) {
                if (board[column][row] != null) {
                    buffer.append(board[column][row].toString()).append(positions[column][row]);
                    if(!board[column][row].hasMovedMark())
                        buffer.append("*");
                }
            }
        }
        return buffer.toString();
    }



    public void importGame(String boardString) {
        String[] splitboard = boardString.split(",");
        char[][] positions = {{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}, {'8', '7', '6', '5', '4', '3', '2', '1'}};

        for (String piece : splitboard) {
            char pieceChar = piece.charAt(0);
            char columnChar = piece.charAt(1);
            char rowChar = piece.charAt(2);
            Piece p = PieceFactoryTxt.createPiece(pieceChar); //TODO adicionar aqui o square para construir de razi e nao usao o pircefactorytxt a chamar o piecefactorytype
            if((pieceChar=='r' || pieceChar =='R' ||pieceChar=='k' || pieceChar =='K')&& piece.length()!=4)
                p.setHasMoved();
            int columnIndex = Arrays.asList(positions[0]).indexOf(columnChar);
            int rowIndex = rowChar -1;
            board[columnIndex][rowIndex]=p;
        }
    }


    public boolean isWithinBounds(int col, int row) {
        return col < BOARD_SIZE && row < BOARD_SIZE;
    }

    public Piece getPieceAt(int col, int row) {
        return board[col][row];
    }
}

