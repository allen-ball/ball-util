/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.LinkedHashSet;

/**
 * {@link LinkedHashSet} implementation that calculates the superclasses and
 * super-interfaces of a {@link Class}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class SuperclassSet extends LinkedHashSet<Class<?>> {
    private static final long serialVersionUID = -3268498163976821406L;

    /**
     * Sole constructor.
     *
     * @param   type            The {@link Class}.
     */
    public SuperclassSet(Class<?> type) {
        super();

        addSuperclassesOf(type);
        addInterfacesOf(type);
    }

    private void addSuperclassesOf(Class<?> type) {
        if (type != null) {
            add(type);
            addSuperclassesOf(type.getSuperclass());
        }
    }

    private void addInterfacesOf(Class<?> type) {
        if (type != null) {
            for (Class<?> supertype : type.getInterfaces()) {
                addSuperclassesOf(supertype);
            }

            addInterfacesOf(type.getSuperclass());
        }
    }
}
