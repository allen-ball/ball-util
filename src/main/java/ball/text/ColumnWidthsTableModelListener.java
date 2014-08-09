/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import java.util.Arrays;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * {@link TableModelListener} implementation that calculates column widths.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ColumnWidthsTableModelListener implements TableModelListener {
    private transient int[] widths = new int[] { };

    /**
     * Sole constructor.
     *
     * @param   widths          Initial column width values.
     */
    public ColumnWidthsTableModelListener(int... widths) {
        this.widths = (widths != null) ? widths : new int[] { };
    }

    /**
     * Method to get the column widths.
     *
     * @return  The array of column widths.
     */
    public int[] getWidths() { return Arrays.copyOf(widths, widths.length); }

    @Override
    public void tableChanged(TableModelEvent event) {
        TableModel model = (TableModel) event.getSource();

        widths = Arrays.copyOf(widths, model.getColumnCount());

        for (int x = 0; x < widths.length; x += 1) {
            widths[x] = length(model.getColumnName(x));

            for (int y = 0, n = model.getRowCount(); y < n; y += 1) {
                Object object = model.getValueAt(y, x);
                String string = (object != null) ? object.toString() : null;

                widths[x] = Math.max(widths[x], length(string));
            }
        }
    }

    @Override
    public String toString() { return Arrays.asList(getWidths()).toString(); }

    private static int length(CharSequence sequence) {
        return (sequence != null) ? sequence.length() : 0;
    }
}
