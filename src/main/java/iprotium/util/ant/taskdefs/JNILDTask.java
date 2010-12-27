/*
 * $Id: JNILDTask.java,v 1.9 2010-12-27 20:34:36 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.io.FileImpl;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to link JNI shared libraries.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.9 $
 */
public class JNILDTask extends AbstractJNIExecuteOnTask {
    private String libname = null;
    private String prefix = null;
    private String suffix = null;
    private String property = null;
    private LinkedHashSet<Library> linkSet = new LinkedHashSet<Library>();

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

    public void addConfiguredLink(Library library) { linkSet.add(library); }

    @Override
    protected String command() {
        String string =
            getBundleString("ld")
            + SPACE + new FileImpl(getDestdir(), getName()).getAbsolutePath();

        for (Library library : linkSet) {
            if (library.isActive(getProject())) {
                string += SPACE;
                string += getBundleString("ld-l");
                string += library.getName();
            }
        }

        return string;
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

        if (getLibname() == null) {
            throw new BuildException("`libname' attribute must be specified");
        }

        if (getProperty() != null) {
            getProject().setProperty(getProperty(), getName());
        }
    }

    private String getName() {
        return (((getPrefix() != null) ? getPrefix() : "")
                + getLibname()
                + ((getSuffix() != null) ? ("." + getSuffix()) : ""));
    }

    /**
     * {@link JNILDTask} LD link definition.
     */
    public static class Library extends Optional {

        /**
         * Sole constructor.
         *
         * @param       name            The library name.
         *
         * @see #setName(String)
         */
        public Library(String name) { super(name); }

        /**
         * No-argument constructor.
         */
        public Library() { this(null); }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
