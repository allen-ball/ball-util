/*
 * $Id: SequenceIterator.java,v 1.1 2010-11-09 05:27:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Sequence {@link Iterator} implementation.
 *
 * @param       <E>             The type of {@link Object} this
 *                              {@link Iterator} and the member
 *                              {@link Iterator}s produces.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class SequenceIterator<E> extends AbstractIterator<E> {
    private final Iterator<Iterable<E>> sequence;
    private Iterator<E> iterator = null;

    /**
     * Construct a {@link SequenceIterator} from an {@link Iterable} of
     * {@link Iterable}s.
     *
     * @param   iterable        The {@link Iterable} of {@link Iterable}s.
     */
    public SequenceIterator(Iterable<Iterable<E>> iterable) {
        sequence = iterable.iterator();
        iterator = sequence.hasNext() ? sequence.next().iterator() : null;
    }

    /**
     * Construct a {@link SequenceIterator} from the argument
     * {@link Iterable}s.
     *
     * @param   iterables       The array of {@link Iterable}s.
     */
    public SequenceIterator(Iterable<E>... iterables) {
        this(Arrays.asList(iterables));
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = false;

        synchronized (this) {
            while (iterator != null && (! iterator.hasNext())) {
                iterator =
                    sequence.hasNext() ? sequence.next().iterator() : null;
            }

            hasNext = (iterator != null && iterator.hasNext());
        }

        return hasNext;
    }

    @Override
    public E next() {
        E next = null;

        synchronized (this) {
            if (hasNext()) {
                next = iterator.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        return next;
    }

    @Override
    public void remove() {
        synchronized (this) {
            if (iterator != null) {
                iterator.remove();
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
