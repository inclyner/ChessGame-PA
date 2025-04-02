package pt.isec.pa.chess.model.data;

import java.io.*;

public class ChessGameSerialization {


    void exportGame(String filename,ChessGame chessGame){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(chessGame);

        } catch (Exception e) {
            System.out.println("Error saving data: "+e.getMessage());
        }
    }

    void importGame(String filename){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))){
            ChessGame chessGame = (ChessGame) ois.readObject();
        } catch (Exception e) {
            System.out.println("Error saving data: "+e.getMessage());
        }
    }

}
