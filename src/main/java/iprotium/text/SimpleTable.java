/*
 * $Id: SimpleTable.java,v 1.3 2010-10-23 21:51:32 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

/**
 * Simple {@link TextTable} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
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
/*
 * $Log: not supported by cvs2svn $
 */
