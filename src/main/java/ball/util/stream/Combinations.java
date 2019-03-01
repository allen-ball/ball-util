/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.stream;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * {@link Stream} implementaion that provides all combinations of a
 * {@link List}.
 *
 * @param       <T>             The {@link List} element type.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface Combinations<T> extends Stream<List<T>> {

    /**
     * Method to get the {@link Stream} of combinations.
     *
     * @param   collection      The {@link Collection} of elements to
     *                          permute.
     * @param   <T>             The {@link Collection} element type.
     * @param   take            The number to take in each combination.
     *
     * @return  The {@link Stream} of combinations.
     */
    public static <T> Stream<List<T>> of(Collection<T> collection, int take) {
        SpliteratorImpl<T> spliterator =
            new SpliteratorImpl<T>(collection, take);

        return StreamSupport.<List<T>>stream(spliterator, false);
    }

    /**
     * {@link Combinations} {@link java.util.Spliterator} implementation
     */
    public static class SpliteratorImpl<T>
                        extends Spliterators.AbstractSpliterator<List<T>> {
        protected final List<T> prefix;
        protected final List<T> remaining;
        private final int take;
        private Spliterator<List<T>> spliterator = null;
        private final Iterator<Supplier<Spliterator<List<T>>>> combinations;

        /**
         * {@link Combinations} constructor
         *
         * @param       collection      The {@link Collection}.
         * @param       take            The number to take in each
         *                              combination.
         */
        private SpliteratorImpl(Collection<T> collection, int take) {
            this(Collections.emptyList(), new LinkedList<>(collection), take);
        }

        private SpliteratorImpl(List<T> prefix, List<T> remaining, int take) {
            super(estimateSize(remaining.size(), take),
                  IMMUTABLE|NONNULL|SIZED|SUBSIZED);

            this.prefix = requireNonNull(prefix);
            this.remaining = requireNonNull(remaining);
            this.take = take;
            this.spliterator = Spliterators.emptySpliterator();
            this.combinations = combinations().iterator();
        }

        private List<Supplier<Spliterator<List<T>>>> combinations() {
            LinkedList<Supplier<Spliterator<List<T>>>> list =
                new LinkedList<>();
            LinkedList<List<T>> combinations = new LinkedList<>();

            for (int i = 0, n = remaining.size(); i < n; i += 1) {
                List<T> prefix = new LinkedList<>(this.prefix);

                prefix.add(remaining.get(i));

                if (prefix.size() == take) {
                    combinations.add(prefix);
                } else if (prefix.size() < take) {
                    List<T> remaining = new LinkedList<>(this.remaining);

                    remaining.remove(i);
                    list.add(() -> new SpliteratorImpl<T>(prefix, remaining, take));
                }
            }

            if (! combinations.isEmpty()) {
                list.add(() -> Spliterators
                               .spliterator(combinations,
                                            IMMUTABLE|NONNULL|SIZED));
            }

            return list;
        }

        @Override
        public long estimateSize() {
            return estimateSize(remaining.size(), take);
        }

        @Override
        public boolean tryAdvance(Consumer<? super List<T>> consumer) {
            boolean accepted = spliterator.tryAdvance(consumer);

            if (! accepted) {
                if (combinations.hasNext()) {
                    spliterator = combinations.next().get();
                    accepted = tryAdvance(consumer);
                }
            }

            return accepted;
        }

        @Override
        public String toString() {
            String string = prefix.toString();

            if (! remaining.isEmpty()) {
                string += ":" + remaining.toString();
            }

            string += "/" + take;

            return string;
        }

        protected static long estimateSize(long n, long take) {
            if (n < 0) {
                throw new IllegalStateException();
            }

            long product = 1;

            if (take > 0) {
                switch ((int) n) {
                case 1:
                case 0:
                    break;

                default:
                    product = n * estimateSize(n - 1, take - 1);
                    break;
                }
            }

            return product;
        }
    }
}
