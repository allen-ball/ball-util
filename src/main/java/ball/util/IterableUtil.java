/*
 * $Id$
 *
 * Copyright 2012 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * {@link Iterable} (and {@link Iterator} and {@link Enumeration}) utility
 * methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class IterableUtil {
    private IterableUtil() { }

    /**
     * Static method to wrap a {@link Iterator} as a {@link Iterable}.
     *
     * @param   <E>             The element type.
     * @param   iterator        The {@link Iterator}.
     *
     * @return  An {@link Iterable} that wraps the {@link Iterator}.
     */
    public static <E> Iterable<E> asIterable(Iterator<E> iterator) {
        return new FromIterator<>(iterator);
    }

    /**
     * Static method to wrap a {@link Enumeration} as a {@link Iterable}.
     *
     * @param   <E>             The element type.
     * @param   enumeration     The {@link Enumeration}.
     *
     * @return  An {@link Iterable} that wraps the {@link Enumeration}.
     */
    public static <E> Iterable<E> asIterable(Enumeration<E> enumeration) {
        return new FromEnumeration<>(enumeration);
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
}
