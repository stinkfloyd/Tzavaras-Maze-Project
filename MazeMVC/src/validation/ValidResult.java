
package validation;

/**
 * Data object for returning results of validation and standardization.
 * <p>
 * All String forms are idempotent, in the sense that if any of the String
 * forms generated are provided as input to the corresponding validation method,
 * the same String forms will result.
 * <p>
 * The fields are public, and may be accessed directly. Used with permission.
 *
 * @param <T>	the type of the expected result
 *
 * @author		Dr. Bruce K. Haddon, Instructor
 * @version		1.3, 2015-05-01
 */
@SuppressWarnings("PublicField")
public class ValidResult<T>
{
/**
 * This the form regarded as most common, or, at least, can be a commonly used
 * used written form. Useful when starting with a machine form, and a form is
 * needed that can be easily read by humans.
 */
public String common;

/**
 * The result as the expected type for the validated item. This will be, in the
 * case of String results, the "minimalist" form, and probably the most suitable
 * for entry to databases, etc. Otherwise, it will be the form of the given
 * type for this item. For this reason, it is called the {@code machine} form.
 *
 */
public T machine;

/**
 * This the particular form most closely conforming to the given input, but
 * rewritten to be as regular as possible (removing excess characters, etc.).
 * This is usually the form to echo to users for them to verify their input.
 */
public String particular;

@Override
public String toString()
{
	return "m=" + machine.toString() + ", c=" + common + ", p=" + particular;
}
}
