/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.predicate;

import iprotium.util.AbstractPredicate;
import iprotium.util.Predicate;

/**
 * Or {@link Predicate} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
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
