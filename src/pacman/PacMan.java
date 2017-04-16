package pacman;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PacMan {

    static final int CHUNK_RATIO = 16;
    private static final int VERTICAL_CHUNKS = 21;
    private static final int HORIZONTAL_CHUNKS = 19;
    private static final int VERTICAL_SQUARES = VERTICAL_CHUNKS * CHUNK_RATIO;
    static final int HORIZONTAL_SQUARES = HORIZONTAL_CHUNKS * CHUNK_RATIO;
    private static final int NANOSECONDS_PER_SECOND = 1000000000;
    private static final int NANOSECONDS_PER_FRAME = NANOSECONDS_PER_SECOND / 50;
    private static final String GAME_TITLE = "Pac-Man";

    private static final JFrame frame = new JFrame(GAME_TITLE);

    static Square[][] board = new Square[VERTICAL_SQUARES][HORIZONTAL_SQUARES];

    private static boolean isGameOngoing = true;
    private Player player = new Player();

    public static void main(String[] args) {
        PacMan gameStart = new PacMan();
        gameStart.controlCentre();
    }

    private void controlCentre() {
        initializeBoard();
        configureGUI();
        runTimeManager();
    }

    private void initializeBoard() {
        final int status[][] =
                {
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                        {1, 3, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 1, 3, 1},
                        {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                        {1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 1},
                        {1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1},
                        {1, 1, 1, 1, 2, 1, 1, 1, 0, 1, 0, 1, 1, 1, 2, 1, 1, 1, 1},
                        {1, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 1, 1, 1},
                        {1, 1, 1, 1, 2, 1, 0, 1, 1, 0, 1, 1, 0, 1, 2, 1, 1, 1, 1},
                        {4, 0, 0, 0, 2, 0, 0, 1, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0, 5},
                        {1, 1, 1, 1, 2, 1, 0, 1, 1, 1, 1, 1, 0, 1, 2, 1, 1, 1, 1},
                        {1, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 1, 1, 1},
                        {1, 1, 1, 1, 2, 1, 0, 1, 1, 1, 1, 1, 0, 1, 2, 1, 1, 1, 1},
                        {1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                        {1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 1, 2, 1},
                        {1, 3, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 3, 1},
                        {1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 1},
                        {1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1},
                        {1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1},
                        {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
        for (int vertical = 0; vertical < VERTICAL_CHUNKS; vertical++) {
            for (int horizontal = 0; horizontal < HORIZONTAL_CHUNKS; horizontal++) {
                switch (status[vertical][horizontal]) {
                    case 0:
                        setChunk(horizontal, vertical, Square.CLEAR);
                        break;
                    case 1:
                        setChunk(horizontal, vertical, Square.WALL);
                        break;
                    case 2:
                        setPoint(horizontal, vertical);
                        break;
                    case 3:
                        setBall(horizontal, vertical);
                        break;
                    case 4:
                        setLeftColumn(horizontal, vertical);
                        break;
                    case 5:
                        setRightColumn(horizontal, vertical);
                        break;
                    default:
                        System.err.println("Error In PacMan.initializeBoard: Unhandled Case.");
                        setChunk(horizontal, vertical, Square.CLEAR);
                        break;
                }
            }
        }
    }

    private void setChunk(int xChunk, int yChunk, Square setting) {
        for (int vertical = yChunk * CHUNK_RATIO; vertical < (yChunk + 1) * CHUNK_RATIO; vertical++) {
            for (int horizontal = xChunk * CHUNK_RATIO; horizontal < (xChunk + 1) * CHUNK_RATIO; horizontal++) {
                board[vertical][horizontal] = setting;
            }
        }
    }

    private void setPoint(int xChunk, int yChunk) {
        final int pointStatus[][] =
                {
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
        setBoardFromStatusBoard(pointStatus, Square.POINT, xChunk, yChunk);
    }

    private void setBall(int xChunk, int yChunk) {
        final int ballStatus[][] =
                {
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };
        setBoardFromStatusBoard(ballStatus, Square.BALL, xChunk, yChunk);
    }

    private void setBoardFromStatusBoard(int[][] statusBoard, Square meaningOfOne, int xChunk, int yChunk) {
        final int xLocation = xChunk * CHUNK_RATIO;
        final int yLocation = yChunk * CHUNK_RATIO;
        for (int vertical = yLocation; vertical < yLocation + CHUNK_RATIO; vertical++) {
            for (int horizontal = xLocation; horizontal < xLocation + CHUNK_RATIO; horizontal++) {
                if (statusBoard[vertical - yLocation][horizontal - xLocation] == 0) {
                    board[vertical][horizontal] = Square.CLEAR;
                } else {
                    board[vertical][horizontal] = meaningOfOne;
                }
            }
        }
    }

    private void setLeftColumn(int xChunk, int yChunk) {
        final int xLocation = xChunk * CHUNK_RATIO;
        final int yLocation = yChunk * CHUNK_RATIO;
        for (int vertical = yLocation; vertical < yLocation + CHUNK_RATIO; vertical++) {
            board[vertical][0] = Square.JUMP;
            for (int horizontal = xLocation + 1; horizontal < xLocation + CHUNK_RATIO; horizontal++) {
                board[vertical][horizontal] = Square.CLEAR;
            }
        }
    }

    private void setRightColumn(int xChunk, int yChunk) {
        final int xLocation = xChunk * CHUNK_RATIO;
        final int yLocation = yChunk * CHUNK_RATIO;
        for (int vertical = yLocation; vertical < yLocation + CHUNK_RATIO; vertical++) {
            board[vertical][xLocation + CHUNK_RATIO - 1] = Square.JUMP;
            for (int horizontal = xLocation; horizontal < xLocation + CHUNK_RATIO - 1; horizontal++) {
                board[vertical][horizontal] = Square.CLEAR;
            }
        }
    }

    private void configureGUI() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(new GridPane());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        doKeyListener(frame);
    }

    private void doKeyListener(JFrame frame) {
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                final int LEFT = 37;
                final int UP = 38;
                final int RIGHT = 39;
                final int DOWN = 40;
                switch (e.getKeyCode()) {
                    case LEFT:
                        player.changeDirection(Direction.LEFT);
                        break;
                    case UP:
                        player.changeDirection(Direction.UP);
                        break;
                    case RIGHT:
                        player.changeDirection(Direction.RIGHT);
                        break;
                    case DOWN:
                        player.changeDirection(Direction.DOWN);
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Not needed.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not needed.
            }
        });
    }

    private void runTimeManager() {
        long lastFrameTime = System.nanoTime();
        long currentTime;
        while (isGameOngoing) {
            currentTime = System.nanoTime();
            final long timeSinceLastFrame = currentTime - lastFrameTime;
            if (timeSinceLastFrame >= NANOSECONDS_PER_FRAME) {
                executeFrame();
                lastFrameTime = System.nanoTime();
            }
        }
    }

    static void gameWin() {
        gameEndMessage("Congratulations, You Won!");
        System.exit(0);
    }

    static void gameLose() {
        gameEndMessage("Sadly, You Lost...");
        System.exit(0);
    }

    private static void gameEndMessage(String text) {
        if (isGameOngoing) {
            isGameOngoing = false;
            final String[] options = new String[]{"Continue"};
            final int check = JOptionPane.showOptionDialog(null, text, GAME_TITLE, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (check == -1) {
                System.exit(0);
            }
        } else {
            System.err.println("Error in PacMan.gameEndMessage: multiple win and/or lose instances were called.");
        }
    }

    private void executeFrame() {
        player.attemptMove();
    }

    private class GridPane extends JPanel {

        private final List<Rectangle> cells = new ArrayList<>(VERTICAL_SQUARES * HORIZONTAL_SQUARES);

        @Override
        public Dimension getPreferredSize() {
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int guiDisplay;
            if (screenSize.getWidth() < screenSize.getHeight()) {
                guiDisplay = (int) (screenSize.getWidth() * 0.8);
            } else {
                guiDisplay = (int) (screenSize.getHeight() * 0.8);
            }
            final int multiplier = guiDisplay / VERTICAL_SQUARES;
            return new Dimension(multiplier * HORIZONTAL_SQUARES, multiplier * VERTICAL_SQUARES);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g.create();
            final int cellWidth = getWidth() / HORIZONTAL_SQUARES;
            final int cellHeight = getHeight() / VERTICAL_SQUARES;
            if (cells.isEmpty()) {
                for (int row = 0; row < VERTICAL_SQUARES; row++) {
                    for (int col = 0; col < HORIZONTAL_SQUARES; col++) {
                        Rectangle cell = new Rectangle(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                        cells.add(cell);
                    }
                }
            }

            for (int vertical = 0; vertical < VERTICAL_SQUARES; vertical++) {
                for (int horizontal = 0; horizontal < HORIZONTAL_SQUARES; horizontal++) {
                    switch (board[vertical][horizontal]) {
                        case WALL:
                            g2d.setColor(Color.BLACK);
                            break;
                        case CLEAR:
                        case JUMP:
                        case WHITE:
                            g2d.setColor(Color.LIGHT_GRAY);
                            break;
                        case POINT:
                        case BALL:
                        case YELLOW:
                            g2d.setColor(Color.YELLOW);
                            break;
                        case RED:
                            g2d.setColor(Color.RED);
                            break;
                        case CYAN:
                            g2d.setColor(Color.CYAN);
                            break;
                        case PINK:
                            g2d.setColor(Color.PINK);
                            break;
                        case ORANGE:
                            g2d.setColor(Color.ORANGE);
                            break;
                        case BLUE:
                        case SCARED_RED:
                        case SCARED_CYAN:
                        case SCARED_ORANGE:
                        case SCARED_PINK:
                            g2d.setColor(Color.BLUE);
                            break;
                        default:
                            System.err.println("Error in PacMan.GridPane.paintComponent: Invalid Color.");
                            break;
                    }
                    final Rectangle cell = cells.get(horizontal + vertical * HORIZONTAL_SQUARES);
                    g2d.fill(cell);
                    repaint();
                }
            }
        }
    }
}
