/*
 * $Id$
 *
 * Copyright 2008 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * Time unit {@link Enum} type.  Inspired by
 * {@link java.util.concurrent.TimeUnit}.
 *
 * @see java.util.concurrent.TimeUnit
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public enum TimeUnitEnum {
    MILLISECOND(1), MILLISECONDS(MILLISECOND),
        SECOND(1000, MILLISECONDS), SECONDS(SECOND),
        MINUTE(60, SECONDS), MINUTES(MINUTE),
        HOUR(60, MINUTES), HOURS(HOUR),
        DAY(24, HOURS), DAYS(DAY),
        WEEK(7, DAYS), WEEKS(WEEK),
        MONTH(30, DAYS), MONTHS(MONTH),
        YEAR(365, DAYS), YEARS(YEAR);

    private final long milliseconds;

    private TimeUnitEnum(Number milliseconds) {
        if (milliseconds.longValue() > 0) {
            this.milliseconds = milliseconds.longValue();
        } else {
            throw new IllegalArgumentException("milliseconds=" + milliseconds);
        }
    }

    private TimeUnitEnum(Number duration, TimeUnitEnum unit) {
        this(unit.toMilliseconds(duration));
    }

    private TimeUnitEnum(TimeUnitEnum unit) { this(1, unit); }

    /**
     * Method to convert the specified duration from milliseconds to
     * {@link TimeUnitEnum}.
     *
     * @param   duration        The duration in milliseconds.
     *
     * @return  The duration in {@link TimeUnitEnum}.
     */
    public long fromMilliseconds(Number duration) {
        return duration.longValue() / milliseconds;
    }

    /**
     * Method to convert the specified duration from {@link TimeUnitEnum} to
     * milliseconds.
     *
     * @param   duration        The duration in {@link TimeUnitEnum}.
     *
     * @return  The duration in milliseconds.
     */
    public long toMilliseconds(Number duration) {
        return duration.longValue() * milliseconds;
    }

    /**
     * Method to convert duration of one {@link TimeUnitEnum} to milliseconds.
     *
     * @return  The duration in milliseconds.
     */
    public long toMilliseconds() { return toMilliseconds(1); }

    /**
     * Method to determine if the argument time is older than the current
     * duration.
     *
     * @param   duration        The duration in {@link TimeUnitEnum}.
     * @param   time            The millisecond value to test.
     *
     * @return  {@code true} if the argument time is older than this
     *          duration; {@code false} otherwise.
     */
    public boolean isOlderThan(Number duration, long time) {
        return (System.currentTimeMillis() - toMilliseconds(duration)) > time;
    }
}
