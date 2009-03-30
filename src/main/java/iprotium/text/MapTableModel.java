/*
 * $Id: MapTableModel.java,v 1.1 2009-03-30 06:35:16 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.Map;
import javax.swing.event.TableModelEvent;

/**
 * Map TableModel implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class MapTableModel<K,V> extends ArrayListTableModel<Map.Entry<K,V>> {
    private static final long serialVersionUID = -8443672322792101653L;

    private final Map<K,V> map;

    /**
     * Sole constructor.
     *
     * @param   map             The underlying Map.
     * @param   key             The Object describing the key ColumnModel.
     * @param   value           The Object describing the value
     *                          ColumnModel.
     */
    public MapTableModel(Map<K,V> map, Object key, Object value) {
        super(key, value);

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
/*
 * $Log: not supported by cvs2svn $
 */
