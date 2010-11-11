/*
 * $Id: Combinations.java,v 1.1 2010-11-11 08:22:38 ball Exp $
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
public class Combinations<E> implements Iterable<List<E>> {
    private final List<E> list;
    private final int count;

    /**
     * Sole constructor.
     *
     * @param   collection      The superset {@link Collection}.
     * @param   count           The count of each combination.
     */
    public Combinations(Collection<? extends E> collection, int count) {
        this.list = Collections.unmodifiableList(new ArrayList<E>(collection));

        if (count > 0) {
            this.count = count;
        } else {
            throw new IllegalArgumentException("count=" + count);
        }
    }

    @Override
    public Iterator<List<E>> iterator() {
        return new IterableImpl(new Combination(), list).iterator();
    }

    private class Combination extends ArrayList<E> {
        private static final long serialVersionUID = 5029981695321289139L;

        public Combination() { super(count); }

        public int remaining() { return Math.max(count - size(), 0); }

        public Combination combine(E element) {
            Combination clone = clone();

            clone.add(element);

            return clone;
        }

        @Override
        public Combination clone() { return (Combination) super.clone(); }
    }

    private class IterableImpl implements Iterable<List<E>> {
        private final Combination combination;
        private final List<E> list;

        public IterableImpl(Combination combination, List<E> list) {
            this.combination = combination;
            this.list = list;
        }

        @Override
        public Iterator<List<E>> iterator() {
            Iterator<List<E>> iterator = null;

            if (combination.remaining() > 0) {
                if (combination.remaining() > 1) {
                    iterator = new Branch(combination, list).iterator();
                } else {
                    iterator = new Leaf(combination, list).iterator();
                }
            } else {
                throw new IllegalStateException();
            }

            return iterator;
        }
    }

    private class Branch implements Iterable<List<E>> {
        private ArrayList<Iterable<List<E>>> collection =
            new ArrayList<Iterable<List<E>>>();

        public Branch(Combination prefix, List<E> list) {
            for (int i = 0, n = list.size(); i < n; i += 1) {
                Combination combination = prefix.combine(list.get(i));
                List<E> subList = list.subList(i + 1, n);

                collection.add(new IterableImpl(combination, subList));
            }
        }

        @Override
        public Iterator<List<E>> iterator() {
            return new SequenceIterator<List<E>>(collection);
        }
    }

    private class Leaf extends ArrayList<List<E>> {
        private static final long serialVersionUID = 7484357465317513535L;

        public Leaf(Combination prefix, List<E> list) {
            super();

            for (E element : list) {
                add(prefix.combine(element));
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
