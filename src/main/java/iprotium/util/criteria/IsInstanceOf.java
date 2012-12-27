/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.criteria;

import iprotium.util.AbstractCriteria;
import iprotium.util.ClassOrder;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * {@link iprotium.util.Criteria} to specify types ({@link Class}es).
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class IsInstanceOf extends And {

    /**
     * Sole constructor.
     *
     * @param   types           The {@link Class}es to filter for.
     */
    public IsInstanceOf(Class<?>... types) {
        super(IsNotNull.getDefault(), new Impl(types));
    }

    private static class Impl extends AbstractCriteria {
        private final TreeSet<Class<?>> set =
            new TreeSet<Class<?>>(ClassOrder.INHERITANCE);

        public Impl(Class<?>... types) {
            super();

            set.addAll(Arrays.asList(types));
        }

        @Override
        public boolean match(Object object) {
            boolean match = set.isEmpty();

            for (Class<?> type : set) {
                match |= type.isAssignableFrom(object.getClass());

                if (match) {
                    break;
                }
            }

            return match;
        }
    }
}
