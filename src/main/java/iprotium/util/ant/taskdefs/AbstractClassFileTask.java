/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.ClassOrder;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFile;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFileUtils;
import org.apache.tools.ant.taskdefs.optional.depend.DirectoryIterator;
import org.apache.tools.ant.types.Path;

/**
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} implementations that select
 * {@code *.CLASS} files.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractClassFileTask extends AbstractClasspathTask {
    private static final String DOT_JAVA = ".java";

    private File basedir = null;
    private Path srcPath = null;

    /**
     * Sole constructor.
     */
    protected AbstractClassFileTask() { super(); }

    protected File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }

    protected Path getSrcdir() { return srcPath; }
    public void setSrcdir(Path srcdir) {
        if (srcPath == null) {
            srcPath = srcdir;
        } else {
            srcPath.append(srcdir);
        }
    }

    public Path createSrc() {
        if (srcPath == null) {
            srcPath = new Path(getProject());
        }

        return srcPath.createPath();
    }

    @Override
    public void execute() throws BuildException {
        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }
    }

    protected Set<Class<?>> getClassSet() throws BuildException {
        TreeSet<Class<?>> set = new TreeSet<Class<?>>(ClassOrder.NAME);

        try {
            DirectoryIterator iterator =
                new DirectoryIterator(getBasedir(), true);
            ClassLoader loader = getClassLoader();
            ClassFile file = null;

            while ((file = iterator.getNextClassFile()) != null) {
                set.add(Class.forName(file.getFullClassName(), false, loader));
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }

        return set;
    }

    protected File getJavaFile(Class<?> type) {
        File file = null;

        if (srcPath != null && type != null) {
            while (type.getDeclaringClass() != null) {
                type = type.getDeclaringClass();
            }

            while (type.getEnclosingClass() != null) {
                type = type.getEnclosingClass();
            }

            String child =
                ClassFileUtils.convertDotName(type.getCanonicalName())
                + DOT_JAVA;

            for (String parent : srcPath.list()) {
                file = new File(parent, child);

                if (file.isFile()) {
                    break;
                } else {
                    file = null;
                }
            }
        }

        return file;
    }

    protected void log(File file, int lineno, String message) {
        super.log(String.valueOf(file) + ":" + String.valueOf(lineno)
                  + ": " + message);
    }
}
