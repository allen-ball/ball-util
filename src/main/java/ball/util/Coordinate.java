/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Arrays;

/**
 * X-Y coordinate representation.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Coordinate implements Comparable<Coordinate>, Serializable {
    private static final long serialVersionUID = 1158814562434472666L;

    private final int y;
    private final int x;

    /**
     * @param   y               The Y-coordinate.
     * @param   x               The X-coordinate.
     */
    @ConstructorProperties({ "y", "x" })
    public Coordinate(Number y, Number x) { this(y.intValue(), x.intValue()); }

    private Coordinate(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public int getY() { return y; }

    public int getX() { return x; }

    /**
     * Method to determine if {@code this} {@link Coordinate} is within the
     * area described by the argument {@link Coordinates}.
     *
     * @param   min             The minimum {@link Coordinate}.
     * @param   max             The maximum {@link Coordinate}.
     *
     * @return  {@code true} if within the area; {@code false} otherwise.
     */
    public boolean within(Coordinate min, Coordinate max) {
        return ((min.getY() <= getY() && getY() < max.getY())
                && (min.getX() <= getX() && getX() < max.getX()));
    }

    @Override
    public String toString() { return Arrays.asList(y, x).toString(); }

    @Override
    public int compareTo(Coordinate that) {
        int difference =
            Integer.valueOf(this.y).compareTo(Integer.valueOf(that.y));

        if (difference < 0) {
            difference = Integer.MIN_VALUE;
        } else if (difference > 0) {
            difference = Integer.MAX_VALUE;
        } else {
            difference =
                Integer.valueOf(this.x).compareTo(Integer.valueOf(that.x));
        }

        return difference;
    }
}
