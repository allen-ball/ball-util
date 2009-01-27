/*
 * $Id: ArrayOrder.java,v 1.2 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * Order implementation to order arrays.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class ArrayOrder<T> extends Order<T[]> {
    private static final long serialVersionUID = 4969844544789108256L;

    private final Order<T> order;

    /**
     * Sole constructor.
     *
     * @param   order           The Order to use to compare array elements.
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
 * Revision 1.1  2008/10/26 23:55:24  ball
 * Initial writing.
 *
 */
