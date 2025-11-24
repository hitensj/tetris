import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * TetrisGame - A complete implementation of the classic Tetris game.
 * This application uses JavaFX for rendering and follows OOP principles.
 * 
 * @author Tetris Implementation
 * @version 1.0
 */
public class TetrisGame extends Application {
    
    private static final int CELL_SIZE = 30;
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 20;
    private static final int CANVAS_WIDTH = CELL_SIZE * GRID_WIDTH;
    private static final int CANVAS_HEIGHT = CELL_SIZE * GRID_HEIGHT;
    
    private GameEngine gameEngine;
    private Canvas canvas;
    private Canvas previewCanvas;
    private Text scoreText;
    private Text levelText;
    private Text gameOverText;
    
    /**
     * Main entry point for the JavaFX application.
     * 
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        gameEngine = new GameEngine(GRID_WIDTH, GRID_HEIGHT);
        
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        previewCanvas = new Canvas(CELL_SIZE * 5, CELL_SIZE * 5);
        
        scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font(20));
        
        levelText = new Text("Level: 1");
        levelText.setFont(Font.font(20));
        
        gameOverText = new Text("");
        gameOverText.setFont(Font.font(24));
        gameOverText.setFill(Color.RED);
        
        VBox sidePanel = new VBox(10);
        sidePanel.getChildren().addAll(
            new Text("Next Piece:"),
            previewCanvas,
            scoreText,
            levelText,
            gameOverText
        );
        sidePanel.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");
        
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setRight(sidePanel);
        
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        
        primaryStage.setTitle("Tetris Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        startGameLoop();
    }
    
    /**
     * Handles keyboard input for game controls.
     * 
     * @param code The key code of the pressed key
     */
    private void handleKeyPress(KeyCode code) {
        if (gameEngine.isGameOver()) {
            if (code == KeyCode.SPACE) {
                gameEngine.reset();
                gameOverText.setText("");
            }
            return;
        }
        
        switch (code) {
            case LEFT:
                gameEngine.moveLeft();
                break;
            case RIGHT:
                gameEngine.moveRight();
                break;
            case DOWN:
                gameEngine.softDrop();
                break;
            case UP:
            case X:
                gameEngine.rotate();
                break;
        }
        render();
    }
    
    /**
     * Starts the main game loop using AnimationTimer.
     */
    private void startGameLoop() {
        new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    render();
                    return;
                }
                
                long elapsed = now - lastUpdate;
                if (elapsed >= gameEngine.getDropInterval() * 1_000_000) {
                    gameEngine.update();
                    render();
                    lastUpdate = now;
                    
                    if (gameEngine.isGameOver()) {
                        gameOverText.setText("GAME OVER!\nPress SPACE");
                    }
                }
            }
        }.start();
    }
    
    /**
     * Renders the game state to the canvas.
     */
    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // Draw grid background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // Draw settled blocks
        GameGrid grid = gameEngine.getGrid();
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid.isFilled(x, y)) {
                    drawCell(gc, x, y, grid.getColor(x, y));
                }
            }
        }
        
        // Draw current block
        Block currentBlock = gameEngine.getCurrentBlock();
        if (currentBlock != null) {
            int[][] shape = currentBlock.getShape();
            Color color = currentBlock.getColor();
            int blockX = currentBlock.getX();
            int blockY = currentBlock.getY();
            
            for (int y = 0; y < shape.length; y++) {
                for (int x = 0; x < shape[y].length; x++) {
                    if (shape[y][x] == 1) {
                        drawCell(gc, blockX + x, blockY + y, color);
                    }
                }
            }
        }
        
        // Draw grid lines
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);
        for (int i = 0; i <= GRID_WIDTH; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, CANVAS_HEIGHT);
        }
        for (int i = 0; i <= GRID_HEIGHT; i++) {
            gc.strokeLine(0, i * CELL_SIZE, CANVAS_WIDTH, i * CELL_SIZE);
        }
        
        // Draw next piece preview
        renderPreview();
        
        // Update score and level
        scoreText.setText("Score: " + gameEngine.getScore());
        levelText.setText("Level: " + gameEngine.getLevel());
    }
    
    /**
     * Renders the next piece preview.
     */
    private void renderPreview() {
        GraphicsContext gc = previewCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());
        
        Block nextBlock = gameEngine.getNextBlock();
        if (nextBlock != null) {
            int[][] shape = nextBlock.getShape();
            Color color = nextBlock.getColor();
            
            for (int y = 0; y < shape.length; y++) {
                for (int x = 0; x < shape[y].length; x++) {
                    if (shape[y][x] == 1) {
                        gc.setFill(color);
                        gc.fillRect(x * CELL_SIZE + CELL_SIZE/2, y * CELL_SIZE + CELL_SIZE/2, 
                                  CELL_SIZE - 2, CELL_SIZE - 2);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(x * CELL_SIZE + CELL_SIZE/2, y * CELL_SIZE + CELL_SIZE/2, 
                                    CELL_SIZE - 2, CELL_SIZE - 2);
                    }
                }
            }
        }
    }
    
    /**
     * Draws a single cell on the canvas.
     * 
     * @param gc The graphics context
     * @param x The x-coordinate in grid units
     * @param y The y-coordinate in grid units
     * @param color The color to fill the cell
     */
    private void drawCell(GraphicsContext gc, int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
    }
    
    /**
     * Main method to launch the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * Block class represents a single Tetromino piece.
 * Handles rotation logic and shape definition for all 7 standard Tetris pieces.
 */
