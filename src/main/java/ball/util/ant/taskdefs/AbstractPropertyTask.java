/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant} base class for
 * {@link org.apache.tools.ant.Task}s that may assign property values.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractPropertyTask extends AbstractClasspathTask {
    private String property = null;

    /**
     * Sole constructor.
     */
    protected AbstractPropertyTask() { super(); }

    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    /**
     * Method to get the value to assign to the property.
     *
     * @return  The property value.
     */
    protected abstract String getPropertyValue() throws Throwable;

    @Override
    public void execute() throws BuildException {
        super.execute();

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
