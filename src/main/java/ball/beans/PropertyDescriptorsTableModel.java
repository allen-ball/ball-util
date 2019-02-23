/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.beans;

import ball.swing.table.ArrayListTableModel;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@code BeanInfo} {@link PropertyDescriptor properties} {@link TableModel}
 * implementation
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PropertyDescriptorsTableModel
             extends ArrayListTableModel<PropertyDescriptor> {
    private static final long serialVersionUID = -3731165693820299522L;

    private static final String R = "R";
    private static final String W = "W";

    /**
     * Sole constructor.
     *
     * @param   rows            The {@link PropertyDescriptor}s.
     */
    public PropertyDescriptorsTableModel(PropertyDescriptor[] rows) {
        super(Arrays.asList(rows),
              "Name", "Mode", "Type",
              "isHidden", "isBound", "isConstrained");
    }

    @Override
    protected Object getValueAt(PropertyDescriptor row, int x) {
        Object value = null;

        switch (x) {
        case 0:
        default:
            value = row.getName();
            break;

        case 1:
            value = getMode(row);
            break;

        case 2:
            value = row.getPropertyType();
            break;

        case 3:
            value = row.isHidden();
            break;

        case 4:
            value = row.isBound();
            break;

        case 5:
            value = row.isConstrained();
            break;
        }

        return value;
    }

    private String getMode(PropertyDescriptor descriptor) {
        String mode =
            getMode(descriptor.getReadMethod(), descriptor.getWriteMethod());

        if (descriptor instanceof IndexedPropertyDescriptor) {
            mode += "[";
            mode += getMode((IndexedPropertyDescriptor) descriptor);
            mode += "]";
        }

        return mode;
    }

    private String getMode(IndexedPropertyDescriptor descriptor) {
        return getMode(descriptor.getIndexedReadMethod(),
                       descriptor.getIndexedWriteMethod());
    }

    private String getMode(Method read, Method write) {
        return ((read != null) ? R : EMPTY) + ((write != null) ? W : EMPTY);
    }
}
