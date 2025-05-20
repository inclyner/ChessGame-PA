package pt.isec.pa.chess.model.data;


import java.io.Serializable;

public record Square(int column, int row) implements Serializable {}
