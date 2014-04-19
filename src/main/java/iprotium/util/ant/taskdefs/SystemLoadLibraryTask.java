/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to load a system library.
 *
 * {@bean-info}
 *
 * @see System#loadLibrary(String)
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("system-load-library")
public class SystemLoadLibraryTask extends AbstractPropertyTask {
    private String libname = null;

    /**
     * Sole constructor.
     */
    public SystemLoadLibraryTask() { super(); }

    protected String getLibname() { return libname; }
    public void setLibname(String libname) { this.libname = libname; }

    @Override
    public void execute() throws BuildException {
        if (getLibname() == null) {
            throw new BuildException("`libname' attribute must be specified");
        }

        super.execute();
    }

    @Override
    protected String getPropertyValue() throws Throwable {
        System.loadLibrary(getLibname());

        return System.mapLibraryName(getLibname());
    }
}
