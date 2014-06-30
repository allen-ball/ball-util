/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Package search path mechanism.
 *
 * @param       <T>             The type of {@link Object} represented by
 *                              {@link Class}es located on this search
 *                              path.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PackageSearchPath<T> extends LinkedHashSet<Package> {
    private static final long serialVersionUID = 4130977837876384091L;

    /**
     * {@link #DOT} = {@value #DOT}
     */
    protected static final String DOT = ".";

    private final Class<? extends T> superclass;

    /**
     * @param   superclass      The type of {@link Class} to search for.
     * @param   packages        The {@link Package}s to search.
     */
    @ConstructorProperties({ "superclass", "" })
    public PackageSearchPath(Class<? extends T> superclass,
                             Package... packages) {
        this(superclass, Arrays.asList(packages));
    }

    /**
     * @param   superclass      The type of {@link Class} to search for.
     * @param   collection      The {@link Collection} of {@link Package}s
     *                          to search.
     */
    @ConstructorProperties({ "superclass", "" })
    public PackageSearchPath(Class<? extends T> superclass,
                             Collection<Package> collection) {
        super();

        if (superclass != null) {
            this.superclass = superclass;
        } else {
            throw new NullPointerException("superclass");
        }

        addAll(collection);
    }

    /**
     * Method to get the search path super-{@link Class}.
     *
     * @return  The search path super-{@link Class}.
     */
    public Class<? extends T> getSuperclass() { return superclass; }

    /**
     * Method to look-up a {@link Class} on the search path.
     *
     * @param   name            The simple name of the {@link Class}.
     *
     * @return  The {@link Class} matching the name or {@code null} if none
     *          is found.
     */
    public Class<? extends T> getClass(String name) {
        Class<? extends T> cls = null;

        if (cls == null) {
            for (Package pkg : this) {
                cls = getClass(pkg, name);

                if (cls != null) {
                    break;
                }
            }
        }

        if (cls == null) {
            cls = getClass(null, name);
        }

        return cls;
    }

    private Class<? extends T> getClass(Package pkg, String name) {
        if (pkg != null) {
            name = pkg.getName() + DOT + name;
        }

        Class<? extends T> cls = null;

        try {
            cls =
                Class
                .forName(name, false, getSuperclass().getClassLoader())
                .asSubclass(getSuperclass());
        } catch (ClassNotFoundException exception) {
        } catch (ClassCastException exception) {
        }

        return cls;
    }
}
