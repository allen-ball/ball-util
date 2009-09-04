/*
 * $Id: ClassOrder.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * Abstract {@link Order} base class for ordering {@link Class} objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
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
            return left.getName().compareTo(right.getName());
        }
    }

    private static class Inheritance extends Name {
        private static final long serialVersionUID = 6763372029100680642L;

        public Inheritance() { super(); }

        @Override
        public int compare(Class<?> left, Class<?> right) {
            int comparison =
                (left.isAssignableFrom(right) ? +1 : 0)
                + (right.isAssignableFrom(left) ? -1 : 0);

            return (comparison != 0) ? comparison : super.compare(left, right);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
