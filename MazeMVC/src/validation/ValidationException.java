
package validation;

/**
 * This class is basically a wrapper on the superclass Exception, has just two
 * of the constructors, and inherits the same methods. Used with permission.
 *
 * @author		Dr. Bruce K. Haddon, Instructor
 * @version		1.3, 2015-01-20
 */
public class ValidationException extends Exception
{
/**
 * Serialization version (generated 2016-03-09).
 */
private static final long serialVersionUID = 8565967882011426235L;

/**
 * Constructs a new Throwable with the specified detail message. The cause is
 * not initialized, and may subsequently be initialized by a call to
 * {@link #initCause}.
 *
 * @param message	the detail message. The detail message is saved for later
 *					retrieval by the {@link #getMessage()} method.
 *
 * @see	Exception
 */
public ValidationException(String message)
{
	super(message);
}

/**
 * Constructs a new Throwable with the specified detail message and cause.
 *
 * @param message	the detail message. The detail message is saved for later
 *					retrieval by the {@link #getMessage()} method.
 * @param cause		the cause (which is saved for later retrieval by the
 *					getCause() method). (A null value is permitted, and
 *					indicates that the cause is nonexistent or unknown.)
 *
 * @see	Exception
 */
public ValidationException(String message, Throwable cause)
{
	super(message, cause);
}

}
