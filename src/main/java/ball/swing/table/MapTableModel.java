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
    private static final long serialVersionUID = 299182543130741505L;

    private final Map<?,?> map;

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   map             The underlying {@link Map}.
     * @param   names           The column names.
     */
    public MapTableModel(Map<?,?> map, String... names) {
        super(map.keySet(), names);

        this.map = map;
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(int)
     *
     * @param   map             The underlying {@link Map}.
     * @param   columns         The number of columns.
     */
    public MapTableModel(Map<?,?> map, int columns) {
        this(map, new String[columns]);
    }

    /**
     * @param   map             The underlying {@link Map}.
     */
    public MapTableModel(Map<?,?> map) { this(map, 2); }

    /**
     * Method to get the underlying {@link Map}.
     *
     * @return  The underlying {@link Map}.
     */
    protected Map<?,?> map() { return map; }

    @Override
    protected Object getValueAt(Object row, int x) {
        Object value = null;

        switch (x) {
        case 0:
            value = row;
            break;

        default:
            value = map().get(row);
            break;
        }

        return value;
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        list().clear();
        list().addAll(map().keySet());

        super.tableChanged(event);
    }
}
