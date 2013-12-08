/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract base class for {@link Comparator} implementations.
 *
 * @param       <T>             The type to be compared.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class Order<T> implements Comparator<T>, Serializable {

    /**
     * @see Null
     */
    public static final Null<Object> NULL = new Null<Object>();

    /**
     * @see NonNull
     */
    public static final NonNull<Object> NONNULL = new NonNull<Object>();

    /**
     * Sole constructor.
     */
    protected Order() { }

    @Override
    public abstract int compare(T left, T right);

    /**
     * Method to construct a {@link List} from a {@link Collection} sorted
     * in this {@link Order}.
     *
     * @param   collection      The {@link Collection} to be sorted.
     *
     * @return  A {@link List} containing the {@link Collection} sorted in
     *          this {@link Order}.
     */
    public List<T> asList(Collection<? extends T> collection) {
        List<T> list = new ArrayList<T>(collection);

        sort(list);

        return list;
    }

    /**
     * Method to sort an array in this {@link Order}.
     *
     * @param   array           The array to be sorted.
     *
     * @return  The array after sorting.
     */
    public T[] sort(T[] array) {
        if (array != null) {
            Arrays.sort(array, this);
        }

        return array;
    }

    /**
     * Method to sort a {@link List} in this {@link Order}.
     *
     * @param   list            The {@link List} to be sorted.
     *
     * @return  The {@link List} after sorting.
     */
    public List<? extends T> sort(List<? extends T> list) {
        if (list != null) {
            Collections.sort(list, this);
        }

        return list;
    }

    protected static boolean allAreNonNull(Object... objects) {
        boolean notNull = true;

        for (Object object : objects) {
            notNull &= (object != null);

            if (! notNull) {
                break;
            }
        }

        return notNull;
    }

    protected static int intValue(boolean bool) { return bool ? 1 : 0; }

    /**
     * Orders non-{@code null} {@link Object}s before {@code null}
     * {@link Object}s.
     */
    public static class NonNull<T extends Object> extends Order<T> {
        private static final long serialVersionUID = -791750204466099902L;

        /**
         * Sole constructor.
         */
        protected NonNull() { super(); }

        @Override
        public int compare(T left, T right) {
            return intValue(right != null) - intValue(left != null);
        }
    }

    /**
     * Orders {@code null} {@link Object}s before non-{@code null}
     * {@link Object}s.
     */
    public static class Null<T extends Object> extends Order<T> {
        private static final long serialVersionUID = -94289223176191889L;

        /**
         * Sole constructor.
         */
        protected Null() { super(); }

        @Override
        public int compare(T left, T right) {
            return intValue(right == null) - intValue(left == null);
        }
    }
}
