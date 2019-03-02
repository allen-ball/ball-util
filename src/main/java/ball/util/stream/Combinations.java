/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.stream;

import ball.util.DispatchSpliterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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
     * Method to get the {@link Stream} of combinations.  Combinations will
     * be streamed largest to smallest or smallest to largest depending on
     * the relative magnitude of {@code size0} and {@code sizeN}.  E.g., to
     * stream largest to smallest specify {@code sizeN} that is greater than
     * {@code size0}.
     *
     * @param   collection      The {@link Collection} of elements to
     *                          permute.
     * @param   <T>             The {@link Collection} element type.
     * @param   size0           The combination size range start
     *                          (inclusive).
     * @param   sizeN           The combination size range end (inclusive).
     *
     * @return  The {@link Stream} of combinations.
     */
    public static <T> Stream<List<T>> of(Collection<T> collection,
                                         int size0, int sizeN) {
        return StreamSupport.<List<T>>stream(new Of<T>(collection,
                                                       size0, sizeN),
                                             false);
    }

    /**
     * Method to get the {@link Stream} of combinations.
     *
     * @param   collection      The {@link Collection} of elements to
     *                          permute.
     * @param   <T>             The {@link Collection} element type.
     * @param   size            The combination size.
     *
     * @return  The {@link Stream} of combinations.
     */
    public static <T> Stream<List<T>> of(Collection<T> collection, int size) {
        return of(collection, size, size);
    }

    /**
     * {@link Combinations} {@link Spliterator} implementation
     */
    public static class Of<T> extends DispatchSpliterator<List<T>> {
        private final Collection<T> collection;
        private final int size0;
        private final int sizeN;

        private Of(Collection<T> collection, int size0, int sizeN) {
            super(estimateSize(collection.size(), size0, sizeN),
                  IMMUTABLE|NONNULL|SIZED|SUBSIZED);

            this.collection = requireNonNull(collection);
            this.size0 = size0;
            this.sizeN = sizeN;
        }

        @Override
        protected Iterator<Supplier<Spliterator<List<T>>>> spliterators() {
            List<Supplier<Spliterator<List<T>>>> list = new LinkedList<>();

            IntStream.rangeClosed(Math.min(size0, sizeN),
                                  Math.max(size0, sizeN))
                .filter(t -> ! (collection.size() < t))
                .forEach(t -> list.add(() -> new ForSize<T>(collection, t)));

            if (size0 > sizeN) {
                Collections.reverse(list);
            }

            return list.iterator();
        }

        @Override
        public String toString() {
            return collection + "/" + Arrays.asList(size0, sizeN);
        }

        private class ForSize<T> extends DispatchSpliterator<List<T>> {
            protected final List<T> prefix;
            protected final List<T> remaining;
            private final int size;

            public ForSize(Collection<T> collection, int size) {
                this(Collections.emptyList(),
                     new LinkedList<>(collection), size);
            }

            private ForSize(List<T> prefix, List<T> remaining, int size) {
                super(Of.estimateSize(remaining.size(), size),
                      IMMUTABLE|NONNULL|SIZED|SUBSIZED);

                this.prefix = requireNonNull(prefix);
                this.remaining = requireNonNull(remaining);
                this.size = size;
            }

            @Override
            protected Iterator<Supplier<Spliterator<List<T>>>> spliterators() {
                List<Supplier<Spliterator<List<T>>>> list = new LinkedList<>();

                if (prefix.size() < size) {
                    IntStream.range(0, remaining.size())
                        .forEach(i -> list.add(() -> descend(i)));
                } else if (prefix.size() == size) {
                    list.add(() -> Collections.singleton(prefix).spliterator());
                } else {
                    throw new IllegalStateException();
                }

                return list.iterator();
            }

            private Spliterator<List<T>> descend(int index) {
                List<T> prefix = new LinkedList<>(this.prefix);
                List<T> remaining = new LinkedList<>(this.remaining);

                prefix.add(remaining.remove(index));

                return new ForSize<T>(prefix, remaining, size);
            }

            @Override
            public long estimateSize() {
                return Of.estimateSize(remaining.size(), size);
            }

            @Override
            public String toString() {
                String string = prefix.toString();

                if (! remaining.isEmpty()) {
                    string += ":" + remaining.toString();
                }

                string += "/" + size;

                return string;
            }
        }

        private static long estimateSize(long set, long group0, long groupN) {
            long size =
                LongStream.rangeClosed(Math.min(group0, groupN),
                                       Math.max(group0, groupN))
                .filter(t -> ! (set < t))
                .map(t -> estimateSize(set, t))
                .sum();

            return size;
        }

        private static long estimateSize(long set, long group) {
            if (set < 0) {
                throw new IllegalStateException();
            }

            long product = 1;

            if (group > 0) {
                switch ((int) set) {
                case 1:
                case 0:
                    break;

                default:
                    product = set * estimateSize(set - 1, group - 1);
                    break;
                }
            }

            return product;
        }
    }
}
