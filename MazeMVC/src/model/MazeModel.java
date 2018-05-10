package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This class acts as the model for the computation of a maze, in the MVC
 * (model-view-controller) design pattern used to create this application.
 *
 * @author	Nicholas Tzavaras, s02150247
 * @version	Beta, 5/3/2018
 */
public class MazeModel {
    
    private final int MAX_MAZE_SIZE = 25;

    private int cols;
    private int rows;
    private int[][] maze;
    private char[][] charMaze;
    private String[] solvedLines;
    private final LinkedList<Point> solution;   //Save the points of solution for later when stepping through.

    /**
     * Constructor.
     *
     */
    public MazeModel() {
        solution = new LinkedList<>();
    }

    public void newMaze(int x, int y) {
        maze = new int[x][y];
        cols = x;
        rows = y;
        generateMaze(0, 0);
    }

    /**
     * TODO
     */
    private void generateMaze(int x, int y) {
        solution.clear();
        Direction[] values = Direction.values();
        Collections.shuffle(Arrays.asList(values));
        for (Direction dir : values) {
            int i = x + dir.dx;
            int j = y + dir.dy;
            if (between(i, cols) && between(j, rows)
                    && (maze[i][j] == 0)) {
                maze[x][y] |= dir.bit;
                maze[i][j] |= dir.opposite.bit;
                generateMaze(i, j);
            }
        }
        charMaze = halveMaze(getBlankMazeLines());
        solveMaze(charMaze);
        solvedLines = expandMaze(charMaze);
    }
    
    /**
     * Returns a one dimensional array of strings, each element in the array
     * contains one row of display characters.
     *
     * @return
     */
    public String[] getSolvedMazeLines(){
        return solvedLines;
    }
    
    /**
     * Returns a one dimensional array of strings, each element in the array
     * contains one row of display characters.
     *
     * @return
     */
    public String[] getBlankMazeLines() {
        ArrayList<String> mazeStrings = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            // draw the north edge
            for (int j = 0; j < cols; j++) {
                if (j == 0 && i ==0) {
                    sb.append("+ * ");
                }
                else sb.append((maze[j][i] & 1) == 0 ? "+---" : "+   ");
            }
            sb.append("+");
            mazeStrings.add(sb.toString());
            sb.setLength(0);
            // draw the west edge
            for (int j = 0; j < cols; j++) {
                sb.append((maze[j][i] & 8) == 0 ? "|   " : "    ");
            }
            sb.append("|");
            mazeStrings.add(sb.toString());
            sb.setLength(0);
        }
        // draw the bottom line
        for (int j = 0; j < cols - 1; j++) {
            sb.append("+---");
        }
        sb.append("+   +");
        mazeStrings.add(sb.toString());
        sb.setLength(0);
        return mazeStrings.toArray(new String[mazeStrings.size()]);
    }

    /**
     * Makes the maze half as wide. Converts each line of the maze from a String
     * to a char[], to allow for addition of the path.
     */
    private char[][] halveMaze(String[] lines) {
        final int width = (lines[0].length() + 1) / 2;
        char[][] c = new char[lines.length][width];
        for (int i = 0; i < lines.length; i++) {
            for (int j = 0; j < width; j++) {
                c[i][j] = lines[i].charAt(j * 2);
            }
        }
        return c;
    }

    /**
     * Solve the maze and draw the solution if possible.
     *
     * @param maze
     */
    private void solveMaze(char[][] maze) {
        solveRecursively(maze, maze[0].length - 2, maze.length - 2, Direction.S);
    }

    /**
     * Return true if solvable, and draw the solution if so.
     */
    private boolean solveRecursively(char[][] maze,
            int x, int y, Direction d) {
        boolean solved = false;
        for (Direction direction : Direction.values()) {
            if (solved)  break;
            if (direction != d) {
                switch (direction) {
                    // 0 = up, 1 = right, 2 = down, 3 = left
                    case N:
                        if (maze[y - 1][x] == ' ') {
                            solved = solveRecursively(maze, x, y - 2, Direction.S);
                        }
                        break;
                    case E:
                        if (maze[y][x + 1] == ' ') {
                            solved = solveRecursively(maze, x + 2, y, Direction.W);
                        }
                        break;
                    case S:
                        if (maze[y + 1][x] == ' ') {
                            solved = solveRecursively(maze, x, y + 2, Direction.N);
                        }
                        break;
                    case W:
                        if (maze[y][x - 1] == ' ') {
                            solved = solveRecursively(maze, x - 2, y, Direction.E);
                        }
                        break;
                }
            }
        }
        // Base Case
        if (x == 1 && y == 1) {
            solved = true;
        }
        /**
         * If we have a solution, draw it as it works backwards. Save the points
         * of solution for later when stepping through.
         */
        if (solved) {
            maze[y][x] = '*';
            switch (d) {
                case N:
                    maze[y - 1][x] = '*';
                    solution.addLast(new Point(y - 1, x));
                    break;
                case E:
                    maze[y][x + 1] = '*';
                    solution.addLast(new Point(y, x + 1));
                    break;
                case S:
                    maze[y + 1][x] = '*';
                    solution.addLast(new Point(y + 1, x));
                    break;
                case W:
                    maze[y][x - 1] = '*';
                    solution.addLast(new Point(y, x - 1));
                    break;
            }
        }
        return solved;
    }

    /**
     * Converts each line from char[] back to String. Reverts the maze to look
     * appealing after it was halved.
     */
    public String[] expandMaze(char[][] maze) {
        char[] tmp = new char[3];
        String[] lines = new String[maze.length];
        for (int i = 0; i < maze.length; i++) {
            StringBuilder sb = new StringBuilder(maze[i].length * 2);
            for (int j = 0; j < maze[i].length; j++) {
                if (j % 2 == 0) {
                    sb.append(maze[i][j]);
                } else {
                    tmp[0] = tmp[1] = tmp[2] = maze[i][j];
                    if (tmp[1] == '*') {
                        tmp[0] = tmp[2] = ' ';
                    }
                    sb.append(tmp);
                }
            }
            lines[i] = sb.toString();
        }
        return lines;
    }

    private boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }
    
    public int getMaxMazeSize(){
        return MAX_MAZE_SIZE;
    }

    private static enum Direction {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }
        
        final int bit;
        final int dx;
        final int dy;
        Direction opposite;

        Direction(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    };

//    /**
//     * Main entry point: for testing of the model.
//     *
//     * @param args	unused
//     */
//    @SuppressWarnings({"CallToThreadDumpStack", "CallToPrintStackTrace"})
//    public static void main(String[] args) {
//        MazeModel test = new MazeModel();
//        test.newMaze(25,25);
//        test.displayBlank();
//        test.displaySolution();
//    }
}
