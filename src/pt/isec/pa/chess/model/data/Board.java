package pt.isec.pa.chess.model.data;

public class Board {
    //* column a to h
    //* row 1 to 8
    private static final int BOARD_SIZE = 8;

    private static Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];

    Board() {
        this.setupBoard();
    }

    private void setupBoard() {
        //TODO rever lógica
        // board [coluna, linha]
        for (int column = 0; column <= BOARD_SIZE-1; column++) {
            for (int row = 0; row <= BOARD_SIZE-1; row++) {
                if(row == 2) {
                    row = 6;
                    column=0;
                } //skips middle of the board
                if (column == 0 || column == 7) {
                    if (row == 0) {
                        board[column][row] = new Rook(true);
                    } else if (row == 7) {
                        board[column][row] = new Rook(false);
                    }
                }
                if (column == 1 || column == 6) {
                    if (row == 0) {
                        board[column][row] = new Knight(true);
                    } else if (row == 7) {
                        board[column][row] = new Knight(false);
                    }
                }
                if (column == 2 || column == 5) {
                    if (row == 0) {
                        board[column][row] = new Bishop(true);
                    } else if (row == 7) {
                        board[column][row] = new Bishop(false);
                    }
                }
                if (column == 4) {
                    if (row == 0) {
                        board[column][row] = new King(true);
                    } else if (row == 7) {
                        board[column][row] = new King(false);
                    }
                }
                if (column == 5) {
                    if (row == 0 ) {
                        board[column][row] = new Queen(true);
                    } else if (row ==7) {
                        board[column][row] = new Queen(false);
                    }
                }
                if (row == 1) {
                    board[column][row] = new Pawn(true);
                }
                if (row == 6) {
                    board[column][row] = new Pawn(false);
                }
            }
        }
    }

    public void addPiece(Piece piece, int column, int row) {}

    public void removePiece(Piece piece,int column, int row) {}


    public boolean checkMove(int column, int row) {
        //checks if piece can be moved
        //eventually will call checkCheck method
        return false;
    }

    public boolean checkCheck(Piece piece, int column, int row) {
        return false;
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(); //thread safe for multiple games
        //for logic is reversed to print board correctly
        char [][] positions = {{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}, {'8', '7', '6', '5', '4', '3', '2', '1'}};
        for (int column = 0; column <= BOARD_SIZE-1; column++) {
            for (int row = 0; row <= BOARD_SIZE-1; row++) {
                if (board[column][row] != null) {
                    buffer.append(board[column][row].toString()).append(positions[column][row]);
                }
            }
        }



          return "";
    }


}
