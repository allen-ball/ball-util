/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * {@link SortedMap} implementation populated with Java primitive
 * {@link Class}es mapped to their respective wrapper {@link Class}es.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class PrimitiveClassMap extends TreeMap<Class<?>,Class<?>> {
    private static final long serialVersionUID = -9112243509682936487L;

    /**
     * An unmodifiable instance of {@link PrimitiveClassMap}.
     */
    public static final SortedMap<Class<?>,Class<?>> INSTANCE =
        Collections.unmodifiableSortedMap(new PrimitiveClassMap());

    private PrimitiveClassMap() {
        super(ClassOrder.NAME);

        put(Boolean.TYPE, Boolean.class);
        put(Byte.TYPE, Byte.class);
        put(Character.TYPE, Character.class);
        put(Double.TYPE, Double.class);
        put(Float.TYPE, Float.class);
        put(Integer.TYPE, Integer.class);
        put(Long.TYPE, Long.class);
        put(Short.TYPE, Short.class);
        put(Void.TYPE, Void.class);
    }
}
