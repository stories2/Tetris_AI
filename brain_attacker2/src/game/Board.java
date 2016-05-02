package game;

import game.Shape.Tetrominoes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import team0.MyAI;

public class Board extends JPanel implements ActionListener {
    
    public static final int WIDTH = 10;
    public static final int HEIGHT = 22;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    int score = 0;
    int curX = 0;
    int curY = 0;
    
    JLabel scoreLabel;
    Shape curPiece;
    
    public static Tetrominoes[] board;
    
    public static final int NUMPIECESLIMIT = 300;
    public static final int TIMELIMIT = 180 * 1000;
    long startTime = 0;
    int nDropped = 0;
    
    boolean playingMode = false;
    private Random random;
    public long seed = (long)(Math.random() * Long.MAX_VALUE);
    AI ai;

    public Board(GameFrame game,String[] args) {
       if (playingMode) {
           setFocusable(true);
           addKeyListener(new KeyHandler());
       }
       
       curPiece = new Shape();
       timer = new Timer(400, this);
       timer.start();     
       
       try
       {
    	   seed = Long.parseLong(args[5]);
       }
       catch(Exception e)
       {
    	   System.out.println("Error : "+e.getMessage());
       }
       
       scoreLabel = game.scoreLabel;
       game.infoLabel.setText("seed: " + seed);
       board = new Tetrominoes[WIDTH * HEIGHT];
       clearBoard();
       
       random = new Random(seed);
       ai = new MyAI(args);
       ai.setBoard(this);
    }

    public int squareWidth() { return (int) getSize().getWidth() / WIDTH; }
    public int squareHeight() { return (int) getSize().getHeight() / HEIGHT; }
    public Tetrominoes shapeAt(int x, int y) { return board[(y * WIDTH) + x]; }    

    public void start() {      
        isStarted = true;
        isFallingFinished = false;
        score = 0;
        clearBoard();
        
        startTime = System.currentTimeMillis();        
        timer.start();              
        if (playingMode) {
            requestFocus();
        } else {
            ai.start();        
        }
        newPiece();
    }
    
    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();        
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }

    }
    
    private void pieceDropped() {
        isFallingFinished = true;
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY + curPiece.y(i);
            board[(y * WIDTH) + x] = curPiece.getShape();
        }        
        removeFullLines();       
    }
    
    private void delete() {
        isFallingFinished = true;
        score -= 1;
        scoreLabel.setText(String.valueOf(score));
    }

    private void clearBoard() {
        for (int i = 0; i < HEIGHT * WIDTH; ++i)
            board[i] = Tetrominoes.EMPTY;
    }
    
    private void newPiece() {       
        nDropped += 1;
        curPiece.setRandomShape(random);
        curX = WIDTH / 2;
        curY = HEIGHT -  1 - curPiece.maxY();     
                
        if (!tryMove(curPiece, curX, curY))
            gameover("Game over");
        if (nDropped > NUMPIECESLIMIT)
            gameover("No more pieces");        
        if (System.currentTimeMillis() - startTime > TIMELIMIT)            
            gameover("Time over");
    }
    
    private void gameover(String message) {
        curPiece.setShape(Tetrominoes.EMPTY);
        timer.stop();        
        isStarted = false;
        scoreLabel.setText("score: " + score + " / " + message);
    }
    
    private boolean tryMove(Shape piece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + piece.x(i);
            int y = newY + piece.y(i);
            if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
                return false;
            if (shapeAt(x, y) != Tetrominoes.EMPTY)
                return false;
        }

        curPiece = piece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }    

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = HEIGHT; i --> 0;) {
            boolean lineIsFull = true;
            for (int j = 0; j < WIDTH; ++j) {
                if (shapeAt(j, i) == Tetrominoes.EMPTY) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                ++numFullLines;                
                for (int k = i; k < HEIGHT - 1; ++k) {
                    for (int j = 0; j < WIDTH; ++j)
                        board[(k * WIDTH) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            score += 10 * (numFullLines * 2 - 1);
            scoreLabel.setText(String.valueOf(score));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.EMPTY);
            repaint();
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - HEIGHT * squareHeight();

        for (int i = 0; i < HEIGHT; ++i) {
            for (int j = 0; j < WIDTH; ++j) {
                Tetrominoes shape = shapeAt(j, HEIGHT - i - 1);
                if (shape != Tetrominoes.EMPTY)
                    drawSquare(g, 0 + j * squareWidth(), boardTop + i
                            * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.EMPTY) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY + curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(), boardTop
                        + (HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }
    
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color color = shape.getColor();
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y
                + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x
                + squareWidth() - 1, y + 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {            
            newPiece();
            isFallingFinished = false;
        } else {
            oneLineDown();
        }
    }
    
    public enum ControlKeys {
        
        LEFT(KeyEvent.VK_LEFT), RIGHT(KeyEvent.VK_RIGHT), UP(KeyEvent.VK_UP), 
        DOWN(KeyEvent.VK_DOWN), D(KeyEvent.VK_D), SPACE(KeyEvent.VK_SPACE),
        DEL(KeyEvent.VK_DELETE);
        
        int keyCode;
        
        ControlKeys(int keyCode) {
            this.keyCode = keyCode;
        }
        
        int getKeyCode() {
            return keyCode; 
        }
    };
    
    void control(int keycode) {
        if (!isStarted || isFallingFinished || curPiece.getShape() == Tetrominoes.EMPTY)  
            return;

        switch (keycode) {
        case KeyEvent.VK_LEFT:
            tryMove(curPiece, curX - 1, curY);
            break;
        case KeyEvent.VK_RIGHT:
            tryMove(curPiece, curX + 1, curY);
            break;
        case KeyEvent.VK_DOWN:
            tryMove(curPiece.rotateCW(), curX, curY);
            break;
        case KeyEvent.VK_UP:
            tryMove(curPiece.rotateCCW(), curX, curY);
            break;
        case KeyEvent.VK_SPACE:
            dropDown();
            break;
        case KeyEvent.VK_D:
            oneLineDown();
            break;
        case KeyEvent.VK_DELETE:
            delete();
            break;
        }
    }
    
    class KeyHandler extends KeyAdapter {        
        @Override
        public void keyPressed(KeyEvent e) {
            control(e.getKeyCode());    
        }
    }
}