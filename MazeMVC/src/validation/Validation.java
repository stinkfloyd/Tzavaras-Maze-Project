
package validation;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.Math.rint;
import static java.lang.StrictMath.min;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Class containing the different methods for validation, verification, and
 * standardization of input strings and values for various styles of values. These
 * are very general, and may be used in any validation and verification context.
 * <p>
 * The methods whose names being with {@code validation...} throw a
 * {@code ValidationException} if a character sequence or value does not pass
 * validation. The {@code ValidationException} will contain a message giving that
 * reason for the problem, and also possible a (@code cause) with further detail.
 * </p>
 * <p>
 * Similarly, the methods whose names being with {@code validation...} return a
 * {@code ValidResult<T>} instance if the given input is valid. This object contains
 * three public fields, called "machine," "common," and "particular." The
 * {@code machine} field will contain a value of type T, being the canonical, and
 * usually the most compact, representation of the value. The {@code common} field
 * will contain a {@code String} representing the most common way of showing that
 * value as a printable or viewable {@code String}. The {@code particular} field
 * will be a {@code String} similar to the {@code common} field, but will use
 * elements from the original input, attempting to replicate particular usages but
 * in a more standardized way. </p>
 * <p>
 * The methods whose names being with {@code verify...} return a value if the given
 * input value is valid (usually lying within a given range or having some other
 * characteristic). The returned value will be of the same type as the input value,
 * and the returned value may be a rounded or otherwise modified version of the
 * given value; otherwise a {@code ValidationException} will be thrown. </p>
 * <p>
 * The methods whose names begin with {@code compareInRange...} verify that the
 * range conditions specified hold, and return an integer value indicating how the
 * given value is inside or outside the limits desired. </p>
 * <p>
 * Except where otherwise documented, all parameter values are assumed to be not
 * null, and may lead to {@code NullPointerException}s or
 * {@code ValidationException}s. Used with permission.
 *
 * @author		Dr. Bruce K. Haddon, Instructor
 * @version		1.4, 2017-03-22
 */
