/*
 * $Id$
 *
 * Copyright 2009 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.swing.event.TableModelEvent;

/**
 * {@link Map} {@link javax.swing.table.TableModel} implementation.  Table
 * rows are made up of one or more {@link Map}s.
 *
 * @param       <K>     The type of the underlying {@link Map} key.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class MapTableModel<K> extends ArrayListTableModel<K> {
    private static final long serialVersionUID = -1688193597062997567L;

    private final ArrayList<Map<? extends K,?>> list =
        new ArrayList<Map<? extends K,?>>();

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   collection      The {@link Collection} of {@link Map}s.
     * @param   names           The column names.
     */
    public MapTableModel(Collection<Map<? extends K,?>> collection,
                         String... names) {
        super(collection.iterator().next().keySet(), names);

        list.addAll(collection);
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(int)
     *
     * @param   collection      The {@link Collection} of {@link Map}s.
     * @param   columns         The number of columns.
     */
    public MapTableModel(Collection<Map<? extends K,?>> collection) {
        this(collection, new String[collection.size() + 1]);
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   map             The underlying {@link Map}.
     * @param   key             The key column name.
     * @param   value           The value column name.
     */
    public MapTableModel(Map<? extends K,?> map, String key, String value) {
        this(Arrays.<Map<? extends K,?>>asList(map), key, value);
    }

    /**
     * @param   map             The underlying {@link Map}.
     */
    public MapTableModel(Map<? extends K,?> map) {
        this(map, null, null);
    }

    @Override
    protected Object getValueAt(K row, int x) {
        Object value = null;

        switch (x) {
        case 0:
            value = row;
            break;

        default:
            value = list.get(x - 1).get(row);
            break;
        }

        return value;
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        list().clear();
        list().addAll(list.get(0).keySet());

        super.tableChanged(event);
    }
}
