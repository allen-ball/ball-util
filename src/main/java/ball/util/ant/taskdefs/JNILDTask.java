/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import ball.io.FileImpl;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to link JNI shared libraries.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("jni-ld")
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
        return (((getPrefix() != null) ? getPrefix() : NIL)
                + getLibname()
                + ((getSuffix() != null) ? ("." + getSuffix()) : NIL));
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
