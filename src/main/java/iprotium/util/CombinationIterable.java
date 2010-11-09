/*
 * $Id: CombinationIterable.java,v 1.1 2010-11-09 05:27:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * {@link Iterable} implementation provides an {@link Iterator} for all
 * combinations of a {@link Collection}.
 *
 * @param       <E>             The type of {@link Object} the superset
 *                              {@link Collection} contains.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class CombinationIterable<E> implements Iterable<List<E>> {
    private final List<E> list;
    private final int count;

    /**
     * Sole constructor.
     *
     * @param   collection      The superset {@link Collection}.
     * @param   count           The count of each combination.
     */
    public CombinationIterable(Collection<? extends E> collection, int count) {
        this.list = Collections.unmodifiableList(new ArrayList<E>(collection));

        if (count > 0) {
            this.count = count;
        } else {
            throw new IllegalArgumentException("count=" + count);
        }
    }

    @Override
    public Iterator<List<E>> iterator() {
        return new CombinationIterator(list, count);
    }

    private class CombinationIterator extends AbstractIterator<List<E>> {
        private final List<E> list;
        private final Iterator<int[]> iterator;

        public CombinationIterator(List<E> list, int count) {
            super();

            this.list = list;
            this.iterator =
                new IndicesIterable(new int[] { }, 0, list.size(), count)
                .iterator();
        }

        @Override
        public boolean hasNext() { return iterator.hasNext(); }

        @Override
        public List<E> next() {
            ArrayList<E> next = null;

            synchronized (this) {
                if (hasNext()) {
                    next = new ArrayList<E>();

                    for (int i : iterator.next()) {
                        next.add(list.get(i));
                    }
                } else {
                    throw new NoSuchElementException();
                }
            }

            return next;
        }
    }

    private class IndicesIterable implements Iterable<int[]> {
        private final int[] prefix;
        private final int start;
        private final int size;
        private final int count;

        public IndicesIterable(int[] prefix, int start, int size, int count) {
            this.prefix = prefix;
            this.start = start;
            this.size = size;

            if (count > 0) {
                this.count = count;
            } else {
                throw new IllegalArgumentException("count=" + count);
            }
        }

        @Override
        public Iterator<int[]> iterator() {
            Iterator<int[]> iterator = null;

            if (count > 1) {
                ArrayList<Iterable<int[]>> list =
                    new ArrayList<Iterable<int[]>>();

                for (int i = start; i < size; i += 1) {
                    list.add(new IndicesIterable(append(prefix, i),
                                                 i + 1, size, count - 1));
                }

                iterator = new SequenceIterator<int[]>(list);
            } else if (count == 1) {
                ArrayList<int[]> list = new ArrayList<int[]>();

                for (int i = start; i < size; i += 1) {
                    list.add(append(prefix, i));
                }

                iterator = list.iterator();
            } else {
                throw new IllegalStateException();
            }

            return iterator;
        }

        private int[] append(int[] array, int element) {
            array = Arrays.copyOf(array, array.length + 1);
            array[array.length - 1] = element;

            return array;
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
