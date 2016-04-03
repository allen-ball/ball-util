/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
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
public class Not extends AbstractPredicate<Object> {
    private final Predicate<Object> predicate;

    /**
     * Sole constructor.
     *
     * @param   predicate       The {@link Predicate} to {@code not}.
     */
    public Not(Predicate<Object> predicate) {
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
