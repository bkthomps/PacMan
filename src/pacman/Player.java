package pacman;

class Player {
    private int xCoordinate = 9 * PacMan.CHUNK_RATIO;
    private int yCoordinate = 11 * PacMan.CHUNK_RATIO;
    private Direction direction = Direction.NONE;
    private Direction lastDirection = Direction.LEFT;

    void changeDirection(Direction newDirection) {
        lastDirection = direction;
        direction = newDirection;
    }

    void attemptMove() {
        if (direction == Direction.NONE || isMovePossible()) {
            move();
        } else {
            lastDirection = direction;
            direction = Direction.NONE;
        }
    }

    private boolean isMovePossible() {
        Square[] moveFront = new Square[PacMan.CHUNK_RATIO];
        switch (direction) {
            case LEFT:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate + i][xCoordinate - 1];
                }
                break;
            case RIGHT:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate + i][xCoordinate + PacMan.CHUNK_RATIO];
                }
                break;
            case UP:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate - 1][xCoordinate + i];
                }
                break;
            case DOWN:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate + PacMan.CHUNK_RATIO][xCoordinate + i];
                }
                break;
        }
        boolean isClear = true;
        for (int i = 0; i < moveFront.length; i++) {
            switch (moveFront[i]) {
                case WALL:
                    isClear = false;
                    break;
                case CLEAR:
                    // Do Nothing.
                    break;
                case JUMP:
                    if (direction == Direction.LEFT) {
                        xCoordinate = PacMan.HORIZONTAL_SQUARES - PacMan.CHUNK_RATIO - 1;
                        resetChunk(1);
                    } else {
                        xCoordinate = 1;
                        resetChunk(PacMan.HORIZONTAL_SQUARES - PacMan.CHUNK_RATIO - 1);
                    }
                    break;
                case POINT:
                    // TODO: point code
                    break;
                case BALL:
                    // TODO: ball code
                    break;
                case RED:
                case CYAN:
                case PINK:
                case ORANGE:
                    // TODO: player dies
                    break;
                case SCARED_RED:
                    // TODO: ghost dies
                    break;
                case SCARED_CYAN:
                    // TODO: ghost dies
                    break;
                case SCARED_PINK:
                    // TODO: ghost dies
                    break;
                case SCARED_ORANGE:
                    // TODO: ghost dies
                    break;
                default:
                    System.err.println("Error: reached break in Player.isMovePossible");
                    break;
            }
        }
        return isClear;
    }

    private void resetChunk(int start) {
        for (int vertical = yCoordinate; vertical < yCoordinate + PacMan.CHUNK_RATIO; vertical++) {
            for (int horizontal = start; horizontal < start + PacMan.CHUNK_RATIO; horizontal++) {
                PacMan.board[vertical][horizontal] = Square.CLEAR;
            }
        }
    }

    private void move() {
        switch (direction) {
            case LEFT:
                xCoordinate--;
                break;
            case RIGHT:
                xCoordinate++;
                break;
            case UP:
                yCoordinate--;
                break;
            case DOWN:
                yCoordinate++;
                break;
        }
        draw();
    }

    private void draw() {
        resetOldGrid();

        final int[][] leftDirectionPacMan =
                {
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                        {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };

        final int[][] pacMan = new int[PacMan.CHUNK_RATIO][PacMan.CHUNK_RATIO];

        if (direction == Direction.RIGHT || (direction == Direction.NONE && lastDirection == Direction.RIGHT)) {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[vertical][PacMan.CHUNK_RATIO - horizontal - 1];
                }
            }
        } else if (direction == Direction.DOWN || (direction == Direction.NONE && lastDirection == Direction.DOWN)) {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[horizontal][PacMan.CHUNK_RATIO - vertical - 1];
                }
            }
        } else if (direction == Direction.UP || (direction == Direction.NONE && lastDirection == Direction.UP)) {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[horizontal][vertical];
                }
            }
        } else {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[vertical][horizontal];
                }
            }
        }

        for (int vertical = yCoordinate; vertical < yCoordinate + PacMan.CHUNK_RATIO; vertical++) {
            for (int horizontal = xCoordinate; horizontal < xCoordinate + PacMan.CHUNK_RATIO; horizontal++) {
                if (pacMan[vertical - yCoordinate][horizontal - xCoordinate] == 1) {
                    PacMan.board[vertical][horizontal] = Square.YELLOW;
                }
            }
        }
    }

    private void resetOldGrid() {
        int oldX = xCoordinate;
        int oldY = yCoordinate;
        switch (direction) {
            case RIGHT:
                oldX = xCoordinate - 1;
                break;
            case LEFT:
                oldX = xCoordinate + 1;
                break;
            case UP:
                oldY = yCoordinate + 1;
                break;
            case DOWN:
                oldY = yCoordinate - 1;
                break;
        }
        for (int vertical = oldY; vertical < oldY + PacMan.CHUNK_RATIO; vertical++) {
            for (int horizontal = oldX; horizontal < oldX + PacMan.CHUNK_RATIO; horizontal++) {
                PacMan.board[vertical][horizontal] = Square.CLEAR;
            }
        }
    }
}
