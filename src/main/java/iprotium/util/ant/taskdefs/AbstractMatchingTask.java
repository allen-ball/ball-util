/*
 * $Id: AbstractMatchingTask.java,v 1.7 2010-11-07 22:01:54 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for <a href="http://ant.apache.org/">Ant</a>
 * {@link MatchingTask} implementations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.7 $
 */
public abstract class AbstractMatchingTask extends MatchingTask {
    private boolean initialize = false;
    private ClasspathUtils.Delegate delegate = null;
    private File basedir = null;
    private File file = null;

    /**
     * Sole constructor.
     */
    protected AbstractMatchingTask() { super(); }

    protected boolean getInitialize() { return initialize; }
    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    public void setClasspathRef(Reference reference) {
        delegate.setClasspathref(reference);
    }

    public Path createClasspath() { return delegate.createClasspath(); }

    protected File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }

    protected File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    @Override
    public void init() throws BuildException {
        super.init();

        if (delegate == null) {
            delegate = ClasspathUtils.getDelegate(this);
        }
    }

    @Override
    public void execute() throws BuildException {
        if (getBasedir() == null && getFile() == null) {
            setBasedir(getProject().resolveFile("."));
        }
    }

    protected AntClassLoader getClassLoader() {
        if (delegate.getClasspath() == null) {
            delegate.createClasspath();
        }

        AntClassLoader loader = (AntClassLoader) delegate.getClassLoader();

        loader.setParent(Thread.currentThread().getContextClassLoader());

        return loader;
    }

    protected Class<?> getClass(String name) throws ClassNotFoundException {
        return AbstractClasspathTask.getClass(name,
                                              getInitialize(),
                                              getClassLoader());
    }

    protected Set<File> getMatchingFileSet() {
        Set<File> set = new TreeSet<File>();
        File base = getBasedir();

        if (base != null) {
            for (String path : getDirectoryScanner(base).getIncludedFiles()) {
                set.add(new File(base, path));
            }
        }

        File file = getFile();

        if (file != null) {
            set.add(file);
        }

        return set;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
