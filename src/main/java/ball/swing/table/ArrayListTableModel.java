package ball.swing.table;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract base class for {@link javax.swing.table.TableModel}
 * implementations based on an {@link ArrayList}.
 *
 * @param       <R>     The type of table row.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class ArrayListTableModel<R> extends AbstractTableModelImpl {
    private static final long serialVersionUID = -7927458440773401575L;

    /** @serial */ private final ArrayList<R> list = new ArrayList<>();

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   iterable        The {@link Iterable} of row values.
     * @param   names           The column names.
     */
    protected ArrayListTableModel(Iterable<? extends R> iterable,
                                  String... names) {
        super(names);

        if (iterable != null) {
            for (R element : iterable) {
                list.add(element);
            }
        }
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(int)
     *
     * @param   iterable        The {@link Iterable} of row values.
     * @param   columns         The number of columns.
     */
    protected ArrayListTableModel(Iterable<? extends R> iterable,
                                  int columns) {
        this(iterable, new String[columns]);
    }

    /**
     * Method to access the underlying row {@link java.util.List}.
     *
     * @return  The underlying row {@link java.util.List}.
     */
    public ArrayList<R> list() { return list; }

    @Override
    public int getRowCount() { return list().size(); }

    /**
     * Implementation method to retrieve a column value from a row object.
     *
     * @param   row             The {@link Object} representing the row.
     * @param   x               The column index.
     *
     * @return  The column value from the row.
     */
    protected abstract Object getValueAt(R row, int x);

    @Override
    public Object getValueAt(int y, int x) {
        return getColumnClass(x).cast(getValueAt(list().get(y), x));
    }
}
