/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import ball.lang.PrimitiveTypeMap;
import java.util.TreeMap;

/**
 * Conversion utility based on {@link Factory}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Converter extends TreeMap<Class<?>,Factory<?>> {
    private static final long serialVersionUID = -3178874315076658917L;

    private static final Converter INSTANCE = new Converter();

    private Converter() {
        super(ClassOrder.NAME);

        put(String.class, new Factory<>(String.class));

        PrimitiveTypeMap.INSTANCE.values()
            .stream()
            .forEach(t -> put(t, new Factory<>(t)));
    }

    /**
     * Static method to convert the argument to the specified type
     * ({@link Class}).
     *
     * @param   <T>             The target type.
     * @param   from            The source value.
     * @param   type            The target type ({@link Class}).
     *
     * @return  The converted value.
     */
    public static <T> T convertTo(Object from, Class<T> type) {
        Object to = null;

        try {
            if (from == null || type.isAssignableFrom(from.getClass())) {
                to = from;
            } else {
                to =
                    INSTANCE.
                    computeIfAbsent(type,
                                    k -> (INSTANCE.values()
                                          .stream()
                                          .filter(t -> k.isAssignableFrom(t.getType()))
                                          .findFirst()
                                          .orElse(new Factory<>(k))))
                    .getInstance(from);
            }
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }

        return type.cast(to);
    }
}
