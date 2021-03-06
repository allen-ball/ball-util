package ball.swing.table;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.util.Arrays;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import lombok.ToString;

/**
 * {@link AbstractTableModel} implementation.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ToString
public abstract class AbstractTableModelImpl extends AbstractTableModel implements TableModelListener {
    private static final long serialVersionUID = -4459832803497493630L;

    /** @serial */ private String[] names = new String[] { };
    /** @serial */ private Class<?>[] types = new Class<?>[] { };

    /**
     * Construct a {@link javax.swing.table.TableModel} with the specified
     * column names.
     *
     * @param   names           The column names.
     */
    protected AbstractTableModelImpl(String... names) {
        super();

        if (names.length > 0) {
            this.names = Arrays.copyOf(names, names.length);
            this.types = Arrays.copyOf(types, names.length);
        } else {
            throw new IllegalArgumentException("names=" + Arrays.asList(names));
        }

        addTableModelListener(this);
    }

    /**
     * Construct a {@link javax.swing.table.TableModel} with the specified
     * number of columns.
     *
     * @param   columns         The number of columns.
     */
    protected AbstractTableModelImpl(int columns) {
        this(new String[columns]);
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
            column[y] = getColumnClass(x).cast(getValueAt(y, x));
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
            row[x] = getColumnClass(x).cast(getValueAt(y, x));
        }

        return row;
    }

    public int getColumnCount() { return names.length; }

    @Override
    public Class<?> getColumnClass(int x) {
        return (types[x] != null) ? types[x] : super.getColumnClass(x);
    }

    public void setColumnClass(int x, Class<?> type) { types[x] = type; }

    @Override
    public String getColumnName(int x) { return names[x]; }

    public void setColumnName(int x, String name) { names[x] = name; }

    @Override
    public abstract int getRowCount();

    @Override
    public boolean isCellEditable(int y, int x) { return false; }

    @Override
    public abstract Object getValueAt(int y, int x);

    @Override
    public void setValueAt(Object value, int y, int x) {
        throw new UnsupportedOperationException("setValueAt(Object,int,int)");
    }

    @Override
    public void tableChanged(TableModelEvent event) { }
}
