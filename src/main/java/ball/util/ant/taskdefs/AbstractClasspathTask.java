/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link Task} implementations that require a classpath.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractClasspathTask extends Task
                                            implements AnnotatedAntTask,
                                                       ClasspathDelegateAntTask,
                                                       AntTaskLogMethods {
    private ClasspathUtils.Delegate delegate = null;

    /**
     * Sole constructor.
     */
    protected AbstractClasspathTask() { super(); }

    @Override
    public ClasspathUtils.Delegate delegate() { return delegate; }

    @Override
    public AbstractClasspathTask delegate(ClasspathUtils.Delegate delegate) {
        this.delegate = delegate;

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * Invokes {@link ConfigurableAntTask#configure()} if {@link.this}
     * implements {@link ConfigurableAntTask}.
     */
    @Override
    public void init() throws BuildException {
        super.init();

        /* ClasspathDelegateAntTask.super.init(); */
        if (this instanceof ClasspathDelegateAntTask) {
            if (delegate() == null) {
                delegate(ClasspathUtils.getDelegate(this));
            }
        }

        /* ConfigurableAntTask.super.init(); */
        if (this instanceof ConfigurableAntTask) {
            ((ConfigurableAntTask) this).configure();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Invokes {@link AnnotatedAntTask#validate()} if {@link.this}
     * implements {@link AnnotatedAntTask}.
     */
    @Override
    public void execute() throws BuildException {
        super.execute();

        /* AnnotatedAntTask.super.execute(); */
        if (this instanceof AnnotatedAntTask) {
            ((AnnotatedAntTask) this).validate();
        }
    }

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
