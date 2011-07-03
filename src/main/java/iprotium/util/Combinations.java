/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link Iterable} implementation provides an {@link Iterator} for all
 * combinations of a {@link Collection}.
 *
 * @param       <E>             The type of {@link Object} the superset
 *                              {@link Collection} contains.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class Combinations<E> extends Permutations<E> {
    private final int count;

    /**
     * Sole constructor.
     *
     * @param   collection      The superset {@link Collection}.
     * @param   count           The count of each combination.
     */
    public Combinations(Collection<? extends E> collection, int count) {
        super(collection);

        if (count > 0) {
            this.count = count;
        } else {
            throw new IllegalArgumentException("count=" + count);
        }
    }

    @Override
    public Iterator<List<E>> iterator() {
        return new IterableImpl(new ListImpl(), list).iterator();
    }

    private class IterableImpl implements Iterable<List<E>> {
        private final List<E> prefix;
        private final List<E> list;

        public IterableImpl(List<E> prefix, List<E> list) {
            this.prefix = prefix;
            this.list = list;
        }

        @Override
        public Iterator<List<E>> iterator() {
            Iterable<List<E>> iterable = null;
            int required = count - prefix.size();

            if (required > 0) {
                if (required > 1) {
                    iterable = new Branch(prefix, list);
                } else {
                    iterable = new Leaf(prefix, list);
                }
            } else {
                throw new IllegalStateException();
            }

            return iterable.iterator();
        }

        private class Branch implements Iterable<List<E>> {
            private final List<E> prefix;
            private final List<E> list;

            public Branch(List<E> prefix, List<E> list) {
                this.prefix = prefix;
                this.list = list;
            }

            @Override
            public Iterator<List<E>> iterator() {
                Collection<Iterable<List<E>>> collection =
                    new LinkedList<Iterable<List<E>>>();

                for (int i = 0, n = list.size(); i < n; i += 1) {
                    List<E> combination = new ListImpl(prefix, list.get(i));
                    List<E> rest = list.subList(i + 1, n);

                    collection.add(new IterableImpl(combination, rest));
                }

                return new SequenceIterator<List<E>>(collection);
            }
        }

        private class Leaf extends LinkedList<List<E>> {
            private static final long serialVersionUID = -1915066076900671781L;

            public Leaf(List<E> prefix, List<E> list) {
                super();

                for (E element : list) {
                    add(new ListImpl(prefix, element));
                }
            }
        }
    }
}
