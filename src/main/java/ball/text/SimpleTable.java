/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.text;

/**
 * Simple {@link TextTable} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class SimpleTable extends TextTable {

    /**
     * @see TableModel#TableModel(Object...)
     */
    public SimpleTable(Object... columns) {
        super(new SimpleTableModel(columns));
    }

    /**
     * @see TableModel#TableModel(int)
     */
    public SimpleTable(int columns) { super(new SimpleTableModel(columns)); }

    /**
     * Convenience method to add a new row.
     *
     * @param   row             The array of Objects that make up the row to
     *                          be added.
     */
    public void row(Object... row) { getModel().row(row); }

    @Override
    public SimpleTableModel getModel() {
        return (SimpleTableModel) super.getModel();
    }
}