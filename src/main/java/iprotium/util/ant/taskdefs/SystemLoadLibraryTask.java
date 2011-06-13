/*
 * $Id: SystemLoadLibraryTask.java,v 1.3 2011-06-13 02:24:14 ball Exp $
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to load a system library.
 *
 * @see System#loadLibrary(String)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
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
/*
 * $Log: not supported by cvs2svn $
 */
