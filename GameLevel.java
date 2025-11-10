import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameLevel {
    private int spacing;
    private int wallThickness;
    private int speedC;
    private Integer level = 0;
    private Level gameLevel;
    private List<Level> levels = new ArrayList<>();

    public GameLevel(int spacing, int wallThickness, int speedC) {
        this.spacing = spacing;
        this.wallThickness = wallThickness;
        this.speedC = speedC;
    }

    public void nextLevel() throws IOException {
        level++;

        String fileName = "Level" + level.toString() + ".txt";
        String filePath = "Levels/" + fileName;
        Scanner scanner = new Scanner(new File(filePath));

        int rowCount = scanner.nextInt();
        int colCount = scanner.nextInt();
        int brickWidth = scanner.nextInt();
        int brickHeight = scanner.nextInt();

        gameLevel = new Level(rowCount, colCount, brickWidth, brickHeight, spacing, wallThickness, speedC);
        int[][] arr = new int[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) arr[i][j] = scanner.nextInt();
        }

        gameLevel.makeBricks(arr);
        levels.add(gameLevel);
    }

    // Tải level cụ thể (không phụ thuộc vào thứ tự nextLevel)
    public void loadLevel(int targetLevel) throws IOException {
        level = targetLevel - 1;
        nextLevel();
    }

    public Level getCurrentLevel() {
        return gameLevel;
    }
}