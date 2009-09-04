/*
 * $Id: ArrayListTableModel.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
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
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public abstract class ArrayListTableModel<R> extends TableModel {
    private final ArrayList<R> list = new ArrayList<R>();

    /**
     * @see TableModel#TableModel(Object...)
     *
     * @param   collection      The {@link Collection} of row values.
     */
    public ArrayListTableModel(Collection<R> collection, Object... columns) {
        super(columns);

        if (collection != null) {
            list.addAll(collection);
        }
    }

    /**
     * @see TableModel#TableModel(int)
     *
     * @param   collection      The {@link Collection} of row values.
     */
    public ArrayListTableModel(Collection<R> collection, int columns) {
        this(collection, new Object[columns]);
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
/*
 * $Log: not supported by cvs2svn $
 */
