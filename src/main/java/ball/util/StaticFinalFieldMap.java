/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static ball.util.ClassUtil.isFinal;
import static ball.util.ClassUtil.isPublic;
import static ball.util.ClassUtil.isStatic;

/**
 * {@link TreeMap} implementation that maps a {@link Class}'s constant
 * ("{@code public static final}") fields' names to their values.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class StaticFinalFieldMap<T> extends TreeMap<String,T> {
    private static final long serialVersionUID = 287075666237483203L;

    /**
     * @param   type            The {@link Class} containing the constant
     *                          fields.
     * @param   ofType          The type ({@link Class}) of constant fields
     *                          to include in the map.
     */
    public StaticFinalFieldMap(Class<?> type, Class<T> ofType) {
        this(type, ofType, null);
    }

    /**
     * @param   type            The {@link Class} containing the constant
     *                          fields.
     * @param   ofType          The type ({@link Class}) of constant fields
     *                          to include in the map.
     * @param   pattern         If non-{@code null}, the {@link Pattern} the
     *                          field name must match.
     */
    public StaticFinalFieldMap(Class<?> type,
                               Class<T> ofType, Pattern pattern) {
        super();

        for (Field field : type.getFields()) {
            if (isPublic(field) && isStatic(field) && isFinal(field)) {
                String key = field.getName();

                if (matches(pattern, key)) {
                    try {
                        Object value = field.get(null);

                        if (ofType.isAssignableFrom(value.getClass())) {
                            put(key, ofType.cast(value));
                        }
                    } catch (Exception exception) {
                        throw new ExceptionInInitializerError(exception);
                    }
                }
            }
        }
    }

    private boolean matches(Pattern pattern, CharSequence sequence) {
        return pattern == null || pattern.matcher(sequence).matches();
    }

    /**
     * Method to return an unmodifiable map of constant field values to
     * their names.
     *
     * @return  The reverse {@link Map}.
     */
    public SortedMap<T,String> reverseMap() {
        TreeMap<T,String> map = new TreeMap<T,String>();

        for (Map.Entry<String,T> entry : entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }

        return Collections.unmodifiableSortedMap(map);
    }
}
