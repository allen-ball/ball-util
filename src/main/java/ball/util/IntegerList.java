/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * {@link Integer} {@link java.util.List} implementation useful for testing
 * {@link Combinations} and {@link Permutations} {@link Iterable}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class IntegerList extends ArrayList<Integer> {
    private static final long serialVersionUID = -3133103099144540508L;

    /**
     * @param   count           The element count.
     */
    public IntegerList(int count) {
        super(count);

        Collections.addAll(this,
                           IntStream.range(0, count)
                           .boxed()
                           .toArray(Integer[]::new));
    }

    @Override
    public Integer[] toArray() { return toArray(new Integer[] { }); }

    @Override
    public IntegerList clone() { return (IntegerList) super.clone(); }
}
