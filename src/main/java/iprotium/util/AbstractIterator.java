/*
 * $Id: AbstractIterator.java,v 1.2 2010-11-09 05:46:28 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Abstract {@link Iterator} base class.  Default {@link #remove()}
 * implementation always throws {@link UnsupportedOperationException}.
 *
 * @param       <E>             The type of {@link Object} the
 *                              {@link Iterator} produces.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractIterator<E> implements Iterator<E>,
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

    @Override
    public final boolean hasMoreElements() { return hasNext(); }

    @Override
    public final E nextElement() { return next(); }
}
/*
 * $Log: not supported by cvs2svn $
 */
