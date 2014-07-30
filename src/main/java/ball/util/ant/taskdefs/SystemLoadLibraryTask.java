/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

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

    @NotNull
    public String getLibname() { return libname; }
    public void setLibname(String libname) { this.libname = libname; }

    @Override
    protected String getPropertyValue() throws Throwable {
        System.loadLibrary(getLibname());

        return System.mapLibraryName(getLibname());
    }
}