@SuppressWarnings("NestedAssignment")
public class Validation
{
/**
 * Access to the Decimal Format Symbols
 */
private static final DecimalFormatSymbols ADFS = DecimalFormatSymbols.getInstance();

/**
 * Base of the decimal numbering system.
 */
private static final int BASE_DECIMAL = 10;

/**
 * Base of the hexadecimal numbering system.
 */
private static final int BASE_HEXADECIMAL = 16;

/**
 * Character at_sign (used in email addresses).
 */
private static final char CHAR_ANGLE_CLOSE = '>';

/**
 * Character angle_open (used in email addresses).
 */
private static final char CHAR_ANGLE_OPEN = '<';

/**
 * Character at_sign (used in email addresses).
 */
private static final char CHAR_AT = '@';

/**
 * Character bracket close (used in email addresses).
 */
private static final char CHAR_BRACKET_CLOSE = ']';

/**
 * Character bracket open (used in email addresses).
 */
private static final char CHAR_BRACKET_OPEN = '[';

/**
 * Dash character to use in standardized social security numbers.
 */
private static final char CHAR_DASH = '-';

/**
 * Character used to separate the decimal part of a floating number.
 */
private static final char CHAR_DECIMAL = ADFS.getDecimalSeparator();

/**
 * Period character, used to form abbreviations when recognized, separators in
 * email addresses, and punctuation in phone numbers.
 */
private static final char CHAR_DOT = '.';

/**
 * Character double quote sign (used as an escape character in email addresses).
 */
private static final char CHAR_DOUBLE_QUOTE = '\"';

/**
 * Character reverse slash (used as an escape character).
 */
private static final char CHAR_ESCAPE = '\\';

/**
 * The character used in numbers (and formatting) for thousands separation.
 */
private static final char CHAR_GROUPING = ADFS.getGroupingSeparator();

/**
 * Character representing minus sign in numbers.
 */
private static final char CHAR_MINUS = ADFS.getMinusSign();

/**
 * Character close parenthesis (used in email and phone numbers).
 */
private static final char CHAR_PAREN_CLOSE = ')';

/**
 * Character open parenthesis (used in email and phone numbers).
 */
private static final char CHAR_PAREN_OPEN = '(';

/**
 * Percentage sign. (Used for percentages, and as a hexadecimal escape in email
 * addresses.
 */
private static final char CHAR_PERCENTAGE = ADFS.getPercent();

/**
 * Character representing plus sign in numbers.
 */
private static final char CHAR_PLUS = '+';

/**
 * Single quote (or apostrophe) character.
 */
private static final char CHAR_SINGLE_QUOTE = '\'';

/**
 * Space character.
 */
private static final char CHAR_SPACE = ' ';

/**
 * Underscore character.
 */
private static final char CHAR_UNDERSCORE = '_';

/**
 * Slash character (used in phone numbers).
 */
private static final char CHAR_VIRGULE = '/';

/**
 * String version of the symbol indicating a country code in a phone number.
 */
private static final String COUNTRY_CODE_FLAG = "+";

/**
 * The minimum number of digits that can appear in a credit card number.
 */
private static final int CREDIT_CARD_MINIMUM_LENGTH = 7;

/**
 * The modulus for checking the result of the Luhn credit card check algorithm.
 */
private static final int CREDIT_CARD_MODULUS = 10;

/**
 * The radix for interpreting the digits in a credit card number.
 */
private static final int CREDIT_CARD_RADIX = 10;

/**
 * Currency character (default)
 */
private static final char CURRENCY_DEFAULT = ADFS.getCurrencySymbol().charAt(0);

/**
 * The standard default format for a dateValue is the indicated format within
 * the given list.
 */
private static final int DEFAULT_DATE_FORMAT = 4;

/**
 * The input format of a dateValue may be any of the following formats.
 */
private static final DateTimeFormatter[] FORMATS =
		{
			ofPattern("M/d/yy"),		// 0
			ofPattern("M/d/yyyy"),		// 1
			ofPattern("d.M.yy"),		// 2
			ofPattern("d.M.yyyy"),		// 3
			ISO_LOCAL_DATE,				// 4
			ofPattern("yyyy-M-d"),		// 5
			ofPattern("yy-M-d"),		// 6
			ofPattern("MMM d yy"),		// 7
			ofPattern("MMM d yyyy"),	// 8
			ofPattern("MMM d, yy"),		// 9
			ofPattern("MMM d, yyyy"),	// 10
			ofPattern("d MMM yy"),		// 11
			ofPattern("d MMM yyyy"),	// 12
			ofPattern("d MMM, yy"),		// 13
			ofPattern("d MMM, yyyy"),	// 14
			ofPattern("MMMM d yy"),		// 15
			ofPattern("MMMM d yyyy"),	// 16
			ofPattern("MMMM d, yy"),	// 17
			ofPattern("MMMM d, yyyy"),	// 18
			ofPattern("d MMMM yy"),		// 19
			ofPattern("d MMMM yyyy"),	// 20
			ofPattern("d MMMM, yy"),	// 21
			ofPattern("d MMMM, yyyy")	// 22
		};

/**
 * Number of significant characters in an ISBN10 number.
 */
private static final int ISBN10 = 10;

/**
 * Number of significant characters in an ISBN13 number.
 */
private static final int ISBN13 = 13;

/**
 * Longest permissable length of an Internet domain.
 */
private static final int LENGTH_EMAIL_DOMAIN = 253;

/**
 * Longest permissable length of the local-part of an email address.
 */
private static final int LENGTH_EMAIL_LOCAL_PART = 64;

/**
 * Total permissable length of an email address (local-part and domain)
 */
private static final int LENGTH_EMAIL_TOTAL = 254;

/**
 * Minimum value for any percentage (positive or negative).
 */
private static final double MINIMUM_PERCENTAGE_VALUE = 1.0E-6;

/**
 * Country codes for all the countries using the North American Numbering Plan
 * for telephone numbers.
 */
private static final Set<String> NANP = new HashSet<>();

/**
 * NANP start of area code in machine telephone number.
 */
private static final int NANP_START_AREA = 2;

/**
 * NANP start of office code in machine telephone number.
 */
private static final int NANP_START_OFFICE = 5;

/**
 * NANP start of phone code in machine telephone number.
 */
private static final int NANP_START_PHONE = 8;

/**
 * Country code for North America Numbering Plan (NANP).
 */
private static final String NORTH_AMERICA_COUNTRY_CODE = COUNTRY_CODE_FLAG + "1";

/**
 *  Default number of digits in a percentage value
 */
private static final int PERCENTAGE_DEFAULT_DIGITS = 2;

/**
 * Multiplier to convert fraction to percentage.
 */
private static final double PERCENTAGE_MULTIPLIER = 100.0;

/**
 * End of the Unicode private use area.
 */
private static final char PRIVATE_USE_END = '\uF8FF';

/**
 * Start of the Unicode private use area.
 */
private static final char PRIVATE_USE_START = '\uE000';

/**
 * Place in the Unicode private use area about 2/3 of the way up.
 */
private static final char PRIVATE_USE_TERTIARY =
		(char) ((2 * PRIVATE_USE_END + PRIVATE_USE_START) / 3);

/**
 * The first field of an SNN starts at the beginning of the SSN, and ends of the
 * defined end of the first field.
 */
private static final int SNN_FIRST_FIELD = 3;

/**
 * The second field of an SNN starts at the end of the first field of the SSN,
 * and ends of the defined end of the second field.
 */
private static final int SNN_SECOND_FIELD = 5;

/**
 * The third field of an SNN starts at the end of the second field of the SSN,
 * and ends of the defined length of the SNN.
 */
private static final int SNN_TOTAL_LENGTH = 9;

/**
 * String corresponding to the at-sign character.
 */
private static final String STR_AT = Character.valueOf(CHAR_AT).toString();

/**
 * String version of the dash character.
 */
private static final String STR_DASH = Character.valueOf(CHAR_DASH).toString();

/**
 * String version of the character dot (period, full-stop, etc.).
 */
private static final String STR_DOT = Character.valueOf(CHAR_DOT).toString();

/**
 * Character(s) used to separate the exponent part of a floating number.
 */
private static final String STR_EXPONENT = ADFS.getExponentSeparator();

/**
 * String version of the character used in numbers (and formatting) for
 * thousands separation.
 */
private static final String STR_GROUPING = Character.toString(CHAR_GROUPING);

/**
 * String version of the close parenthesis character.
 */
private static final String STR_PAREN_CLOSE = Character.toString(CHAR_PAREN_CLOSE);

/**
 * String version of the open parenthesis character.
 */
private static final String STR_PAREN_OPEN = Character.toString(CHAR_PAREN_OPEN);

/**
 * String containing a single (straight) quote character.
 */
private static final String STR_QUOTE = "\"";

/**
 * String corresponding to the space character.
 */
private static final String STR_SPACE =	Character.toString(CHAR_SPACE);

/**
 * String corresponding to the underscore character.
 */
private static final String STR_UNDERSCORE = Character.toString(CHAR_UNDERSCORE);

/**
 * String corresponding to the virgule character.
 */
private static final String STR_VIRGULE = Character.toString(CHAR_VIRGULE);


/**
 * When ensuring content, leave the white space in the input.
 */
private static final boolean WS_LEAVE = false;
/**
 * When ensuring content, remove white space.
 */
private static final boolean WS_REMOVE = true;

/**
 * Symbols to ignore in an numeric input (space here represents any white
 * space).
 */
private static final String XTRA_NUMBER_SYMBOLS =
									CHAR_GROUPING + STR_SPACE + STR_UNDERSCORE;

/**
 * Symbols to ignore in an SNN input (space here represents any white space).
 */
private static final String XTRA_SSN_OR_ISBN_SYMBOLS = STR_DASH + STR_SPACE;


/**
 * Internet document defining ISBN ranges.
 */
private static Document rangeMessageDocument = null;
/**
 * Add the country codes to the NANP set.
 */
static
{
	NANP.addAll(Arrays.asList( //
			"AS", // American Samoa
			"AI", // Anguilla
			"AG", // Antigua and Barbuda
			"BS", // Bahamas
			"BB", // Barbados
			"BM", // Bermuda
			"VG", // British Virgin Islands
			"CA", // Canada
			"KY", // Cayman Islands
			"DM", // Dominica
			"DO", // Dominican Republic
			"GD", // Grenada
			"GU", // Guam
			"JM", // Jamaica
			"MS", // Montserrat
			"MP", // Northern Mariana Islands
			"PR", // Puerto Rico
			"KN", // Saint Kitts and Nevis
			"WL", // Saint Lucia
			"VC", "WV", // Saint Vincent and the Grenadines
			"SX", "MF", // Sint Maarten
			"TT", // Trinidad and Tobago
			"TC", // Turks and Caicos Islands
			"US", // United States
			"VI", // United States Virgin Islands
			"021" // Northern America (USA + Canada)
	));
}

/**
 * Constructor: private to prevent instantiation.
 */
private Validation() { }

/**
 * Compare a {@code BigDecimal} value, checking the value is in the given range,
 * inclusive of the limits. A positive value is returned if above the range,
 * negative if below, otherwise zero.
 *
 * @param value		{@code BigDecimal} value
 * @param minimum	minimum acceptable value, or null if there is no minimum
 * @param maximum	maximum acceptable value, or null if there is no maximum
 * @return			positive, zero, or negative
 */
public static int compareInRangeBigDecimal(BigDecimal value,
										BigDecimal minimum, BigDecimal maximum)
{
	int result;
	return nonNull(minimum) && (result = value.compareTo(minimum)) < 0 ||
		  nonNull(maximum) && (result = value.compareTo(maximum)) > 0 ? result : 0;
}

/**
 * Compare a {@code LocalDate} value, checking the value is in the given range,
 * inclusive of the limits. A positive value is returned if above the range,
 * negative if below, otherwise zero.
 *
 * @param value		the given LocalDate value
 * @param minimum	the earliest date permitted, or null if there is none
 * @param maximum	the latest date permitted, or null if there is none
 * @return			positive, zero, or negative
 */
public static int compareInRangeDate(LocalDate value,
		LocalDate minimum, LocalDate maximum)
{
	return nonNull(minimum) && value.isBefore(minimum) ? -1 :
			nonNull(maximum) && value.isAfter(maximum) ? +1 : 0;
}

/**
 * Compare a double value, checking the value is in the given range, inclusive of
 * the limits. A positive value is returned if above the range, negative if below,
 * otherwise zero.
 *
 * @param value		double value
 * @param minimum	minimum acceptable value
 * @param maximum	maximum acceptable value (ignored if less than minimum)
 * @return			positive, zero, or negative
 */
public static int compareInRangeDouble(double value, double minimum, double maximum)
{
	return value < minimum ? -1 : maximum >= minimum && value > maximum ? 1 : 0;
}

/**
 * Compare an integer value, checking the value is in the given range, inclusive of
 * the limits. A positive value is returned if above the range, negative if below,
 * otherwise zero.
 *
 * @param value		integer value
 * @param minimum	minimum acceptable value (inclusive)
 * @param maximum	maximum acceptable value (inclusive; ignored if less than
 *					minimum)
 * @return			positive, zero, or negative
 */
public static int compareInRangeInteger(int value, int minimum, int maximum)
{
	return value < minimum ? -1 : maximum >= minimum && value > maximum ? 1 : 0;
}

/**
 * A {@code CharSequence} representing a credit card number is validated by seeing
 * if it can be parsed and has the correct check sum. Dashes and spaces in the input
 * are ignored. If valid, a (@code ValidResult} is returned, otherwise an exception
 * is thrown.
 * <p>
 * In the (@code ValidResult}, the {@code String} representations of the machine and
 * common values are just the valid digits from the input. The particular value is
 * the same, except if the input is punctuated by hyphens or spaces, in which case
 * this value will be punctuated every four digits with the same punctuation.
 *
 * @param input		{@code CharSequence} representing a credit card number.
 * @return			result containing the the CC number with just digits as both the
 *					machine and common version, and a particular version that
 *					may be broken into groups of four digits with spaces or dashes
 *					if these were present in the input.
 * @throws ValidationException if the credit card number is invalid
 */
public static ValidResult<String> validateCreditCard(CharSequence input)
		throws ValidationException
{
	/* Ensure there is input, and extract digits, ignoring spaces and dashes. */
	Assembler bb = Assembler.ensureContent(input, "credit card number", WS_LEAVE);
	Assembler[] edit = bb.editNumber(STR_DASH + STR_SPACE, false, false);

	/* The minumum length for a credit card number is seven digits. */
	if( edit[0].length() < CREDIT_CARD_MINIMUM_LENGTH )
		throw new ValidationException(
				"Credit card number \"" + input  + "\" has insufficient digits");

	/* Compute checksum -- the Luhn algorighm
	   (see https://en.wikipedia.org/wiki/Luhn_algorithm). */
	int sum = 0;
	final int step = 2;		// step through digits backwards, two at a time
	final int cast_out = 9;	// if a value is greater than 9, cast out the 9
	for( int i = edit[0].length()- 1; i >= 0; i -= step )
		/* Digits in odd and even positions are treated differently, in order to
		   detect transpositions. */
		for( int j = 0; j != step; ++j )
			if( i - j >= 0 )
				sum += (Character.digit(edit[0].charAt(i - j),
								CREDIT_CARD_RADIX) * (j + 1) - 1) % cast_out + 1;

	/* Check that the sum is divisble by the credit card modulus. */
	if( sum % CREDIT_CARD_MODULUS != 0 )
		throw new ValidationException(
				"Credit card number \"" + input  + "\" is incorrect");

	/* Return the results. */
	ValidResult<String> result = new ValidResult<>();
	result.machine = result.common = result.particular = edit[0].toString();
	String insert;
	if( edit[1].contains(insert = STR_DASH) || edit[1].contains(insert = STR_SPACE))
	{
		Integer[] spacing = new Integer[]{4, 8, 12, 16, 20, 24, 28, 32};
		result.particular = edit[0].decorate(insert, spacing).toString();
	}
	return result;
}

/**
 * A currency value {@code CharSequence} is validated by seeing if it can be parsed
 * as a {@code BigDecimal}. If valid, a ValidResult is returned, otherwise an
 * exception is thrown.
 * <p>
 * A currency value may contain a currency symbol before any of the digits, and
 * contain grouping characters. These are ignored. A sign may also appear anywhere
 * before the first of the digits, or the value may be surrounded by parentheses to
 * indicate a negative amount.
 * <p>
 * In the (@code ValidResult}, the machine representation is a {@code BigDecimal}
 * instance with the scale set to the given number of decimals. The common value is
 * a {@code String} showing the value to the requested number of digits, preceded by
 * a currency symbol, grouped into thousands, and all this surrounded by parentheses
 * if negative. The particular value is a similar {@code String}, but following the
 * original input as closely as possible for the existence of a currency symbol,
 * whether thousands are grouped (and with which character), and, if negative, the
 * use of a minus sign or parentheses.</p>
 * <p>
 * A double value may be obtained from the machine value by use of the
 * {@code BigDecimal} method, {@code doubleValue}.
 *
 * @param input		{@code CharSequence} representing a currency value
 * @param minimum	minimum acceptable value, or null if there is no minimum
 * @param maximum	maximum acceptable value, or null if there is no maximum
 * @param decimals	number of decimal places in the currency amount, or zero if
 *					not to be checked and imposed
 * @return			result containing the {@code BigDecimal} value as the machine value,
 *					and a common version, and a version standardized near the
 *					original
 * @throws ValidationException if the currency value is invalid.
 */
public static ValidResult<BigDecimal> validateCurrency(CharSequence input,
		BigDecimal minimum, BigDecimal maximum, int decimals)
		throws ValidationException
{
	Assembler bb = Assembler.ensureContent(input, "currency", WS_REMOVE);

	/* Search to see if there is a currency symbol, before finding any digit.
	   The search does not include the last position, since if a currency symbol
	   were there, deleting it would mean there were no digits. */
	char currency_symbol = CURRENCY_DEFAULT;
	boolean currencySymbolPresent = false;

	for( int i = 0; i != bb.length() - 1; ++i )
	{
		if( Character.getType(bb.charAt(i)) == Character.CURRENCY_SYMBOL )
		{
			/* Remember the currency symbol used, and delete it from the input
			   sequence. */
			currency_symbol = bb.charAt(i);
			currencySymbolPresent = true;
			bb.deleteCharAt(i);
			break;
		}
		/* Terminate the search if a digit is seen before the currency sign. */
		if( Character.getType(bb.charAt(i)) == Character.DECIMAL_DIGIT_NUMBER )
			break;
	}

	/* Check if negative value is indicated by parentheses. */
	int length = bb.length();
	boolean negParen = false;
	if( length != 0 &&
			(negParen = bb.charAt(0) == '(' &&  bb.charAtEnd() == ')') )
		bb.deleteCharAtEnd().setCharAt(0, CHAR_MINUS);

	/* What remains should be an acceptable representation of a big decimal
	   value. Remove any extraneous separators in the number, and parse it. */
	BigDecimal bigDecimal;
	Assembler[] edit = bb.editNumber(XTRA_NUMBER_SYMBOLS, true, true);
	try
	{
		bigDecimal = new BigDecimal(edit[0].toString());
	} catch( NumberFormatException ex )
	{
		throw new ValidationException("Amount \"" + input +
														"\" not understood", ex);
	}
	/* This line will throw an exception if there is any problem. */
	bigDecimal = verifyBigDecimal(bigDecimal, minimum, maximum, decimals);
	/* Otherwise, return the validated value. */
	ValidResult<BigDecimal> result = new ValidResult<>();
	result.machine = bigDecimal;
	/* Ensure that a negative sign, if needed, precedes the currency sign. */
	boolean negative = bigDecimal.signum() < 0;
	if( negative ) bigDecimal = bigDecimal.abs();
	/* This format only occurs in this method.  */
	String formatCommon = "%c%,." + decimals + "f";
	String formatWithOutSeparator = "%." + decimals + "f";
	String formatWithSeparator = "%,." + decimals + "f";
	result.common = String.format(formatCommon, currency_symbol, bigDecimal);
	if( negative ) result.common = "(" + result.common + ")";
	/* Individual result, matching the original input in a standard way.*/
	if( edit[1].contains(STR_GROUPING) || edit[1].contains(STR_UNDERSCORE) )
		result.particular = String.format(formatWithSeparator, bigDecimal);
	else
		result.particular = String.format(formatWithOutSeparator, bigDecimal);
	if( edit[1].contains(STR_UNDERSCORE) )
		result.particular =
				result.particular.replace(CHAR_GROUPING, CHAR_UNDERSCORE);
	if( currencySymbolPresent )
		result.particular = currency_symbol + result.particular;
	if( negative )
		result.particular = negParen ? "(" + result.particular + ")" :
				CHAR_MINUS + result.particular;
	return result;
}

/**
 * Validate a {@code CharSequence} as a representation of a LocalDate . The input
 * should contain a date value representation in one of the approved formats. If
 * valid, a {@code ValidResult} is returned, otherwise an exception is thrown.
 * <p>
 * In the (@code ValidResult}, the machine value is an instance of a LocalDate. The
 * {@code String} representations given as the common value will be that date in
 * ISO-8601 standard format. The {@code String} in the particular value will be as
 * close as possible to the format recognized in the original input.
 *
 * @param input		the input {@code CharSequence} possible containing a date
 * @param minimum	the earliest date permitted, or null if there is none
 * @param maximum	the latest date permitted, or null if there is none
 * @return			result containing the LocalDate value, and a common version,
 *					and a version standardized near the original
 * @throws ValidationException if the input String is not recognizable as one.
 *                             of the recognized dateValue formats
 */
public static ValidResult<LocalDate> validateDate(CharSequence input,
		LocalDate minimum, LocalDate maximum) throws ValidationException
{
	/* Try each of the different formats, looking for one that works. */
	Assembler bb = Assembler.ensureContent(input, "date value", WS_LEAVE);
	DateTimeFormatter particularFormat;
	String possible = bb.trim().toString();
	for( DateTimeFormatter formatter : FORMATS )
		try
		{
			particularFormat = formatter;
			DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
			builder.parseCaseInsensitive();
			builder.append(formatter.withResolverStyle(ResolverStyle.SMART));
									// ensure the format is interpreted smartly
			DateTimeFormatter resolved = builder.toFormatter();

			/* If parse succeeds, return the value and the common value. */
			LocalDate date = LocalDate.parse(possible, resolved);
			/* Check range permitted. */
			date = verifyDate(date, minimum, maximum);

			ValidResult<LocalDate> result = new ValidResult<>();
			result.machine = date;
			result.common = FORMATS[DEFAULT_DATE_FORMAT].format(date);
			result.particular = particularFormat.format(date);
			return result;
		} catch( DateTimeParseException ex )
		{
			/* This format did not work; try the next one. */
		}
	/* No date format that works has been found. */
	throw new ValidationException("Given date \"" + input + "\" not understood");
}

/**
 * Given an input {@code CharSequence}, validates that is represents an double
 * number. If not, a {@code ValidationException} is thrown.
 * <p>
 * In the (@code ValidResult}, the machine result is an instance of type
 * {@code Double}, containing the validated value. The common value is just the
 * {@code String} obtained from the {@code Double} {@code toString} method. The
 * particular {@code String} is the same, except that if the input representation
 * contained (a) grouping character(s), the particular version will contain the same
 * grouping.
 *
 * @param input		{@code CharSequence} perhaps representing an double
 * @param minimum	minimum acceptable value
 * @param maximum	maximum acceptable value (ignored if less than minimum)
 * @param digits	number of significant digits in resulting double, or zero if
 *					not to be imposed
 * @return			result containing the double value, and a common version,
 *					and a version standardized near the original
 * @throws ValidationException if the double value is invalid
 */
public static ValidResult<Double> validateDouble(CharSequence input, double minimum,
		double maximum, int digits) throws ValidationException
{
	Assembler bb =  Assembler.ensureContent(input, "double value", WS_REMOVE);

	/* Remove any extraneous separators in the input. */
	Assembler[] edit = bb.editNumber(XTRA_NUMBER_SYMBOLS, true, true);
	double value;
	try
	{
		 /* Attempt to parse the number. */
		value = Double.parseDouble(edit[0].toString());
	} catch( NumberFormatException ex )
	{
		throw new ValidationException("Number \"" + input +
				"\" not understood", ex);
	}
	value = verifyDouble(value, minimum, maximum, digits);
	ValidResult<Double> result = new ValidResult<>();
	result.machine = value;
	/* These formats only occur in this method. */
	String formatWithSeparator = "%," + (digits == 0 ? "" : digits) + "f";
	/* Common result. */
	result.common = result.particular = Double.toString(value);
	/* Individual result, matching the original input in a standard way. */
	if( edit[1].contains(STR_GROUPING) || edit[1].contains(STR_UNDERSCORE) )
		result.particular = String.format(formatWithSeparator, value);
	if( edit[1].contains(STR_UNDERSCORE) )
		result.particular =
				result.particular.replace(CHAR_GROUPING, CHAR_UNDERSCORE);
	return result;
}

/**
 * Given a {@code CharSequence} value, validate that it represents an email address.
 * Otherwise, throw an exception.
 * <p>
 * This implementation is built around a simplified interpretation of the formal
 * definitions found in RFC 5322 (sections 3.2.3 and 3.4.1) and RFC 5321, as well
 * as additional information found in RFC 3696 and the associated errata. The
 * standardization removes all optional elements in the email address, including
 * eliminating white space everywhere, escaped or un-escaped, and returning a
 * "bare" email address {@code String}. In particular, note that domain names and IP
 * addresses are not checked to actually exist. The relevant links are: </p>
 * <ul>
 * <li>RFC 5322:
 * <a href="http://tools.ietf.org/html/rfc5322">
 * http://tools.ietf.org/html/rfc5322</a></li>
 * <li>RFC 6854:
 * <a href="http://tools.ietf.org/html/rfc6854">
 * http://tools.ietf.org/html/rfc6854</a></li>
 * </ul>
 * <p>
 * Caution: the use of escaped character or character sequences in the the email
 * address. other than whitespace, bypasses validation, and will be passed through
 * to the final validated email address, even if these escaped characters are
 * ultimately not permissible in an email address. </p>
 * <p>
 * In the (@code ValidResult}, the machine result, the common result, and the
 * particular result are all equal {@code String}s, and are the minimal form of the
 * email address.
 *
 * @param input		{@code String} value that contains a possible email address
 * @return			result containing the three copies of the {@code String}
 *					containing the email address
 * @throws ValidationException if any part of the email address does not conform
 *                             to the rules.
 */
public static ValidResult<String> validateEmail(CharSequence input) throws
		ValidationException
{
	/* Make sure this something to check. */
	Assembler bb = Assembler.ensureContent(input, "email address", WS_LEAVE);

	/* Deal with percentage sign and hexadecimal character representations. */
	int i = 0;
	while( i != bb.length() )
	{
		/* Deal with escaped characters. Special case of escaped percentage, as
		   that percentage will not be used for hexadecimal replacement. */
		if( bb.charAt(i) == CHAR_ESCAPE && i + 1 < bb.length() )
			if( bb.charAt(i + 1) == CHAR_PERCENTAGE ||
					bb.charAt(i + 1) == CHAR_ESCAPE )
			{
				/* Leave both the escape and the following character for later
				   processing. */
				i += 2;
				continue;
			}
		/* Unescaped percentage sign indicates possible hexadecimal replacement. */
		int digit1, digit2;
		if( bb.charAt(i) == CHAR_PERCENTAGE && i + 2 < bb.length() &&
				(digit1 = Character.digit(bb.charAt(i + 1),
						BASE_HEXADECIMAL)) >= 0 &&
				(digit2 = Character.digit(bb.charAt(i + 2),
						BASE_HEXADECIMAL)) >= 0 )
		{
			char c = (char) (digit1 * BASE_HEXADECIMAL + digit2);
			bb.setCharAt(i, c);
			/* Delete first digit character and the one after it. */
			bb.delete(i + 1, i + 2);
			if( c == CHAR_ESCAPE ) ++i;
		} else
			++i;
	}

	/* Replace curly quotes with straight quotes in the email address. */
	bb.replace('\u201c', '\"').replace('\u201d', '\"');

	/* Now that all characters have been replaced by their actual versions, deal
	   with quoted substrings and slash-escaped characters. Use characters from
	   within the Unicode private use area as markers. This approach replaces
	   escaped characters with (hopefully) unused private use characters, which
	   will be converted back later. */

	char escapesource = PRIVATE_USE_TERTIARY;
	Map<Character, String> replacementMap = new HashMap<>();
	loop:
	for( i = 0; i != bb.length(); ++i )
		/* Simple escape of one character. Make the escape character the
		   escapesource replacement, and delete the following character. */
		if( bb.charAt(i) == CHAR_ESCAPE && i + 1 < bb.length() )
		{
			replacementMap.put(escapesource, Character.toString(bb.charAt(i + 1)));
			bb.setCharAt(i, escapesource--);
			bb.deleteCharAt(i + 1);
		}
		/* A double quote either at the start of the string or following a dot
		   may be the start of a quoted escape. The quoted escape must end with
		   another double quote that is either at the end of the string, or is
		   followed by a dot or the @ sign. */
		else if( bb.charAt(i) == CHAR_DOUBLE_QUOTE &&
				(i == 0 || bb.charAt(i - 1) == '.') &&	i + 1 < bb.length() )
		{
			/* Look for the ending quote, followed by the proper ending. */
			for( int j = i + 1; j != bb.length(); ++j )
				if( bb.charAt(j) == CHAR_DOUBLE_QUOTE && (j + 1 == bb.length() ||
						bb.charAt(j + 1) == CHAR_DOT ||
						bb.charAt(j + 1) == CHAR_AT) )
				{
					/* Quoted escape of a whole string. Make the forst escape
					   character the escapesource replacement, save the string
					   (without the quotes), then delete from the just after the
					   starting quote to the closing quote. */
					replacementMap.put(escapesource,
											bb.subSequence(i, j + 1).toString());
					bb.setCharAt(i, escapesource--);
					bb.delete(i + 1, j + 1);
					continue loop;
				}
			throw new ValidationException("Unterminated quoted escape");
		}

	/* If < and > are present or [ and ] are present, then anything outside
	   those markers are comments,and should be removed and ignored. */
	int first;
	int last;
	char[][] markers = new char[][]
			{
				{ CHAR_ANGLE_OPEN, CHAR_ANGLE_CLOSE},
				{ CHAR_BRACKET_OPEN, CHAR_BRACKET_CLOSE }
			};
	for( char[] marker : markers )
	{
		first = bb.indexOf(marker[0]);
		last = bb.indexOf(marker[1]);
		if( first < 0 && last < 0 ) continue;
		if( first < 0 || last < first )
			throw new ValidationException(marker[0] + " or " +
					marker[1] + " present but not matched");
		else
			bb.delete(last, bb.length()).delete(0, first + 1);
	}

	/* If ( and ) are present in the email address, then anything inside those
	   markers are comments, and should be removed and ignored. */
	while( true )
	{
		first = bb.indexOf(CHAR_PAREN_OPEN);
		last = bb.indexOf(CHAR_PAREN_CLOSE);
		if( first < 0 && last < 0 ) break;
		if( first < 0 || last < first )
			throw new ValidationException(STR_PAREN_OPEN + " or " +
					STR_PAREN_CLOSE + " present but not matched");
		else
			bb.delete(first, last + 1);
	}

	/* Remove whitespace or other formatting characters. */
	bb.editWhitespace(false);

	/* Split the address into the local-part and the domain part. */
	final int LOCAL = 0;
	final int DOMAIN = 1;
	/* Protect any @ symbol that may be at the end. */
	if( bb.length() > 0 && bb.charAt(bb.length() - 1) == CHAR_AT )
		bb.append(CHAR_SPACE);
	Assembler[] parts = bb.split(STR_AT);
	if( parts.length != DOMAIN + 1 )
		throw new ValidationException("Missing, or too many, " +
				STR_AT + " symbols");

	/** Remove additional single quotes if at the beginning and the end. */
	if( parts[LOCAL].length() > 0 &&
			parts[LOCAL].charAt(0) == CHAR_SINGLE_QUOTE &&
			parts[DOMAIN].length() > 0 &&
			parts[DOMAIN].charAt(parts[DOMAIN].length() - 1) == CHAR_SINGLE_QUOTE )
	{
		parts[LOCAL].deleteCharAt(0);
		parts[DOMAIN].deleteCharAtEnd();
	}

	/* Check that there is still a local-part. */
	if( parts[LOCAL].isEmpty() )
		throw new ValidationException("No local-part of address present");
	/* Check that there is still a domain given. */
	if( parts[DOMAIN].isEmpty() )
		throw new ValidationException("No domain for address present");

	/* Check permitted characters in the local-part. */
	{
		String permitted = "#-_~$&'()*+,;=:.";
		StringBuilder notPermitted = new StringBuilder();
		for( i = 0; i != parts[LOCAL].length(); ++i )
		{
			char c = parts[LOCAL].charAt(i);
			if( !(Character.isAlphabetic(c) || Character.isDigit(c) ||
					permitted.indexOf(c) >= 0 ||
					c >= PRIVATE_USE_START && c <= PRIVATE_USE_END) &&
					notPermitted.toString().indexOf(c) < 0 )
				notPermitted.append(c);
		}
		if( notPermitted.length() != 0 )
			throw new ValidationException("Character(s) \"" +
					notPermitted.toString() + "\" not permitted in local-part");
	}

	/* Replace the escaped sequences with their actual values. */
	replacementMap.entrySet().stream().forEach((entry) ->
	{
		String marker = Character.toString(entry.getKey());
		for( int part = LOCAL; part <= DOMAIN; ++part )
			parts[part].replace(marker, entry.getValue());
	});

	/* Check the domain part of the email address. The domain part must match
	   Internet rules for domain names. */
	{
		StringBuilder notPermitted = new StringBuilder();
		int dlength = parts[DOMAIN].length();
		for( i = 0; i != dlength; ++i )
		{
			char c = parts[DOMAIN].charAt(i);
			if( !(Character.isAlphabetic(c) || Character.isDigit(c) ||
				(i != 0 && i != dlength) && (c == CHAR_DASH || c == CHAR_DOT)) &&
					notPermitted.toString().indexOf(c) < 0 )
				notPermitted.append(c);
		}
		if( notPermitted.length() != 0 )
			throw new ValidationException("Character(s) \"" +
					notPermitted.toString() + "\" not permitted in domain name");
	}

	/* Check the position of dots. */
	{
		Matcher match2 = Pattern.compile("^\\" + STR_DOT + "|" + "\\" +
				STR_DOT + "\\" + STR_DOT + "|\\" + STR_DOT + "$").matcher("");
		for( int part = LOCAL; part <= DOMAIN; ++part )
		{
			match2.reset(parts[part]);
			if( match2.find() )
				throw new ValidationException("\'" + STR_DOT +
						"\' not permitted in \"" + parts[part] +
						"\" at character " + match2.end());
		}
		if( !parts[DOMAIN].contains(CHAR_DOT) )
			throw new ValidationException("Domain must contain at least one .");
	}

	/* Check the lengths of the two parts. The are limits for the local (user)
	   part of the address, the domain part, and for the overall length. */
	if( parts[LOCAL].length() > LENGTH_EMAIL_LOCAL_PART )
		throw new ValidationException("Local-part length " +
				parts[LOCAL].length() + " longer than " +
				LENGTH_EMAIL_LOCAL_PART);
	if( parts[DOMAIN].length() > LENGTH_EMAIL_DOMAIN )
		throw new ValidationException("Domain length " +
				parts[DOMAIN].length() + " longer than " + LENGTH_EMAIL_DOMAIN);

	ValidResult<String> result = new ValidResult<>();
	/* The three results are all the same. */
	bb = parts[LOCAL].append(CHAR_AT).append(parts[DOMAIN]);
	result.machine = result.common = result.particular = bb.toString();
	 /* Check the overall length. */
	if( result.machine.length() > LENGTH_EMAIL_TOTAL )
		throw new ValidationException("Email address length " +
				result.machine.length() + " longer than " + LENGTH_EMAIL_TOTAL);
	/* And at last, there is a valid email address. */
	return result;
}


/**
 * Given an input {@code CharSequence}, validates that it represents a correct ISBN.
 * If not, a {@code ValidationException} is thrown.
 * <p>
 * This validation requires an active Internet connection so that the current ISBN
 * rules can be downloaded from the web site
 * <a href="https://www.isbn-international.org">www.isbn-international.org</a>. If
 * this validation is not used, then the connection is not made.
 * <p>
 * In the (@code ValidResult}, the machine contains a {@code String} with just the
 * digits and check character from the input. The common value contains the same
 * value, but punctuated with "-" to show the language, country, publisher, serial
 * number and check character. The particular form will be the same as the machine
 * form if the input contained no punctuation, or will be the same as the common
 * value if it did.
 *
 * @param input		{@code CharSequence} perhaps representing an ISBN
 * @param kind		Indication of the kind of ISBN to be returned (10 for an ISBN10,
 *					13 for an ISBN13, or 0 if the kind is to be the same as the
 *					given input.
 * @return			result containing the ISBN value as an un-punctuated String,
 *					and a common (punctuated) version, and a version standardized
 *					near the original
 * @throws ValidationException if the input String does not represent an valid ISBN
 */
public static ValidResult<String> validateISBN(CharSequence input, int kind)
													throws ValidationException
{
	if( rangeMessageDocument == null )
	{
		InputStream stream;
		try
		{
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection connection =
					(HttpURLConnection) new URL(
					"https://www.isbn-international.org/export_rangemessage.xml").
							openConnection();
			stream = connection.getInputStream();
		} catch( IOException ex )
		{
			throw new ValidationException("Internet document not available", ex);
		}
		try( InputStream internetText = stream )
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			rangeMessageDocument = builder.parse(internetText);
			rangeMessageDocument.getDocumentElement().normalize();
		} catch( IOException | FactoryConfigurationError |
				SAXException | ParserConfigurationException ex )
		{
			throw new ValidationException("Internet document not parsed", ex);
		}
	}

