/*
 * $Id: BeanInfoForTask.java,v 1.1 2008-11-20 06:36:30 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to display BeanInfo for a specified Class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class BeanInfoForTask extends AbstractClasspathTask {
    private static final String TAB = "\t";

    private String type = null;

    /**
     * Sole constructor.
     */
    public BeanInfoForTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public void execute() throws BuildException {
        super.execute();

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
        BeanDescriptor descriptor = bean.getBeanDescriptor();

        log("Bean Class:", getName(descriptor.getBeanClass()));
        log("Customizer Class:", getName(descriptor.getCustomizerClass()));

        log("");
        log("Property Name", "Mode", "Type", "isBound", "isConstrained");

        for (PropertyDescriptor property : bean.getPropertyDescriptors()) {
            log(property.getName(),
                (((property.getReadMethod() != null) ? "R" : "")
                 + ((property.getWriteMethod() != null) ? "W" : "")),
                getName(property.getPropertyType()),
                property.isBound(),
                property.isConstrained());
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

    private void log(Object... objects) {
        String string = null;

        for (Object object : objects) {
            if (string == null) {
                string = "";
            } else {
                string += TAB;
            }

            string += String.valueOf(object);
        }

        if (string != null) {
            super.log(string);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */