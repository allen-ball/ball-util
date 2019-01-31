/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Comparator;
import java.util.Objects;

/**
 * {@link Order} implementation to order arrays.
 *
 * @param       <T>             The array element type to be compared.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ArrayOrder<T> extends Order<T[]> {
    private static final long serialVersionUID = -490553470060610287L;

    /** @serial */ private final Comparator<? super T> comparator;

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

        this.comparator = Objects.requireNonNull(comparator);
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
