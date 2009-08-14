/*
 * $Id: AbstractClasspathTask.java,v 1.9 2009-08-14 22:47:08 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
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
 * @version $Revision: 1.9 $
 */
public abstract class AbstractClasspathTask extends Task {
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
        return Class.forName(name, getInitialize(), getClassLoader());
    }

    protected static boolean isAbstract(Class type) {
        return Modifier.isAbstract(type.getModifiers());
    }

    protected static boolean isAbstract(Member member) {
        return Modifier.isAbstract(member.getModifiers());
    }

    protected static boolean isPublic(Class type) {
        return Modifier.isPublic(type.getModifiers());
    }

    protected static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    protected static boolean isStatic(Class type) {
        return Modifier.isStatic(type.getModifiers());
    }

    protected static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    protected static boolean isNative(Class type) {
        return Modifier.isNative(type.getModifiers());
    }

    protected static boolean isNative(Member member) {
        return Modifier.isNative(member.getModifiers());
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
