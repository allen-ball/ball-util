/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

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
    public static <T> Iterable<T> ofType(Class<T> type, Iterable<?> iterable) {
        return ofType(type, iterable.iterator());
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
    public static <T> Iterable<T> ofType(Class<T> type, Iterator<?> iterator) {
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
    public static <T> Iterable<T> ofType(Class<T> type,
                                         Enumeration<?> enumeration) {
        return ofType(type, asIterable(enumeration));
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
        private final Iterator<?> iterator;
        private final LinkedList<T> list = new LinkedList<T>();

        public TypeFilter(Class<? extends T> type, Iterator<?> iterator) {
            if (type != null) {
                this.type = type;
            } else {
                throw new NullPointerException("type");
            }

            if (iterator != null) {
                this.iterator = iterator;
            } else {
                throw new NullPointerException("iterator");
            }
        }

        @Override
        public boolean hasNext() {
            synchronized (this) {
                while (list.isEmpty()) {
                    if (iterator.hasNext()) {
                        Object next = iterator.next();

                        if (next != null
                            && type.isAssignableFrom(next.getClass())) {
                            list.add(type.cast(next));
                        }
                    } else {
                        break;
                    }
                }
            }

            return (! list.isEmpty());
        }

        @Override
        public T next() {
            T next = null;

            synchronized (this) {
                next = hasNext() ? list.remove() : type.cast(iterator.next());
            }

            return next;
        }
    }
}
