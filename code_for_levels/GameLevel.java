package code_for_levels;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_object.*;
import code_for_update.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameLevel {
    private int spacing;
    private int wallThickness;
    private int speedC;
    private Integer levelNumber = 0;
    private Level level;

    public GameLevel(int spacing, int wallThickness, int speedC) {
        this.spacing = spacing;
        this.wallThickness = wallThickness;
        this.speedC = speedC;
    }

    public void nextLevel() throws IOException {
        levelNumber ++;

        String fileName = "Level" + levelNumber.toString() + ".txt";
        String filePath = "Levels/" + fileName;
        Scanner scanner = new Scanner(new File(filePath));

        int rowCount = scanner.nextInt();
        int colCount = scanner.nextInt();
        int brickWidth = scanner.nextInt();
        int brickHeight = scanner.nextInt();

        level = new Level(rowCount, colCount, brickWidth, brickHeight, spacing, wallThickness, speedC);
        int[][] arr = new int[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) arr[i][j] = scanner.nextInt();
        }

        level.makeBricks(arr);;
    }

    public void loadLevel(int targetLevel) throws IOException {
        levelNumber = targetLevel - 1;
        nextLevel();
    }

    public Level getCurrentLevel() {
        return level;
    }
}