/*
 * $Id: BeanInfoForTask.java,v 1.7 2009-08-14 22:55:25 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.text.ArrayListTableModel;
import iprotium.text.SimpleTable;
import iprotium.text.Table;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to display BeanInfo for a specified Class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.7 $
 */
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
            log(Introspector.getBeanInfo(getClass(getType())));
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }

    private void log(BeanInfo bean) {
        Table header = new BeanHeaderTable(bean.getBeanDescriptor());
        Table table = new BeanPropertyTable(bean.getPropertyDescriptors());

        for (String line : header) {
            log(line);
        }

        log("");

        for (String line : table) {
            log(line);
        }

        log(bean.getAdditionalBeanInfo());
    }

    private void log(BeanInfo[] beans) {
        if (beans != null) {
            for (BeanInfo bean : beans) {
                log("");
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

    private class BeanPropertyTable extends Table {
        public BeanPropertyTable(PropertyDescriptor[] rows) {
            super(new BeanPropertyTableModel(rows));
        }
    }

    private class BeanPropertyTableModel
                  extends ArrayListTableModel<PropertyDescriptor> {
        private static final long serialVersionUID = 4092624978038121007L;

        public BeanPropertyTableModel(PropertyDescriptor[] rows) {
            super(Arrays.asList(rows),
                  "Property Name", "Mode", "Type", "isBound", "isConstrained");
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
                value =
                    ((row.getReadMethod() != null) ? "R" : "")
                    + ((row.getWriteMethod() != null) ? "W" : "");
                break;

            case 2:
                value =
                    (row.getPropertyType() != null)
                        ? row.getPropertyType().getName() : "";
                break;

            case 3:
                value = row.isBound();
                break;

            case 4:
                value = row.isConstrained();
                break;
            }

            return value;
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
