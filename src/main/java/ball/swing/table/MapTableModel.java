/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.Map;
import javax.swing.event.TableModelEvent;

/**
 * {@link Map} {@link javax.swing.table.TableModel} implementation.
 *
 * @param       <K>     The type of the underlying {@link Map} key.
 * @param       <V>     The type of the underlying {@link Map} value.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class MapTableModel<K,V> extends ArrayListTableModel<Map.Entry<K,V>> {
    private static final long serialVersionUID = 7979196059433549407L;

    private final Map<K,V> map;

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   map             The underlying {@link Map}.
     * @param   names           The column names.
     */
    public MapTableModel(Map<K,V> map, String... names) {
        super(map.entrySet(), names);

        this.map = map;
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(int)
     *
     * @param   map             The underlying {@link Map}.
     * @param   columns         The number of columns.
     */
    public MapTableModel(Map<K,V> map, int columns) {
        this(map, new String[columns]);
    }

    /**
     * @param   map             The underlying {@link Map}.
     */
    public MapTableModel(Map<K,V> map) { this(map, 2); }

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
