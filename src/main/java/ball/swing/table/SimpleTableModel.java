/*
 * $Id$
 *
 * Copyright 2009 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.Arrays;

/**
 * Simple {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class SimpleTableModel extends ArrayListTableModel<Object[]> {
    private static final long serialVersionUID = 8874758772454561013L;

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
     *
     * @return  {@link.this} {@link SimpleTableModel}.
     */
    public SimpleTableModel row(Object... row) {
        list().add(row);

        fireTableStructureChanged();
        fireTableDataChanged();

        return this;
    }

    @Override
    protected Object getValueAt(Object[] row, int x) { return row[x]; }
}
