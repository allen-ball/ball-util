/*
 * $Id: JNILibPropertyTask.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Abstract Ant {@link Task} to get platform-specific JNI values.
 *
 * @see Prefix
 * @see Suffix
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public abstract class JNILibPropertyTask extends Task {
    private static final String PREFIX;
    private static final String SUFFIX;

    static {
        String name = "LIBNAME";
        String[] tokens = System.mapLibraryName(name).split(name, 2);
        String prefix = (tokens.length > 0) ? tokens[0] : null;
        String suffix = (tokens.length > 1) ? tokens[1] : null;

        if (suffix != null) {
            String dot = ".";

            while (suffix.startsWith(dot)) {
                suffix = suffix.substring(dot.length());
            }
        }

        PREFIX = prefix;
        SUFFIX = suffix;
    }

    private String property = null;

    /**
     * Sole constructor.
     */
    protected JNILibPropertyTask() { super(); }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    /**
     * Method to get the value to assign to the property.
     *
     * @return  The property value.
     */
    protected abstract String getPropertyValue();

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getProperty() != null && getPropertyValue() != null) {
            getProject().setProperty(getProperty(), getPropertyValue());
        } else {
            log(getPropertyValue());
        }
    }

    /**
     * Ant {@link Task} to get platform-specific JNI library prefix.
     */
    public static class Prefix extends JNILibPropertyTask {

        /**
         * Sole constructor.
         */
        public Prefix() { super(); }

        @Override
        protected String getPropertyValue() { return PREFIX; }
    }

    /**
     * Ant {@link Task} to get platform-specific JNI library suffix.
     */
    public static class Suffix extends JNILibPropertyTask {

        /**
         * Sole constructor.
         */
        public Suffix() { super(); }

        @Override
        protected String getPropertyValue() { return SUFFIX; }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
