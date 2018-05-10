package main;

import view.MazeView;

/**
 * Main class with the {@code main} method extracted from the MazeView
 * class. This ensures the the {@code main} method does nothing more than 
 * start the application. All classes used by other authors are used with 
 * permission. Credit is given when due. This includes MessageDisplay.java, 
 * ValidResult.java, Validation.java, ValidationException.java
 *
 * @author	Nicholas Tzavaras, s02150247
 * @version	Beta, 5/3/2018
 */
public class Maze {

    /**
     * Main entry point.
     * <p>
     * Execute: </p>
     * <pre>java main.Maze</pre>
     *
     * @param args	not used
     */
    public static void main(String args[]) {
        MazeView.launch();
    }
}
