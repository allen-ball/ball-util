/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.ArrayList;

/**
 * {@link Integer} {@link java.util.List} implementation useful for testing
 * {@link Combinations} and {@link Permutations} {@link Iterable}s.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class IntegerList extends ArrayList<Integer> {
    private static final long serialVersionUID = 1585903284962589623L;

    /**
     * @param   count           The element count.
     */
    public IntegerList(Integer count) { this(count.intValue()); }

    /**
     * @param   count           The element count.
     */
    public IntegerList(int count) {
        super(count);

        for (int i = 0; i < count; i += 1) {
            add(i);
        }
    }

    @Override
    public Integer[] toArray() { return toArray(new Integer[] { }); }

    @Override
    public IntegerList clone() { return (IntegerList) super.clone(); }
}
