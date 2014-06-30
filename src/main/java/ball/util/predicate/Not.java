/*
 * $Id$
 *
 * Copyright 2012 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.predicate;

import ball.util.AbstractPredicate;
import ball.util.Predicate;

/**
 * Not {@link Predicate} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
