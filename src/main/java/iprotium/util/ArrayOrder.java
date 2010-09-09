/*
 * $Id: ArrayOrder.java,v 1.6 2010-09-09 03:17:40 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Comparator;

/**
 * {@link Order} implementation to order arrays.
 *
 * @param       <T>             The array element type to be compared.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.6 $
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
        int difference = left.length - right.length;

        for (int i = left.length - 1; difference == 0 && i >= 0; i -= 1) {
            difference = comparator.compare(left[i], right[i]);

            if (difference != 0) {
                break;
            }
        }

        return difference;
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2010/08/23 03:39:24  ball
 * Element [n] is more significant than element [n - 1].
 *
 */
