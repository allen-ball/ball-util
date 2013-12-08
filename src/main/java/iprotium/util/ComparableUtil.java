/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * Static utility methods for working with {@link Comparable}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ComparableUtil {
    private ComparableUtil() { }

    /**
     * Static method to compare to {@link ComparableUtil} with
     * {@link Comparable#compareTo(Object)} falling back to
     * {@link Order#NONNULL} if either argument is {@code null}.
     *
     * @param   left            The first {@link Comparable}.
     * @param   right           The second {@link Comparable}.
     *
     * @return  An {@code int} value per
     *          {@link Comparable#compareTo(Object)}.
     */
    public static <T extends Comparable<T>> int compare(T left, T right) {
        int difference = 0;

        if (difference == 0) {
            if (Order.allAreNonNull(left, right)) {
                difference = left.compareTo(right);
            } else {
                difference = Order.NONNULL.compare(left, right);
            }
        }

        return difference;
    }
}
