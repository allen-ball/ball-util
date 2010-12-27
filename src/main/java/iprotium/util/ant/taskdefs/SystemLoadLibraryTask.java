/*
 * $Id: SystemLoadLibraryTask.java,v 1.1 2010-12-27 21:10:29 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to load a system library.
 *
 * @see System#loadLibrary(String)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class SystemLoadLibraryTask extends Task {
    private String libname = null;
    private String property = null;

    /**
     * Sole constructor.
     */
    public SystemLoadLibraryTask() { super(); }

    protected String getLibname() { return libname; }
    public void setLibname(String libname) { this.libname = libname; }

    protected String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    @Override
    public void execute() throws BuildException {
        if (getLibname() == null) {
            throw new BuildException("`libname' attribute must be specified");
        }

        String name = null;

        try {
            log("Loading " + getLibname() + "...");
            System.loadLibrary(getLibname());
            name = System.mapLibraryName(getLibname());
            log(name);
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            if (getProperty() == null) {
                throw new BuildException(throwable);
            }
        }

        if (getProperty() != null) {
            if (name != null) {
                getProject().setProperty(getProperty(), name);
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
