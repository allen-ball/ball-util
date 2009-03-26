/*
 * $Id: JNILDTask.java,v 1.4 2009-03-26 01:37:23 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to link JNI shared libraries.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class JNILDTask extends AbstractJNIExecuteOnTask {
    private String libname = null;
    private String prefix = null;
    private String suffix = null;
    private String property = null;

    /**
     * Sole constructor.
     */
    public JNILDTask() { super(); }

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
        return (getBundleString("ld")
                + SPACE + new File(getDestdir(), getName()).getAbsolutePath());
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
