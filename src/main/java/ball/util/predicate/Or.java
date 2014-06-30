/*
 * $Id$
 *
 * Copyright 2012 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.predicate;

import ball.util.AbstractPredicate;
import ball.util.Predicate;

/**
 * Or {@link Predicate} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Or extends AbstractPredicate {
    private final Predicate[] predicates;

    /**
     * Sole constructor.
     *
     * @param   predicates      The {@link Predicate}s to {@code or}.
     */
    public Or(Predicate... predicates) {
        super();

        if (predicates != null) {
            this.predicates = predicates;
        } else {
            throw new NullPointerException("predicate");
        }
    }

    @Override
    public boolean apply(Object object) {
        boolean result = (predicates.length == 0);

        for (Predicate predicate : predicates) {
            result |= predicate.apply(object);

            if (result) {
                break;
            }
        }

        return result;
    }
}
