/*
 * $Id: AbstractClassFileTask.java,v 1.8 2009-01-29 05:36:30 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.util.ClasspathUtils;

import static iprotium.util.ant.taskdefs.AbstractClasspathTask.TAB;

/**
 * Abstract base class for Ant Task implementations that select *.CLASS
 * files.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.8 $
 */
public abstract class AbstractClassFileTask extends AbstractMatchingTask {
    private static final String DOT_CLASS = ".class";
    private static final String DOT_JAVA = ".java";

    private boolean initialize = false;
    private ClasspathUtils.Delegate delegate = null;
    private AntClassLoader loader = null;
    private Path srcPath = null;

    /**
     * Sole constructor.
     */
    protected AbstractClassFileTask() { super(); }

    protected boolean getInitialize() { return initialize; }
    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    protected Path getSrcdir() { return srcPath; }
    public void setSrcdir(Path srcdir) {
        if (srcPath == null) {
            srcPath = srcdir;
        } else {
            srcPath.append(srcdir);
        }
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

    public Path createSrc() {
        if (srcPath == null) {
            srcPath = new Path(getProject());
        }

        return srcPath.createPath();
    }

    @Override
    public void init() throws BuildException {
        super.init();

        if (delegate == null) {
            delegate = ClasspathUtils.getDelegate(this);
        }

        add(new ClassFileSelector());
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (delegate.getClasspath() == null) {
            delegate.createClasspath();
        }

        delegate.getClasspath().setLocation(getBasedir());
    }

    protected Map<File,Class> getMatchingClassFileMap() throws BuildException {
        Map<File,Class> map = new LinkedHashMap<File,Class>();

        for (File file : getMatchingFileSet()) {
            String name =
                getBasedir().toURI().relativize(file.toURI()).toString();

            if (name.toLowerCase().endsWith(DOT_CLASS)) {
                name = name.substring(0, name.length() - DOT_CLASS.length());
            }

            name = name.replaceAll("[/]", ".");

            try {
                map.put(file, getClass(name));
            } catch (ClassNotFoundException exception) {
                throw new BuildException(exception);
            }
        }

        return map;
    }

    protected Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(name, getInitialize(), getClassLoader());
    }

    protected File getJavaFile(Map<File,Class> map, File file) {
        File javaFile = null;
        Class<?> type = map.get(file);

        if (srcPath != null && type != null) {
            while (type.getDeclaringClass() != null) {
                type = type.getDeclaringClass();
            }

            while (type.getEnclosingClass() != null) {
                type = type.getEnclosingClass();
            }

            String child =
                type.getCanonicalName().replaceAll("[.]", File.separator)
                + DOT_JAVA;

            for (String parent : srcPath.list()) {
                javaFile = new File(parent, child);

                if (javaFile.isFile()) {
                    break;
                } else {
                    javaFile = null;
                }
            }
        }

        return (javaFile != null) ? javaFile : file;
    }

    protected void log(File file, int lineno, String message) {
        super.log(String.valueOf(file) + ":" + String.valueOf(lineno)
                  + ": " + message);
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
        return AbstractClasspathTask.getName(type);
    }

    protected static boolean isAbstract(Class type) {
        return AbstractClasspathTask.isAbstract(type);
    }

    protected static boolean isAbstract(Member member) {
        return AbstractClasspathTask.isAbstract(member);
    }

    protected static boolean isPublic(Class type) {
        return AbstractClasspathTask.isPublic(type);
    }

    protected static boolean isPublic(Member member) {
        return AbstractClasspathTask.isPublic(member);
    }

    protected static boolean isStatic(Class type) {
        return AbstractClasspathTask.isStatic(type);
    }

    protected static boolean isStatic(Member member) {
        return AbstractClasspathTask.isStatic(member);
    }

    protected static boolean isNative(Class type) {
        return AbstractClasspathTask.isNative(type);
    }

    protected static boolean isNative(Member member) {
        return AbstractClasspathTask.isNative(member);
    }

    private static class ClassFileSelector implements FileSelector {
        public ClassFileSelector() { }

        public boolean isSelected(File basedir, String name, File file) {
            return name.toLowerCase().endsWith(DOT_CLASS);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
