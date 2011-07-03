/*
 * $Id$
 *
 * Copyright 2008 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.PrimitiveClassMap;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for <a href="http://ant.apache.org/">Ant</a>
 * {@link Task} implementations that require a classpath.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractClasspathTask extends Task {
    private static final SortedMap<String,Class<?>> JAVA_PRIMITIVE_MAP =
        Collections.unmodifiableSortedMap(new JavaPrimitiveMap());

    private boolean initialize = false;
    private ClasspathUtils.Delegate delegate = null;

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

    @Override
    public void init() throws BuildException {
        super.init();

        if (delegate == null) {
            delegate = ClasspathUtils.getDelegate(this);
        }
    }

    @Override
    public abstract void execute() throws BuildException;

    protected AntClassLoader getClassLoader() {
        if (delegate.getClasspath() == null) {
            delegate.createClasspath();
        }

        AntClassLoader loader = (AntClassLoader) delegate.getClassLoader();

        loader.setParent(Thread.currentThread().getContextClassLoader());

        return loader;
    }

    protected Class<?> getClass(String name) throws ClassNotFoundException {
        return getClass(name, getInitialize(), getClassLoader());
    }

    protected static Class<?> getClass(String name,
                                       boolean initialize, ClassLoader loader)
            throws ClassNotFoundException {
        Class<?> type = JAVA_PRIMITIVE_MAP.get(name);

        if (type == null) {
            type = Class.forName(name, initialize, loader);
        }

        return type;
    }

    public static class JavaPrimitiveMap extends TreeMap<String,Class<?>> {
        private static final long serialVersionUID = -4043723550289168765L;

        public JavaPrimitiveMap() {
            super();

            for (Class<?> type : PrimitiveClassMap.INSTANCE.keySet()) {
                put(type.getName(), type);
            }
        }
    }
}