	/* Check the character sequence input. */
	Assembler bb = Assembler.ensureContent(input, "ISBN", WS_REMOVE);

	/* Check the kind of output selection. */
	if( kind != 0 && kind != ISBN13 && kind != ISBN10 )
		throw new ValidationException("ISBN kind must be " +
													ISBN13 + " or " + ISBN10);
	char lastChar = bb.charAtEnd();
	bb.deleteCharAtEnd();
	Assembler[] edit = bb.editNumber(XTRA_SSN_OR_ISBN_SYMBOLS, false, false);
	String isbn = edit[0].toString();
	int ISBNlength = isbn.length();
	if( ISBNlength != ISBN13 - 1 && ISBNlength != ISBN10 - 1 )
		throw new ValidationException("Given ISBN \"" + input +
											"\" not of permissible length.");
	/* If kind not specified, make it the same as the input. */
	if( kind == 0 ) kind = ISBNlength + 1;
	if( ISBNlength == ISBN10 - 1 ) isbn = "978" + isbn;

	/* Thee record where the punctuation is to be inserted. */
	int index = 0;
	int leader_length = 0;
	int chars = 0;

	@SuppressWarnings("null")
	NodeList listOfRegistrationGroups = rangeMessageDocument.getElementsByTagName(
														"RegistrationGroups");
	int number_of_groups = listOfRegistrationGroups.getLength();	// should be 1
	int i = 0;
scan:
	for( ; i != number_of_groups; ++i )
	{
		Node registrationNode = listOfRegistrationGroups.item(i);
		if( registrationNode.getNodeType() == Node.ELEMENT_NODE )
		{
			Element registrationElement = (Element) registrationNode;
			NodeList listOfGroups = registrationElement.
													getElementsByTagName("Group");
			for( int j = 0; j != listOfGroups.getLength(); ++j )
			{
				Node groupNode = listOfGroups.item(j);
				if( groupNode.getNodeType() == Node.ELEMENT_NODE )
				{
					Element groupElement = (Element) groupNode;

					/* Get the prefix field. */
					Element nodeElement = (Element) groupElement.
										getElementsByTagName("Prefix").item(0);
					/* Get the child nodes and extract the text from the first
					   child node. */
					String prefix_language = nodeElement.getChildNodes().
													item(0).getNodeValue().trim();
					index = prefix_language.indexOf('-');
					String leader = prefix_language.substring(index + 1);
					leader_length = leader.length();

					if( !prefix_language.substring(0, index).
							equals(isbn.substring(0, index)) ||
							!leader.equals(isbn.substring(index, index +
									leader_length)) ) continue;

					/* Get the Rules field. */
					NodeList listOfRules =
							groupElement.getElementsByTagName("Rules");
					Element rulesNodeElement = (Element) listOfRules.item(0);

					NodeList listOfRule =
							rulesNodeElement.getElementsByTagName("Rule");
					//int totalRules = listOfRule.getLength();
					//System.out.println("  Total no of Rules : " + totalRules);
					for( int k = 0; k != listOfRule.getLength(); ++k )
					{
						Node ruleNode = listOfRule.item(k);

						if( ruleNode.getNodeType() == Node.ELEMENT_NODE )
						{
							Element ruleElement = (Element) ruleNode;

							/* Get the Length field. */
							Element lengthElement = (Element) ruleElement.
										getElementsByTagName("Length").item(0);
							/* Get the child nodes and extract the text from the
							   first child node. */
							String length = lengthElement.getChildNodes().
													item(0).getNodeValue().trim();
							chars = Integer.parseInt(length);

							if( chars != 0 )
							{
								/* Get the Range field. */
								Element rangeElement = (Element) ruleElement.
										getElementsByTagName("Range").item(0);
								/* Get the child nodes and extract the text from the
								   first child node. */
								String range = rangeElement.getChildNodes().
													item(0).getNodeValue().trim();
								int index1 = range.indexOf('-');

								String lower_limit = range.substring(0, chars);
								String upper_limit = range.substring(
													index1 + 1, index1 + 1 + chars);
								String compare_string = isbn.substring(
													index + leader_length,
													index + leader_length + chars);

								if( compare_string.compareTo(lower_limit) >= 0 &&
										compare_string.compareTo(upper_limit) <= 0 )
									break scan;
							}
						}
					}
				}
			}
		}
	}

