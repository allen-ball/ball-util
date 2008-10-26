/*
 * $Id: AbstractClasspathTask.java,v 1.1 2008-10-26 23:59:22 ball Exp $
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
 * @version $Revision: 1.1 $
 */
public abstract class AbstractClasspathTask extends Task {
    protected ClasspathUtils.Delegate delegate = null;

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
}
/*
 * $Log: not supported by cvs2svn $
 */
