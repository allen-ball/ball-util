/*
 * $Id$
 *
 * Copyright 2008 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.swing.table.ArrayListTableModel;
import ball.swing.table.SimpleTableModel;
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
 * {@bean.info}
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

    @NotNull
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public void execute() throws BuildException {
        super.execute();

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
        log(new BeanHeaderTableModel(bean.getBeanDescriptor()));
        log(NIL);
        log(new BeanPropertyTableModel(bean.getPropertyDescriptors()));
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

    private class BeanHeaderTableModel extends SimpleTableModel {
        private static final long serialVersionUID = 8971908583993581514L;

        public BeanHeaderTableModel(BeanDescriptor descriptor) {
            super(new Object[][] { }, 2);

            row("Bean Class:",
                descriptor.getBeanClass().getName());

            if (descriptor.getCustomizerClass() != null) {
                row("Customizer Class:",
                    descriptor.getCustomizerClass().getName());
            }
        }
    }

    private class BeanPropertyTableModel
                  extends ArrayListTableModel<PropertyDescriptor> {
        private static final long serialVersionUID = -8940320167807577680L;

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