	/* If the search is exhausted, the ISBN is not valid. */
	if( i == number_of_groups )
		throw new ValidationException("ISBN \"" + input +
												"\" contains invalid sequence");

	/* Function for computing ISBN10 sum check. */
	final Function<Assembler, Character> ISBN10_check =
			(Assembler assembler) ->
			{
				int sum = 0;
				for( int j = 0; j != ISBN10 - 1; ++j )
					sum += Character.digit(assembler.charAt(j),
										BASE_DECIMAL) * (j + 1);
				sum %= BASE_DECIMAL + 1;
				return "0123456789X".charAt(sum);
	};

	/* Function for computing ISBN13 sum check. */
	final Function<Assembler, Character> ISBN13_check =
			(Assembler assembler) ->
			{
				/* Literal constants are local to this function. */
				int sum = 0;
				for( int j = 0; j != ISBN13 - 1; ++j )
					sum += Character.digit(assembler.charAt(j),
										BASE_DECIMAL) *	(((j & 0x1) == 0) ? 1 : 3);
				sum = (BASE_DECIMAL - sum % BASE_DECIMAL) % BASE_DECIMAL;
				return "0123456789".charAt(sum);
	};

	/* Check ISBN check character. The element edit[0] contains the undecorated
	   original version of the ISBN, minus the last characer.*/
	if( ISBNlength == ISBN10 - 1 )
	{
		if( ISBN10_check.apply(edit[0]) != lastChar )
			throw new ValidationException("ISBN \"" + input +
													"\" incorrect sum check.");
		if( kind == ISBN13 )
		{
			edit[0].insert(0, "978");
			lastChar = ISBN13_check.apply(edit[0]);
		}
	} else
	{
		/* ISBN13 */
		if( ISBN13_check.apply(edit[0]) != lastChar )
			throw new ValidationException("ISBN \"" + input +
													"\" incorrect sum check.");

		if( kind == ISBN10 )
		{
			edit[0].delete(0,3);			/* Remove the prefix. */
			lastChar = ISBN10_check.apply(edit[0]);
		}
	}

