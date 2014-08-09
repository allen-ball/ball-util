/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract base class for {@link javax.swing.table.TableModel}
 * implementations based on an {@link ArrayList}.
 *
 * @param       <R>     The type of table row.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ArrayListTableModel<R> extends AbstractTableModelImpl {
    private final ArrayList<R> list = new ArrayList<R>();

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   iterable        The {@link Iterable} of row values.
     * @param   names           The column names.
     */
    protected ArrayListTableModel(Iterable<? extends R> iterable,
                                  String... names) {
        super(names);

        if (iterable != null) {
            for (R element : iterable) {
                list.add(element);
            }
        }
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(int)
     *
     * @param   iterable        The {@link Iterable} of row values.
     * @param   columns         The number of columns.
     */
    protected ArrayListTableModel(Iterable<? extends R> iterable,
                                  int columns) {
        this(iterable, new String[columns]);
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
