/*
 * $Id$
 *
 * Copyright 2009 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.Map;
import javax.swing.event.TableModelEvent;

/**
 * {@link Map} {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class MapTableModel extends ArrayListTableModel<Object> {

    private final Map<?,?> map;

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   map             The underlying {@link Map}.
     * @param   key             The key column name.
     * @param   value           The value column name.
     */
    public MapTableModel(Map<?,?> map, String key, String value) {
        super(map.keySet(), key, value);

        this.map = map;
    }

    /**
     * @param   map             The underlying {@link Map}.
     */
    public MapTableModel(Map<?,?> map) { this(map, null, null); }

    @Override
    protected Object getValueAt(Object row, int x) {
        Object value = null;

        switch (x) {
        case 0:
            value = row;
            break;

        default:
            value = map.get(row);
            break;
        }

        return value;
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        list().clear();
        list().addAll(map.keySet());

        super.tableChanged(event);
    }
}