class Block {
    private int[][] shape;
    private Color color;
    private int x;
    private int y;
    private int type;
    
    // The 7 standard Tetris pieces
    private static final int[][][] SHAPES = {
        // I piece
        {{1, 1, 1, 1}},
        // O piece
        {{1, 1}, {1, 1}},
        // T piece
        {{0, 1, 0}, {1, 1, 1}},
        // S piece
        {{0, 1, 1}, {1, 1, 0}},
        // Z piece
        {{1, 1, 0}, {0, 1, 1}},
        // J piece
        {{1, 0, 0}, {1, 1, 1}},
        // L piece
        {{0, 0, 1}, {1, 1, 1}}
    };
    
    private static final Color[] COLORS = {
        Color.CYAN,    // I
        Color.YELLOW,  // O
        Color.PURPLE,  // T
        Color.GREEN,   // S
        Color.RED,     // Z
        Color.BLUE,    // J
        Color.ORANGE   // L
    };
    
    /**
     * Creates a new Block with a random shape.
     */
    public Block() {
        this.type = (int) (Math.random() * SHAPES.length);
        this.shape = copyShape(SHAPES[type]);
        this.color = COLORS[type];
        this.x = 3;
        this.y = 0;
    }
    
    /**
     * Creates a new Block with a specified type.
     * 
     * @param type The type of Tetromino (0-6)
     */
    public Block(int type) {
        this.type = type;
        this.shape = copyShape(SHAPES[type]);
        this.color = COLORS[type];
        this.x = 3;
        this.y = 0;
    }
    
    /**
     * Copies a 2D shape array to avoid reference issues.
     * 
     * @param original The original shape array
     * @return A deep copy of the shape array
     */
    private int[][] copyShape(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
    
    /**
     * Rotates the block 90 degrees clockwise.
     */
    public void rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                rotated[x][rows - 1 - y] = shape[y][x];
            }
        }
        
        shape = rotated;
    }
    
    /**
     * Undoes the last rotation (rotates counter-clockwise once).
     */
    public void undoRotate() {
        // Rotate 3 times to undo one clockwise rotation
        for (int i = 0; i < 3; i++) {
            rotate();
        }
    }
    
    /**
     * Moves the block down by one unit.
     */
    public void moveDown() {
        y++;
    }
    
    /**
     * Moves the block left by one unit.
     */
    public void moveLeft() {
        x--;
    }
    
    /**
     * Moves the block right by one unit.
     */
    public void moveRight() {
        x++;
    }
    
    /**
     * Undoes the last downward movement.
     */
    public void undoMoveDown() {
        y--;
    }
    
    /**
     * Undoes the last leftward movement.
     */
    public void undoMoveLeft() {
        x++;
    }
    
    /**
     * Undoes the last rightward movement.
     */
    public void undoMoveRight() {
        x--;
    }
    
    /**
     * Gets the current shape of the block.
     * 
     * @return The 2D array representing the block shape
     */
    public int[][] getShape() {
        return shape;
    }
    
    /**
     * Gets the color of the block.
     * 
     * @return The block's color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Gets the x-coordinate of the block.
     * 
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }
    
    /**
     * Gets the y-coordinate of the block.
     * 
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }
    
    /**
     * Gets the type of the block.
     * 
     * @return The block type (0-6)
     */
    public int getType() {
        return type;
    }
}

