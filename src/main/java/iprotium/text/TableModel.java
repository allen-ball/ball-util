/*
 * $Id: TableModel.java,v 1.4 2010-09-11 22:11:34 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.text.DateFormat;
import java.text.NumberFormat;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * Abstract text table model base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public abstract class TableModel extends AbstractTableModel
                                 implements TableModelListener {
    private final ColumnModel[] columns;
    private transient int[] widths = null;

    /**
     * Construct a {@link TableModel} with the specified columns.
     *
     * @see ColumnModel
     *
     * @param   columns         The column specifiers.  A column specifier
     *                          can be a {@link ColumnModel},
     *                          {@link CharSequence} (name), or
     *                          {@link DateFormat} or {@link NumberFormat}
     *                          (format).
     *
     * @throws  ClassCastException
     *                          If a column object cannot be translated to a
     *                          {@link ColumnModel}.
     */
    protected TableModel(Object... columns) {
        super();

        if (columns.length > 0) {
            this.columns = new ColumnModel[columns.length];

            for (int x = 0; x < columns.length; x += 1) {
                if (columns[x] != null) {
                    if (columns[x] instanceof ColumnModel) {
                        this.columns[x] = (ColumnModel) columns[x];
                    } else if (columns[x] instanceof CharSequence) {
                        this.columns[x] =
                            new ColumnModel(columns[x].toString());
                    } else if (columns[x] instanceof DateFormat) {
                        this.columns[x] =
                            new ColumnModel(null, (DateFormat) columns[x]);
                    } else if (columns[x] instanceof NumberFormat) {
                        this.columns[x] =
                            new ColumnModel(null, (NumberFormat) columns[x]);
                    } else {
                        throw new ClassCastException();
                    }
                } else {
                    this.columns[x] = new ColumnModel(null);
                }
            }
        } else {
            throw new IllegalArgumentException("columns");
        }

        addTableModelListener(this);
    }

    /**
     * Construct a {@link TableModel} with the specified number of columns.
     *
     * @param   columns         The number of columns.
     */
    protected TableModel(int columns) { this(new Object[columns]); }

    public int getColumnCount() { return columns.length; }

    public abstract int getRowCount();

    /**
     * Method to get the {@link ColumnModel} for a specified column.
     *
     * @param   x               The column index.
     *
     * @return  The ColumnModel.
     */
    public ColumnModel getColumnModel(int x) { return columns[x]; }

    @Override
    public Class<?> getColumnClass(int x) { return Object.class; }

    @Override
    public String getColumnName(int x) { return getColumnModel(x).getName(); }

    /**
     * Method to get the calculated width for a specified column.
     *
     * @param   x               The column index.
     *
     * @return  The width.
     */
    public int getColumnWidth(int x) { return widths()[x]; }

    private int[] widths() {
        int[] widths = this.widths;

        if (widths == null) {
            widths = new int[getColumnCount()];

            for (int x = 0; x < widths.length; x += 1) {
                widths[x] = length(getColumnName(x));

                for (int y = 0; y < getRowCount(); y += 1) {
                    String string = getColumnModel(x).format(getValueAt(y, x));

                    widths[x] = Math.max(widths[x], length(string));
                }
            }

            this.widths = widths;
        }

        return widths;
    }

    @Override
    public boolean isCellEditable(int y, int x) { return false; }

    public abstract Object getValueAt(int y, int x);

    @Override
    public void setValueAt(Object value, int y, int x) {
        throw new UnsupportedOperationException("setValueAt(Object,int,int)");
    }

    /**
     * Convenience method to get the column names as an array.
     *
     * @return  The array of column names.
     */
    protected String[] header() {
        String[] header = new String[getColumnCount()];

        for (int x = 0; x < header.length; x += 1) {
            header[x] = getColumnName(x);
        }

        return header;
    }

    /**
     * Convenience method to get a column's values as an array.
     *
     * @param   x               The column index.
     *
     * @return  The array of column values.
     */
    protected Object[] column(int x) {
        Object[] column = new Object[getRowCount()];

        for (int y = 0; y < column.length; y += 1) {
            column[y] = getValueAt(y, x);
        }

        return column;
    }

    /**
     * Convenience method to get a row's values as an array.
     *
     * @param   y               The row index.
     *
     * @return  The array of row values.
     */
    protected Object[] row(int y) {
        Object[] row = new Object[getColumnCount()];

        for (int x = 0; x < row.length; x += 1) {
            row[x] = getValueAt(y, x);
        }

        return row;
    }

    /**
     * @see TableModelListener#tableChanged(TableModelEvent)
     */
    public void tableChanged(TableModelEvent event) { this.widths = null; }

    private static int length(CharSequence sequence) {
        return (sequence != null) ? sequence.length() : 0;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
