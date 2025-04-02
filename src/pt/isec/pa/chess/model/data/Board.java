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


    private static Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];
    private static Piece[][] nextMoveBoard;
    private static ArrayList<int []> movelist= new ArrayList<>();
    private boolean isWhitePlaying;



    public Board() {
        this.setupBoard();
    }


    private void setupBoard() {
        //? devo considerar que as peças brancas podem estar em cima ou em baixo? (isto mexe com os vetores),teria de ter um bool whiteontop para verificar tudo
        //TODO tirar ifs e meter so board[0][0] = factory ROOT  por exemplo9
        for (int column = 0; column <= BOARD_SIZE-1; column++) {
            for (int row = 0; row <= BOARD_SIZE-1; row++) {
                if(row == 2) {
                    row = 6;
                    column=0;
                } //skips middle of the board
                if (column == 0 || column == 7) {
                    if (row == 0) {
                        addPiece(PieceFactoryType.createPiece(PieceType.ROOK, true), column, row);
                        //board[column][row] = new Rook(true);
                    } else if (row == 7) {
                        addPiece(PieceFactoryType.createPiece(PieceType.ROOK, false), column, row);
                        //board[column][row] = new Rook(false);
                    }
                }
                if (column == 1 || column == 6) {
                    if (row == 0) {
                        addPiece(PieceFactoryType.createPiece(PieceType.KNIGHT, true), column, row);
                        //board[column][row] = new Knight(true);
                    } else if (row == 7) {
                        addPiece(PieceFactoryType.createPiece(PieceType.KNIGHT, false), column, row);
                        //board[column][row] = new Knight(false);
                    }
                }
                if (column == 2 || column == 5) {
                    if (row == 0) {
                        addPiece(PieceFactoryType.createPiece(PieceType.BISHOP, true), column, row);
                        //board[column][row] = new Bishop(true);
                    } else if (row == 7) {
                        //board[column][row] = new Bishop(false);
                        addPiece(PieceFactoryType.createPiece(PieceType.BISHOP, false), column, row);
                    }
                }
                if (column == 4) {
                    if (row == 0) {
                        //board[column][row] = new King(true);
                        addPiece(PieceFactoryType.createPiece(PieceType.KING, true), column, row);
                    } else if (row == 7) {
                        //board[column][row] = new King(false);
                        addPiece(PieceFactoryType.createPiece(PieceType.KING, false), column, row);
                    }
                }
                if (column == 5) {
                    if (row == 0 ) {
                        addPiece(PieceFactoryType.createPiece(PieceType.QUEEN, true), column, row);
                        //board[column][row] = new Queen(true);
                    } else if (row ==7) {
                        //board[column][row] = new Queen(false);
                        addPiece(PieceFactoryType.createPiece(PieceType.QUEEN, false), column, row);
                    }
                }
                if (row == 1) {
                    //oard[column][row] = new Pawn(true);
                    addPiece(PieceFactoryType.createPiece(PieceType.PAWN, true), column, row);
                }
                if (row == 6) {
                    //board[column][row] = new Pawn(false);
                    addPiece(PieceFactoryType.createPiece(PieceType.PAWN, false), column, row);

                }
            }
        }
    }

    public boolean addPiece(Piece piece, int column, int row) {
        // será chamado com addPiece (Knight,1,1); com o uso de uma factory de peças
        if(board[column][row] != null) return false;
        board[column][row] = piece;
        return true;
    }

    public boolean removePiece(int column, int row) {
        //checks if piece can be removed
        //! if(board[column][row] instanceof King) return false; //viola o encapsulamento e polimorfismo
        if(board[column][row].isKing())
            return false;
        board[column][row] = null;
        return true;
    }

    public boolean movePiece(Piece piece, int column, int row, boolean isWhite) {
        if (checkMove(piece, column, row, isWhite)){
            board[column][row] = piece;
            return true;
        }
        return false;
    }

    //TODO a peça sabe que é branco ou preta, a partir daqui entra o jogador (o isWhite nao entra aqui)
    public boolean checkMove(Piece piece,int column, int row,boolean isWhite) {
        //checks if piece can be moved
        //eventually will call checkCheck method
        nextMoveBoard = board.clone();
        nextMoveBoard[column][row] = piece;
        return checkCheck(isWhite,true);
    }

    public boolean checkCheck(boolean isWhite, boolean selfCheck) {
        //checks if player is in check (self check is when a player is moving a piece)

        for (int column = 0; column <= BOARD_SIZE - 1; column++) {
            for (int row = 0; row <= BOARD_SIZE - 1; row++) {
                if(selfCheck) {
                    //TODO pelo move set de cada peça verificar se alguma atinge o próprio king a usar o nextMoveBoard
                    //aplicar vetor de movimento até encontrar uma peça ou estar fora da board (quando encontro uma peça verifico se é King)
                    for (MoveVector move : nextMoveBoard[column][row].getMoves()){
                        if(isWhite) {
                            if (nextMoveBoard[move.column()][move.row()].isKing() && nextMoveBoard[move.column()][move.row()].isWhite)
                                return true;
                        }
                    }
                }

            }
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
            Piece p = PieceFactoryTxt.createPiece(pieceChar);
            if((pieceChar=='r' || pieceChar =='R' ||pieceChar=='k' || pieceChar =='K')&& piece.length()!=4)
                p.setHasMoved();
            int columnIndex = Arrays.asList(positions[0]).indexOf(columnChar);
            int rowIndex = rowChar -1;
            board[columnIndex][rowIndex]=p;
        }
    }


}

