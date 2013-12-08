/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link MatchingTask} implementations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractMatchingTask extends MatchingTask {
    private File basedir = null;
    private File file = null;

    /**
     * Sole constructor.
     */
    protected AbstractMatchingTask() { super(); }

    protected File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }

    protected File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    @Override
    public void execute() throws BuildException {
        if (getBasedir() == null && getFile() == null) {
            setBasedir(getProject().resolveFile("."));
        }
    }

    protected Set<File> getMatchingFileSet() {
        TreeSet<File> set = new TreeSet<File>();
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

    /**
     * See {@link #log(String)}.
     */
    protected void log(Iterable<String> iterable) {
        log(iterable, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     */
    protected void log(Iterable<String> iterable, int msgLevel) {
        for (String line : iterable) {
            log(line, msgLevel);
        }
    }

    /**
     * See {@link #log(String)}.
     */
    protected void log(Iterator<String> iterator) {
        log(iterator, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     */
    protected void log(Iterator<String> iterator, int msgLevel) {
        while (iterator.hasNext()) {
            log(iterator.next(), msgLevel);
        }
    }

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
