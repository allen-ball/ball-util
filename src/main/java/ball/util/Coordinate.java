/*
 * $Id$
 *
 * Copyright 2016 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * X-Y coordinate representation.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Coordinate implements Comparable<Coordinate>, Serializable {
    private static final long serialVersionUID = 4588951577088861983L;

    /** @serial */ private final int y;
    /** @serial */ private final int x;

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
     * area described by the argument {@link Coordinate}s.
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

    @Override
    public boolean equals(Object that) {
        return ((that instanceof Coordinate)
                    ? (this.compareTo((Coordinate) that) == 0)
                    : super.equals(that));
    }

    @Override
    public int hashCode() { return Arrays.asList(y, x).hashCode(); }

    @Override
    public String toString() { return Arrays.asList(y, x).toString(); }

    /**
     * Static method to return a {@link SortedSet} of {@link Coordinate}s
     * specified by the parameters.
     *
     * @param   min             {@code [y0, x0]}
     * @param   max             {@code [yN, xN]}
     *
     * @return  The {@link SortedSet} of {@link Coordinate}s.
     */
    public static SortedSet<Coordinate> generate(Coordinate min,
                                                 Coordinate max) {
        return generate(Math.min(min.getY(), max.getY()),
                        Math.min(min.getX(), max.getX()),
                        Math.max(min.getY(), max.getY()),
                        Math.max(min.getX(), max.getX()));
    }

    /**
     * Static method to return a {@link SortedSet} of {@link Coordinate}s
     * specified by the parameters.
     *
     * @param   y0              {@code MIN(y)}
     * @param   x0              {@code MIN(x)}
     * @param   yN              {@code MAX(y) + 1}
     * @param   xN              {@code MAX(x) + 1}
     *
     * @return  The {@link SortedSet} of {@link Coordinate}s.
     */
    public static SortedSet<Coordinate> generate(int y0, int x0,
                                                 int yN, int xN) {
        TreeSet<Coordinate> set = new TreeSet<Coordinate>();

        for (int y = y0; y < yN; y += 1) {
            for (int x = x0; x < xN; x += 1) {
                set.add(new Coordinate(y, x));
            }
        }

        return set;
    }
}
