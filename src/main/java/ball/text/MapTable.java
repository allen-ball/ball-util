/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import java.util.Map;

/**
 * {@link Map} {@link TextTable} implementation.
 *
 * @param       <K>     The type of the underlying {@link Map} key.
 * @param       <V>     The type of the underlying {@link Map} value.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class MapTable<K,V> extends TextTable {

    /**
     * @param   model           The {@link MapTableModel} describing the
     *                          {@link Map}.
     * @param   tabs            The preferred tab stops.
     */
    public MapTable(MapTableModel<K,V> model, int... tabs) {
        super(model, tabs);
    }

    /**
     * @param   map             The underlying {@link Map}.
     * @param   key             The {@link Object} describing the key
     *                          {@link ColumnModel}.
     * @param   value           The {@link Object} describing the value
     *                          {@link ColumnModel}.
     * @param   tabs            The preferred tab stops.
     *
     * @see MapTableModel
     */
    public MapTable(Map<K,V> map, Object key, Object value, int... tabs) {
        this(new MapTableModel<K,V>(map, key, value), tabs);
    }

    @Override
    public MapTableModel<?,?> getModel() {
        return (MapTableModel<?,?>) super.getModel();
    }
}
