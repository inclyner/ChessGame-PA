package pt.isec.pa.chess.model.data;

import java.io.Serial;
import java.io.Serializable;


//Facade
public class ChessGame implements Serializable {
    @Serial
    static final long serialVersionUID = 100L;

    Board board;

    public ChessGame() {
        this.board = new Board();
    }


    public boolean checkEndGame() {
        return board.checkEndGame();
    }

    public boolean isWhitePlaying() {
        return board.isWhitePlaying();
    }

//! não está de acordo com o padrão facade
    public void importGame(String boardString) {
        board.clear();
        board.importGame(boardString);


}
