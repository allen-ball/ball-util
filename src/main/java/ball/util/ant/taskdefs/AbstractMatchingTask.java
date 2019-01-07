/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.util.ClasspathUtils;

/**
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link MatchingTask} implementations.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractMatchingTask extends MatchingTask
                                           implements AnnotatedAntTask,
                                                      ClasspathDelegateAntTask,
                                                      AntTaskLogMethods {
    private ClasspathUtils.Delegate delegate = null;
    private File basedir = null;
    private File file = null;

    /**
     * Sole constructor.
     */
    protected AbstractMatchingTask() { super(); }

    public File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }

    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    @Override
    public ClasspathUtils.Delegate delegate() { return delegate; }

    @Override
    public AbstractMatchingTask delegate(ClasspathUtils.Delegate delegate) {
        this.delegate = delegate;

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * Invokes {@link ConfigurableAntTask#configure()} if {@link.this}
     * implements {@link ConfigurableAntTask}.
     */
    @Override
    public void init() throws BuildException {
        super.init();

        /* ClasspathDelegateAntTask.super.init(); */
        if (this instanceof ClasspathDelegateAntTask) {
            if (delegate() == null) {
                delegate(ClasspathUtils.getDelegate(this));
            }
        }

        /* ConfigurableAntTask.super.init(); */
        if (this instanceof ConfigurableAntTask) {
            ((ConfigurableAntTask) this).configure();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Invokes {@link AnnotatedAntTask#validate()} if {@link.this}
     * implements {@link AnnotatedAntTask}.
     */
    @Override
    public void execute() throws BuildException {
        super.execute();

        /* AnnotatedAntTask.super.execute(); */
        if (this instanceof AnnotatedAntTask) {
            ((AnnotatedAntTask) this).validate();
        }

        if (getBasedir() == null && getFile() == null) {
            setBasedir(getProject().resolveFile("."));
        }
    }

    /**
     * Method to get {@link.this} {@link MatchingTask}'s {@link File}s as a
     * {@link Set}.
     *
     * @return  The {@link Set} of matching {@link File}s.
     */
    protected Set<File> getMatchingFileSet() {
        TreeSet<File> set = new TreeSet<>();
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

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
