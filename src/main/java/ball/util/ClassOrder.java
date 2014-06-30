/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import ball.util.ComparableUtil;

/**
 * Abstract {@link Order} base class for ordering {@link Class} objects.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
        private static final long serialVersionUID = -7367492973691125060L;

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
        private static final long serialVersionUID = 2366978649721864109L;

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
