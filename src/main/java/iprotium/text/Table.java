/*
 * $Id: Table.java,v 1.4 2010-09-11 22:32:54 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.activation.ReaderWriterDataSource;
import iprotium.io.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static iprotium.text.FillEnum.CENTER;
import static iprotium.text.FillStringFormat.SPACE;
import static java.lang.Character.isWhitespace;

/**
 * Text-based {@link Table} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class Table extends ReaderWriterDataSource
                   implements TableModelListener {
    private final TableModel model;
    private final int[] tabs;

    /**
     * Sole constructor.
     *
     * @param   model           The {@link Table}'s {@link TableModel}.
     * @param   tabs            The preferred tab stops.
     */
    public Table(TableModel model, int... tabs) {
        super(null, TEXT_PLAIN);

        this.model = model;
        this.tabs = new int[getModel().getColumnCount()];

        getModel().addTableModelListener(this);
        getModel().fireTableDataChanged();

        setTabs(tabs);
    }

    /**
     * Method to get this {@link Table}'s {@link TableModel}.
     *
     * @return  The {@link TableModel}.
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

    /**
     * Method to render the {@link Table}.  This implementation overrides
     * the {@link #getInputStream()} method.  If super.{@link #size()}
     * returns {@code 0}, it then calls {@link #render()} to update the
     * {@link Table}.  The {@link #tableChanged(TableModelEvent)} method may
     * zero the byte array.
     */
    protected void render() {
        PrintWriter out = null;

        try {
            out = getPrintWriter();

            StringBuilder header = line(fill(getModel().header()));
            String boundary = CENTER.fill(header.length(), '-', "");

            if (rtrim(header).length() > 0) {
                out.println(rtrim(header));
                out.println(boundary);
            }

            for (int y = 0, n = getModel().getRowCount(); y < n; y += 1) {
                out.println(rtrim(line(fill(format(getModel().row(y))))));
            }
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        } finally {
            try {
                IOUtil.close(out);
            } finally {
                out = null;
            }
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

    private StringBuilder rtrim(StringBuilder buffer) {
        if (buffer != null) {
            while (buffer.length() > 0) {
                if (isWhitespace(buffer.charAt(buffer.length() - 1))) {
                    buffer.setLength(buffer.length() - 1);
                } else {
                    break;
                }
            }
        }

        return buffer;
    }

    @Override
    public ByteArrayInputStream getInputStream() throws IOException {
        if (! (size() > 0)) {
            render();
        }

        return super.getInputStream();
    }

    @Override
    public void tableChanged(TableModelEvent event) { reset(); }
}
/*
 * $Log: not supported by cvs2svn $
 */