	/* Put back the last character (or the last character as computed. */
	edit[0].append(lastChar);

	Assembler decorated =
			kind == ISBN13 ?
			edit[0].clone().decorate("-", index, index + leader_length,
												index + leader_length + chars,
													edit[0].length() - 1) :
			/* ISBN10 */
			edit[0].clone().decorate("-", leader_length, leader_length + chars,
													edit[0].length() - 1);

	ValidResult<String> result = new ValidResult<>();
	result.machine = edit[0].toString();	/* The shortest (machine) version. */
	result.common = decorated.toString();	/* Version with punctuation. */
	/* If the given form was not punctuated, use the machine form for the
	   particular form, otherwise use the punctuated form. */
	result.particular = edit[1].isEmpty() ? result.machine : result.common;
	return result;
}
/**
 * Given an input {@code CharSequence}, validates that it represents an integer
 * value. If not, a {@code ValidationException} is thrown.
 * <p>
 * In the (@code ValidResult}, the machine result is an instance of type
 * {@code Integer}, containing the validated value. The common value is just the
 * {@code String} obtained from the {@code Integer} {@code toString} method. The
 * particular {@code String} is the same, except that if the input representation
 * contained (a) grouping character(s), the particular version will contain the same
 * grouping.
 *
 * @param input		{@code CharSequence} perhaps representing an integer
 * @param minimum	minimum acceptable value
 * @param maximum	maximum acceptable value (ignored if less than minimum)
 * @return			result containing the {@code Integer} value, and a common
 *					version, and a version standardized near the original
 * @throws ValidationException if the input String does not represent an integer
 */
public static ValidResult<Integer> validateInteger(CharSequence input, int minimum,
		int maximum) throws ValidationException
{
	Assembler bb = Assembler.ensureContent(input, "integer value", WS_REMOVE);
	Assembler[] edit = bb.editNumber(XTRA_NUMBER_SYMBOLS, true, false);
	/* Attempt to parse the number. */
	int machine;
	try
	{
		machine = Integer.parseInt(edit[0].toString());
	} catch( NumberFormatException ex )
	{
		throw new ValidationException("Number \"" + input +
				"\" not understood", ex);
	}
	machine = verifyInteger(machine, minimum, maximum);
	ValidResult<Integer> result = new ValidResult<>();
	result.machine = machine;
	/* This format only occurs in this method. */
	result.common = result.particular = String.format("%,d", machine);
	if( edit[1].contains(STR_UNDERSCORE) )
		result.particular = result.particular.replace(CHAR_DECIMAL, CHAR_UNDERSCORE);
	return result;
}

/**
 * Method to standardize a name. Names may occur as the names of many different
 * entities, of people, places, organizations, <em>etc.</em>
 * <p>
 * Any series of white space characters is replaced by a single actual space
 * character. All letters are converted to lower case, except the initial
 * character, and any that follow white space, other dashes or punctuation,
 * which are converted to title case. </p>
 * <p>
 * If the "name" consists of single letters separated by spaces, or separated by
 * periods, it is assumed to be an abbreviation. The spaces are replaced by periods,
 * and the whole {@code String} is terminated with a period.
 * <p>
 * In the (@code ValidResult}, the machine result, the common result, and the
 * particular result are all the same, as described above.
 *
 * @param input			the input {@code String}
 * @return				the name represented
 * @throws ValidationException if input null or empty
 */
public static ValidResult<String> validateName(CharSequence input)
												throws ValidationException
{
	return validateName(input, true);
}

/**
 * Method to standardize a name. Names may occur as the names of many different
 * entities, of people, places, organizations, etc.
 * <p>
 * Any series of white space characters is replaced by a single actual space
 * character. All letters are converted to lower case, except the initial character,
 * and any that follow white space, other dashes or punctuation, which are converted
 * to title case. </p>
 * <p>
 * If the "name" consists of single letters separated by spaces, or separated by
 * periods, it is assumed to be an abbreviation. If the argument
 * {@code abbreviation} is
 * <b>true</b>, the spaces are replaced by periods, and the whole {@code String} is
 * terminated with a period.
 * <p>
 * In the (@code ValidResult}, the machine result, the common result, and the
 * particular result are all the same, as described above.
 *
 * @param input			the input {@code String}
 * @param abbreviation	true if abbreviations are to be processed
 * @return				the name represented
 * @throws ValidationException if input null or empty
 */
public static ValidResult<String> validateName(CharSequence input,
					boolean abbreviation) throws ValidationException
{
	Assembler bb = Assembler.ensureContent(input, "name", WS_LEAVE);
	if( !Character.isAlphabetic(bb.charAt(0)) &&
			!Character.isIdeographic(bb.charAt(0)) )
					throw new ValidationException(
						 bb.toString() + " does not start like a name.");
	bb.editWhitespace(true);

	/* Abbreviations can be entered by separating each single letter by a space
	   or by a period. Spaces will be replace by periods, and the whole followed
	   by a period. */
	abbreviation &= bb.length() > 0;
	for( int i = 1; i < bb.length(); i += 2 )
		abbreviation &= (bb.charAt(i) == CHAR_SPACE || bb.charAt(i) == CHAR_DOT);
	if( abbreviation )
	{
		for( int i = 0; i < bb.length(); ++i )
			if( bb.charAt(i) == CHAR_SPACE ) bb.setCharAt(i, CHAR_DOT);
		if( bb.charAt(bb.length() - 1) != CHAR_DOT ) bb.append(CHAR_DOT);
	}

	ValidResult<String> result = new ValidResult<>();
	/* The standardized representation value is returned. */
	result.common = result.particular = result.machine = bb.toString();
	return result;
}

/**
 * A percentage value is validated by seeing if it can be parsed as a double. If so,
 * it is returned as a (signed) double value.
 * <p>
 * A percentage value may end with a percentage symbol, which causes the value
 * represented by the input to be divided by 100.0. The input {@code CharSequence}
 * may also contain grouping characters. These are ignored. </p>
 * <p>
 * In the (@code ValidResult}, the machine result is an instance of type
 * {@code Double}, containing the validated value. The common value is just a
 * {@code String} with the given number of significant digits, the value multiplied
 * by 100, and terminated with a "%" symbol. The particular {@code String} is either
 * the same as the common {@code String} if the input included a "%" symbol, or is
 * the {@code String} representing the value of the {@code Double} as obtained by
 * the {@code toString} method.
 *
 * @param input		{@code CharSequence} representing a percentage value
 * @param minimum	minimum acceptable value
 * @param maximum	maximum acceptable value (ignored if less/equal to minimum)
 * @param digits	number of significant digits in resulting percentage, or zero
 *					if not to be imposed
 * @return			result with value and standardized String
 * @throws ValidationException if the percentage value is invalid.
 */
public static ValidResult<Double> validatePercentage(CharSequence input,
		double minimum, double maximum, int digits)
		throws ValidationException
{
	Assembler bb = Assembler.ensureContent(input, "percentage value", WS_REMOVE);
	/* Remove and note any percentage sign. */
	boolean percent_present;
	if( (percent_present = bb.charAtEnd() == CHAR_PERCENTAGE) ) bb.deleteCharAtEnd();
	/* Remove any extraneous separators in the number. */
	Assembler[] edit = bb.editNumber(XTRA_NUMBER_SYMBOLS, true, true);
	/* Check that result can be passed as a double. */
	double value;
	try
	{
		value = Double.parseDouble(edit[0].toString());
	} catch( NumberFormatException ex )
	{
		throw new ValidationException("Percentage \"" + input +
				"\" not understood", ex);
	}
	if( percent_present ) value /= 100.0;
	if( abs(value) < MINIMUM_PERCENTAGE_VALUE ) value = 0.0;
	value = verifyDouble(value, minimum, maximum, digits);
	ValidResult<Double> result = new ValidResult<>();
	result.machine = value;
	/* This format only occurs in this method. */
	String format = "%." + (digits == 0 ?
					PERCENTAGE_DEFAULT_DIGITS : digits) + "g%" + CHAR_PERCENTAGE;
	result.common = String.format(format, value * PERCENTAGE_MULTIPLIER);
	result.particular = percent_present ? result.common : result.machine.toString();
	return result;
}

/**
 * A phone number value is validated by seeing if it can be parsed according to
 * the given locale. Otherwise an exception is thrown.
 * <p>
 * For this time, only default {@code Locale}s specifying countries that are
 * included in the North American Numbering Plan
 * (<a href="https://en.wikipedia.org/wiki/North_American_Numbering_Plan"
 * target="_blank">NANP</a> can be validated and standardized. </p>
 * <p>
 * In the (@code ValidResult}, the machine result is an instance of type
 * {@code String}, containing the validated phone number, consisting of a "+" sign,
 * a country code, and the remaining digits of the telephone number. The common
 * value is the same {@code String} but with the area code in parentheses, and a
 * hyphen (dash) between the office number and the final digits. The particular form
 * will only contain a country code if requested or the was one present in the
 * original input, will have punctuation within the remainder of the number only if
 * punctuation was present in the original input.
 *
 * @param input		{@code CharSequence} representing a phone number
 * @param countryCodeRequired if a country code is required in the standardized
 *					form, otherwise a country code will be included only if present
 *					in the input number (signaled by an initial + sign).
 * @return			result with value and standardized {@code String}
 * @throws ValidationException if the phone number value is invalid.
 */
public static ValidResult<String> validatePhone(CharSequence input,
		boolean countryCodeRequired) throws ValidationException
{
	return validatePhone(input, countryCodeRequired, null);
}

/**
 * A phone number value is validated by seeing if it can be parsed according to
 * the given locale. Otherwise an exception is thrown.
 * <p>
 * For this time, only {@code Locale}s specifying countries that are included in
 * the North American Numbering Plan
 * (<a href="https://en.wikipedia.org/wiki/North_American_Numbering_Plan"
 * target="_blank">NANP</a> can be validated and standardized. </p>
 * <p>
 * In the (@code ValidResult}, the machine result is an instance of type
 * {@code String}, containing the validated phone number, consisting of a "+" sign,
 * a country code, and the remaining digits of the telephone number. The common
 * value is the same {@code String} but with the area code in parentheses, and a
 * hyphen (dash) between the office number and the final digits. The particular form
 * will only contain a country code if requested or the was one present in the
 * original input, will have punctuation within the remainder of the number only if
 * punctuation was present in the original input.
 *
 * @param input		{@code CharSequence} representing a phone number
 * @param countryCodeRequired if a country code is required in the standardized
 *					form, otherwise a country code will be included only if present
 *					in the input number (signaled by an initial + sign).
 * @param locale	locale to indicate the form of the telephone number, or null
 *					(in which case the default locale is assumed).
 * @return			result as a ValidResult containing representations of the valid
 *					phone number
 * @throws ValidationException if the phone number value is invalid.
 */
