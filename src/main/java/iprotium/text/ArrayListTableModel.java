/*
 * $Id: ArrayListTableModel.java,v 1.1 2009-03-30 06:35:16 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract base class for TableModel implementations based on an ArrayList.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public abstract class ArrayListTableModel<R> extends TableModel {
    private final ArrayList<R> list = new ArrayList<R>();

    /**
     * @see TableModel#TableModel(Object...)
     *
     * @param   collection      The Collection of row values.
     */
    public ArrayListTableModel(Collection<R> collection, Object... columns) {
        this(columns);

        list.addAll(collection);
    }

    /**
     * @see TableModel#TableModel(int)
     *
     * @param   collection      The Collection of row values.
     */
    public ArrayListTableModel(Collection<R> collection, int columns) {
        this(collection, new Object[columns]);
    }

    /**
     * @see TableModel#TableModel(Object...)
     */
    public ArrayListTableModel(Object... columns) { super(columns); }

    /**
     * @see TableModel#TableModel(int)
     */
    public ArrayListTableModel(int columns) { this(new Object[columns]); }

    /**
     * Method to access the underlying row List.
     *
     * @return  The underlying row List.
     */
    public ArrayList<R> list() { return list; }

    public int getRowCount() { return list().size(); }

    /**
     * Implementation method to retrieve a column value from a row object.
     *
     * @param   row             The Object representing the row.
     * @param   x               The column index.
     *
     * @return  The column value from the row.
     */
    protected abstract Object getValueAt(R row, int x);

    public Object getValueAt(int y, int x) {
        return getColumnClass(x).cast(getValueAt(list().get(y), x));
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
