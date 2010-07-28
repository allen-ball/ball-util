/*
 * $Id: ArrayOrder.java,v 1.4 2010-07-28 04:56:50 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Comparator;

/**
 * {@link Order} implementation to order arrays.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class ArrayOrder<T> extends Order<T[]> {
    private static final long serialVersionUID = 69419214559265223L;

    private final Comparator<? super T> comparator;

    /**
     * Sole constructor.
     *
     * @param   comparator      The {@link Comparator} to use to compare
     *                          array elements.
     *
     * @throws  NullPointerException
     *                          If the comparator parameter is null.
     */
    public ArrayOrder(Comparator<? super T> comparator) {
        super();

        if (comparator != null) {
            this.comparator = comparator;
        } else {
            throw new NullPointerException("comparator");
        }
    }

    @Override
    public int compare(T[] left, T[] right) {
        int comparison = right.length - left.length;

        for (int i = 0, n = left.length; comparison == 0 && i < n; i += 1) {
            comparison = comparator.compare(left[i], right[i]);
        }

        return comparison;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
