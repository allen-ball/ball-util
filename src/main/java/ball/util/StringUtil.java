/*
 * $Id$
 *
 * Copyright 2010 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Arrays;
import java.util.regex.Pattern;

import static java.lang.Character.isWhitespace;

/**
 * {@link String} utility methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class StringUtil {
    private StringUtil() { }

    private static final Pattern BLANK = Pattern.compile("[\\p{Space}]*");

    /**
     * {@link #NIL} = {@value #NIL}
     */
    public static final String NIL = "";

    /**
     * {@link #SPACE} = {@value #SPACE}
     */
    public static final char SPACE = ' ';

    /**
     * Method to determine if the argument {@link CharSequence}
     * {@link #isNil(CharSequence)} or blank.
     *
     * @param   sequence        The  {@link CharSequence} to test.
     *
     * @return  {@code true} if the {@link CharSequence}
     *           {@link #isNil(CharSequence)} or blank; {@code false}
     *           otherwise.
     */
    public static boolean isBlank(CharSequence sequence) {
        return (isNil(sequence) || BLANK.matcher(sequence).matches());
    }

    /**
     * Method to determine if the argument {@link CharSequence} is
     * {@code null} or empty.
     *
     * @param   sequence        The {@link CharSequence} to test.
     *
     * @return  {@code true} if the {@link CharSequence} is {@code null} or
     *          equal to {@value NIL}; {@code false} otherwise.
     */
    public static boolean isNil(CharSequence sequence) {
        return (sequence == null || sequence.length() == 0);
    }

    /**
     * Method to fill the argument {@link CharSequence}.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String fill(CharSequence sequence,
                              int length, char character) {
        String string = null;

        if (sequence != null) {
            string =
                fill(new StringBuilder(sequence), length, character)
                .toString();
        }

        return string;
    }

    /**
     * Method to fill the argument {@link CharSequence}
     * with {@link #SPACE}s.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String fill(CharSequence sequence, int length) {
        return fill(sequence, length, SPACE);
    }

    /**
     * Method to fill the argument {@link StringBuffer}.
     *
     * @param   buffer          The {@link StringBuffer} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  The argument {@link StringBuffer} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer fill(StringBuffer buffer,
                                    int length, char character) {
        if (buffer != null) {
            for (;;) {
                if (buffer.length() < length) {
                    buffer.append(character);
                } else {
                    break;
                }

                if (buffer.length() < length) {
                    buffer.insert(0, character);
                } else {
                    break;
                }
            }
        }

        return buffer;
    }

    /**
     * Method to fill the argument {@link StringBuffer}
     * with {@link #SPACE}s.
     *
     * @param   buffer          The {@link StringBuffer} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  The argument {@link StringBuffer} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer fill(StringBuffer buffer, int length) {
        return fill(buffer, length, SPACE);
    }

    /**
     * Method to fill the argument {@link StringBuilder}.
     *
     * @param   buffer          The {@link StringBuilder} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  The argument {@link StringBuilder} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder fill(StringBuilder buffer,
                                      int length, char character) {
        if (buffer != null) {
            for (;;) {
                if (buffer.length() < length) {
                    buffer.append(character);
                } else {
                    break;
                }

                if (buffer.length() < length) {
                    buffer.insert(0, character);
                } else {
                    break;
                }
            }
        }

        return buffer;
    }

    /**
     * Method to fill the argument {@link StringBuilder}
     * with {@link #SPACE}s.
     *
     * @param   buffer          The {@link StringBuilder} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  The argument {@link StringBuilder} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder fill(StringBuilder buffer, int length) {
        return fill(buffer, length, SPACE);
    }

    /**
     * Method to left-fill the argument {@link CharSequence}.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String lfill(CharSequence sequence,
                               int length, char character) {
        String string = null;

        if (sequence != null) {
            string =
                lfill(new StringBuilder(sequence), length, character)
                .toString();
        }

        return string;
    }

    /**
     * Method to left-fill the argument {@link CharSequence} with
     * {@link #SPACE}s.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String lfill(CharSequence sequence, int length) {
        return lfill(sequence, length, SPACE);
    }

    /**
     * Method to left-fill the argument {@link StringBuffer}.
     *
     * @param   buffer          The {@link StringBuffer} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  The argument {@link StringBuffer} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer lfill(StringBuffer buffer,
                                     int length, char character) {
        if (buffer != null) {
            while (buffer.length() < length) {
                buffer.insert(0, character);
            }
        }

        return buffer;
    }

    /**
     * Method to left-fill the argument {@link StringBuffer} with
     * {@link #SPACE}s.
     *
     * @param   buffer          The {@link StringBuffer} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  The argument {@link StringBuffer} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer lfill(StringBuffer buffer, int length) {
        return lfill(buffer, length, SPACE);
    }

    /**
     * Method to left-fill the argument {@link StringBuilder}.
     *
     * @param   buffer          The {@link StringBuilder} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  The argument {@link StringBuilder} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder lfill(StringBuilder buffer,
                                      int length, char character) {
        if (buffer != null) {
            while (buffer.length() < length) {
                buffer.insert(0, character);
            }
        }

        return buffer;
    }

    /**
     * Method to left-fill the argument {@link StringBuilder} with
     * {@link #SPACE}s.
     *
     * @param   buffer          The {@link StringBuilder} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  The argument {@link StringBuilder} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder lfill(StringBuilder buffer, int length) {
        return lfill(buffer, length, SPACE);
    }

    /**
     * Method to right-fill the argument {@link CharSequence}.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String rfill(CharSequence sequence,
                               int length, char character) {
        String string = null;

        if (sequence != null) {
            string =
                rfill(new StringBuilder(sequence), length, character)
                .toString();
        }

        return string;
    }

    /**
     * Method to right-fill the argument {@link CharSequence} with
     * {@link #SPACE}s.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String rfill(CharSequence sequence, int length) {
        return rfill(sequence, length, SPACE);
    }

    /**
     * Method to right-fill the argument {@link StringBuffer}.
     *
     * @param   buffer          The {@link StringBuffer} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  The argument {@link StringBuffer} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer rfill(StringBuffer buffer,
                                     int length, char character) {
        if (buffer != null) {
            while (buffer.length() < length) {
                buffer.append(character);
            }
        }

        return buffer;
    }

    /**
     * Method to right-fill the argument {@link StringBuffer} with
     * {@link #SPACE}s.
     *
     * @param   buffer          The {@link StringBuffer} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  The argument {@link StringBuffer} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer rfill(StringBuffer buffer, int length) {
        return rfill(buffer, length, SPACE);
    }

    /**
     * Method to right-fill the argument {@link StringBuilder}.
     *
     * @param   buffer          The {@link StringBuilder} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  The argument {@link StringBuilder} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder rfill(StringBuilder buffer,
                                      int length, char character) {
        if (buffer != null) {
            while (buffer.length() < length) {
                buffer.append(character);
            }
        }

        return buffer;
    }

    /**
     * Method to right-fill the argument {@link StringBuilder} with
     * {@link #SPACE}s.
     *
     * @param   buffer          The {@link StringBuilder} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  The argument {@link StringBuilder} after filling
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder rfill(StringBuilder buffer, int length) {
        return rfill(buffer, length, SPACE);
    }

    /**
     * Method to left-trim whitespace from the argument
     * {@link CharSequence}.
     *
     * @param   sequence        The {@link CharSequence} to trim.
     *
     * @return  A {@link String} representing the trimmed
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String ltrim(CharSequence sequence) {
        String string = null;

        if (sequence != null) {
            string = ltrim(new StringBuilder(sequence)).toString();
        }

        return string;
    }

    /**
     * Method to left-trim whitespace from the argument
     * {@link StringBuffer}.
     *
     * @param   buffer          The {@link StringBuffer} to trim.
     *
     * @return  The argument {@link StringBuffer} after trimming
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer ltrim(StringBuffer buffer) {
        if (buffer != null) {
            while (buffer.length() > 0 && isWhitespace(buffer.charAt(0))) {
                buffer.deleteCharAt(0);
            }
        }

        return buffer;
    }

    /**
     * Method to left-trim whitespace from the argument
     * {@link StringBuilder}.
     *
     * @param   buffer          The {@link StringBuilder} to trim.
     *
     * @return  The argument {@link StringBuilder} after trimming
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder ltrim(StringBuilder buffer) {
        if (buffer != null) {
            while (buffer.length() > 0 && isWhitespace(buffer.charAt(0))) {
                buffer.deleteCharAt(0);
            }
        }

        return buffer;
    }

    /**
     * Method to right-trim whitespace from the argument
     * {@link CharSequence}.
     *
     * @param   sequence        The {@link CharSequence} to trim.
     *
     * @return  A {@link String} representing the trimmed
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String rtrim(CharSequence sequence) {
        String string = null;

        if (sequence != null) {
            string = rtrim(new StringBuilder(sequence)).toString();
        }

        return string;
    }

    /**
     * Method to right-trim whitespace from the argument
     * {@link StringBuffer}.
     *
     * @param   buffer          The {@link StringBuffer} to trim.
     *
     * @return  The argument {@link StringBuffer} after trimming
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer rtrim(StringBuffer buffer) {
        if (buffer != null) {
            while (buffer.length() > 0
                   && isWhitespace(buffer.charAt(buffer.length() - 1))) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
        }

        return buffer;
    }

    /**
     * Method to right-trim whitespace from the argument
     * {@link StringBuilder}.
     *
     * @param   buffer          The {@link StringBuilder} to trim.
     *
     * @return  The argument {@link StringBuilder} after trimming
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder rtrim(StringBuilder buffer) {
        if (buffer != null) {
            while (buffer.length() > 0
                   && isWhitespace(buffer.charAt(buffer.length() - 1))) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
        }

        return buffer;
    }

    /**
     * Method to trim whitespace from the argument {@link CharSequence}.
     *
     * @param   sequence        The {@link CharSequence} to trim.
     *
     * @return  A {@link String} representing the trimmed
     *          {@link CharSequence} or {@code null} if the argument is
     *          {@code null}.
     */
    public static String trim(CharSequence sequence) {
        String string = null;

        if (sequence != null) {
            string = trim(new StringBuilder(sequence)).toString();
        }

        return string;
    }

    /**
     * Method to trim whitespace from the argument {@link StringBuffer}.
     *
     * @param   buffer          The {@link StringBuffer} to trim.
     *
     * @return  The argument {@link StringBuffer} after trimming
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuffer trim(StringBuffer buffer) {
        return ltrim(rtrim(buffer));
    }

    /**
     * Method to trim whitespace from the argument {@link StringBuilder}.
     *
     * @param   buffer          The {@link StringBuilder} to trim.
     *
     * @return  The argument {@link StringBuilder} after trimming
     *          ({@code null} if the argument is {@code null}).
     */
    public static StringBuilder trim(StringBuilder buffer) {
        return ltrim(rtrim(buffer));
    }

    /**
     * Static method to concatenate the argument {@link Iterable} member
     * {@link Object}s' {@link String} representations to a {@link String}
     * with each member separated by an optional {@link CharSequence}.
     *
     * @param   separator       The separator {@link CharSequence}.
     * @param   iterable        The {@link Iterable}.
     *
     * @return  A {@link String}.
     */
    public static String concat(CharSequence separator, Iterable<?> iterable) {
        StringBuilder buffer = new StringBuilder();

        for (Object object : iterable) {
            if (buffer.length() > 0 && separator != null) {
                buffer.append(separator);
            }

            buffer.append(String.valueOf(object));
        }

        return buffer.toString();
    }

    /**
     * Static method to concatenate the argument {@link Iterable} member
     * {@link Object}s' {@link String} representations to a {@link String}.
     *
     * @param   iterable        The {@link Iterable}.
     *
     * @return  A {@link String}.
     */
    public static String concat(Iterable<?> iterable) {
        return concat(NIL, iterable);
    }

    /**
     * Static method to concatenate the argument {@link Object} array
     * elements' {@link String} representations to a {@link String} with
     * each member separated by an optional {@link CharSequence}.
     *
     * @param   separator       The separator {@link CharSequence}.
     * @param   objects         The {@link Object} array.
     *
     * @return  A {@link String}.
     */
    public static String concat(CharSequence separator, Object[] objects) {
        return concat(separator, Arrays.asList(objects));
    }

    /**
     * Static method to concatenate the argument {@link Object} array
     * elements' {@link String} representations to a {@link String}.
     *
     * @param   objects         The {@link Object} array.
     *
     * @return  A {@link String}.
     */
    public static String concat(Object[] objects) {
        return concat(NIL, objects);
    }
}
