import java.util.Random;

public class GameLogic {

    public static final int ROWS = 7;
    public static final int COLS = 7;
    public static final int NUM_BOXES = 2;

    private char[][] board;
    private int playerX, playerY;
    private final Random random = new Random();

    public void generateLevel() {
        board = new char[ROWS][COLS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (i == 0 || j == 0 || i == ROWS - 1 || j == COLS - 1)
                    board[i][j] = '#';
                else
                    board[i][j] = random.nextDouble() < 0.15 ? '#' : ' ';
            }
        }

        placePlayer();
        placeBoxesAndTargets();
    }

    private void placePlayer() {
        do {
            playerX = 1 + random.nextInt(ROWS - 2);
            playerY = 1 + random.nextInt(COLS - 2);
        } while (board[playerX][playerY] != ' ');
        board[playerX][playerY] = '@';
    }

    private void placeBoxesAndTargets() {
        for (int i = 0; i < NUM_BOXES; i++) {
            placeChar('$');
            placeChar('.');
        }
    }

    private void placeChar(char c) {
        int x, y;
        do {
            x = 1 + random.nextInt(ROWS - 2);
            y = 1 + random.nextInt(COLS - 2);
        } while (board[x][y] != ' ');
        board[x][y] = c;
    }

    public boolean movePlayer(int dx, int dy) {
        int nx = playerX + dx;
        int ny = playerY + dy;

        if (board[nx][ny] == '#') return false;

        if (board[nx][ny] == '$') {
            int bx = nx + dx;
            int by = ny + dy;
            if (board[bx][by] != ' ' && board[bx][by] != '.') return false;
            board[bx][by] = '$';
        }

        board[playerX][playerY] = ' ';
        playerX = nx;
        playerY = ny;
        board[playerX][playerY] = '@';

        return true;
    }

    public boolean isWin() {
        for (char[] row : board)
            for (char c : row)
                if (c == '.') return false;
        return true;
    }

    public char[][] getBoard() {
        return board;
    }

    public int calculateScore(int seconds, int moves) {
        return Math.max(10, 100 - seconds - (moves * 2));
    }
}
