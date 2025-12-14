import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameUI {

    private static final int TILE_SIZE = 60;

    private final GameLogic logic = new GameLogic();
    private final GridPane grid = new GridPane();
    private final VBox root = new VBox(20);
    private Button start, pause, next, end;
    private boolean keyboardEnabled = false;
    private int seconds = 0;
    private int moves = 0;
    private int score = 0;
    private int level = 1;

    private Timeline timer;

    private final Label status = new Label("Press Start to play");
    private final Label timeLabel = new Label("Time: 0");
    private final Label moveLabel = new Label("Moves: 0");
    private final Label scoreLabel = new Label("Score: 0");
    private static final Color WALL_COLOR = Color.DARKGRAY;
    private static final Color EMPTY_COLOR = Color.BLACK;
    private static final Color TARGET_COLOR = Color.GOLD;
    private static final Color BOX_COLOR = Color.DODGERBLUE;
    private static final Color PLAYER_COLOR = Color.RED;

    public GameUI() {
        setupUI();
        loadLevel();
    }

    private void setupUI() {
        grid.setHgap(2);
        grid.setVgap(2);

        start = new Button("Start");
        pause = new Button("Pause");
        next = new Button("Next Level");
        end = new Button("End Game");

        start.setOnAction(e -> startGame());
        pause.setOnAction(e -> pauseGame());
        next.setOnAction(e -> nextLevel());
        end.setOnAction(e -> endGame());

        // High-contrast stats
        status.setTextFill(Color.LIGHTGREEN);
        timeLabel.setTextFill(Color.LIGHTBLUE);
        moveLabel.setTextFill(Color.LIGHTYELLOW);
        scoreLabel.setTextFill(Color.ORANGE);

        status.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        timeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        moveLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox statsBox = new VBox(10, status, timeLabel, moveLabel, scoreLabel);
        statsBox.setStyle("-fx-background-color: #333333; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Legend items
        VBox legendBox = createLegend();

        // Buttons vertical
        start.setMaxWidth(Double.MAX_VALUE);
        pause.setMaxWidth(Double.MAX_VALUE);
        next.setMaxWidth(Double.MAX_VALUE);
        end.setMaxWidth(Double.MAX_VALUE);

        VBox buttonsBox = new VBox(10, start, pause, next, end);

        // Combine legend + stats + buttons
        VBox controlsBox = new VBox(20, legendBox, statsBox, buttonsBox);
        controlsBox.setStyle("-fx-background-color: #222222; -fx-padding: 20; -fx-border-radius: 5; -fx-background-radius: 5;");

        HBox game = new HBox(30, grid, controlsBox);
        game.setStyle("-fx-padding:20; -fx-background-color:#222222;");

        root.getChildren().add(game);

        // Key press handling remains the same
        root.setOnKeyPressed(e -> {
            if (!keyboardEnabled) return;
            boolean moved = switch (e.getCode()) {
                case W -> logic.movePlayer(-1, 0);
                case S -> logic.movePlayer(1, 0);
                case A -> logic.movePlayer(0, -1);
                case D -> logic.movePlayer(0, 1);
                default -> false;
            };

            if (moved) {
                moves++;
                moveLabel.setText("Moves: " + moves);
                drawBoard();
                if (logic.isWin()) finishLevel();
            }
        });
    }

    private void loadLevel() {
        logic.generateLevel();
        seconds = 0;
        moves = 0;
        keyboardEnabled = false;

        timeLabel.setText("Time: 0");
        moveLabel.setText("Moves: 0");
        status.setText("Level " + level + " ready");

        drawBoard();
        root.requestFocus();
    }

    private void startGame() {
        keyboardEnabled = true;
        status.setText("Level " + level + " started");
// Disable Start while playing
        start.setDisable(true);
        next.setDisable(true);
        if (timer == null) {
            timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                seconds++;
                timeLabel.setText("Time: " + seconds);
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
        }
        timer.play();
        root.requestFocus();
    }

    private void startNewGame() {
        // Reset all stats
        level = 1;
        score = 0;
        seconds = 0;
        moves = 0;

        // Enable buttons
        pause.setDisable(false);
        next.setDisable(false);

        loadLevel();   // prepare first level
        start.setText("Start");
        start.setDisable(true);           // disabled until startGame() is called
        start.setOnAction(e -> startGame());

        startGame();   // immediately start the first level
    }


    private void pauseGame() {
        keyboardEnabled = false;
        if (timer != null) timer.pause();
        status.setText("Paused");
    }

    private void finishLevel() {
        keyboardEnabled = false;
        timer.pause();

        int levelScore = logic.calculateScore(seconds, moves);
        score += levelScore;
        scoreLabel.setText("Score: " + score);

        status.setText("Level complete! +" + levelScore);
        pause.setDisable(true);
        next.setDisable(false);
        }

    private void nextLevel() {
        level++;
        loadLevel();
        start.setDisable(false);
        next.setDisable(true);
    }

    private void endGame() {
        keyboardEnabled = false;
        if (timer != null) timer.stop();
        status.setText("Game Over! Final Score: " + score);

        // Disable buttons
        pause.setDisable(true);

        next.setDisable(true);
        start.setDisable(false);
        // Set start button to create new game
        start.setText("Start New Game");
        start.setOnAction(e -> startNewGame());
    }

    private void drawBoard() {
        grid.getChildren().clear();
        char[][] board = logic.getBoard();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Rectangle r = new Rectangle(TILE_SIZE, TILE_SIZE);
                r.setStroke(Color.GRAY);

                r.setFill(switch (board[i][j]) {
                    case '#' -> WALL_COLOR;
                    case '.' -> TARGET_COLOR;
                    case '$' -> BOX_COLOR;
                    case '@' -> PLAYER_COLOR;
                    default -> EMPTY_COLOR;
                });


                grid.add(r, j, i);
            }
        }
    }

    private VBox createLegend() {
        VBox legend = new VBox(10);
        legend.getChildren().addAll(legendItem(WALL_COLOR, "Wall"), legendItem(EMPTY_COLOR, "Empty"), legendItem(TARGET_COLOR, "Target"), legendItem(BOX_COLOR, "Box"), legendItem(PLAYER_COLOR, "Player"));
        return legend;
    }

    private HBox legendItem(Color color, String text) {
        Rectangle r = new Rectangle(30, 30, color);
        Label l = new Label(text);
        l.setTextFill(Color.WHITE);
        l.setStyle("-fx-font-size: 14px;");
        return new HBox(10, r, l);
    }

    public VBox getRoot() {
        return root;
    }

}
