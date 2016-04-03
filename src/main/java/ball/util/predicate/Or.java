/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.predicate;

import ball.util.AbstractCompoundPredicate;
import ball.util.Predicate;

/**
 * Or {@link Predicate} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Or<T> extends AbstractCompoundPredicate<T> {

    /**
     * Sole constructor.
     *
     * @param   predicates      The {@link Predicate}s to {@code or}.
     */
    @SafeVarargs
    @SuppressWarnings({ "unchecked", "varargs" })
    public Or(Predicate<T>... predicates) { super(predicates); }

    @Override
    public boolean apply(T object) {
        boolean result = list().isEmpty();

        for (Predicate<T> predicate : list()) {
            result |= predicate.apply(object);

            if (result) {
                break;
            }
        }

        return result;
    }
}
