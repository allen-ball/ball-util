/*
 * $Id: ISO8601DateFormat.java,v 1.2 2010-08-23 03:43:54 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static iprotium.util.TimeUnitEnum.HOURS;
import static iprotium.util.TimeUnitEnum.MINUTES;

/**
 * <a href="http://www.w3.org/TR/NOTE-datetime">ISO 8601</a>
 * {@link java.text.DateFormat} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class ISO8601DateFormat extends SimpleDateFormat {
    private static final long serialVersionUID = 3281262236122567400L;

    /**
     * {@link Pattern} that matches parsable {@link ISO8601DateFormat}.
     */
    public static final Pattern PATTERN =
        Pattern.compile("[\\p{Digit}]{4}(-[\\p{Digit}]{1,2}){2}"
                        + "(" + ("T[\\p{Digit}]{1,2}(:[\\p{Digit}]{2}){2}"
                                 + "([.][\\p{Digit}]{3})?"
                                 + "(Z|[-+][\\p{Digit}]{2}:[\\p{Digit}]{2})?")
                        + ")?");

    /**
     * {@link TimeZone} constant representing UTC.
     */
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME = "HH:mm:ss";
    private static final String FRACTION = "SSS";

    private static final String COLON = ":";
    private static final String DOT = ".";
    private static final String MINUS = "-";
    private static final String PLUS = "+";
    private static final String QUOTE = "'";
    private static final String T = "T";
    private static final String Z = "Z";

    private final SimpleDateFormat date = new SimpleDateFormat(DATE);
    private final SimpleDateFormat time = new SimpleDateFormat(TIME);
    private final SimpleDateFormat fraction = new SimpleDateFormat(FRACTION);
    private final NumberFormat nn = new DecimalFormat("00");
    private boolean isFractionFormatted = false;

    /**
     * Sole constructor.
     *
     * @param   tz              The {@link TimeZone}.
     *
     * @see #setTimeZone(TimeZone)
     */
    public ISO8601DateFormat(TimeZone tz) {
        super(DATE + QUOTE + T + QUOTE + TIME);

        setLenient(true);
        setTimeZone(tz);
    }

    /**
     * Bean attribute getter method to determine whether or not the fraction
     * of the second should be included when formatting the date-time.
     *
     * @return  {@code true} if the fraction of the second should be
     *          specified; {@code false} otherwise.
     */
    public boolean isFractionFormatted() { return isFractionFormatted; }

    /**
     * Bean attribute setter method to specify whether or not the fraction
     * of the second should be included when formatting the date-time.
     *
     * @param   isFractionFormatted
     *                          Boolean specifying whether or not the
     *                          fraction of the second should be included.
     */
    public void setFractionFormatted(boolean isFractionFormatted) {
        this.isFractionFormatted = isFractionFormatted;
    }

    @Override
    public void setLenient(boolean lenient) {
        super.setLenient(lenient);

        date.setLenient(isLenient());
        time.setLenient(isLenient());
        fraction.setLenient(isLenient());
    }

    /**
     * {@link TimeZone} bean attribute setter method.
     *
     * @param   tz              If {@code null}, the value of {@link #UTC}
     *                          is used.
     */
    @Override
    public void setTimeZone(TimeZone tz) {
        super.setTimeZone((tz != null) ? tz : UTC);

        date.setTimeZone(getTimeZone());
        time.setTimeZone(getTimeZone());
        fraction.setTimeZone(getTimeZone());
    }

    @Override
    public StringBuffer format(Date date, StringBuffer in, FieldPosition pos) {
        StringBuffer out = super.format(date, in, pos);

        if (isFractionFormatted()) {
            synchronized (fraction) {
                out.append(DOT);
                out.append(fraction.format(date));
            }
        }

        long offset = getTimeZone().getRawOffset();

        if (offset != 0) {
            int sign = (offset < 0) ? -1 : 1;
            long remainder = (sign < 0) ? (- offset) : offset;
            long hours = HOURS.fromMilliseconds(remainder);

            remainder -= HOURS.toMilliseconds(hours);

            long minutes = MINUTES.fromMilliseconds(remainder);

            synchronized (nn) {
                out.append((sign < 0) ? MINUS : PLUS);
                out.append(nn.format(hours));
                out.append(COLON);
                out.append(nn.format(minutes));
            }
        } else {
            out.append(Z);
        }

        return out;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        long milliseconds = 0;

        if (pos.getErrorIndex() < 0) {
            synchronized (date) {
                milliseconds += date.parse(source, pos).getTime();
            }
        }

        if (pos.getErrorIndex() < 0 && lookingAt(source, pos, T)) {
            advance(pos, T.length());

            synchronized (time) {
                milliseconds += time.parse(source, pos).getTime();
            }

            if (pos.getErrorIndex() < 0 && lookingAt(source, pos, DOT)) {
                advance(pos, DOT.length());

                synchronized (fraction) {
                    milliseconds += fraction.parse(source, pos).getTime();
                }
            }

            if (pos.getErrorIndex() < 0) {
                long sign = 0;

                if (lookingAt(source, pos, Z)) {
                    advance(pos, Z.length());
                    sign = 0;
                } else if (lookingAt(source, pos, MINUS)) {
                    advance(pos, MINUS.length());
                    sign = -1;
                } else if (lookingAt(source, pos, PLUS)) {
                    advance(pos, PLUS.length());
                    sign = +1;
                }

                if (sign != 0) {
                    long offset = 0;

                    synchronized (nn) {
                        offset += HOURS.toMilliseconds(nn.parse(source, pos));
                    }

                    if (pos.getErrorIndex() < 0
                        && lookingAt(source, pos, COLON)) {
                        advance(pos, COLON.length());

                        synchronized (nn) {
                            offset +=
                                MINUTES.toMilliseconds(nn.parse(source, pos));
                        }
                    }

                    milliseconds += sign * offset;
                }
            }
        }

        return new Date(milliseconds);
    }

    private boolean lookingAt(String string, ParsePosition pos,
                              String substring) {
        return (pos.getIndex() < string.length()
                && string.substring(pos.getIndex()).startsWith(substring));
    }

    private void advance(ParsePosition pos, int length) {
        pos.setIndex(pos.getIndex() + length);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
