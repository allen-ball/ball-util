/*
 * $Id$
 *
 * Copyright 2011 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools;

import iprotium.util.AbstractIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * Abstract base class for {@link Diagnostic} remedies.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class Remedy {
    private static class Lazy {
        public static final SortedMap<String,Remedy> MAP;

        static {
            TreeMap<String,Remedy> map = new TreeMap<String,Remedy>();

            for (Remedy remedy : ServiceLoader.load(Remedy.class)) {
                map.put(remedy.getCode(), remedy);
            }

            MAP = Collections.unmodifiableSortedMap(map);
        }
    }

    /**
     * Method to get the {@link SortedMap} of available {@link Remedy}s
     * (keyed by {@link Diagnostic} code).
     *
     * @return  The {@link SortedMap}.
     */
    public static SortedMap<String,Remedy> getRemedyMap() { return Lazy.MAP; }

    /**
     * Sole constructor.
     */
    protected Remedy() { }

    /**
     * See {@link Diagnostic#getCode()}.
     */
    public abstract String getCode();

    /**
     * Method to provide a prescriptive message.
     *
     * @param   diagnostic      The {@link Diagnostic} to remedy.
     * @param   fm              The {@link StandardJavaFileManager}.
     * @param   classes         The {@link SortedSet} of output classes.
     *
     * @return  The remedy ({@link String}).
     */
    public abstract String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                                 StandardJavaFileManager fm,
                                 SortedSet<Class<?>> classes);

    /**
     * Method to get a {@link Class} for the argument {@code name}.
     *
     * @param   name            The name of the {@link Class}.
     * @param   classes         See {@link #getRx(Diagnostic,StandardJavaFileManager,SortedSet)}.
     *
     * @return  A {@link Class} corresponding to the name if one could be
     *          found; {@code null} otherwise.
     */
    protected Class<?> getClassForNameFrom(String name,
                                           SortedSet<Class<?>> classes) {
        Class<?> type = null;

        if (type == null) {
            try {
                type = Class.forName(name);
            } catch (ClassNotFoundException exception) {
            }
        }

        if (type == null) {
            type = new ClassMap(classes).get(name);
        }

        if (type == null) {
            Package[] pkgs = Package.getPackages();

            for (Package pkg : pkgs) {
                try {
                    type = Class.forName(pkg.getName() + "." + name);
                } catch (ClassNotFoundException exception) {
                }

                if (type != null) {
                    break;
                }
            }
        }

        return type;
    }

    /**
     * {@link TreeMap} implementation to map {@link Class#getSimpleName()}
     * to {@link Class}.
     */
    protected class ClassMap extends TreeMap<String,Class<?>> {
        private static final long serialVersionUID = -2499643033389673830L;

        /**
         * Sole constructor.
         *
         * @param       values          The {@link Iterable} of
         *                              {@link Class} values.
         */
        public ClassMap(Iterable<Class<?>> values) {
            super();

            for (Class<?> value : values) {
                String key = value.getSimpleName();

                if (key != null && (! containsKey(key))) {
                    put(key, value);
                }
            }
        }
    }
}