public static ValidResult<String> validatePhone(CharSequence input,
		boolean countryCodeRequired, Locale locale)
		throws ValidationException
{
	/* Is there any phone number to validate and standardize? */
	Assembler bb = Assembler.ensureContent(input, "telephone number", WS_LEAVE);

	if( isNull(locale) ) locale = Locale.getDefault();
	String country = locale.getCountry();
	if( isNull(country) ) country = "021";

	/* Do the basic analysis, find the digits and any acceptable punctuation,
	   check for a country code, and prepare a list of what is not acceptable. */
	bb.trim();
	boolean countryCodeFlagPresent;
	if( (countryCodeFlagPresent = bb.indexOf(COUNTRY_CODE_FLAG) == 0) )
		bb.delete(0, COUNTRY_CODE_FLAG.length());

	/* North American Numbering Plan (NANP). */
	if( NANP.contains(country) )
	{
		String countryCode = NORTH_AMERICA_COUNTRY_CODE;

		if( countryCodeFlagPresent )
		{
			if( bb.length() == 0 || bb.charAt(0) != countryCode.charAt(1) )
				throw new ValidationException("Country code not found");
			bb.deleteCharAt(0);
			countryCodeRequired = true;
		}

		Assembler[] edit = bb.editNumber(STR_PAREN_OPEN + STR_PAREN_CLOSE + STR_DASH +
			STR_VIRGULE + STR_DOT + STR_SPACE, false, false);

		int requiredDigits = 10;
		int digitCount = edit[0].length();

		if( digitCount != requiredDigits )
			throw new ValidationException(digitCount + " digit(s) given but " +
					requiredDigits + " needed");
		/* Determine terminator for the country code. */
		Assembler countryCodeTerm = new Assembler();
		char c;
		c = edit[1].contains(CHAR_SPACE, CHAR_SPACE);
		countryCodeTerm.append(c);

		/* Get the area code and determine the punctuation for it. */
		Assembler areaCode = (Assembler) edit[0].subSequence(0, 3);
		Assembler areaCodeTerm = new Assembler();

		if( edit[1].contains(CHAR_PAREN_OPEN, CHAR_PAREN_CLOSE) != '\u0000' )
		{
			areaCode.append(CHAR_PAREN_CLOSE).insert(0, CHAR_PAREN_OPEN);
			c = edit[1].contains(CHAR_SPACE, CHAR_SPACE);
		} else
			c = edit[1].contains(CHAR_VIRGULE, CHAR_DASH, CHAR_DOT, CHAR_SPACE);

		areaCodeTerm.append(c);
		/* Get the office code and determine the punctuation for it.*/
		Assembler officeCode = (Assembler) edit[0].subSequence(3, 6);
		String officeCodeTerm = edit[1].contains(STR_DASH) ? STR_DASH :
				edit[1].contains(".") ? "." :
						edit[1].contains(STR_SPACE) ? STR_SPACE : "";

		/* Create the ValidResult object to return as the result. */
		ValidResult<String> result = new ValidResult<>();
		Assembler machine = new Assembler(countryCode).append(edit[0]);
		result.machine = machine.toString();
		result.particular = (countryCodeRequired ?
				countryCode + countryCodeTerm : "") + areaCode + areaCodeTerm +
				officeCode + officeCodeTerm +
							edit[0].subSequence(6, edit[0].length()).toString();
		result.common = machine.decorate(STR_PAREN_OPEN + STR_PAREN_CLOSE +
				STR_DASH, NANP_START_AREA, NANP_START_OFFICE,
													NANP_START_PHONE).toString();
		return result;
	}
	throw new ValidationException("Locale " +
										locale.toString() + " not implemented");
}

/**
 * Validate a Social Security Number (SSN). The input should contain only digits,
 * dashes, and spaces. The SocialSecurityNumber is valid if it contains the
 * correct number of digits.
 * In the (@code ValidResult}, the machine result is an instance of type
 * {@code String}, containing just the digits of the SSN. The common form contains
 * the same digits, but with hyphens (dashes) in the usual places. The particular
 * form will be the same as the common form if the original input contained (a)
 * dash(es), otherwise it will be the same as the machine form.
 *
 * @param input		the input {@code String}
 * @return			the value/standardized SocialSecurityNumber
 * @throws ValidationException if the input {@code String} contains anything but the
 *					permitted characters, or does not contain the correct number of
 *					digits
 */
public static ValidResult<String> validateSSN(CharSequence input) throws
		ValidationException
{
	Assembler bb = Assembler.ensureContent(input, "Social Security number", WS_REMOVE);
	Assembler[] edit = bb.editNumber(XTRA_SSN_OR_ISBN_SYMBOLS, false, false);

	if( edit[0].length() > SNN_TOTAL_LENGTH )
		throw new ValidationException("SSN \"" + input +
				"\" contains too many digits");
	if( edit[0].length() < SNN_TOTAL_LENGTH )
		throw new ValidationException("SSN \"" + input +
				"\" contains too few digits");
	ValidResult<String> result = new ValidResult<>();
	result.machine = result.particular = edit[0].toString();
	result.common = edit[0].decorate(STR_DASH,
								SNN_FIRST_FIELD, SNN_SECOND_FIELD).toString();
	if( edit[1].contains(CHAR_DASH) ) result.particular = result.common;
	return result;
}

/**
 * Verify a {@code BigDecimal} value, checking the number of decimals, the minimum
 * and maximum value.
 *
 * @param value		{@code BigDecimal} value
 * @param minimum	minimum acceptable value, or null if there is no minimum
 * @param maximum	maximum acceptable value, or null if there is no maximum
 * @param decimals	maximum number of decimal places in the currency amount, or
 *					zero if	not to be checked and imposed
 * @return	the value rounded if necessary
 * @throws NullPointerException if the given value is null
 * @throws ValidationException is the value is invalid
 */
public static BigDecimal verifyBigDecimal(BigDecimal value, BigDecimal minimum,
		BigDecimal maximum, int decimals) throws ValidationException
{
	/* If requested, do not permit values with more than the specified number of
	   decimal places. */
	BigDecimal roundedValue = value;
	int scale = value.scale();			// perhaps a NullPointerException
	if( decimals != 0 )
	{
		if( scale > decimals ) throw new ValidationException(
					"Decimal digits (" + scale + ")" + " more than " + decimals);
		/* Round to the specified number of decimal places. */
		roundedValue = value.setScale(decimals, RoundingMode.HALF_UP);
	}
	int comparison = compareInRangeBigDecimal(roundedValue, minimum, maximum);
	if( comparison > 0 )
		throw new ValidationException("Value \"" + roundedValue +
				"\" more than " + maximum);
	else if( comparison < 0 )
		throw new ValidationException("Value \"" + roundedValue +
				"\" less than " + minimum);
	return roundedValue;
}

/**
 * Verify a local date. The returned value is a LocalDate object, if verified,
 * otherwise an exception is thrown.
 *
 * @param value		the given local date value
 * @param minimum	the earliest date permitted, or null if there is none
 * @param maximum	the latest date permitted, or null if there is none
 * @return			the LocalDate represented
 * @throws NullPointerException if the value is null
 * @throws ValidationException if the value is not in the desired range
 */
public static LocalDate verifyDate(LocalDate value, LocalDate minimum,
		LocalDate maximum) throws ValidationException
{
	int comparison = compareInRangeDate(value, minimum, maximum);
	if( comparison == 0 ) return value;

	String compare;
	String limit;
	if( comparison > 0 )
	{
		limit = FORMATS[DEFAULT_DATE_FORMAT].format(maximum);
		compare = "after";
	} else
	{
		limit = FORMATS[DEFAULT_DATE_FORMAT].format(minimum);
		compare = "before";
	}
	String valueString = FORMATS[DEFAULT_DATE_FORMAT].format(value);
	throw new ValidationException("Date \"" + valueString + "\" " +
			compare + " " + limit);
}

/**
 * A double value is verified as being of an acceptable form, and within the given
 * range. The returned value is adjusted to the given number of significant digits.
 *
 * @param value		given double value;
 * @param minimum	minimum acceptable value
 * @param maximum	maximum acceptable value (ignored if equal to minimum)
 * @param digits	number of significant digits in resulting percentage, or zero
 *					if not to be imposed
 * @return			the given value if verified
 * @throws ValidationException if the currency value is invalid.
 */
public static double verifyDouble(double value, double minimum, double maximum,
		int digits) throws ValidationException
{
	if( digits != 0 && value != 0.0 )
	{
		int log = (int) floor(log10(value) + 1.0);
		double power = pow(10, digits - log);
		value = rint(value * power) / power;
	}
	int comparison = compareInRangeDouble(value, minimum, maximum);
	if( comparison > 0 )
		throw new ValidationException("Value \"" + value + STR_QUOTE +
				" higher than " + maximum);
	else if( comparison < 0 )
		throw new ValidationException("Value \"" + value + STR_QUOTE +
				" lower than " + minimum);
	return value;
}

/**
 * Given an integer value, verify that it represents an integer in the given
 * range. Otherwise, throw an exception.
 *
 * @param value		integer value
 * @param minimum	minimum acceptable value
 * @param maximum	maximum acceptable value (ignored if less/equal to minimum)
 * @return			the given value if verified
 * @throws ValidationException if value does not fall in the given range.
 */
public static int verifyInteger(int value, int minimum, int maximum) throws
		ValidationException
{
	int comparison = compareInRangeInteger(value, minimum, maximum);
	if( comparison > 0 )
		throw new ValidationException("Value \"" + value + "\"" +
				" higher than " + maximum);
	else if( comparison < 0 )
		throw new ValidationException("Value \"" + value + "\"" +
				" lower than " + minimum);
	return value;
}

/**
 * Private class to replace {@code String} and {@code StringBuilder}, with
 * additional needed methods.
 */
