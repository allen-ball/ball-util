/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compound {@link Predicate} abstract base class.
 *
 * @param       <T>             The type to be tested.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractCompoundPredicate<T>
                      extends AbstractPredicate<T> {
    private final List<Predicate<T>> list = new ArrayList<Predicate<T>>();

    /**
     * Sole constructor.
     *
     * @param   predicates      The {@link Predicate}s to include.
     */
    @SafeVarargs
    @SuppressWarnings({ "unchecked", "varargs" })
    protected AbstractCompoundPredicate(Predicate<T>... predicates) {
        super();

        Collections.addAll(list, predicates);
    }

    protected List<Predicate<T>> list() { return list; }
}
