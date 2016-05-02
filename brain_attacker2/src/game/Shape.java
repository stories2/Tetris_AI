package game;

import java.awt.Color;
import java.util.Random;

public class Shape {

    public enum Tetrominoes {
        
        EMPTY(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, Color.BLACK), 
        ZSHAPE(new int[][] { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, Color.RED),  
        SSHAPE(new int[][] { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, Color.GREEN), 
        ISHAPE(new int[][] { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, Color.CYAN), 
        TSHAPE(new int[][] { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, -1 } }, Color.MAGENTA), 
        SQUARE(new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, Color.YELLOW), 
        LSHAPE(new int[][] { { -1, 1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, Color.ORANGE),
        JSHAPE(new int[][] { { 1, 1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, Color.BLUE);        
        
        Color color;
        int[][] coords;
        
        Tetrominoes(int[][] coords, Color color) {
            this.coords = new int[coords.length][];
            for (int i = 0; i < coords.length; i++) {
                this.coords[i] = new int[coords[i].length];
                for (int j = 0; j < coords[i].length; ++j) {
                    this.coords[i][j] = coords[i][j];
                }
            }
            this.color = color;
        }
        
        public int[][] getCoords() {return coords; }
        public Color getColor() { return color; }
    };

    private Tetrominoes pieceShape;
    private int coords[][];

    Shape() {
        coords = new int[4][2];
        setShape(Tetrominoes.EMPTY);
    }

    void setShape(Tetrominoes shape) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = shape.getCoords()[i][j];
            }
        }
        pieceShape = shape;
    }

    private void setX(int index, int x) {
        coords[index][0] = x;
    }

    private void setY(int index, int y) {
        coords[index][1] = y;
    }

    public int x(int index) {
        return coords[index][0];
    }

    public int y(int index) {
        return coords[index][1];
    }

    public Tetrominoes getShape() {
        return pieceShape;
    }

    void setRandomShape(Random r) {        
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
    }

    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }
    
    public int maxY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.max(m, coords[i][1]);
        }
        return m;
    }

    public Shape rotateCW() {
        if (pieceShape == Tetrominoes.SQUARE)
            return this;
        
        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {            
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }
    
    public Shape rotateCCW() {
        if (pieceShape == Tetrominoes.SQUARE)
            return this;
        
        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {            
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}