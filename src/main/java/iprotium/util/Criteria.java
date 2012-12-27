/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Criteria} interface.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public interface Criteria {

    /**
     * Method to test if an {@link Object} matches {@code this}
     * {@link Criteria}.
     *
     * @return  {@code true} if the argument {@link Object} satisfies the
     *          {@link Criteria}; {@code false} otherwise.
     */
    public boolean match(Object object);
}
