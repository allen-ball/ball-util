/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * Abstract {@link Predicate} base class.
 *
 * @param       <T>             The type to be tested.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractPredicate<T> implements Predicate<T> {

    /**
     * Sole constructor.
     */
    protected AbstractPredicate() { }

    @Override
    public String toString() { return super.toString(); }
}
