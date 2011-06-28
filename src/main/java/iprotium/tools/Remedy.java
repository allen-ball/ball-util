/*
 * $Id: Remedy.java,v 1.1 2011-06-28 04:17:40 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
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
 * @version $Revision: 1.1 $
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
     * @return  The remedy (@link String}).
     */
    public abstract String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                                 StandardJavaFileManager fm,
                                 SortedSet<Class<?>> classes);
}
/*
 * $Log: not supported by cvs2svn $
 */
