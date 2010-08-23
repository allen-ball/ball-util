/*
 * $Id: JNILDTask.java,v 1.7 2010-08-23 03:43:55 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.io.FileImpl;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to link JNI shared libraries.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.7 $
 */
public class JNILDTask extends AbstractJNIExecuteOnTask {
    private String libname = null;
    private String prefix = null;
    private String suffix = null;
    private String property = null;

    /**
     * Sole constructor.
     */
    public JNILDTask() {
        super();

        setParallel(true);
    }

    protected String getLibname() { return libname; }
    public void setLibname(String libname) { this.libname = libname; }

    protected String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    protected String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    @Override
    protected String command() {
        return (getBundleString("ld") + SPACE
                + new FileImpl(getDestdir(), getName()).getAbsolutePath());
    }

    @Override
    public void init() throws BuildException {
        super.init();

        setPrefix(getBundleString("jnilib-prefix"));
        setSuffix(getBundleString("jnilib-suffix"));
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getProperty() != null) {
            getProject().setProperty(getProperty(), getName());
        }
    }

    private String getName() {
        return (((getPrefix() != null) ? getPrefix() : "")
                + getLibname()
                + ((getSuffix() != null) ? ("." + getSuffix()) : ""));
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
