/*
 * $Id: SimpleTableModel.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.util.Arrays;
import java.util.Collection;

/**
 * Simple {@link TableModel} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class SimpleTableModel extends ArrayListTableModel<Object[]> {
    private static final long serialVersionUID = 5405482298661568L;

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Collection,Object...)
     *
     * @param   rows            The {@link TableModel}'s rows.
     */
    public SimpleTableModel(Object[][] rows, Object... columns) {
        super((rows != null) ? Arrays.asList(rows) : null, columns);
    }

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Collection,int)
     *
     * @param   rows            The TableModel's rows.
     */
    public SimpleTableModel(Object[][] rows, int columns) {
        this(rows, new Object[columns]);
    }

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Collection,Object...)
     */
    public SimpleTableModel(Object... columns) {
        this(null, columns);
    }

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Collection,int)
     */
    public SimpleTableModel(int columns) { this(null, new Object[columns]); }

    /**
     * Convenience method to add a new row.
     *
     * @param   row             The array of {@link Object}s that make up
     *                          the row to be added.
     */
    public void row(Object... row) {
        list().add(row);

        fireTableDataChanged();
    }

    @Override
    protected Object getValueAt(Object[] row, int x) { return row[x]; }
}
/*
 * $Log: not supported by cvs2svn $
 */
