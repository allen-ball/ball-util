package ball.swing.table;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
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
import lombok.ToString;

/**
 * Simple {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ToString
public class SimpleTableModel extends ArrayListTableModel<Object[]> {
    private static final long serialVersionUID = 4763741378592706296L;

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Iterable,String...)
     *
     * @param   rows            The {@link javax.swing.table.TableModel}'s
     *                          rows.
     * @param   names           The column names.
     */
    public SimpleTableModel(Object[][] rows, String... names) {
        super(Arrays.asList(rows), names);
    }

    /**
     * @see ArrayListTableModel#ArrayListTableModel(Iterable,int)
     *
     * @param   rows            The TableModel's rows.
     * @param   columns         The number of columns.
     */
    public SimpleTableModel(Object[][] rows, int columns) {
        this(rows, new String[columns]);
    }

    /**
     * Convenience method to add a new row.
     *
     * @param   row             The array of {@link Object}s that make up
     *                          the row to be added.
     *
     * @return  {@link.this} {@link SimpleTableModel}.
     */
    public SimpleTableModel row(Object... row) {
        list().add(row);

        fireTableStructureChanged();
        fireTableDataChanged();

        return this;
    }

    @Override
    protected Object getValueAt(Object[] row, int x) { return row[x]; }
}
