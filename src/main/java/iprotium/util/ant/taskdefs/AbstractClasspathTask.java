/*
 * $Id: AbstractClasspathTask.java,v 1.3 2008-10-30 07:46:38 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for Ant Task implementations that require a classpath.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public abstract class AbstractClasspathTask extends Task {
    private boolean initialize = false;
    private ClasspathUtils.Delegate delegate = null;
    private AntClassLoader loader = null;

    /**
     * Sole constructor.
     */
    protected AbstractClasspathTask() { super(); }

    protected boolean getInitialize() { return initialize; }
    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    public void setClasspathRef(Reference reference) {
        delegate.setClasspathref(reference);
    }

    public Path createClasspath() { return delegate.createClasspath(); }

    protected AntClassLoader getClassLoader() {
        if (loader == null) {
            loader = (AntClassLoader) delegate.getClassLoader();
            loader.setParent(getClass().getClassLoader());
        }

        return loader;
    }

    @Override
    public void init() throws BuildException {
        super.init();

        if (delegate == null) {
            delegate = ClasspathUtils.getDelegate(this);
        }
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (delegate.getClasspath() == null) {
            delegate.createClasspath();
        }
    }

    protected Class getClass(String name) throws ClassNotFoundException {
        return Class.forName(name, getInitialize(), getClassLoader());
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
