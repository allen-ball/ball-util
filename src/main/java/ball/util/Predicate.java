/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * {@link Predicate} interface.
 *
 * @param       <T>             The type to be tested.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface Predicate<T> {

    /**
     * Method to apply {@code this}{@link Predicate} to an {@link Object}.
     *
     * @param   object          The (generic) {@link Object} to test.
     *
     * @return  {@code true} if the argument {@link Object} satisfies the
     *          {@link Predicate}; {@code false} otherwise.
     */
    public boolean apply(T object);
}
