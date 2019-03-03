/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.swing.table;

import java.util.List;
import javax.swing.event.TableModelEvent;

/**
 * {@link List} {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class ListTableModel extends ArrayListTableModel<Object> {
    private static final long serialVersionUID = -3491544585475074062L;

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
