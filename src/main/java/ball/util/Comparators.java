/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * {@link Comparator}s
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class Comparators {
    private Comparators() { }

    /**
     * Static method to return a {@link Comparator} ordering members in the
     * same order as the {@link List}.
     *
     * @param   list            The {@link List} describing the order.
     * @param   <T>             The type to be ordered.
     *
     * @return  A {@link Comparator} enforcing the specified order.
     */
    public static <T> Comparator<T> orderedBy(List<T> list) {
        return new OrderedComparator<T>(list);
    }

    private static class OrderedComparator<T> extends HashMap<T,Integer>
                                              implements Comparator<T> {
        private static final long serialVersionUID = 4745150194266705710L;

        public OrderedComparator(List<T> list) {
            for (int i = 0, n = list.size(); i < n; i += 1) {
                put(list.get(i), i);
            }
        }

        @Override
        public int compare(T left, T right) {
            return Integer.compare(getOrDefault(left, -1),
                                   getOrDefault(right, size()));
        }
    }
}
