/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Filtered {@link Iterator} implementation.
 *
 * @param       <E>             The type of {@link Object} this
 *                              {@link Iterator} produces.
 *
 * @see IterableUtil#filter(Criteria,Iterable)
 * @see IterableUtil#filter(Criteria,Iterator)
 * @see IterableUtil#filter(Criteria,Enumeration)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class FilteredIterator<E> extends AbstractIterator<E> {
    private final Criteria criteria;
    private final Iterator<? extends E> iterator;
    private final LinkedList<E> list = new LinkedList<E>();


    /**
     * Construct a {@link FilteredIterator} from the argument
     * {@link Iterable}.
     *
     * @param   criteria        The {@link Criteria}.
     * @param   iterable        The {@link Iterable}.
     */
    public FilteredIterator(Criteria criteria,
                            Iterable<? extends E> iterable) {
        this(criteria, iterable.iterator());
    }

    /**
     * Construct a {@link FilteredIterator} from the argument
     * {@link Iterator}.
     *
     * @param   criteria        The {@link Criteria}.
     * @param   iterator        The {@link Iterator}.
     */
    public FilteredIterator(Criteria criteria,
                            Iterator<? extends E> iterator) {
        if (criteria != null) {
            this.criteria = criteria;
        } else {
            throw new NullPointerException("criteria");
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
                    E next = iterator.next();

                    if (criteria.match(next)) {
                        list.add(next);
                    }
                } else {
                    break;
                }
            }
        }

        return (! list.isEmpty());
    }

    @Override
    public E next() {
        E next = null;

        synchronized (this) {
            next = hasNext() ? list.remove() : iterator.next();
        }

        return next;
    }
}
