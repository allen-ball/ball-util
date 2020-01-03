/*
 * $Id$
 *
 * Copyright 2016 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * X-Y coordinate representation.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class Coordinate implements Comparable<Coordinate>, Serializable {
    private static final long serialVersionUID = -4428900365914120909L;

    private static final Comparator<? super Coordinate> COMPARATOR =
        Comparator
        .comparingInt(Coordinate::getY)
        .thenComparingInt(Coordinate::getX);

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
     * Method to get the {@link Coordinate} relative to {@link.this}
     * {@link Coordinate}.
     *
     * @param   dy              The change in Y-coordinate.
     * @param   dx              The change in X-coordinate.
     *
     * @return  The relative {@link Coordinate}.
     */
    public Coordinate translate(Number dy, Number dx) {
        return new Coordinate(getY() + dy.intValue(), getX() + dx.intValue());
    }

    /**
     * Method to getthe {@link Coordinate} relative to {@link.this}
     * {@link Coordinate}.
     *
     * @param   ds              The {@link Coordinate} describing the change.
     *
     * @return  The relative {@link Coordinate}.
     */
    public Coordinate translate(Coordinate ds) {
        return translate(ds.getY(), ds.getX());
    }

    /**
     * Method to determine if {@link.this} {@link Coordinate} is within the
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

    /**
     * Method to translate this {@link Coordinate} to a {@link Point2D}.
     *
     * @return  The {@link Point}.
     */
    public Point2D asPoint() { return new Point(getX(), getY()); }

    @Override
    public int compareTo(Coordinate that) {
        return Objects.compare(this, that, COMPARATOR);
    }

    @Override
    public boolean equals(Object object) {
        return ((object instanceof Coordinate)
                    ? (this.compareTo((Coordinate) object) == 0)
                    : super.equals(object));
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
    public static SortedSet<Coordinate> range(Coordinate min, Coordinate max) {
        return range(Math.min(min.getY(), max.getY()),
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
    public static SortedSet<Coordinate> range(int y0, int x0, int yN, int xN) {
        TreeSet<Coordinate> set = new TreeSet<>();

        for (int y = y0; y < yN; y += 1) {
            for (int x = x0; x < xN; x += 1) {
                set.add(new Coordinate(y, x));
            }
        }

        return set;
    }
}
