/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link Iterable} implementation provides an {@link Iterator} for all
 * permutations of a {@link Collection}.
 *
 * @param       <E>             The type of {@link Object} the superset
 *                              {@link Collection} contains.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Permutations<E> implements Iterable<List<E>> {
    protected final List<E> list;

    /**
     * Sole constructor.
     *
     * @param   collection      The superset {@link Collection}.
     */
    public Permutations(Collection<? extends E> collection) {
        this.list = Collections.unmodifiableList(new ArrayList<E>(collection));
    }

    @Override
    public Iterator<List<E>> iterator() {
        Iterable<List<E>> iterable =
            ((! list.isEmpty())
                 ? new IterableImpl(new ListImpl(), list)
                 : Collections.<List<E>>emptyList());

        return iterable.iterator();
    }

    protected class ListImpl extends ArrayList<E> {
        private static final long serialVersionUID = 622428941474888801L;

        public ListImpl(Collection<? extends E> collection, E element) {
            this();

            addAll(collection);
            add(element);
        }

        public ListImpl(Collection<? extends E> c1,
                        Collection<? extends E> c2) {
            this();

            addAll(c1);
            addAll(c2);
        }

        public ListImpl() { super(list.size()); }
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
            Collection<Iterable<List<E>>> collection = new LinkedList<>();

            if (! list.isEmpty()) {
                for (int i = 0, n = list.size(); i < n; i += 1) {
                    E element = list.get(i);

                    if (! list.subList(0, i).contains(element)) {
                        Iterable<List<E>> iterable = null;
                        List<E> permutation = new ListImpl(prefix, element);

                        if (n > 1) {
                            List<E> sublist =
                                new ListImpl(list.subList(0, i),
                                             list.subList(i + 1, n));

                            iterable = new IterableImpl(permutation, sublist);
                        } else {
                            iterable = Collections.singleton(permutation);
                        }

                        collection.add(iterable);
                    }
                }
            } else {
                collection.add(Collections.singleton(prefix));
            }

            return new SequenceIterator<>(collection);
        }
    }
}
