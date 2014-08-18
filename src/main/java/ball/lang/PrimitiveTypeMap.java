/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides mapping of Java primitive {@link Class}es to their "wrapper"
 * {@link Class}es.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PrimitiveTypeMap extends HashMap<Class<?>,Class<?>> {
    private static final long serialVersionUID = -4786403682565499928L;

    /**
     * Unmodifiable instance of a {@link PrimitiveTypeMap}.
     */
    public static final Map<Class<?>,Class<?>> INSTANCE =
        Collections.unmodifiableMap(new PrimitiveTypeMap());

    /**
     * Sole constructor.
     */
    public PrimitiveTypeMap() {
        super();

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
