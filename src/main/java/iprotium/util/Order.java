/*
 * $Id: Order.java,v 1.5 2010-08-23 03:43:54 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
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
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public abstract class Order<T> implements Comparator<T>, Serializable {

    /**
     * @see Natural
     */
    public static final Natural<Object> NATURAL = new Natural<Object>();

    /**
     * Sole constructor.
     */
    protected Order() { }

    @Override
    public abstract int compare(T left, T right);

    protected boolean allAreNonNull(Object... objects) {
        boolean notNull = true;

        for (Object object : objects) {
            notNull &= (object != null);

            if (! notNull) {
                break;
            }
        }

        return notNull;
    }

    protected int intValue(boolean bool) { return bool ? 1 : 0; }

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

    /**
     * @see Comparable#compareTo(Object)
     */
    public static class Natural<T> extends Order<T> {
        private static final long serialVersionUID = 3417528038704549459L;

        /**
         * Sole constructor.
         */
        protected Natural() { super(); }

        @Override
        public int compare(T left, T right) {
            return compare((Comparable) left, (Comparable) right);
        }

        @SuppressWarnings("unchecked")
        private int compare(Comparable left, Comparable right) {
            return (allAreNonNull(left, right)
                        ? left.compareTo(right)
                        : (intValue(right != null) - intValue(left != null)));
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
