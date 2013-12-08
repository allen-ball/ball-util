/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Predicate} interface.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface Predicate {

    /**
     * Method to apply {@code this}{@link Predicate} to an {@link Object}.
     *
     * @return  {@code true} if the argument {@link Object} satisfies the
     *          {@link Predicate}; {@code false} otherwise.
     */
    public boolean apply(Object object);
}
