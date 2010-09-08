/*
 * $Id: StaticFinalFieldMap.java,v 1.2 2010-08-23 03:43:54 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static iprotium.util.ClassUtil.isFinal;
import static iprotium.util.ClassUtil.isPublic;
import static iprotium.util.ClassUtil.isStatic;

/**
 * {@link TreeMap} implementation that maps a {@link Class}'s constant
 * ("{@code public static final}") fields' names to their values.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class StaticFinalFieldMap<T> extends TreeMap<String,T> {
    private static final long serialVersionUID = -6986552313804842052L;

    /**
     * Sole constructor.
     *
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
                    } catch (IllegalAccessException exception) {
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
/*
 * $Log: not supported by cvs2svn $
 */