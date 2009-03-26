/*
 * $Id: JNILibPropertyTask.java,v 1.1 2009-03-26 01:08:21 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Abstract Ant Task to get platform-specific JNI values.
 *
 * @see Prefix
 * @see Suffix
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public abstract class JNILibPropertyTask extends Task {
    public static final JNIResourceBundle BUNDLE;

    static {
        try {
            BUNDLE = new JNIResourceBundle();
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private String arch = null;
    private String os = null;
    private String property = null;

    /**
     * Sole constructor.
     */
    protected JNILibPropertyTask() { super(); }

    protected String getArch() { return arch; }
    public void setArch(String arch) { this.arch = arch; }

    protected String getOS() { return os; }
    public void setOS(String os) { this.os = os; }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    protected abstract String getPropertyValue();

    @Override
    public void init() throws BuildException {
        super.init();

        if (getOS() == null) {
            setOS(getProject().getProperty("os.name"));
        }

        if (getArch() == null) {
            setArch(getProject().getProperty("os.arch"));
        }
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getProperty() != null && getPropertyValue() != null) {
            getProject().setProperty(getProperty(), getPropertyValue());
        } else {
            log(getPropertyValue());
        }
    }

    protected String getBundleString(String name) {
        return BUNDLE.getString(getOS(), getArch(), name);
    }

    /**
     * Ant Task to get platform-specific JNI library prefix.
     */
    public static class Prefix extends JNILibPropertyTask {

        /**
         * Sole constructor.
         */
        public Prefix() { super(); }

        @Override
        protected String getPropertyValue() {
            return getBundleString("jnilib-prefix");
        }
    }

    /**
     * Ant Task to get platform-specific JNI library suffix.
     */
    public static class Suffix extends JNILibPropertyTask {

        /**
         * Sole constructor.
         */
        public Suffix() { super(); }

        @Override
        protected String getPropertyValue() {
            return getBundleString("jnilib-suffix");
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
