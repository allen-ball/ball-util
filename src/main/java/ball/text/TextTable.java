/*
 * $Id$
 *
 * Copyright 2009 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import ball.activation.ReaderWriterDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.commons.lang3.StringUtils.stripEnd;

/**
 * Text-based {@link javax.swing.JTable} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class TextTable extends ReaderWriterDataSource {
    private final TableModel model;
    private int[] tabs = new int[] { };
    private int[] widths = new int[] { };

    /**
     * Sole constructor.
     *
     * @param   model           The {@link TextTable}'s {@link TableModel}.
     * @param   tabs            The preferred tab stops.
     */
    public TextTable(TableModel model, int... tabs) {
        super(null, TEXT_PLAIN);

        this.model = model;

        getModel().addTableModelListener(new ModelListenerImpl());
    }

    /**
     * Method to get {@link.this} {@link TextTable}'s {@link TableModel}.
     *
     * @return  The {@link TableModel}.
     */
    public TableModel getModel() { return model; }

    /**
     * Method to render the {@link TextTable}.
     */
    protected void render() {
        TableModel model = getModel();

        if (model instanceof AbstractTableModel) {
            ((AbstractTableModel) model).fireTableDataChanged();
        }

        tabs = Arrays.copyOf(tabs, model.getColumnCount());
        widths = Arrays.copyOf(widths, model.getColumnCount());

        for (int x = 0; x < widths.length; x += 1) {
            widths[x] = length(model.getColumnName(x));

            for (int y = 0, n = model.getRowCount(); y < n; y += 1) {
                Object object = model.getValueAt(y, x);
                String string =
                    (object != null) ? object.toString() : null;

                widths[x] = Math.max(widths[x], length(string));
            }
        }

        try (PrintWriter out = getPrintWriter()) {
            StringBuilder header = line(fill(header()));

            if (! isBlank(header)) {
                out.println(stripEnd(header.toString(), null));
                out.println(repeat('-', header.length()));
            }

            for (int y = 0, n = model.getRowCount(); y < n; y += 1) {
                out.println(stripEnd(line(fill(format(row(y)))).toString(), null));
            }
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String[] header() {
        String[] header = new String[getModel().getColumnCount()];

        for (int x = 0; x < header.length; x += 1) {
            String string = getModel().getColumnName(x);

            header[x] = (string != null) ? string : EMPTY;
        }

        return header;
    }

    private Object[] row(int y) {
        Object[] row = new Object[getModel().getColumnCount()];

        for (int x = 0; x < row.length; x += 1) {
            row[x] = getModel().getValueAt(y, x);
        }

        return row;
    }

    private String[] format(Object... row) {
        String[] strings = new String[row.length];

        for (int x = 0; x < strings.length; x += 1) {
            strings[x] = String.valueOf(row[x]);
        }

        return strings;
    }

    private String[] fill(String... row) {
        String[] strings = new String[row.length];

        for (int x = 0; x < strings.length; x += 1) {
            strings[x] = rightPad(row[x], widths[x], SPACE);
        }

        return strings;
    }

    private StringBuilder line(String[] row) {
        StringBuilder line = new StringBuilder();

        for (int x = 0; x < row.length; x += 1) {
            if (x > 0) {
                line.append(SPACE);
            }

            while (line.length() < tabs[x]) {
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

    private static int length(CharSequence sequence) {
        return (sequence != null) ? sequence.length() : 0;
    }

    private class ModelListenerImpl implements TableModelListener {
        public ModelListenerImpl() { }

        @Override
        public void tableChanged(TableModelEvent event) { clear(); }

        @Override
        public String toString() { return super.toString(); }
    }
}
