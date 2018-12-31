/*
 * $Id$
 *
 * Copyright 2008 - 2018 Allen D. Ball.  All rights reserved.
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

    /**
     * {@inheritDoc}
     *
     * Invokes {@link ConfigurableAntTask#configure()} if {@code this}
     * implements {@link ConfigurableAntTask}.
     */
    @Override
    public void init() throws BuildException {
        super.init();

        if (this instanceof ClasspathDelegateAntTask) {
            if (delegate == null) {
                delegate = ClasspathUtils.getDelegate(this);
            }
        }

        if (this instanceof ConfigurableAntTask) {
            ((ConfigurableAntTask) this).configure();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Invokes {@link AnnotatedAntTask#validate()} if {@code this}
     * implements {@link AnnotatedAntTask}.
     */
    @Override
    public void execute() throws BuildException {
        super.execute();

        if (this instanceof AnnotatedAntTask) {
            ((AnnotatedAntTask) this).validate();
        }
    }

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
