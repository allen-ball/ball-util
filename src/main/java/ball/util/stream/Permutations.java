/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.stream;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
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
        return of(null, collection);
    }

    /**
     * Method to get the {@link Stream} of permutations.
     *
     * @param   predicate       The optional {@link Predicate} (may be
     *                          {@code null}) specifying prerequisite
     *                          requirement(s) for the combinations.  Any
     *                          path that does not match will be pruned.
     * @param   collection      The {@link Collection} of elements to
     *                          permute.
     * @param   <T>             The {@link Collection} element type.
     *
     * @return  The {@link Stream} of permutations.
     */
    public static <T> Stream<List<T>> of(Predicate<List<T>> predicate,
                                         Collection<T> collection) {
        int size = collection.size();

        return Combinations.of(size, size, predicate, collection);
    }
}
