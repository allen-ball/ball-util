package ball.util;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
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
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.LongStream;

/**
 * {@link Spliterator} abstract base class that dispatches to
 * {@link Spliterator}s supplied through an {@link Spliterator} of
 * {@link Spliterator} {@link Supplier}s.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class DispatchSpliterator<T> extends AbstractSpliterator<T> {
    private Iterator<Supplier<Spliterator<T>>> spliterators = null;
    private Spliterator<T> spliterator = Spliterators.emptySpliterator();

    /**
     * See {@link AbstractSpliterator} constructor.
     *
     * @param   estimate        The estimated size of {@link.this}
     *                          {@link Spliterator} if known, otherwise
     *                          {@link Long#MAX_VALUE}.
     * @param   characteristics Properties of this {@link Spliterator}'s
     *                          source or elements.  If {@link #SIZED} is
     *                          reported then {@link.this}
     *                          {@link Spliterator} will additionally report
     *                          {@link #SUBSIZED}.
     */
    protected DispatchSpliterator(long estimate, int characteristics) {
        super(estimate, characteristics);
    }

    /**
     * Method to provide the {@link Spliterator} {@link Supplier}s.  This
     * method is not called until the first call of
     * {@link #tryAdvance(Consumer)}.
     *
     * @return  The {@link Iterator} of {@link Spliterator}
     *          {@link Supplier}s.
     */
    protected abstract Spliterator<Supplier<Spliterator<T>>> spliterators();

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        boolean accepted = spliterator.tryAdvance(consumer);

        if (! accepted) {
            if (spliterators == null) {
                spliterators = Spliterators.iterator(spliterators());
            }

            if (spliterators.hasNext()) {
                spliterator = spliterators.next().get();
                accepted = tryAdvance(consumer);
            }
        }

        return accepted;
    }

    /**
     * Method to count the number of combinations of {@code [k0,kN]} size
     * that may be chosen from a set of {@code n}-size (binomial
     * coefficient).
     *
     * @param   n               The size of the set.
     * @param   k0              The beginning of the interval (inclusive) of
     *                          size of the subsets to be chosen.
     * @param   kN              The end of the interval (inclusive) of size
     *                          of the subsets to be chosen.
     *
     * @return  The total number of combinations.
     */
    protected static long binomial(long n, long k0, long kN) {
        long size =
            LongStream.rangeClosed(Math.min(k0, kN), Math.max(k0, kN))
            .filter(t -> ! (n < t))
            .map(t -> binomial(n, t))
            .sum();

        return size;
    }

    /**
     * Method to count the number of combinations of {@code k}-size that may
     * be chosen from a set of {@code n}-size (binomial coefficient).
     *
     * @param   n               The size of the set.
     * @param   k               The size of the subset to be chosen.
     *
     * @return  The total number of {@code k}-combinations.
     */
    protected static long binomial(long n, long k) {
        if (n < 0) {
            throw new IllegalStateException();
        }

        long product = 1;

        if (k > 0) {
            switch ((int) n) {
            case 1:
            case 0:
                break;

            default:
                product = n * binomial(n - 1, k - 1);
                break;
            }
        }

        return product;
    }
}
