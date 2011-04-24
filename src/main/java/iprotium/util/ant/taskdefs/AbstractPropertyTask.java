/*
 * $Id: AbstractPropertyTask.java,v 1.4 2011-04-24 20:18:06 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;

/**
 * Abstract <a href="http://ant.apache.org/">Ant</a> base class for
 * {@link org.apache.tools.ant.Task}s that may assign property values.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
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
    protected abstract String getPropertyValue() throws Exception;

    @Override
    public void execute() throws BuildException {
        String key = getProperty();
        String value = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());

            value = getPropertyValue();
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            if (key == null) {
                throw new BuildException(throwable);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(loader);
        }

        if (key != null && value != null) {
            getProject().setProperty(key, value);
        } else {
            log(value);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
