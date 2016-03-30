/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Map;
import java.util.Properties;

import static ball.util.StringUtil.NIL;
import static ball.util.StringUtil.isNil;

/**
 * {@code Map} utility methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class MapUtil {
    private MapUtil() { }

    /**
     * Method to copy one {@link Map}'s entries to another.
     *
     * @param   from            The source {@link Map}.
     * @param   to              The target {@link Map}.
     *
     * @return  The target {@link Map}.
     */
    public static <K,V> Map<K,V> copy(Map<? extends K,? extends V> from,
                                      Map<K,V> to) {
        return copy(from.entrySet(), to);
    }

    /**
     * Method to copy {@link Map.Entry}s to another {@link Map}.
     *
     * @param   from            The source {@link Map.Entry}
     *                          {@link Iterable}.
     * @param   to              The target {@link Map}.
     *
     * @return  The target {@link Map}.
     */
    public static <K,V> Map<K,V> copy(Iterable<? extends Map.Entry<? extends K,? extends V>> from,
                                      Map<K,V> to) {
        for (Map.Entry<? extends K,? extends V> entry : from) {
            to.put(entry.getKey(), entry.getValue());
        }

        return to;
    }

    /**
     * Method to copy one {@link Map}'s entries to a {@link Properties}.
     *
     * @param   from            The source {@link Map}.
     * @param   to              The target {@link Properties}.
     *
     * @return  The target {@link Properties}.
     */
    public static Properties copy(Map<String,?> from, Properties to) {
        return (Properties) copy(from.entrySet(), to);
    }
}
