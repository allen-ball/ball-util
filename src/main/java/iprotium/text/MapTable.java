/*
 * $Id: MapTable.java,v 1.2 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.Map;

/**
 * {@link Map} {@link Table} implementation.
 *
 * @param       <K>     The type of the underlying {@link Map} key.
 * @param       <V>     The type of the underlying {@link Map} value.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class MapTable<K,V> extends Table {

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
    public MapTableModel getModel() {
        return (MapTableModel) super.getModel();
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
