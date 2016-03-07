/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * {@link Map}s {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class MapsTableModel extends MapTableModel {
    private static final long serialVersionUID = -2742452353623299139L;

    private final ArrayList<Map<?,?>> list = new ArrayList<Map<?,?>>();;

    /**
     * @see MapTableModel#MapTableModel(Map,String...)
     *
     * @param   iterable        The {@link Iterable} of underlying
     *                          {@link Map}s.
     * @param   names           The column names.
     */
    public MapsTableModel(Iterable<? extends Map<?,?>> iterable,
                          String... names) {
        this(iterable.iterator(), names);
    }

    /**
     * @see MapTableModel#MapTableModel(Map,int)
     *
     * @param   iterable        The {@link Iterable} of underlying
     *                          {@link Map}s.
     * @param   columns         The number of columns.
     */
    public MapsTableModel(Iterable<? extends Map<?,?>> iterable, int columns) {
        this(iterable, new String[columns]);
    }

    /**
     * @see MapsTableModel#MapsTableModel(Iterable,String...)
     *
     * @param   iterator        The {@link Iterator} of underlying
     *                          {@link Map}s.
     * @param   names           The column names.
     */
    protected MapsTableModel(Iterator<? extends Map<?,?>> iterator,
                             String... names) {
        super(iterator.next(), names);

        list.add(super.map());

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
    }

    @Override
    protected Object getValueAt(Object row, int x) {
        Object value = null;

        switch (x) {
        case 0:
        case 1:
            value = super.getValueAt(row, x);
            break;

        default:
            value = list.get(x - 1).get(row);
            break;
        }

        return value;
    }
}
