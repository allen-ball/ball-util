/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.TreeSet;

/**
 * {@link TreeSet} implementation that calculates the superclasses and
 * super-interfaces of a {@link Class}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class SuperclassSet extends TreeSet<Class<?>> {
    private static final long serialVersionUID = -3268498163976821406L;

    /**
     * Sole constructor.
     *
     * @param   type            The {@link Class}.
     */
    public SuperclassSet(Class<?> type) {
        super(ClassOrder.NAME);

        addSuperclassesOf(type);
    }

    private void addSuperclassesOf(Class<?> type) {
        if (type != null) {
            if (! contains(type)) {
                add(type);

                addSuperclassesOf(type.getSuperclass());
                addSuperclassesOf(type.getInterfaces());
            }
        }
    }

    private void addSuperclassesOf(Class<?>[] types) {
        for (Class<?> type : types) {
            addSuperclassesOf(type);
        }
    }
}
