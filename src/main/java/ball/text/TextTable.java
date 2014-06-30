/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import ball.activation.ReaderWriterDataSource;
import ball.io.IOUtil;
import ball.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static ball.util.StringUtil.SPACE;
import static ball.util.StringUtil.rtrim;

/**
 * Text-based {@link javax.swing.JTable} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class TextTable extends ReaderWriterDataSource
                       implements TableModelListener {
    private final TableModel model;
    private final int[] tabs;

    /**
     * Sole constructor.
     *
     * @param   model           The {@link TextTable}'s {@link TableModel}.
     * @param   tabs            The preferred tab stops.
     */
    public TextTable(TableModel model, int... tabs) {
        super(null, TEXT_PLAIN);

        this.model = model;
        this.tabs = new int[getModel().getColumnCount()];

        getModel().addTableModelListener(this);
        getModel().fireTableDataChanged();

        setTabs(tabs);
    }

    /**
     * Method to get {@code this} {@link TextTable}'s {@link TableModel}.
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
     * Method to render the {@link TextTable}.  This implementation
     * overrides the {@link #getInputStream()} method.  If
     * {@code super}.{@link #length()} returns less than {@code 0}, it then
     * calls {@link #render()} to update the {@link TextTable}.  The
     * {@link #tableChanged(TableModelEvent)} method may clear the
     * {@link TextTable}.
     */
    protected void render() {
        PrintWriter out = null;

        try {
            out = getPrintWriter();

            StringBuilder header = line(fill(getModel().header()));
            StringBuilder boundary =
                StringUtil.fill(new StringBuilder(), header.length(), '-');

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

            strings[x] = getModel().getColumnModel(x).fill(row[x], width);
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

    @Override
    public InputStream getInputStream() throws IOException {
        if (! (length() > 0)) {
            render();
        }

        return super.getInputStream();
    }

    @Override
    public void tableChanged(TableModelEvent event) { clear(); }
}
