/*
 * $Id: Order.java,v 1.2 2009-01-27 22:00:19 ball Exp $
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
 * @version $Revision: 1.2 $
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
     */
    public void sort(T[] array) { Arrays.sort(array, this); }

    /**
     * Method to sort a List in this Order.
     *
     * @param   list            The List to be sorted.
     */
    public void sort(List<? extends T> list) { Collections.sort(list, this); }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/10/26 23:55:24  ball
 * Initial writing.
 *
 */
