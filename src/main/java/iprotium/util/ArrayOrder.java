/*
 * $Id: ArrayOrder.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Order} implementation to order arrays.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class ArrayOrder<T> extends Order<T[]> {
    private static final long serialVersionUID = 4969844544789108256L;

    private final Order<T> order;

    /**
     * Sole constructor.
     *
     * @param   order           The {@link Order} to use to compare array
     *                          elements.
     *
     * @throws  NullPointerException
     *                          If the order parameter is null.
     */
    public ArrayOrder(Order<T> order) {
        super();

        if (order != null) {
            this.order = order;
        } else {
            throw new NullPointerException("order");
        }
    }

    @Override
    public int compare(T[] left, T[] right) {
        int comparison = right.length - left.length;

        for (int i = 0, n = left.length; comparison == 0 && i < n; i += 1) {
            comparison = order.compare(left[i], right[i]);
        }

        return comparison;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
