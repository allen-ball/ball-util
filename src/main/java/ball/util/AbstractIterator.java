/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Abstract {@link Iterator} base class.  Default {@link #remove()}
 * implementation always throws {@link UnsupportedOperationException}.
 *
 * @param       <E>             The type of {@link Object} the
 *                              {@link Iterator} produces.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractIterator<E> implements Iterator<E>,
                                                     Iterable<E>,
                                                     Enumeration<E> {

    /**
     * Sole constructor.
     */
    protected AbstractIterator() { }

    @Override
    public abstract boolean hasNext();

    @Override
    public abstract E next();

    @Override
    public void remove() { throw new UnsupportedOperationException(); }

    /**
     * @return  {@code this} {@link Iterator}
     */
    @Override
    public final Iterator<E> iterator() { return this; }

    /**
     * @see #hasNext()
     */
    @Override
    public final boolean hasMoreElements() { return hasNext(); }

    /**
     * @see #next()
     */
    @Override
    public final E nextElement() { return next(); }
}