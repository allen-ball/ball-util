/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * {@link Map} implementation that provides counting methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class Histogram<K> extends TreeMap<K,Long> {
    private static final long serialVersionUID = 379159598348785204L;

    /**
     * No-argument constructor.
     */
    public Histogram() { this(null); }

    /**
     * Constructor to specify a {@link Comparator}.
     *
     * @param   comparator      The {@link Comparator}.
     */
    public Histogram(Comparator<? super K> comparator) { super(comparator); }

    /**
     * Method to count a single instance of a key.
     *
     * @param   key             The key value to count.
     */
    public void count(K key) { count(key, 1); }

    /**
     * Method to count a key.
     *
     * @param   key             The key value to count.
     * @param   count           The count to increment (or decrement).
     */
    public void count(K key, Number count) {
        synchronized (this) {
            put(key, longValue(get(key)) + count.longValue());
        }
    }

    /**
     * Method to count keys in an {@link Iterable}.
     *
     * @param   iterable        The {@link Iterable} of key values to count.
     */
    public void countAll(Iterable<? extends K> iterable) {
        for (K key : iterable) {
            count(key);
        }
    }

    /**
     * Method to count keys in a {@link Map}.
     *
     * @param   map             The {@link Map} of key values and counts to
     *                          count in this {@link Histogram}.
     */
    public void countAll(Map<? extends K,? extends Number> map) {
        for (Map.Entry<? extends K,? extends Number> entry : map.entrySet()) {
            count(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Method to get the total of the {@link Map#values()} of {@code this}
     * {@link Histogram}.
     *
     * @return  The total of {@link #values()}.
     */
    public long total() {
        long total = 0;

        synchronized (this) {
            total = sum(values());
        }

        return total;
    }

    @Override
    public Histogram clone() { return (Histogram) super.clone(); }

    /**
     * Convenience method to interpret a {@link Number} as a {@link Long}
     * value (see {@link Number#longValue()}).
     *
     * @param   number          The {@link Number} (may be {@code null}).
     *
     * @return  {@link Number#longValue()} if the argument is
     *          non-{@code null}; {@code 0L} otherwise.
     */
    protected static long longValue(Number number) {
        return (number != null) ? number.longValue() : 0;
    }

    /**
     * Convenience method to sum an {@link Iterable} of {@link Number}s.
     *
     * @param   iterable        The {@link Iterable} of {@link Number}s.
     *
     * @return  The sum of the {@link Number}s.
     */
    protected static long sum(Iterable<? extends Number> iterable) {
        long total = 0;

        for (Number value : iterable) {
            total += longValue(value);
        }

        return total;
    }
}
