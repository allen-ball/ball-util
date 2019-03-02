/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.stream;

import ball.util.SupplierSpliterator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
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
     * @param   group           The number to group in each combination.
     *
     * @return  The {@link Stream} of combinations.
     */
    public static <T> Stream<List<T>> of(Collection<T> collection, int group) {
        SpliteratorImpl<T> spliterator =
            new SpliteratorImpl<T>(collection, group);

        return StreamSupport.<List<T>>stream(spliterator, false);
    }

    /**
     * {@link Combinations} {@link Spliterator} implementation
     */
    public static class SpliteratorImpl<T>
                        extends SupplierSpliterator<List<T>> {
        protected final List<T> prefix;
        protected final List<T> remaining;
        private final int group;

        private SpliteratorImpl(Collection<T> collection, int group) {
            this(Collections.emptyList(), new LinkedList<>(collection), group);
        }

        private SpliteratorImpl(List<T> prefix, List<T> remaining, int group) {
            super(estimateSize(remaining.size(), group),
                  IMMUTABLE|NONNULL|SIZED|SUBSIZED);

            this.prefix = requireNonNull(prefix);
            this.remaining = requireNonNull(remaining);
            this.group = group;
        }

        @Override
        protected List<Supplier<Spliterator<List<T>>>> spliterators() {
            return spliterators(group);
        }

        private List<Supplier<Spliterator<List<T>>>> spliterators(int group) {
            LinkedList<Supplier<Spliterator<List<T>>>> spliterators =
                new LinkedList<>();
            LinkedList<List<T>> combinations = new LinkedList<>();

            for (int i = 0, n = remaining.size(); i < n; i += 1) {
                List<T> prefix = new LinkedList<>(this.prefix);

                prefix.add(remaining.get(i));

                if (prefix.size() == group) {
                    combinations.add(prefix);
                } else if (prefix.size() < group) {
                    List<T> remaining = new LinkedList<>(this.remaining);

                    remaining.remove(i);
                    spliterators.add(() -> new SpliteratorImpl<T>(prefix, remaining, group));
                }
            }

            if (! combinations.isEmpty()) {
                spliterators.add(supplier(combinations));
            }

            return spliterators;
        }

        @Override
        public long estimateSize() {
            return estimateSize(remaining.size(), group);
        }

        @Override
        public String toString() {
            String string = prefix.toString();

            if (! remaining.isEmpty()) {
                string += ":" + remaining.toString();
            }

            string += "/" + group;

            return string;
        }

        protected static long estimateSize(long set, long group) {
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
