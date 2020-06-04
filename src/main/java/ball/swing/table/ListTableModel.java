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
import java.util.List;
import javax.swing.event.TableModelEvent;
import lombok.ToString;

/**
 * {@link List} {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ToString
public class ListTableModel extends ArrayListTableModel<Object> {
    private static final long serialVersionUID = -7448852717174575332L;

    /** @serial */ private final List<?> list;

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(String...)
     *
     * @param   list            The underlying {@link List}.
     * @param   name            The column name.
     */
    public ListTableModel(List<?> list, String name) {
        super(list, new String[] { name });

        this.list = list;
    }

    /**
     * @see AbstractTableModelImpl#AbstractTableModelImpl(int)
     *
     * @param   list             The underlying {@link List}.
     */
    public ListTableModel(List<?> list) { this(list, null); }

    @Override
    protected Object getValueAt(Object row, int x) {
        Object value = null;

        switch (x) {
        case 0:
        default:
            value = row;
            break;
        }

        return value;
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        list().clear();
        list().addAll(list);

        super.tableChanged(event);
    }
}
