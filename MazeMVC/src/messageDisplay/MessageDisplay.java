
package messageDisplay;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

/**
 * This class defines a displayMessage method for creating a JOptionPane dialog,
 * with controls to ensure that multiple messages do not display at the same time.
 * The dialog is always shown on the EDT.
 *
 * @author		Dr. Bruce K. Haddon, Instructor
 * @version		3.0, 2018-02-14
 */
public class MessageDisplay
{
/**
 * Flag for ensuring only one message is showing at a time.
 */
private static final AtomicBoolean MESSAGE_SHOWING = new AtomicBoolean(false);

/**
 * Default location for the display of the message.
 */
private static JFrame frame;

/**
 * Constructor: private to prevent instantiation
 */
private MessageDisplay() {}

/**
 * Called when the view should display an error message.
 *
 * @param title			title to appear with the message
 * @param message		the message to display
 */
@SuppressWarnings("Convert2Lambda")
public static void displayMessage(final String title, final String message)
{
	displayMessage(ERROR_MESSAGE, title, message);
}

/**
 * Called when the view should display a message of a given type.
 *
 * @param messageType	ERROR_MESSGE, INFORMATION_MESSGE, PLAIN_MESSAGE,
 *						QUESTION_MESSAGE, WARNING_MESSAGE
 * @param title			title to appear with the message
 * @param message		the message to display
 */
@SuppressWarnings("Convert2Lambda")
public static void displayMessage(
					int messageType, final String title, final String message)
{
	/* Do not show another message if there is already a message showing. */
	if( MESSAGE_SHOWING.compareAndSet(false, true) )
	{
		/* If no title is given for the message dialog, use an empty string. */
		final String dialogTitle = title == null ? "" : title;

		/* Invoke this with "later" just to ensure this is on the EDT. */
		SwingUtilities.invokeLater(new Runnable()
		{
			/**
			 * This run method is executed on the EDT
			 */
			@Override
			public void run()
			{
				/* The position is the position on the screen, in this case, on top
				   of every other window on the screen and inside the JFrame (if
				   given). */
				final JDialog position = new JDialog();
				position.setAlwaysOnTop(true);
				position.setVisible(true);
				if( frame != null ) position.setLocationRelativeTo(frame);
				JOptionPane.showMessageDialog(
					position,						// location
					message,						// message
					dialogTitle,					// dialog title
					messageType);					// message type
				/* When user dismisses the dialog, the message showing is over. */
				position.dispose();
				MESSAGE_SHOWING.set(false);
			}
		});
	}
}

/**
 * Create the place in which the alert messages will be shown.
 *
 * @param frame		the window for the application
 */
public static void setJFrame(JFrame frame)
{
	MessageDisplay.frame = frame;
}

/**
 * Optimization: allow for the short-circuit of some work if there is a message
 * showing that has not yet been dismissed by the user.
 *
 * @return		true if there is a message showing
 */
public static boolean isMessageShowing()
{
	return MESSAGE_SHOWING.get();
}
}
