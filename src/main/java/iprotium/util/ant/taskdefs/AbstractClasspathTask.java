/*
 * $Id: AbstractClasspathTask.java,v 1.2 2008-10-28 09:19:31 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for Ant Task implementations that require a classpath.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractClasspathTask extends Task {
    private ClasspathUtils.Delegate delegate = null;

    /**
     * Sole constructor.
     */
    protected AbstractClasspathTask() { super(); }

    public Path createClasspath() { return delegate.createClasspath(); }
    public void setClasspathRef(Reference reference) {
        delegate.setClasspathref(reference);
    }

    @Override
    public void init() throws BuildException {
        delegate = ClasspathUtils.getDelegate(this);

        super.init();
    }

    protected Class getClass(String name) throws ClassNotFoundException {
        return Class.forName(name, false, delegate.getClassLoader());
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