private static class Assembler implements CharSequence, Appendable, Cloneable
{
/**
 * Initial spare space
 */
private static final int SPARE = 16;

/**
 * Array holding the characters.
 */
private char[] array;

/**
 * Number of actually used positions in the array.
 */
private int length;

/**
 * Constructor. initialize an empty instance.
 */
Assembler()
{
	this("");
}

/**
 * Constructor: initialize a Buffer instance.
 *
 * @param sequence		any CharSequence
 */
Assembler(CharSequence sequence)
{
	this(sequence, 0, sequence.length());
}

/**
 * Constructor: initialize a Buffer instance.
 *
 * @param sequence		any CharSequence
 * @param start			the index of the first character to use
 * @param end			the index beyond the last character to use
 */
Assembler(CharSequence sequence, int start, int end)
{
	end = min(end, sequence.length());
	if( start < 0 || start > end )
		throw new IndexOutOfBoundsException("Start=" + start + ", end=" + end);
	length = end - start;
	array = new char[length + SPARE];
	for( int i = 0; i != length; ++i )
		array[i] = sequence.charAt(i + start);
}

/**
 * Appends the specified character to this {@code Assembler}.
 *
 * @param c		the character to append
 * @return		a reference to this {@code Appendable}
 */
@Override
public Assembler append(char c)
{
	if( c != '\u0000')
	{
		checkCapacity(1);
		array[length++] = c;
	}
	return this;
}

/**
 * Appends the specified character sequence to this {@code Assembler}.
 * <p>
 * Depending on which class implements the character {@code sequence}, the
 * entire sequence may not be appended. For instance, if {@code sequence} is
 * a {@link java.nio.CharBuffer} then the subsequence to append is defined by the
 * buffer's position and limit.
 *
 * @param sequence	the character sequence to append.
 *
 * @return		reference to this {@code Appendable}
 */
@Override
 public Assembler append(CharSequence sequence)
 {
	 return insert(length, sequence);
 }

/**
 * Appends a subsequence of the specified character sequence to this
 * {@code Assembler}.
 * <p>
 * An invocation of this method of the form {@code append(sequence, start,
 * end)} is identical to:
 * <pre>append(sequence.subSequence(start, end))</pre>
 *
 * @param sequence	the character sequence from which a subsequence will be
 *					appended.
 * @param start		the index of the first character in the subsequence
 * @param end		the index of the character following the last character in
 *					the subsequence
 * @return			reference to this {@code Appendable}
 * @throws IndexOutOfBoundsException if {@code start} or {@code end}
 *					are negative, {@code start} is greater than
 *					{@code end}, or {@code end} is greater than
 *					{@code sequence.length()}
 */
@Override
public Assembler append(CharSequence sequence, int start, int end)
{
	end = min(end, length);
	if( start < 0 || start > end )
		throw new IndexOutOfBoundsException("Start=" + start + ", end=" + end);
	int insertion = end - start;
	checkCapacity(insertion);
	for( int i = 0; i != insertion; ++i ) array[length + i] = sequence.charAt(i);
	length += insertion;
	return this;
}

/**
 * Returns the {@code char} value at the specified index. An index ranges from
 * zero to (@codelength() - 1}. The first {@code char} value of the sequence
 * is at index zero, the next at index one, and so on, as for array indexing.
 * <p>
 * If the {@code char} value specified by the index is a
 * <a href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the
 * surrogate value is returned.
 *
 * @param index		the index of the {@code char} value to be returned
 * @return			the specified {@code char} value
 * @throws IndexOutOfBoundsException if the {@code index} argument is negative
 *					or not less than {@code length()}
 */
@Override
public char charAt(int index)
{
	if( index < 0 || index >= length )
		throw new IndexOutOfBoundsException("index=" + index);
	return array[index];
}

/**
 * Returns the {@code char} value at the specified index. An index ranges from
 * zero to (@codelength() - 1}. The first {@code char} value of the sequence
 * is at index zero, the next at index one, and so on, as for array indexing.
 * <p>
 * If the {@code char} value specified by the index is a
 * <a href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the
 * surrogate value is returned.
 *
 * @return			the last {@code char} value in the Assembler
 * @throws IndexOutOfBoundsException if the (@codeindex} argument is negative or
 *					not less than (@codelength()}
 */
public char charAtEnd()
{
	if( length == 0 )
		throw new IndexOutOfBoundsException("Buffer is empty");
	return array[length - 1];
}

/**
 * Returns true if and only if this {@code Assembler} contains the specified
 * character.
 *
 * @param c			the character to find
 * @return			true if this {@code Assembler} contains the character,
 *					false otherwise
 */
public boolean contains(char c)
{
	return indexOf(c) >= 0;
}

/**
 * Returns true if and only if this {@code Assembler} contains the one of the given
 * characters.
 *
 * @param chars		the array of characters, to find any one of them
 * @return			the first character to match, or a null character if none found
 */
public char contains(char... chars)
{
	for(char c : chars ) if( contains(c) ) return c;
	return '\u0000';

}
/**
 * Returns true if and only if this {@code Assembler} contains the specified
 * sequence.
 *
 * @param sequence	the sequence to find
 * @return			true if this {@code Assembler} contains the sequence,
 *					false otherwise
 */
public boolean contains(CharSequence sequence)
{
	return indexOf(sequence) >= 0;
}

/**
 * Insert characters into a {@code Assembler}. This usually starts with an
 * "undecorated" form, and then inserts characters from the {@code insertions}
 * sequence, in order, into the places indicated. The insertion takes place
 * preceding the numbered (from zero) character. If the insertions string is
 * exhausted, insertion continues starting again at the beginning, until all the
 * places have been assigned. No insertion is done beyond the end of the (@code
 * Assembler}.
 *
 * @param insertions	sequence of characters to insert
 * @param places		the places at which to insert them
 * @return				this object
 */
public Assembler decorate(CharSequence insertions, Integer... places)
{
	/* Next character from insertions to insert. */
	int next = 0;
	/* Count of how many characters have been inserted. */
	int count = 0;
	/* Where to do the insertion. */
	for( int place : places )
	{
		int position = place + count;
		if( position >= length ) break;
		insert(position, insertions.charAt(next));
		++count;
		/* Loop through the insertions. */
		next = (next + 1) % insertions.length();
	}
	return this;
}

/**
 * Removes the characters in a substring of this {@code Assembler}. The
 * substring begins at the specified start and extends to the character at
 * index end - 1 or to the end of the {@code Assembler} if no such character
 * exists. If start is equal to end, no changes are made.
 *
 * @param start		the beginning index, inclusive
 * @param end		ending index, exclusive
 * @return			this object
 * @throws IndexOutOfBoundsException if start is negative, greater than
 *					length(), or greater than end
 */
public Assembler delete(int start, int end)
{
	end = min(end, length);
	if( start < 0 || start > end )
		throw new IndexOutOfBoundsException("Start=" + start + ", end=" + end);
	System.arraycopy(array, end, array, start, length - end);
	length -= end - start;
	return this;
}

/**
 * Removes the char at the specified position in this{@code Assembler}. This
 * {@code Assembler} is shortened by one char.
 * <p>
 * Note: If the character at the given index is a supplementary character, this
 * method does not remove the entire character. If correct handling of supplementary
 * characters is required, determine the number of chars to remove by calling
 * Character.charCount(thisSequence.codePointAt(index)), where thisSequence is this
 * sequence.
 *
 * @param index		index of char to remove
 * @return			this object
 * @throws IndexOutOfBoundsException if the index is negative or greater than or
 *					equal to length()
 */
public Assembler deleteCharAt(int index)
{
	if( index < 0 || index >= length )
		throw new IndexOutOfBoundsException("index=" + index);
	return delete(index, index + 1);
}

/**
 * Removes the char at the last position in the {@code Assembler}. This
 * {@code Assembler} is shortened by one char.
 * <p>
 * Note: If the character at the given index is a supplementary character, this
 * method does not remove the entire character. If correct handling of supplementary
 * characters is required, determine the number of chars to remove by calling
 * Character.charCount(thisSequence.codePointAt(index)), where thisSequence is this
 * sequence.
 *
 * @return			this object
 * @throws IndexOutOfBoundsException if the the {@code Assembler} is empty
 */
public Assembler deleteCharAtEnd()
{
	if( length == 0 )
		throw new IndexOutOfBoundsException("Buffer is empty");
	--length;
	return this;
}

/**
 * Check that there is input of the needed kind. Null objects are rejected, and
 * if the object is a CharSequence, it will be also rejected if it is empty.
 *
 * @param input				input object
 * @param message			identifier of the expected nature of the object
 * @param removeWhitespace	remove whitespace from the input object
 * @return					a modified Assembler
 * @throws ValidationException if the input is rejected
 */
public static Assembler ensureContent(
		CharSequence input, String message, boolean removeWhitespace)
													throws ValidationException
{
	if( nonNull(input) )
	{
		/* If the object is not null, more checking can be done. */
		Assembler result = new Assembler(input);
		/* If edit is requested, edit out all whiteapace (title processing
		   as requested. */
		if( removeWhitespace ) result.editWhitespace(false);
		/* Only if there is content remaining, return a result. */
		if( !result.isEmpty() ) return result;
	}
	/* The item is null, or if it is a CharSequence, there is no content after
	   further checking. */
	throw new ValidationException("No " + message + " present");
}

/**
 * Return the index of the first occurrence of the given character in the
 * {@code Assembler}. Return a -1 if there is no such character.
 *
 * @param c		given character
 * @return		index of the first occurrence of that character, else -1
 */
public int indexOf(char c)
{
	int result = -1;
	for( int i = 0;  result < 0 && i != length; ++i )
		if( array[i] == c ) result = i;
	return result;
}

/**
 * Returns the index in {@code Assembler} of first occurrence of the specified
 * sequence, otherwise -1.
 *
 * @param sequence	the sequence to find
 * @return			the index in this {@code Assembler} if it contains the
 *					sequence, else -1
 */
public int indexOf(CharSequence sequence)
{
	int result = -1;
	int i = 0;
	if( nonNull(sequence) && sequence.length() != 0 )
		while( result < 0 && i != length )
		{
			if( array[i] == sequence.charAt(0) )
			{
				/* Have found the start of a possible match. */
				int j = 1;
				while( i + j != length && j != sequence.length() )
				{
					if( array[i + j] != sequence.charAt(j) ) break;
				}
				if( j == sequence.length() ) result = i;
			}
			++i;
		}
	return result;
}

/**
 * Inserts the char argument into this {@code Assembler}.
 *
 * @param index	the index position at which to insert the character
 * @param c		the character to insert
 * @return		this object
 * @throws IndexOutOfBoundsException if the index is negative or greater than
 *				length()
 */
public Assembler insert(int index, char c)
{
	if( index < 0 || index > length )
		throw new IndexOutOfBoundsException("index=" + index);
	if( c != '\u0000')
	{
		checkCapacity(1);
		System.arraycopy(array, index, array, index + 1, length - index);
		array[index] = c;
		++length;
	}
	return this;
}

/**
 * Inserts the specified CharSequence into this {@code Assembler}.
 * <p>
 * The characters of the CharSequence argument are inserted, in order, into this
 * {@code Assembler} at the indicated offset, moving up any characters originally
 * above that position and increasing the length of this {@code Assembler} by the
 * length of the argument {@code sequence}. If the {@code sequence} is
 * null or empty, no change is made.
 *
 * @param index		the index at which the insertion is started
 * @param sequence	the sequence to be inserted
 * @return			this object
 * @throws IndexOutOfBoundsException if the index is negative or greater than
 *					length()
 */
public Assembler insert(int index, CharSequence sequence)
{
	if( index < 0 || index > length )
		throw new IndexOutOfBoundsException("index=" + index);
	if( nonNull(sequence) || sequence.length() != 0 )
	{
		int insertion = sequence.length();
		checkCapacity(insertion);
		System.arraycopy(array, index, array, index + insertion, length - index);
		for( int i = 0; i != insertion; ++i ) array[index + i] = sequence.charAt(i);
		length += insertion;
	}
	return this;
}

/**
 * If the {@code Assembler} is empty, or contains only white space or similar,
 * then return true, otherwise false.
 *
 * @return		true if empty, otherwise false
 */
public boolean isEmpty()
{
	if( length != 0 )
	{
		for( int i = 0; i != length; ++i )
		{
			char c = array[i];
			int type = Character.getType(c);
			if( !(type == Character.CONTROL ||
				type == Character.SPACE_SEPARATOR ||
				type == Character.FORMAT || Character.isWhitespace(c)) )
					return false;
		}
	}
	return true;
}

/**
 * Returns the length of this character sequence. The length is the number of
 * 16-bit {@code char}s in the sequence.
 *
 * @return	the number of {@code char}s in this sequence
 */
@Override
public int length()
{
	return length;
}

/**
 * Replace method for {@code Assembler}. All occurrences of the
 * {@code target} are replaced with the {@code replacement}.
 *
 * @param target		the char to replace
 * @param replacement	the char to use as the replacement
 * @return				this object, with the replacements made
 */
public Assembler replace(char target, char replacement)
{
	if( replacement != '\u0000')
	{
		for( int i = 0; i != length; ++i )
			if( array[i] == target ) array[i] = replacement;
	}
	return this;
}

/**
 * Replace method for {@code Assembler}. If {@code target} is null or empty,
 * no replacements are made.  If {@code replacement} is null or empty, the
 * target matches will be deleted. Otherwise, all occurrences of the
 * {@code target} are replaced with the {@code replacement}.
 * The {@code replacement} is not rescanned.
 *
 * @param target		the (sub)CharSequence to replace
 * @param replacement	the CharSequence to use as the replacement
 * @return				this object, with the replacements made
 */
public Assembler replace(CharSequence target, CharSequence replacement)
{
	int i = 0;
	if( nonNull(target) && target.length() != 0 )
		while( i != length )
		{
			if( array[i] == target.charAt(0) )
			{
				/* Have found the start of a possible match. */
				int j = 1;
				while( i + j != length && j != target.length() )
				{
					if( array[i + j] != target.charAt(j) ) break;
				}
				if( j == target.length() )
				{
					/* Have found a match for the entire target. So delete the
					   matching  section, and insert the replacement. */
					delete(i, i + j);
					if( nonNull( replacement) )
					{
						insert(i, replacement);
						i += replacement.length() - 1;  // -1 because of increment
					}
				}
			}
			++i;
		}
	return this;
}

/**
 * Sets the {@code char} value at the specified index. An index ranges from
 * zero to {@code length() - 1}. The first {@code char} value of the
 * sequence is at index zero, the next at index one, and so on, as for array
 * indexing.
 * <p>
 * If the {@code char} value specified by the parameter {@code c} is a
 * <a href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the
 Assembler may end up containing an invalid Unicode sequence.
 *
 * @param index		the index of the {@code char} value to be set
 * @param c			the character to be set overwriting the previous content
 * @return			this object
 * @throws IndexOutOfBoundsException if the {@code index} argument is
 *					negative or not less than {@code length()}
 */
public Assembler setCharAt(int index, char c)
{
	if( index < 0 || index >= length )
		throw new IndexOutOfBoundsException("index=" + index);
	if( c != '\u0000') array[index] = c;
	return this;
}

/**
 * Splits this {@code Assembler} around matches of the given {@code sequence}.
 * <p>
 * Trailing empty strings are not included in the resulting array, if the match
 * ends exactly at the end of this buffer.
 * <p>
 * The string "boo:and:foo", for example, yields the following results with
 * these splitting sequences:
 * <table style="text-align: center;"><caption>Splitting</caption>
 * <tr><th>Sequence</th><th>Result</th></tr>
 * <tr><td>:</td><td>{ "boo", "and", "foo" }</td></tr>
 * <tr><td>o</td><td>{ "b", "", ":and:f" }</td></tr>
 * </table>
 *
 * @param sequence		the delimiting sequence
 * @return				an array of {@code Assembler}s computed by splitting
 *						this {@code Assembler} around matches of the given
 *						sequence
 */
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public Assembler[] split(CharSequence sequence)
{
	Assembler toSplit = this.clone();
	toSplit.replace(sequence, "\uFFFF");
	List<Assembler> pieces = new LinkedList<>();
	while( true )
	{
		int j = toSplit.indexOf('\uFFFF');
		if( j < 0 ) break;
		pieces.add(new Assembler(toSplit, 0, j));
		toSplit.delete(0, j + 1);
	}
	if( toSplit.length != 0 ) pieces.add(toSplit);
	return pieces.toArray(new Assembler[pieces.size()]);
}

/**
 * Returns a {@code CharSequence} that is a subsequence of this sequence. The
 * subsequence starts with the {@code char} value at the specified index and ends
 * with the {@code char} value at index (@codeend - 1}, or the last character in the
 * sequence, whichever is shorter. The length (in {@code char}s) of the returned
 * sequence is (@codeend - start}, so if (@codestart == end} then an empty sequence
 * is returned.
 *
 * @param start	the start index, inclusive
 * @param end	the end index, exclusive
 * @return		the specified subsequence
 * @throws IndexOutOfBoundsException if (@codestart} or (@codeend} are
 *				negative, or if (@codestart} is greater than (@codeend}
 */
@Override
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public CharSequence subSequence(int start, int end)
{
	return new Assembler(this, start, end);
}

/**
 * Returns a string containing the characters in this sequence in the same order
 * as this sequence. The length of the string will be the length of this
 * sequence.
 *
 * @return		a String consisting of exactly this sequence of characters
 */
@Override
public String toString()
{
	return new String(array, 0, length);
}

/**
 * Delete any whitespace characters at the beginning or end of the buffer.
 *
 * @return		this object
 */
public Assembler trim()
{
	/* Continue deleting characters so long as the character at the beginning of
	   the buffer is whitespace. */
	while( length != 0 )
	{
		char c = array[0];
		int type = Character.getType(c);
		if( !(type == Character.CONTROL ||
			type == Character.SPACE_SEPARATOR ||
			type == Character.FORMAT || Character.isWhitespace(c)) ) break;
		deleteCharAt(0);
	}
	/* Continue deleting characters so long as the character at the end of
	   the buffer is whitespace. */
	while( length != 0 )
	{
		char c = array[length - 1];
		int type = Character.getType(c);
		if( !(type == Character.CONTROL ||
			type == Character.SPACE_SEPARATOR ||
			type == Character.FORMAT || Character.isWhitespace(c)) ) break;
		deleteCharAtEnd();
	}
	return this;
}
/**
 * Creates and returns a copy of this object.For any {@code Assembler} x, the
 * expression:
 * <pre>x.clone() != x</pre>
 * will be true, and that the expression:
 * <pre>x.clone().getClass() == x.getClass()</pre>
 * will also be true. It is also the case that:
 * <pre>x.clone().equals(x)</pre>
 * is true.
 *
 * @return		a clone of the {@code Assembler}
 */
@Override
@SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject",
	"CloneDeclaresCloneNotSupported" })
