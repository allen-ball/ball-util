/*
 * $Id$
 *
 * Copyright 2008 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.io.FileImpl;
import ball.util.ant.types.OptionalTextType;
import java.util.ArrayList;
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
    private ArrayList<OptionalTextType> linkSet =
        new ArrayList<OptionalTextType>();

    /**
     * Sole constructor.
     */
    public JNILDTask() {
        super();

        setParallel(true);
    }

    @NotNull
    public String getLibname() { return libname; }
    public void setLibname(String libname) { this.libname = libname; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    public void addConfiguredLink(OptionalTextType library) {
        linkSet.add(library);
    }

    @Override
    protected String command() {
        String string =
            getBundleString("ld")
            + SPACE + new FileImpl(getDestdir(), getName()).getAbsolutePath();

        for (OptionalTextType library : linkSet) {
            if (library.isActive(getProject())) {
                string += SPACE + getBundleString("ld-l") + library;
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

        if (getProperty() != null) {
            getProject().setProperty(getProperty(), getName());
        }
    }

    private String getName() {
        return (((getPrefix() != null) ? getPrefix() : NIL)
                + getLibname()
                + ((getSuffix() != null) ? ("." + getSuffix()) : NIL));
    }
}
