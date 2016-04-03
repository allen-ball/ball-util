/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.predicate;

import ball.util.AbstractCompoundPredicate;
import ball.util.Predicate;

/**
 * And {@link Predicate} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class And<T> extends AbstractCompoundPredicate<T> {

    /**
     * Sole constructor.
     *
     * @param   predicates      The {@link Predicate}s to {@code and}.
     */
    @SafeVarargs
    @SuppressWarnings({ "unchecked", "varargs" })
    public And(Predicate<T>... predicates) { super(predicates); }

    @Override
    public boolean apply(T object) {
        boolean result = true;

        for (Predicate<T> predicate : list()) {
            result &= predicate.apply(object);

            if (! result) {
                break;
            }
        }

        return result;
    }
}
