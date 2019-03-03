/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.TreeMap;

/**
 * {@link java.util.SortedMap} implementation which provides
 * {@link String#CASE_INSENSITIVE_ORDER} look-up by name to the
 * corresponding {@link Enum}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class EnumLookupMap extends TreeMap<String,Enum<?>> {
    private static final long serialVersionUID = -3569976523862652175L;

    /**
     * Sole constructor.
     *
     * @param   types           The {@link Enum} types to include.
     */
    @SafeVarargs
    public EnumLookupMap(Class<? extends Enum<?>>... types) {
        super(String.CASE_INSENSITIVE_ORDER);

        for (Class<? extends Enum<?>> type : types) {
            for (Enum<?> constant : type.getEnumConstants()) {
                put(constant.name(), constant);
            }
        }
    }
}
