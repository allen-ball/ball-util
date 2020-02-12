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
import java.util.Map;
import javax.swing.event.TableModelEvent;

/**
 * {@link Map} {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class MapTableModel extends ArrayListTableModel<Object> {
    private static final long serialVersionUID = 299182543130741505L;

    /** @serial */ private final Map<?,?> map;

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   map             The underlying {@link Map}.
     * @param   names           The column names.
     */
    public MapTableModel(Map<?,?> map, String... names) {
        super(map.keySet(), names);

        this.map = map;
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(int)
     *
     * @param   map             The underlying {@link Map}.
     * @param   columns         The number of columns.
     */
    public MapTableModel(Map<?,?> map, int columns) {
        this(map, new String[columns]);
    }

    /**
     * @param   map             The underlying {@link Map}.
     */
    public MapTableModel(Map<?,?> map) { this(map, 2); }

    /**
     * Method to get the underlying {@link Map}.
     *
     * @return  The underlying {@link Map}.
     */
    protected Map<?,?> map() { return map; }

    @Override
    protected Object getValueAt(Object row, int x) {
        Object value = null;

        switch (x) {
        case 0:
            value = row;
            break;

        default:
            value = map().get(row);
            break;
        }

        return value;
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        list().clear();
        list().addAll(map().keySet());

        super.tableChanged(event);
    }
}
