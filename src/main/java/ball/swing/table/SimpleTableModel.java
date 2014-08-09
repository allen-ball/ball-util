/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.Arrays;

/**
 * Simple {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class SimpleTableModel extends ArrayListTableModel<Object[]> {
    private static final long serialVersionUID = 4595844725709334308L;

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Iterable,String...)
     *
     * @param   rows            The {@link javax.swing.table.TableModel}'s
     *                          rows.
     * @param   names           The column names.
     */
    public SimpleTableModel(Object[][] rows, String... names) {
        super(Arrays.asList(rows), names);
    }

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Iterable,int)
     *
     * @param   rows            The TableModel's rows.
     * @param   columns         The number of columns.
     */
    public SimpleTableModel(Object[][] rows, int columns) {
        this(rows, new String[columns]);
    }

    /**
     * Convenience method to add a new row.
     *
     * @param   row             The array of {@link Object}s that make up
     *                          the row to be added.
     */
    public void row(Object... row) {
        list().add(row);

        fireTableStructureChanged();
        fireTableDataChanged();
    }

    @Override
    protected Object getValueAt(Object[] row, int x) { return row[x]; }
}
