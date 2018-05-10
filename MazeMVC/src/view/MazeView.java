package view;

import messageDisplay.MessageDisplay;
import controller.MazeController;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The view for the application.
 *
 * @author	Nicholas Tzavaras, s02150247
 * @version	Beta, 5/3/2018
 */
@SuppressWarnings("serial")
public class MazeView extends JFrame implements Runnable {

    /**
     * To allow the GridBagLayout to equally space items horizontally, a
     * non-zero value must be supplied for the weightx value.
     */
    private static final int NON_ZERO = 1;

    /**
     * Additional spacing around the navigationPanel to get a pleasing width for
     * the window. Determined by experimentation.
     */
    private static final int SPACING_HORIZONTAL_EACH_SIDE = 13;

    /**
     * Extra spacing in the GridBoxLayout after each row.
     */
    private static final int SPACING_VERTICAL_AFTER = 5;

    /**
     * Extra spacing in the GridBoxLayout before each row.
     */
    private static final int SPACING_VERTICAL_BEFORE = 10;
    /**
     * The controller for this view.
     */
    private MazeController controller;

    /**
     * The content pane from the the JFrame.
     */
    private JPanel inputPanel;

    /**
     * Gridbag constraints instance.
     */
    private GridBagConstraints gc;

    /**
     * Run method is used for starting the execution of the window in the Swing
     * event dispatch thread. Called only from invokeLater in the start method.
     */
    @Override
    public void run() {
        /* Catch the window closing, and ensure database connection is closed. */
        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                dispose();					// dispose of the windows resoures
                System.exit(0);
            }
        });
        setTitle(controller.getTitle());
        /* Adjust the sizes of all panels and the window, and make it visible. */
        pack();
        setVisible(true);
    }

    /**
     * Method to create the GUI and to start it.
     */
    public void start() {
        controller = new MazeController(this);
        controller.initialize();

        /* This is where error messages will be positioned. */
        MessageDisplay.setJFrame(this);

        setJMenuBar(controller.getMenuBar());
        /* Create the window frame. */
        inputPanel = new JPanel();

        setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.insets = new Insets(SPACING_VERTICAL_BEFORE, SPACING_HORIZONTAL_EACH_SIDE,
                SPACING_VERTICAL_AFTER, SPACING_HORIZONTAL_EACH_SIDE);
        /* Allow horizontal spacing adjustment within the GridBagLayout. */
        gc.weightx = NON_ZERO; 
        add(controller.getMazeTextArea(), gc);
        inputPanel.add(controller.getColsLabel());	// column
        inputPanel.add(controller.getColsInputField());
        inputPanel.add(controller.getRowsLabel());
        inputPanel.add(controller.getRowsInputField());
        inputPanel.add(controller.getNewMazeButton());
        inputPanel.add(controller.getShowSolutionButton());
        inputPanel.add(controller.getHideSolutionButton());
        gc.gridy = 1;
        add(inputPanel, gc);
        this.setMinimumSize(new Dimension(900,950));
        /* The input fields are initially clear. */
        SwingUtilities.invokeLater(this::run);
    }

    /**
     * Launch the AddressBook application by starting the GUI.
     */
    public static void launch() {
        MazeView mazeDisplay = new MazeView();
        mazeDisplay.start();
    }
}
