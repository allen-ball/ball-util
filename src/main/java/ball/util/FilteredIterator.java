/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.LinkedList;

import static ball.util.StringUtil.NIL;

/**
 * Filtered {@link Iterator} implementation.
 *
 * @param       <E>             The type of {@link Object} this
 *                              {@link Iterator} produces.
 *
 * @see IterableUtil#filter(Predicate,Iterable)
 * @see IterableUtil#filter(Predicate,Iterator)
 * @see IterableUtil#filter(Predicate,Enumeration)
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class FilteredIterator<E> extends AbstractIterator<E> {
    private final Predicate<E> predicate;
    private final Iterator<? extends E> iterator;
    private final LinkedList<E> list = new LinkedList<E>();

    /**
     * Construct a {@link FilteredIterator} from the argument
     * {@link Iterable}.
     *
     * @param   predicate       The {@link Predicate}.
     * @param   iterable        The {@link Iterable}.
     */
    @ConstructorProperties({ "predicate", NIL })
    public FilteredIterator(Predicate<E> predicate,
                            Iterable<? extends E> iterable) {
        this(predicate, iterable.iterator());
    }

    /**
     * Construct a {@link FilteredIterator} from the argument
     * {@link Iterator}.
     *
     * @param   predicate       The {@link Predicate}.
     * @param   iterator        The {@link Iterator}.
     */
    @ConstructorProperties({ "predicate", NIL })
    public FilteredIterator(Predicate<E> predicate,
                            Iterator<? extends E> iterator) {
        if (predicate != null) {
            this.predicate = predicate;
        } else {
            throw new NullPointerException("predicate");
        }

        if (iterator != null) {
            this.iterator = iterator;
        } else {
            throw new NullPointerException("iterator");
        }
    }

    /**
     * Method to get the {@link Predicate}.
     *
     * @return  The {@link Predicate}.
     */
    protected Predicate<E> getPredicate() { return predicate; }

    @Override
    public boolean hasNext() {
        synchronized (this) {
            while (list.isEmpty()) {
                if (iterator.hasNext()) {
                    E next = iterator.next();

                    if (getPredicate().apply(next)) {
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
