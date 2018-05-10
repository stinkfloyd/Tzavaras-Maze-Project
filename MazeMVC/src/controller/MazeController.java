package controller;

import validation.ValidationException;
import validation.ValidResult;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.MazeModel;
import view.MazeView;
import messageDisplay.MessageDisplay;
import static validation.Validation.validateInteger;

/**
 * Controller for the view of this application. The controller provides all the
 * controls (with needed handlers), transferring information to the model
 *
 * @author	Nicholas Tzavaras, s02150247
 * @version	Beta, 5/3/2018
 */
public class MazeController {

    /**
     * The view component of this application.
     */
    private final MazeView view;

    /**
     * The model of this application.
     */
    private final MazeModel model;

    /**
     * Used for validation that maze is present to show or hide solution.
     */
    private boolean mazePreviouslyCreated;

    /**
     * The title to use on the window of the application.
     */
    private final String title;

    /**
     * The button to display a new maze.
     */
    private JButton newMazeButton;

    /**
     * The button to show solution of current maze.
     */
    private JButton showSolutionButton;

    /**
     * The button to hide solution of current maze.
     */
    private JButton hideSolutionButton;

    /**
     * Reference to the text field for the input of number of columns.
     */
    private JTextField colsInputField;

    /**
     * Reference to the text field for the input of number of rows.
     */
    private JTextField rowsInputField;

    /**
     * Label used to label the input field for Columns.
     */
    private JLabel colsLabel;

    /**
     * Label used to label the input field for Rows.
     */
    private JLabel rowsLabel;

    /**
     * Text area used to display the maze as strings.
     */
    private JTextArea mazeTextArea;

    private JMenuBar menuBar;

    public MazeController(MazeView view) {
        this.view = view;
        this.model = new MazeModel();
        this.title = "Maze Generator and Solver";
    }

