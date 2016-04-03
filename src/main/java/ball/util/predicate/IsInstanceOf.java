/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.predicate;

import ball.util.AbstractPredicate;
import ball.util.ClassOrder;
import java.util.Collections;
import java.util.TreeSet;

/**
 * {@link ball.util.Predicate} to specify types ({@link Class}es).
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class IsInstanceOf extends And<Object> {

    /**
     * Sole constructor.
     *
     * @param   types           The {@link Class}es to filter for.
     */
    @SuppressWarnings({ "unchecked" })
    public IsInstanceOf(Class<?>... types) {
        super(IsNotNull.getDefault(), new Impl(types));
    }

    private static class Impl extends AbstractPredicate<Object> {
        private final TreeSet<Class<?>> set =
            new TreeSet<Class<?>>(ClassOrder.INHERITANCE);

        public Impl(Class<?>... types) {
            super();

            Collections.addAll(set, types);
        }

        @Override
        public boolean apply(Object object) {
            boolean result = set.isEmpty();

            for (Class<?> type : set) {
                result |= type.isAssignableFrom(object.getClass());

                if (result) {
                    break;
                }
            }

            return result;
        }
    }
}
