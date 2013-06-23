/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.util.ComparableUtil;

/**
 * Abstract {@link Order} base class for ordering {@link Class} objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class ClassOrder extends Order<Class<?>> {

    /**
     * See {@link Class#getName()}.
     */
    public static final ClassOrder NAME = new Name();

    /**
     * See {@link Class#isAssignableFrom(Class)}.
     */
    public static final ClassOrder INHERITANCE = new Inheritance();

    /**
     * Sole constructor.
     */
    protected ClassOrder() { super(); }

    private static class Name extends ClassOrder {
        private static final long serialVersionUID = 4347942810655992188L;

        public Name() { super(); }

        @Override
        public int compare(Class<?> left, Class<?> right) {
            return ComparableUtil.compare(getName(left), getName(right));
        }

        private String getName(Class<?> type) {
            return (type != null) ? type.getName() : null;
        }
    }

    private static class Inheritance extends Name {
        private static final long serialVersionUID = 6763372029100680642L;

        public Inheritance() { super(); }

        @Override
        public int compare(Class<?> left, Class<?> right) {
            int difference =
                intValue(left.isAssignableFrom(right))
                - intValue(right.isAssignableFrom(left));

            return (difference != 0) ? difference : super.compare(left, right);
        }
    }
}
