package ball.beans;
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
import ball.swing.table.ArrayListTableModel;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@code BeanInfo} {@link PropertyDescriptor properties}
 * {@link javax.swing.table.TableModel} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ToString
public class PropertyDescriptorsTableModel extends ArrayListTableModel<PropertyDescriptor> {
    private static final long serialVersionUID = -4799510974827528198L;

    private static final String R = "R";
    private static final String W = "W";

    /**
     * Sole constructor.
     *
     * @param   rows            The {@link PropertyDescriptor}s.
     */
    public PropertyDescriptorsTableModel(PropertyDescriptor[] rows) {
        super(Arrays.asList(rows),
              "Name", "Mode", "Type", "isHidden", "isBound", "isConstrained");
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
            value = getPropertyType(row);
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
        String mode = getMode(descriptor.getReadMethod(), descriptor.getWriteMethod());

        if (descriptor instanceof IndexedPropertyDescriptor) {
            mode += "[";
            mode += getMode((IndexedPropertyDescriptor) descriptor);
            mode += "]";
        }

        return mode;
    }

    private String getMode(IndexedPropertyDescriptor descriptor) {
        return getMode(descriptor.getIndexedReadMethod(), descriptor.getIndexedWriteMethod());
    }

    private String getMode(Method read, Method write) {
        return ((read != null) ? R : EMPTY) + ((write != null) ? W : EMPTY);
    }

    private Type getPropertyType(PropertyDescriptor descriptor) {
        Type type = null;

        if (descriptor instanceof IndexedPropertyDescriptor) {
            type = getPropertyType((IndexedPropertyDescriptor) descriptor);
        } else if (descriptor.getReadMethod() != null) {
            type = descriptor.getReadMethod().getGenericReturnType();
        } else if (descriptor.getWriteMethod() != null) {
            type = descriptor.getWriteMethod().getGenericParameterTypes()[0];
        } else {
            type = descriptor.getPropertyType();
        }

        return type;
    }

    private Type getPropertyType(IndexedPropertyDescriptor descriptor) {
        Type type = null;

        if (descriptor.getIndexedReadMethod() != null) {
            type = descriptor.getIndexedReadMethod().getGenericReturnType();
        } else if (descriptor.getIndexedWriteMethod() != null) {
            type = descriptor.getIndexedWriteMethod().getGenericParameterTypes()[1];
        } else {
            type = descriptor.getIndexedPropertyType();
        }

        return type;
    }
}
