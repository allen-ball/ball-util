/*
 * $Id: AbstractPropertyTask.java,v 1.2 2011-04-24 19:56:36 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Abstract <a href="http://ant.apache.org/">Ant</a> base class for
 * {@link Task}s that may assign property values.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractPropertyTask extends Task {
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
    protected abstract String getPropertyValue() throws Exception;

    @Override
    public void execute() throws BuildException {
        super.execute();

        String value = null;

        try {
            value = getPropertyValue();
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            if (getProperty() == null) {
                throw new BuildException(throwable);
            }
        }

        if (getProperty() != null && value != null) {
            getProject().setProperty(getProperty(), value);
        } else {
            log(value);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
