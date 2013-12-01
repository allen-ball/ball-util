/*
 * $Id$
 *
 * Copyright 2011 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;

/**
 * Abstract <a href="http://ant.apache.org/">Ant</a> base class for
 * {@link org.apache.tools.ant.Task}s that may assign property values.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractPropertyTask extends AbstractClasspathTask {
    private String property = null;

    /**
     * Sole constructor.
     */
    protected AbstractPropertyTask() { super(); }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    /**
     * Method to get the value to assign to the property.
     *
     * @return  The property value.
     */
    protected abstract String getPropertyValue() throws Throwable;

    @Override
    public void execute() throws BuildException {
        String key = getProperty();
        String value = null;

        try {
            value = getPropertyValue();
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            if (key == null) {
                throw new BuildException(throwable);
            }
        }

        if (key != null) {
            if (value != null) {
                getProject().setProperty(key, value);
            }
        } else {
            log(value);
        }
    }
}
