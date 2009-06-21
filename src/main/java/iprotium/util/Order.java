/*
 * $Id: Order.java,v 1.3 2009-06-21 01:25:03 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
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
 * Abstract base class for Comparator implementations.
 *
 * @see Comparator
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public abstract class Order<T> implements Comparator<T>, Serializable {

    /**
     * Sole constructor.
     */
    protected Order() { }

    public abstract int compare(T left, T right);

    /**
     * Method to construct a List from a Collection sorted in this Order.
     *
     * @param   collection      The Collection to be sorted.
     *
     * @return  A List containing the Collection sorted in this Order.
     */
    public List<T> asList(Collection<? extends T> collection) {
        List<T> list = new ArrayList<T>(collection);

        sort(list);

        return list;
    }

    /**
     * Method to sort an array in this Order.
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
     * Method to sort a List in this Order.
     *
     * @param   list            The List to be sorted.
     *
     * @return  The List after sorting.
     */
    public List<? extends T> sort(List<? extends T> list) {
        if (list != null) {
            Collections.sort(list, this);
        }

        return list;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
