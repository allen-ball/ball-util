/*
 * $Id: TimeUnitEnum.java,v 1.3 2009-03-26 01:01:48 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * Time unit Enum type.  Inspired by java.util.concurrent.TimeUnit.
 *
 * @see java.util.concurrent.TimeUnit
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
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
     * Method to convert the specified duration in milliseconds to
     * TimeUnitEnum.
     *
     * @param   duration        The duration in TimeUnitEnum.
     *
     * @return  The duration in milliseconds.
     */
    public long fromMilliseconds(Number duration) {
        return duration.longValue() / milliseconds;
    }

    /**
     * Method to convert the specified duration in TimeUnitEnum to
     * milliseconds.
     *
     * @param   duration        The duration in TimeUnitEnum.
     *
     * @return  The duration in milliseconds.
     */
    public long toMilliseconds(Number duration) {
        return duration.longValue() * milliseconds;
    }

    /**
     * Method to convert duration of one in TimeUnitEnum to milliseconds.
     *
     * @return  The duration in milliseconds.
     */
    public long toMilliseconds() { return toMilliseconds(1); }
}
/*
 * $Log: not supported by cvs2svn $
 */
