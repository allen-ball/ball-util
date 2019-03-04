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
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

/**
 * {@link Stream} implementaion that provides all combinations of a
 * {@link List}.
 *
 * @param       <T>             The {@link List} element type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
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
     * @param   size0           The combination size range start
     *                          (inclusive).
     * @param   sizeN           The combination size range end (inclusive).
     * @param   collection      The {@link Collection} of elements to
     *                          permute.
     * @param   <T>             The {@link Collection} element type.
     *
     * @return  The {@link Stream} of combinations.
     */
    public static <T> Stream<List<T>> of(int size0, int sizeN,
                                         Collection<T> collection) {
        SpliteratorSupplier<T> supplier =
            new SpliteratorSupplier<T>()
            .collection(collection)
            .size0(size0).sizeN(sizeN);

        return supplier.stream();
    }

    /**
     * Method to get the {@link Stream} of combinations.
     *
     * @param   size            The combination size.
     * @param   collection      The {@link Collection} of elements to
     *                          permute.
     * @param   <T>             The {@link Collection} element type.
     *
     * @return  The {@link Stream} of combinations.
     */
    public static <T> Stream<List<T>> of(int size, Collection<T> collection) {
        return of(size, size, collection);
    }

    /**
     * {@link Combinations} {@link Spliterator} {@link Supplier}
     */
    @NoArgsConstructor(access = PRIVATE) @ToString
    public static class SpliteratorSupplier<T>
                        implements Supplier<Spliterator<List<T>>> {
        @Getter @Setter @Accessors(chain = true, fluent = true)
        private int characteristics =
            Spliterator.IMMUTABLE
            | Spliterator.NONNULL
            | Spliterator.SIZED
            | Spliterator.SUBSIZED;
        @Getter @Setter @Accessors(chain = true, fluent = true)
        private Collection<? extends T> collection = null;
        @Getter @Setter @Accessors(chain = true, fluent = true)
        private Comparator<? super T> comparator = null;
        @Getter @Setter @Accessors(chain = true, fluent = true)
        private int size0 = -1;
        @Getter @Setter @Accessors(chain = true, fluent = true)
        private int sizeN = -1;

        public SpliteratorSupplier<T> size(int size) {
            return size0(size).sizeN(size);
        }

        public Stream<List<T>> stream() {
            return StreamSupport.<List<T>>stream(get(), false);
        }

        @Override
        public Spliterator<List<T>> get() {
            if (comparator() != null) {
                characteristics(characteristics() | Spliterator.ORDERED);
            }

            if (size0() == -1 && sizeN() == -1) {
                size(collection.size());
            } else if (size0() == -1) {
                size0(sizeN());
            } else if (sizeN() == -1) {
                sizeN(size0());
            }

            return new Start();
        }

        private class Start extends DispatchSpliterator<List<T>> {
            public Start() {
                super(choose(collection().size(), size0(), sizeN()),
                      SpliteratorSupplier.this.characteristics());
            }

            @Override
            protected Iterator<Supplier<Spliterator<List<T>>>> spliterators() {
                List<Supplier<Spliterator<List<T>>>> list = new LinkedList<>();

                IntStream.rangeClosed(Math.min(size0(), sizeN()),
                                      Math.max(size0(), sizeN()))
                    .filter(t -> ! (collection.size() < t))
                    .forEach(t -> list.add(() -> new ForSize(t)));

                if (size0() > sizeN()) {
                    Collections.reverse(list);
                }

                return list.iterator();
            }

            @Override
            public long estimateSize() {
                return choose(collection().size(), size0(), sizeN());
            }

            @Override
            public String toString() {
                return collection() + "/" + Arrays.asList(size0(), sizeN());
            }
        }

        private class ForSize extends DispatchSpliterator<List<T>> {
            private final int size;

            public ForSize(int size) {
                super(choose(collection().size(), size),
                      SpliteratorSupplier.this.characteristics());

                this.size = size;
            }

            @Override
            protected Iterator<Supplier<Spliterator<List<T>>>> spliterators() {
                List<T> prefix = Collections.emptyList();
                List<T> remaining = new LinkedList<>(collection());

                if (comparator() != null) {
                    Collections.sort(remaining, comparator());
                }

                Supplier<Spliterator<List<T>>> supplier =
                    () -> new ForPrefix(size, prefix, remaining);

                return Collections.singleton(supplier).iterator();
            }

            @Override
            public long estimateSize() {
                return choose(collection().size(), size);
            }

            @Override
            public String toString() {
                return collection() + "/" + Arrays.asList(size);
            }
        }

        private class ForPrefix extends DispatchSpliterator<List<T>> {
            private final int size;
            protected final List<T> prefix;
            protected final List<T> remaining;

            public ForPrefix(int size, List<T> prefix, List<T> remaining) {
                super(choose(remaining.size(), size),
                      SpliteratorSupplier.this.characteristics());

                this.size = size;
                this.prefix = requireNonNull(prefix);
                this.remaining = requireNonNull(remaining);
            }

            @Override
            protected Iterator<Supplier<Spliterator<List<T>>>> spliterators() {
                List<Supplier<Spliterator<List<T>>>> list = new LinkedList<>();

                if (prefix.size() < size) {
                    for (int i = 0, n = remaining.size(); i < n; i += 1) {
                        List<T> prefix = new LinkedList<>(this.prefix);
                        List<T> remaining = new LinkedList<>(this.remaining);

                        prefix.add(remaining.remove(i));
                        list.add(() -> new ForPrefix(size, prefix, remaining));
                    }
                } else if (prefix.size() == size) {
                    list.add(() -> Collections.singleton(prefix).spliterator());
                } else {
                    throw new IllegalStateException();
                }

                return list.iterator();
            }

            @Override
            public long estimateSize() {
                return choose(remaining.size(), size);
            }

            @Override
            public String toString() {
                return prefix + ":" + remaining + "/" + Arrays.asList(size);
            }
        }
    }
}
