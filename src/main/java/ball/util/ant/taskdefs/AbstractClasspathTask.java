/*
 * $Id$
 *
 * Copyright 2008 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
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
                                                       AntTaskLogMethods {
    private ClasspathUtils.Delegate delegate = null;

    /**
     * Sole constructor.
     */
    protected AbstractClasspathTask() { super(); }

    public void setClasspathRef(Reference reference) {
        delegate.setClasspathref(reference);
    }

    public Path createClasspath() { return delegate.createClasspath(); }

    /**
     * {@inheritDoc}
     *
     * Invokes {@link ConfigurableAntTask#configure()} if {@code this}
     * implements {@link ConfigurableAntTask}.
     */
    @Override
    public void init() throws BuildException {
        super.init();

        if (delegate == null) {
            delegate = ClasspathUtils.getDelegate(this);
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

    /**
     * Method to get the {@link AntClassLoader} specified by this
     * {@link Task}.
     *
     * @return  The {@link AntClassLoader}.
     */
    protected AntClassLoader getClassLoader() {
        if (delegate.getClasspath() == null) {
            delegate.createClasspath();
        }

        AntClassLoader loader = (AntClassLoader) delegate.getClassLoader();

        loader.setParent(getClass().getClassLoader());

        return loader;
    }

    /**
     * Method to get the {@link Class} associated with the argument name
     * using the {@link ClassLoader} provided by {@link #getClassLoader()}.
     *
     * @param   name            The fully qualified name of the desired
     *                          class.
     *
     * @return  The {@link Class} for the specified name.
     *
     * @throws  ClassNotFoundException
     *                          If the {@link Class} is not found.
     */
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        return Class.forName(name, false, getClassLoader());
    }

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
