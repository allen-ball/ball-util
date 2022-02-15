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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import lombok.ToString;

/**
 * {@link Map}s {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ToString
public class MapsTableModel extends MapTableModel {
    private static final long serialVersionUID = -8967894056049317574L;

    /** @serial */
    private final ArrayList<Map<?,?>> list = new ArrayList<>();

    /**
     * @see MapTableModel#MapTableModel(Map,String...)
     *
     * @param   iterable        The {@link Iterable} of underlying
     *                          {@link Map}s.
     * @param   names           The column names.
     */
    public MapsTableModel(Iterable<? extends Map<?,?>> iterable, String... names) {
        this(iterable.iterator(), names);
    }

    /**
     * @see MapTableModel#MapTableModel(Map,int)
     *
     * @param   iterable        The {@link Iterable} of underlying
     *                          {@link Map}s.
     * @param   columns         The number of columns.
     */
    public MapsTableModel(Iterable<? extends Map<?,?>> iterable, int columns) {
        this(iterable, new String[columns]);
    }

    /**
     * @see MapsTableModel#MapsTableModel(Iterable,String...)
     *
     * @param   iterator        The {@link Iterator} of underlying
     *                          {@link Map}s.
     * @param   names           The column names.
     */
    protected MapsTableModel(Iterator<? extends Map<?,?>> iterator, String... names) {
        super(iterator.next(), names);

        list.add(super.map());

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
    }

    @Override
    protected Object getValueAt(Object row, int x) {
        Object value = null;

        switch (x) {
        case 0:
        case 1:
            value = super.getValueAt(row, x);
            break;

        default:
            value = list.get(x - 1).get(row);
            break;
        }

        return value;
    }
}