/**
 * GameGrid class manages the grid state and collision detection.
 * Tracks which cells are filled and their colors.
 */
class GameGrid {
    private int width;
    private int height;
    private boolean[][] filled;
    private Color[][] colors;
    
    /**
     * Creates a new GameGrid with specified dimensions.
     * 
     * @param width The width of the grid
     * @param height The height of the grid
     */
    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.filled = new boolean[height][width];
        this.colors = new Color[height][width];
    }
    
    /**
     * Checks if a specific cell is filled.
     * 
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return true if the cell is filled, false otherwise
     */
    public boolean isFilled(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return filled[y][x];
    }
    
    /**
     * Gets the color of a specific cell.
     * 
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The color of the cell, or null if empty
     */
    public Color getColor(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return colors[y][x];
    }
    
    /**
     * Sets a cell to be filled with a specific color.
     * 
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param color The color to set
     */
    public void setCell(int x, int y, Color color) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            filled[y][x] = true;
            colors[y][x] = color;
        }
    }
    
    /**
     * Checks if a block collides with the grid boundaries or settled blocks.
     * 
     * @param block The block to check
     * @return true if there is a collision, false otherwise
     */
    public boolean checkCollision(Block block) {
        int[][] shape = block.getShape();
        int blockX = block.getX();
        int blockY = block.getY();
        
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] == 1) {
                    int gridX = blockX + x;
                    int gridY = blockY + y;
                    
                    // Check boundaries
                    if (gridX < 0 || gridX >= width || gridY >= height) {
                        return true;
                    }
                    
                    // Check collision with settled blocks
                    if (gridY >= 0 && filled[gridY][gridX]) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Locks a block into the grid permanently.
     * 
     * @param block The block to lock
     */
    public void lockBlock(Block block) {
        int[][] shape = block.getShape();
        int blockX = block.getX();
        int blockY = block.getY();
        Color color = block.getColor();
        
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] == 1) {
                    setCell(blockX + x, blockY + y, color);
                }
            }
        }
    }
    
    /**
     * Checks for and clears complete lines.
     * 
     * @return The number of lines cleared
     */
    public int clearLines() {
        int linesCleared = 0;
        
        for (int y = height - 1; y >= 0; y--) {
            if (isLineFull(y)) {
                removeLine(y);
                linesCleared++;
                y++; // Check the same row again since rows shifted down
            }
        }
        
        return linesCleared;
    }
    
    /**
     * Checks if a specific row is completely filled.
     * 
     * @param y The row to check
     * @return true if the row is full, false otherwise
     */
    private boolean isLineFull(int y) {
        for (int x = 0; x < width; x++) {
            if (!filled[y][x]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Removes a line and shifts all rows above it down.
     * 
     * @param lineY The row to remove
     */
    private void removeLine(int lineY) {
        for (int y = lineY; y > 0; y--) {
            for (int x = 0; x < width; x++) {
                filled[y][x] = filled[y - 1][x];
                colors[y][x] = colors[y - 1][x];
            }
        }
        
        // Clear top row
        for (int x = 0; x < width; x++) {
            filled[0][x] = false;
            colors[0][x] = null;
        }
    }
    
    /**
     * Resets the grid to empty state.
     */
    public void reset() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                filled[y][x] = false;
                colors[y][x] = null;
            }
        }
    }
    
    /**
     * Gets the width of the grid.
     * 
     * @return The grid width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the grid.
     * 
     * @return The grid height
     */
    public int getHeight() {
        return height;
    }
}

/**
 * GameEngine class handles game loop timing, scoring, and level progression.
 * Manages the overall game state and logic.
 */
class GameEngine {
    private GameGrid grid;
    private Block currentBlock;
    private Block nextBlock;
    private int score;
    private int level;
    private int linesCleared;
    private boolean gameOver;
    private long dropInterval;
    
