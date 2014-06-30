/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import ball.text.ArrayListTableModel;
import ball.text.SimpleTable;
import ball.text.TextTable;
import ball.util.BeanInfoUtil;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to display {@link BeanInfo} for a specified {@link Class}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("bean-info-for")
public class BeanInfoForTask extends AbstractClasspathTask {
    private String type = null;

    /**
     * Sole constructor.
     */
    public BeanInfoForTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public void execute() throws BuildException {
        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        try {
            log(Introspector.getBeanInfo(Class.forName(getType(),
                                                       false,
                                                       getClassLoader())));
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    private void log(BeanInfo bean) {
        TextTable header = new BeanHeaderTable(bean.getBeanDescriptor());
        TextTable table = new BeanPropertyTable(bean.getPropertyDescriptors());

        for (String line : header) {
            log(line);
        }

        log(NIL);

        for (String line : table) {
            log(line);
        }

        log(bean.getAdditionalBeanInfo());
    }

    private void log(BeanInfo[] beans) {
        if (beans != null) {
            for (BeanInfo bean : beans) {
                log(NIL);
                log(bean);
            }
        }
    }

    private class BeanHeaderTable extends SimpleTable {
        public BeanHeaderTable(BeanDescriptor descriptor) {
            super(2);

            row("Bean Class:",
                descriptor.getBeanClass().getName());

            if (descriptor.getCustomizerClass() != null) {
                row("Customizer Class:",
                    descriptor.getCustomizerClass().getName());
            }
        }
    }

    private class BeanPropertyTable extends TextTable {
        public BeanPropertyTable(PropertyDescriptor[] rows) {
            super(new BeanPropertyTableModel(rows));
        }
    }

    private class BeanPropertyTableModel
                  extends ArrayListTableModel<PropertyDescriptor> {
        private static final long serialVersionUID = 4092624978038121007L;

        public BeanPropertyTableModel(PropertyDescriptor[] rows) {
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
                value = BeanInfoUtil.getMode(row);
                break;

            case 2:
                value = getCanonicalName(row.getPropertyType());
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

        private String getCanonicalName(Class<?> type) {
            return (type != null) ? type.getCanonicalName() : NIL;
        }
    }
}
