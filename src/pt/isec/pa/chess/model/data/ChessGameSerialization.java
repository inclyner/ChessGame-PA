package pt.isec.pa.chess.model.data;

import java.io.*;

public class ChessGameSerialization {


    public static void serialize(ChessGame game, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(game);
        } catch (IOException e) {
            System.err.println("Erro ao guardar o jogo: " + e.getMessage());
        }
    }

    public static ChessGame deserialize(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (ChessGame) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar o jogo: " + e.getMessage());
            return null;
        }
    }



    public static void exportGame(String path, ChessGame chessGame) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(chessGame.exportGame());
        } catch (IOException e) {
            System.err.println("Erro ao exportar jogo para texto: " + e.getMessage());
        }
    }

    public static void importGame(String path, ChessGame chessGame) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String gameState = reader.readLine();
            chessGame.importGame(gameState);
        } catch (IOException e) {
            System.err.println("Erro ao importar jogo de texto: " + e.getMessage());
        }
    }

}
