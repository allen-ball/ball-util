package ball.util.stream;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
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
