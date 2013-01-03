/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.util.predicate.IsInstanceOf;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * {@link Iterable} (and {@link Iterator} and {@link Enumeration}) utility
 * methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class IterableUtil {
    private IterableUtil() { }

    /**
     * Static method to wrap a {@link Iterator} as a {@link Iterable}.
     *
     * @param   iterator        The {@link Iterator}.
     *
     * @return  An {@link Iterable} that wraps the {@link Iterator}.
     */
    public static <E> Iterable<E> asIterable(Iterator<E> iterator) {
        return new FromIterator<E>(iterator);
    }

    /**
     * Static method to wrap a {@link Enumeration} as a {@link Iterable}.
     *
     * @param   enumeration     The {@link Enumeration}.
     *
     * @return  An {@link Iterable} that wraps the {@link Enumeration}.
     */
    public static <E> Iterable<E> asIterable(Enumeration<E> enumeration) {
        return new FromEnumeration<E>(enumeration);
    }

    /**
     * Static method to wrap an {@link Iterable} to filter for a specific
     * type ({@link Class}).  {@link Object}s that are not of the specified
     * type are skipped.
     *
     * @param   type            The type ({@link Class}).
     * @param   iterable        The {@link Iterable}.
     *
     * @return  An {@link Iterable} that wraps the {@link Iterable}.
     */
    public static <T> Iterable<T> filter(Class<T> type, Iterable<?> iterable) {
        return filter(type, iterable.iterator());
    }

    /**
     * Static method to wrap an {@link Iterator} to filter for a specific
     * type ({@link Class}).  {@link Object}s that are not of the specified
     * type are skipped.
     *
     * @param   type            The type ({@link Class}).
     * @param   iterator        The {@link Iterator}.
     *
     * @return  An {@link Iterable} that wraps the {@link Iterator}.
     */
    public static <T> Iterable<T> filter(Class<T> type, Iterator<?> iterator) {
        return new TypeFilter<T>(type, iterator);
    }

    /**
     * Static method to wrap an {@link Enumeration} to filter for a specific
     * type ({@link Class}).  {@link Object}s that are not of the specified
     * type are skipped.
     *
     * @param   type            The type ({@link Class}).
     * @param   enumeration     The {@link Enumeration}.
     *
     * @return  An {@link Iterable} that wraps the {@link Enumeration}.
     */
    public static <T> Iterable<T> filter(Class<T> type,
                                         Enumeration<?> enumeration) {
        return filter(type, asIterable(enumeration));
    }

    /**
     * Static method to wrap an {@link Iterable} to filter by a specific
     * {@link Predicate}.  {@link Object}s that do not match the
     * {@link Predicate} are skipped.
     *
     * @param   predicate       The {@link Predicate}.
     * @param   iterable        The {@link Iterable}.
     *
     * @return  An {@link Iterable} that wraps the {@link Iterable}.
     */
    public static <T> Iterable<T> filter(Predicate predicate,
                                         Iterable<T> iterable) {
        return filter(predicate, iterable.iterator());
    }

    /**
     * Static method to wrap an {@link Iterator} to filter by a specific
     * {@link Predicate}.  {@link Object}s that do not match the
     * {@link Predicate} are skipped.
     *
     * @param   predicate       The {@link Predicate}.
     * @param   iterator        The {@link Iterator}.
     *
     * @return  An {@link Iterator} that wraps the {@link Iterator}.
     */
    public static <T> Iterable<T> filter(Predicate predicate,
                                         Iterator<T> iterator) {
        return new FilteredIterator<T>(predicate, iterator);
    }

    /**
     * Static method to wrap an {@link Enumeration} to filter by a specific
     * {@link Predicate}.  {@link Object}s that do not match the
     * {@link Predicate} are skipped.
     *
     * @param   predicate       The {@link Predicate}.
     * @param   enumeration     The {@link Enumeration}.
     *
     * @return  An {@link Enumeration} that wraps the {@link Enumeration}.
     */
    public static <T> Iterable<T> filter(Predicate predicate,
                                         Enumeration<T> enumeration) {
        return filter(predicate, asIterable(enumeration));
    }

    private static class FromEnumeration<E> extends AbstractIterator<E> {
        private final Enumeration<E> enumeration;

        public FromEnumeration(Enumeration<E> enumeration) {
            this.enumeration = enumeration;
        }

        @Override
        public boolean hasNext() { return enumeration.hasMoreElements(); }

        @Override
        public E next() { return enumeration.nextElement(); }
    }

    private static class FromIterator<E> extends AbstractIterator<E> {
        private final Iterator<E> iterator;

        public FromIterator(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() { return iterator.hasNext(); }

        @Override
        public E next() { return iterator.next(); }

        @Override
        public void remove() { iterator.remove(); }
    }

    private static class TypeFilter<T> extends AbstractIterator<T> {
        private final Class<? extends T> type;
        private final FilteredIterator<Object> iterator;

        public TypeFilter(Class<? extends T> type, Iterator<?> iterator) {
            this.type = type;
            this.iterator =
                new FilteredIterator<Object>(new IsInstanceOf(type), iterator);
        }

        @Override
        public boolean hasNext() { return iterator.hasNext(); }

        @Override
        public T next() { return type.cast(iterator.next()); }
    }
}
