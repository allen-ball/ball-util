/*
 * $Id: AbstractClasspathTask.java,v 1.7 2009-01-27 22:00:19 ball Exp $
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
 * @version $Revision: 1.7 $
 */
public abstract class AbstractClasspathTask extends Task {
    protected static final String TAB = "\t";

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

    protected Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(name, getInitialize(), getClassLoader());
    }

    protected void log(Object... objects) {
        String string = null;

        for (Object object : objects) {
            if (string == null) {
                string = "";
            } else {
                string += TAB;
            }

            string += String.valueOf(object);
        }

        if (string != null) {
            super.log(string);
        }
    }

    protected static String getName(Class<?> type) {
        return (type != null) ? type.getName() : null;
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
 * Revision 1.6  2008/11/29 06:12:49  ball
 * Added log(Object...) method.
 *
 */
