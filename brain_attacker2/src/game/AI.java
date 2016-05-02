package game;

import game.Board.ControlKeys;
import game.Shape.Tetrominoes;

public class AI extends Thread {
    
    Board board;

    final void setBoard(Board board) {
        this.board = board;
    }
    
    @Override
    final public void run() {
        while (board.isStarted) {
            try {
                ControlKeys key = command();            
                board.control(key.getKeyCode());
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }            
        }
    }

    protected int getCurX() { return board.curX; }
    protected int getCurY() { return board.curY; }
    protected Shape getCurPiece() { return board.curPiece; }
    protected Tetrominoes shapeAt(int x, int y) { return board.shapeAt(x, y); }
    
    protected long getStartTime() { return board.startTime; }
    protected int getnDropped() { return board.nDropped; }
    protected int getScore() { return board.score; }    
    
    protected ControlKeys command() {
        ControlKeys[] values = ControlKeys.values();
        return values[(int)(Math.random() * values.length)];
    }    
}