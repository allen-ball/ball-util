package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Interface to provide common default methods for
 * {@link org.apache.tools.ant.Task}s that implement the syntax described in
 * {@link org.apache.tools.ant.util.ClasspathUtils.Delegate}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
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
     * Required state for implementing {@link org.apache.tools.ant.Task}s.
     * Refer to the discussion in {@link ClasspathUtils}.
     *
     * @param   delegate        The
     *                          {@link org.apache.tools.ant.util.ClasspathUtils.Delegate}.
     *
     * @return  The {@link.this}.
     */
    ClasspathDelegateAntTask delegate(ClasspathUtils.Delegate delegate);

    /**
     * Default implementation for {@link org.apache.tools.ant.Task}
     * subclasses.
     */
    default void init() throws BuildException {
        if (delegate() == null) {
            delegate(ClasspathUtils.getDelegate((ProjectComponent) this));
        }
    }

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
     * See
     * {@link org.apache.tools.ant.util.ClasspathUtils.Delegate#setClassname(String)}.
     *
     * @param   name            The class name ({@link String}).
     */
    default void setClassname(String name) {
        delegate().setClassname(name);
    }

    /**
     * Method to get the {@link AntClassLoader} specified by {@link.this}
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
