/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Interface to provide common default methods for
 * {@link org.apache.tools.ant.Task}s that implement the syntax described in
 * {@link org.apache.tools.ant.util.ClasspathUtils.Delegate}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface ClasspathDelegateAntTask extends AntTaskMixIn {

    /**
     * Required state for implementing {@link org.apache.tools.ant.Task}s.
     * Refer to the discussion in {@link ClasspathUtils}.
     *
     * @return  The {@link org.apache.tools.ant.util.ClasspathUtils.Delegate}
     *          instance created in the
     *          {@link org.apache.tools.ant.Task#init()} method.
     */
    ClasspathUtils.Delegate delegate();

    /**
     * See
     * {@link org.apache.tools.ant.util.ClasspathUtils.Delegate#setClasspathref(Reference)}.
     *
     * @param   reference       The {@link Reference} to the classpath.
     */
    default void setClasspathref(Reference reference) {
        delegate().setClasspathref(reference);
    }

    /**
     * See
     * {@link org.apache.tools.ant.util.ClasspathUtils.Delegate#createClasspath()}.
     *
     * @return  The created {@link Path}.
     */
    default Path createClasspath() { return delegate().createClasspath(); }

    /**
     * Method to get the {@link AntClassLoader} specified by this
     * {@link org.apache.tools.ant.Task}.
     *
     * @return  The {@link AntClassLoader}.
     */
    default AntClassLoader getClassLoader() {
        if (delegate().getClasspath() == null) {
            delegate().createClasspath();
        }

        AntClassLoader loader = (AntClassLoader) delegate().getClassLoader();

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
    default Class<?> getClassForName(String name) throws ClassNotFoundException {
        return Class.forName(name, false, getClassLoader());
    }
}