protected Assembler clone()
{
	try
	{
		Assembler x = (Assembler) super.clone();
		x.array = x.array.clone();
		return x;
	} catch( CloneNotSupportedException ex )
	{
		throw new AssertionError(ex.getMessage(), ex);
	}
}

/**
 * Increases the size of the array if there is not enough space.
 *
 * @param needed	the number of characters needed
 */
private void checkCapacity(int needed)
{
	int capacity = array.length;
	if( length + needed <= capacity ) return;
	while( length + needed > capacity) capacity *= 2;
	char[] longer = new char[capacity];
	System.arraycopy(array, 0, longer, 0, array.length);
	array = longer;
}

/**
 * Edit this {@code Assembler}, extracting digits and other acceptable parts
 * of a number, including + and - if signed, and, if floating, the decimal
 * separator and any exponent indicator. Other symbols that are acceptable but
 * should not appear in the final number are returned as a second
 * {@code Assembler}. Any further characters cause an exception to be thrown.
 *
 * @param symbols	acceptable, but not part of the number
 * @param signed	signs (+/-) are permitted to appear in the number
 * @param floating	decimal point and exponent are permitted to appear
 * @return			two-element array of the edited CharSequence as a String, and
 *					theString containing the removed characters
 * @throws ValidationException in non-acceptable characters appear in the input
 */
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
private Assembler[] editNumber(String symbols, boolean signed, boolean floating)
														throws ValidationException
{
	Assembler bb = clone().trim();
	if( floating ) bb.replace(STR_EXPONENT.toLowerCase(), "\uFFFF").
									replace(STR_EXPONENT.toUpperCase(), "\uFFFF");
	Assembler errors = new Assembler();
	Assembler other = new Assembler();
	int i = 0;
	while( i != bb.length )
	{
		@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
		char c = bb.array[i];
		/* Decide what to do with the character. If it can be part of the number
		   sequence, add it to the acceptable sequence. Any other acceptable
		   character is recorded as "other," and anything left over get added to
		   errors, leading to an exception being thrown. */
		if( Character.isDigit(c) ||
			(floating && (c == CHAR_DECIMAL || c == '\uFFFF')) ||
			(signed && (c == CHAR_PLUS || c == CHAR_MINUS)) ) ++i;
		else
		{
			bb.deleteCharAt(i);
			if( symbols.indexOf(c) >= 0 ) other.append(c);
			else if( errors.indexOf(c) < 0 ) errors.append(c);
		}
	}
	/* Replace any exponentiation string. */
	if( floating ) bb.replace("\uFFFF", STR_EXPONENT);
	/* Report any errors. */
	if( errors.length() != 0 )
		throw new ValidationException("Character(s) \"" + errors.toString() +
							"\" may not appear within \"" + this.toString() + "\"");

	Assembler[] result = new Assembler[2];
	result[0] = bb;
	result[1] = other;
	return result;
}

/**
 * White space is removed from the {@code Assembler}, except if {@code title}
 * is set, in which case one space character is inserted for every run of white
 * space (except at the beginning and end of the {@code Assembler}, i.e., the
 * {@code Assembler} is trimmed) and characters are converted to lower case,
 * except if they follow white space, dashes, or other punctuation, in which case
 * they are converted to title case.
 *
 * @param title		do title processing
 * @return			this object, edited
 */
private Assembler editWhitespace(boolean title)
{
	char c;
	boolean previousWhite;
	/* If title is set, pretend the current character (the one at position -1) is
	   a white space character, so the the next character seen will be converted
	   to title case.
	 */
	boolean currentWhite = title;
	boolean makeNextTitleCase = false;
	/* Scan the current characters starting at the first. */
	int i = 0;
	while( i != length )
	{
		/* Transfer the state of the current character to that of the previous
		   character. If previousWhite is set, note to change the next non-white
		   character to title case. */
		previousWhite = currentWhite;
		makeNextTitleCase |= previousWhite;
		/* Get the type of the character at the current position indicated by the
		   index {@code i}.  Remember the character as the variable {@code c}. */
		int type = Character.getType(c = array[i]);
		/* If the current character is "white," remember that, and remove it. */
		if( currentWhite = (type == Character.CONTROL ||
				type == Character.SPACE_SEPARATOR ||
				type == Character.FORMAT || Character.isWhitespace(c)) )
			deleteCharAt(i);
		else
		{
			/* ... otherwise, if the previous character was "white", and the
			   current position is not the start of the character array, then
			   change the current character to title case (if possible) and place
			   it back in the array.
			 */
			if( title )
			{
				if( previousWhite && i != 0 ) insert(i++, CHAR_SPACE);
				array[i] = makeNextTitleCase ? Character.toTitleCase(c) :
													Character.toLowerCase(c);
				/* If the current character in dash or other punctuation, then
				   set (or otherwise reset) the {@code makeNextTitleCase}
				   variable. */
				makeNextTitleCase =
						Character.getType(c) == Character.DASH_PUNCTUATION ||
						Character.getType(c) == Character.OTHER_PUNCTUATION;
			}
			/* Advance to the next character. */
			++i;
		}
	}
	return this;
}
}
}