    private static final long BASE_DROP_INTERVAL = 1000; // milliseconds
    private static final int LINES_PER_LEVEL = 10;
    
    /**
     * Creates a new GameEngine with specified grid dimensions.
     * 
     * @param width The grid width
     * @param height The grid height
     */
    public GameEngine(int width, int height) {
        this.grid = new GameGrid(width, height);
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
        this.gameOver = false;
        this.dropInterval = BASE_DROP_INTERVAL;
        this.currentBlock = new Block();
        this.nextBlock = new Block();
    }
    
    /**
     * Updates the game state by one tick.
     */
    public void update() {
        if (gameOver) {
            return;
        }
        
        currentBlock.moveDown();
        
        if (grid.checkCollision(currentBlock)) {
            currentBlock.undoMoveDown();
            grid.lockBlock(currentBlock);
            
            int lines = grid.clearLines();
            if (lines > 0) {
                updateScore(lines);
                linesCleared += lines;
                updateLevel();
            }
            
            spawnNewBlock();
        }
    }
    
    /**
     * Spawns a new block at the top of the grid.
     */
    private void spawnNewBlock() {
        currentBlock = nextBlock;
        nextBlock = new Block();
        
        if (grid.checkCollision(currentBlock)) {
            gameOver = true;
        }
    }
    
    /**
     * Moves the current block left.
     */
    public void moveLeft() {
        if (gameOver) return;
        
        currentBlock.moveLeft();
        if (grid.checkCollision(currentBlock)) {
            currentBlock.undoMoveLeft();
        }
    }
    
    /**
     * Moves the current block right.
     */
    public void moveRight() {
        if (gameOver) return;
        
        currentBlock.moveRight();
        if (grid.checkCollision(currentBlock)) {
            currentBlock.undoMoveRight();
        }
    }
    
    /**
     * Performs a soft drop (faster fall).
     */
    public void softDrop() {
        if (gameOver) return;
        
        currentBlock.moveDown();
        if (grid.checkCollision(currentBlock)) {
            currentBlock.undoMoveDown();
        } else {
            score += 1; // Bonus point for soft drop
        }
    }
    
    /**
     * Rotates the current block.
     */
    public void rotate() {
        if (gameOver) return;
        
        currentBlock.rotate();
        if (grid.checkCollision(currentBlock)) {
            currentBlock.undoRotate();
        }
    }
    
    /**
     * Updates the score based on lines cleared.
     * More lines cleared at once = more points.
     * 
     * @param lines Number of lines cleared
     */
    private void updateScore(int lines) {
        int[] points = {0, 100, 300, 500, 800}; // 0, 1, 2, 3, 4 lines
        if (lines >= 1 && lines <= 4) {
            score += points[lines] * level;
        }
    }
    
    /**
     * Updates the level based on lines cleared.
     */
    private void updateLevel() {
        int newLevel = (linesCleared / LINES_PER_LEVEL) + 1;
        if (newLevel > level) {
            level = newLevel;
            dropInterval = Math.max(100, BASE_DROP_INTERVAL - (level - 1) * 100);
        }
    }
    
    /**
     * Resets the game to initial state.
     */
    public void reset() {
        grid.reset();
        score = 0;
        level = 1;
        linesCleared = 0;
        gameOver = false;
        dropInterval = BASE_DROP_INTERVAL;
        currentBlock = new Block();
        nextBlock = new Block();
    }
    
    /**
     * Gets the current game grid.
     * 
     * @return The GameGrid instance
     */
    public GameGrid getGrid() {
        return grid;
    }
    
    /**
     * Gets the current falling block.
     * 
     * @return The current Block
     */
    public Block getCurrentBlock() {
        return currentBlock;
    }
    
    /**
     * Gets the next block to be spawned.
     * 
     * @return The next Block
     */
    public Block getNextBlock() {
        return nextBlock;
    }
    
    /**
     * Gets the current score.
     * 
     * @return The score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Gets the current level.
     * 
     * @return The level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Gets the drop interval in milliseconds.
     * 
     * @return The drop interval
     */
    public long getDropInterval() {
        return dropInterval;
    }
    
    /**
     * Checks if the game is over.
     * 
     * @return true if game over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }
}