/*
 * $Id: SuperclassSet.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.TreeSet;

/**
 * {@link TreeSet} implementation that calculates the superclasses and
 * super-interfaces of a {@link Class}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class SuperclassSet extends TreeSet<Class<?>> {
    private static final long serialVersionUID = -8076508406076608539L;

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
/*
 * $Log: not supported by cvs2svn $
 */
