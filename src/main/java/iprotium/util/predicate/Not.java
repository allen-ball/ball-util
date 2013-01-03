/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.predicate;

import iprotium.util.AbstractPredicate;
import iprotium.util.Predicate;

/**
 * Not {@link Predicate} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class Not extends AbstractPredicate {
    private final Predicate predicate;

    /**
     * Sole constructor.
     *
     * @param   predicate       The {@link Predicate} to {@code not}.
     */
    public Not(Predicate predicate) {
        super();

        if (predicate != null) {
            this.predicate = predicate;
        } else {
            throw new NullPointerException("predicate");
        }
    }

    @Override
    public boolean apply(Object object) { return (! predicate.apply(object)); }
}
