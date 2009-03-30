/*
 * $Id: Table.java,v 1.1 2009-03-30 06:35:16 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static iprotium.text.FillEnum.CENTER;
import static iprotium.text.FillStringFormat.SPACE;

/**
 * Text-based Table implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class Table extends Report implements TableModelListener {
    private final TableModel model;
    private final int[] tabs;

    /**
     * Sole constructor.
     *
     * @param   model           The Table's TableModel.
     * @param   tabs            The preferred tab stops.
     */
    public Table(TableModel model, int... tabs) {
        super();

        this.model = model;
        this.tabs = new int[getModel().getColumnCount()];

        getModel().addTableModelListener(this);
        getModel().fireTableDataChanged();

        setTabs(tabs);
    }

    /**
     * Method to get this Table's TableModel.
     *
     * @return  The TableModel.
     */
    public TableModel getModel() { return model; }

    /**
     * Method to set the suggested tab stops.
     *
     * @param   tabs            The columns' tab stops.
     */
    public void setTabs(int... tabs) {
        for (int x = 0; x < tabs.length; x += 1) {
            setTab(x, tabs[x]);
        }
    }

    /**
     * Method to get the suggested tab stop for a column.
     *
     * @param   x               The column index.
     *
     * @return  The tab stop.
     */
    public int getTab(int x) { return tabs[x]; }

    /**
     * Method to set the suggested tab stop for a column.
     *
     * @param   x               The column index.
     * @param   stop            The tab stop.
     */
    public void setTab(int x, int stop) {
        if (getTab(x) != stop) {
            tabs[x] = stop;

            getModel().fireTableStructureChanged();
        }
    }

    @Override
    public byte[] toByteArray() {
        if (super.toByteArray().length == 0) {
            render();
        }

        return super.toByteArray();
    }

    /**
     * Method to render the table.
     *
     * @see #reset()
     * @see #toByteArray()
     * @see #tableChanged(TableModelEvent)
     *
     * This implementation overrides the toByteArray() method.  If
     * super.toByteArray() returns a zero-length byte array, it then calls
     * render() to update the table.  The tableChanged(TableModelEvent)
     * method may zero the byte array.
     */
    protected void render() {
        StringBuilder header = line(fill(getModel().header()));
        String boundary = CENTER.fill(header.length(), '-', "");

        if (rtrim(header).length() > 0) {
            println(header);
            println(boundary);
        }

        for (int y = 0, count = getModel().getRowCount(); y < count; y += 1) {
            println(line(fill(format(getModel().row(y)))));
        }
    }

    private String[] format(Object... row) {
        String[] strings = new String[row.length];

        for (int x = 0; x < strings.length; x += 1) {
            strings[x] = getModel().getColumnModel(x).format(row[x]);
        }

        return strings;
    }

    private String[] fill(String... row) {
        String[] strings = new String[row.length];

        for (int x = 0; x < strings.length; x += 1) {
            int width = getModel().getColumnWidth(x);

            strings[x] = getModel().getColumnModel(x).fill(width, row[x]);
        }

        return strings;
    }

    private StringBuilder line(String[] row) {
        StringBuilder line = new StringBuilder();

        for (int x = 0; x < row.length; x += 1) {
            if (x > 0) {
                line.append(SPACE);
            }

            while (line.length() < getTab(x)) {
                line.append(SPACE);
            }

            line.append(row[x]);
        }

        return line;
    }

    /**
     * @see TableModelListener#tableChanged(TableModelEvent)
     */
    public void tableChanged(TableModelEvent event) { reset(); }
}
/*
 * $Log: not supported by cvs2svn $
 */
