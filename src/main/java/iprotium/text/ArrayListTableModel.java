/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract base class for {@link TableModel} implementations based on an
 * {@link ArrayList}.
 *
 * @param       <R>     The type of table row.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ArrayListTableModel<R> extends TableModel {
    private final ArrayList<R> list = new ArrayList<R>();

    /**
     * @see TableModel#TableModel(Object...)
     *
     * @param   iterable        The {@link Iterable} of row values.
     */
    public ArrayListTableModel(Iterable<? extends R> iterable,
                               Object... columns) {
        super(columns);

        if (iterable != null) {
            for (R element : iterable) {
                list.add(element);
            }
        }
    }

    /**
     * @see TableModel#TableModel(int)
     *
     * @param   iterable        The {@link Iterable} of row values.
     */
    public ArrayListTableModel(Iterable<? extends R> iterable, int columns) {
        this(iterable, new Object[columns]);
    }

    /**
     * Method to access the underlying row {@link java.util.List}.
     *
     * @return  The underlying row {@link java.util.List}.
     */
    public ArrayList<R> list() { return list; }

    @Override
    public int getRowCount() { return list().size(); }

    /**
     * Implementation method to retrieve a column value from a row object.
     *
     * @param   row             The {@link Object} representing the row.
     * @param   x               The column index.
     *
     * @return  The column value from the row.
     */
    protected abstract Object getValueAt(R row, int x);

    @Override
    public Object getValueAt(int y, int x) {
        return getColumnClass(x).cast(getValueAt(list().get(y), x));
    }
}
