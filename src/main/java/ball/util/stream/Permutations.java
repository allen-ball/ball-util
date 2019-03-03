/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.stream;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * {@link Stream} implementaion that provides all permutations of a
 * {@link List}.
 *
 * @param       <T>             The {@link List} element type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface Permutations<T> extends Combinations<T> {

    /**
     * Method to get the {@link Stream} of permutations.
     *
     * @param   collection      The {@link Collection} of elements to
     *                          permute.
     * @param   <T>             The {@link Collection} element type.
     *
     * @return  The {@link Stream} of permutations.
     */
    public static <T> Stream<List<T>> of(Collection<T> collection) {
        return Combinations.of(collection, collection.size());
    }
}
