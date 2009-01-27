/*
 * $Id: SuperclassSet.java,v 1.2 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.TreeSet;

/**
 * TreeSet implementation that calculates the superclasses and
 * super-interfaces of a Class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class SuperclassSet extends TreeSet<Class<?>> {
    private static final long serialVersionUID = -8076508406076608539L;

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
 * Revision 1.1  2008/10/27 21:56:11  ball
 * Initial writing.
 *
 */
