/*
 * $Id$
 *
 * Copyright 2017 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.TreeMap;
import java.util.stream.Stream;

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
    @SafeVarargs @SuppressWarnings({ "varargs" })
    public EnumLookupMap(Class<? extends Enum<?>>... types) {
        super(String.CASE_INSENSITIVE_ORDER);

        Stream.of(types)
            .flatMap(t -> Stream.of(t.getEnumConstants()))
            .forEach(t -> put(t.name(), t));
    }
}
