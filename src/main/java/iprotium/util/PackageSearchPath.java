/*
 * $Id: PackageSearchPath.java,v 1.3 2010-08-23 03:43:54 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

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
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class PackageSearchPath<T> extends LinkedHashSet<Package> {
    private static final long serialVersionUID = 5887744906132617085L;

    private final Class<? extends T> superclass;

    /**
     * @param   superclass      The type of {@link Class} to search for.
     * @param   packages        The {@link Package}s to search.
     */
    public PackageSearchPath(Class<? extends T> superclass,
                             Package... packages) {
        this(superclass, Arrays.asList(packages));
    }

    /**
     * @param   superclass      The type of {@link Class} to search for.
     * @param   collection      The {@link Collection} of {@link Package}s
     *                          to search.
     */
    public PackageSearchPath(Class<? extends T> superclass,
                             Collection<Package> collection) {
        super();

        this.superclass = superclass;

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
            name = pkg.getName() + "." + name;
        }

        Class<? extends T> cls = null;

        try {
            cls = Class.forName(name).asSubclass(getSuperclass());
        } catch (ClassNotFoundException exception) {
        } catch (ClassCastException exception) {
        }

        return cls;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
