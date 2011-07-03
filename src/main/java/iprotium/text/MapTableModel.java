/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.Map;
import javax.swing.event.TableModelEvent;

/**
 * {@link Map} {@link TableModel} implementation.
 *
 * @param       <K>     The type of the underlying {@link Map} key.
 * @param       <V>     The type of the underlying {@link Map} value.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class MapTableModel<K,V> extends ArrayListTableModel<Map.Entry<K,V>> {
    private static final long serialVersionUID = -8443672322792101653L;

    private final Map<K,V> map;

    /**
     * Sole constructor.
     *
     * @param   map             The underlying {@link Map}.
     * @param   key             The {@link Object} describing the key
     *                          {@link ColumnModel}.
     * @param   value           The {@link Object} describing the value
     *                          {@link ColumnModel}.
     */
    public MapTableModel(Map<K,V> map, Object key, Object value) {
        super(null, key, value);

        this.map = map;
    }

    @Override
    protected Object getValueAt(Map.Entry<K,V> row, int x) {
        Object value = null;

        switch (x) {
        case 0:
        default:
            value = row.getKey();
            break;

        case 1:
            value = row.getValue();
            break;
        }

        return value;
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        list().clear();
        list().addAll(map.entrySet());

        super.tableChanged(event);
    }
}