    public void initialize() {

        {   //Sets Up the Menu Bar.
            menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic('F');
            JMenuItem saveAction = new JMenuItem("Save");
            fileMenu.add(saveAction);
            saveAction.setMnemonic('s');
            saveAction.addActionListener(this::saveActionPerformed);
            menuBar.add(fileMenu);
            JMenuItem exitAction = new JMenuItem("Exit");
            fileMenu.add(exitAction);
            exitAction.setMnemonic('x');
            exitAction.addActionListener((ActionEvent e) -> {
                System.exit(0);
            });
            
        }

        {   //Sets up the Text Area.
            mazeTextArea = new JTextArea();
            mazeTextArea.setFont(new Font(MONOSPACED, PLAIN, 11));
            mazeTextArea.setColumns((model.getMaxMazeSize() * 4) + 1);
            mazeTextArea.setRows((model.getMaxMazeSize() * 2) + 1);
            mazeTextArea.setMinimumSize(mazeTextArea.getSize());

            mazeTextArea.setPreferredSize(mazeTextArea.getSize());
            mazeTextArea.setBackground(Color.white);
            mazeTextArea.setEditable(false);
        }

        {   //Sets up the Input Fields
            colsInputField = new JTextField(2);
            colsInputField.setEnabled(true);
            colsInputField.setHorizontalAlignment(JTextField.CENTER);
            rowsInputField = new JTextField(2);
            rowsInputField.setEnabled(true);
            rowsInputField.setHorizontalAlignment(JTextField.CENTER);
        }
        {   //Sets up the Labels for input fields
            colsLabel = new JLabel("Columns (1-25):");
            colsLabel.setHorizontalAlignment(JLabel.RIGHT);
            rowsLabel = new JLabel("   Rows (1-25):");
            rowsLabel.setHorizontalAlignment(JLabel.RIGHT);
        }
        {   //Set up the Buttons use for New maze, show and hide solution
            newMazeButton = new JButton("New Maze");
            newMazeButton.addActionListener(this::newMazeButtonActionPerformed);
            showSolutionButton = new JButton("Show Solution");
            showSolutionButton.addActionListener(this::showSolutionButtonActionPerformed);
            hideSolutionButton = new JButton("Hide Solution");
            hideSolutionButton.addActionListener(this::hideSolutionButtonActionPerformed);
        }

    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public JTextArea getMazeTextArea() {
        return mazeTextArea;
    }

    /**
     * The model for the application.
     *
     * @return the model for the application
     */
    public MazeModel getModel() {
        return model;
    }

    /**
     * Make the title of the application available to the view.
     *
     * @return	the title of the application
     */
    public String getTitle() {
        return title;
    }

    /**
     * The view for the application.
     *
     * @return	the view
     */
    public MazeView getView() {
        return view;
    }

    public JButton getNewMazeButton() {
        return newMazeButton;
    }

    public JButton getShowSolutionButton() {
        return showSolutionButton;
    }

    public JButton getHideSolutionButton() {
        return hideSolutionButton;
    }

    public JTextField getColsInputField() {
        return colsInputField;
    }

    public JTextField getRowsInputField() {
        return rowsInputField;
    }

    public JLabel getColsLabel() {
        return colsLabel;
    }

    public JLabel getRowsLabel() {
        return rowsLabel;
    }

    private void newMazeButtonActionPerformed(ActionEvent e) {
        mazePreviouslyCreated = true;
        /* If the field contain no text, then just ignore the event. */
        if (colsInputField.getText().trim().isEmpty()
                || rowsInputField.getText().trim().isEmpty()) {
            MessageDisplay.displayMessage("Size Entry Error",
                    "Please make sure both fields have integer values 1-25.");
            return;
        }

        try {
            ValidResult<Integer> colsResult
                    = validateInteger(colsInputField.getText(),
                            /* minimum */ 1,
                            /* maximum */ model.getMaxMazeSize());
            ValidResult<Integer> rowsResult
                    = validateInteger(rowsInputField.getText(),
                            /* minimum */ 1,
                            /* maximum */ model.getMaxMazeSize());
            model.newMaze(colsResult.machine,
                    rowsResult.machine);
            mazeTextArea.setText(null);
            String[] lines = model.getBlankMazeLines();
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line.concat("\n"));
            }
            mazeTextArea.append(sb.toString());
            sb.setLength(0);
        } catch (NumberFormatException | ValidationException ex) {
            MessageDisplay.displayMessage("Size Entry Error",
                    "Please make sure both fields have integer values 1-25.");
        }
    }

    private void showSolutionButtonActionPerformed(ActionEvent e) {
        if (!mazePreviouslyCreated) {
            MessageDisplay.displayMessage("Maze Solution Error",
                    "Please create a maze first.");
            return;
        }
        mazeTextArea.setText(null);
        String[] lines = model.getSolvedMazeLines();
        for (String line : lines) {
            mazeTextArea.append(line.concat("\n"));
        }
    }

    private void hideSolutionButtonActionPerformed(ActionEvent e) {
        if (!mazePreviouslyCreated) {
            MessageDisplay.displayMessage("Maze Solution Error",
                    "Please create a maze first.");
            return;
        }
        mazeTextArea.setText(null);
        String[] lines = model.getBlankMazeLines();
        for (String line : lines) {
            mazeTextArea.append(line.concat("\n"));
        }
    }

    private void saveActionPerformed(ActionEvent e) {
        if (!mazePreviouslyCreated) {
            MessageDisplay.displayMessage("Maze Save Error",
                    "Please create a maze first.");
            return;
        }
        
        FileDialog fDialog = new FileDialog(view, "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path.concat(".txt"));
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, true)) // true for append
            ) {
                mazeTextArea.write(writer);
            }
        } catch (IOException ex) {
            MessageDisplay.displayMessage("Saving Error",
                    "Error saving file. Make sure the file name is correct.");
        }
        
    }
}
